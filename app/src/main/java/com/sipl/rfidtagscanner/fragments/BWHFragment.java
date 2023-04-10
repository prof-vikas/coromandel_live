package com.sipl.rfidtagscanner.fragments;

import static android.content.Context.MODE_PRIVATE;
import static com.sipl.rfidtagscanner.utils.Config.EMPTY_LEP_NUMBER_LIST;
import static com.sipl.rfidtagscanner.utils.Config.EMPTY_REMARKS;
import static com.sipl.rfidtagscanner.utils.Config.EMPTY_WAREHOUSE_NUMBER;
import static com.sipl.rfidtagscanner.utils.Config.isRMGTableRequired;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.sipl.rfidtagscanner.MainActivity;
import com.sipl.rfidtagscanner.R;
import com.sipl.rfidtagscanner.RetrofitController;
import com.sipl.rfidtagscanner.adapter.RmgDataAdapter;
import com.sipl.rfidtagscanner.adapter.TripsDataAdapter;
import com.sipl.rfidtagscanner.dto.dtos.RemarksDto;
import com.sipl.rfidtagscanner.dto.dtos.RfidLepIssueDto;
import com.sipl.rfidtagscanner.dto.dtos.StorageLocationDto;
import com.sipl.rfidtagscanner.dto.dtos.TransactionsDto;
import com.sipl.rfidtagscanner.dto.request.UpdateWareHouseNoRequestDto;
import com.sipl.rfidtagscanner.dto.response.RemarkApiResponse;
import com.sipl.rfidtagscanner.dto.response.RmgNumberApiResponse;
import com.sipl.rfidtagscanner.dto.response.TransactionsApiResponse;
import com.sipl.rfidtagscanner.entites.AuditEntity;
import com.sipl.rfidtagscanner.utils.CustomToast;
import com.sipl.rfidtagscanner.utils.Helper;
import com.sipl.rfidtagscanner.utils.RecyclerviewHardcodedData;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class BWHFragment extends Fragment {

    private final String TAG = "TracingError";
    private final Helper helper = new Helper();
    private final CustomToast customToast = new CustomToast();
    private TextClock tvClock;
    private ProgressBar progressBar;
    private TextView tvLepNumber;
    private AutoCompleteTextView autoCompleteLepNo;
    private Spinner spinnerWarehouseNo, spinnerRemark;
    private EditText edtDriverName, edtTruckNumber, edtCommodity, edtGrossWeight, edtPreviousWareHouseNo;
    private RecyclerView recyclerViewRmgNo, recyclerViewTrip;
    private LinearLayout mainRecyclerViewLayout;
    private Button btnSubmit, btnReset;
    private String selectedLepNumber;
    private Integer selectedLepNumberId;
    private String selectedWareHouseNumber;

    //    userDetails
    private String loginUserName;
    private String token;
    private String loginUserPlantCode;

    private String selectedRemarks;
    private Integer selectedRemarksId;
    private ArrayAdapter<String> remarkAdapter;
    private ArrayAdapter<String> updateWareHouseNoAdapter;
    private ArrayAdapter<String> arrayAdapterForLepNumber;
    private String previousWarehouseCode;

    public BWHFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        ((MainActivity) getActivity()).setActionBarTitle(getResources().getString(R.string.title_bothra));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_b_w_h, container, false);
        spinnerWarehouseNo = view.findViewById(R.id.bwh_spinner_warehouse_no);
        spinnerRemark = view.findViewById(R.id.bwh_spinner_remark);
        tvLepNumber = view.findViewById(R.id.bwh_tv_lep_number);
        tvClock = view.findViewById(R.id.bwh_tv_clock);
        autoCompleteLepNo = view.findViewById(R.id.bwh_auto_complete_lep_number);
        edtDriverName = view.findViewById(R.id.bwh_edt_driver_name);
        edtTruckNumber = view.findViewById(R.id.bwh_edt_truck_no);
        edtCommodity = view.findViewById(R.id.bwh_edt_commodity);
        edtGrossWeight = view.findViewById(R.id.bwh_edt_gross_weight);
        edtPreviousWareHouseNo = view.findViewById(R.id.bwh_edt_previous_ware_house_no);
        progressBar = view.findViewById(R.id.bwh_progressBar);
        btnReset = view.findViewById(R.id.bwh_btn_reset);
        btnSubmit = view.findViewById(R.id.bwh_btn_submit);
        mainRecyclerViewLayout = view.findViewById(R.id.main_recycler_view_layoutout);

        helper.multiColorStringForTv(tvLepNumber, "LEP Number", " *");
        this.token = getToken();
        this.loginUserName = ((MainActivity) getActivity()).getLoginUsername();
        this.loginUserPlantCode = ((MainActivity) getActivity()).getLoginUserPlantCode();

        setTvClock();
        callOnCreateApi();

        btnSubmit.setOnClickListener(view12 -> {
            if (validateLoadingAdviseForm()) {
                updateWareHouseNo(setData());
            }
        });

        btnReset.setOnClickListener(view1 -> resetFields());
        return view;
    }

    private void resetFields() {
        autoCompleteLepNo.setText(null);
        edtDriverName.setText(null);
        edtTruckNumber.setText(null);
        edtCommodity.setText(null);
        edtGrossWeight.setText(null);
        edtPreviousWareHouseNo.setText(null);

        if (updateWareHouseNoAdapter == null || remarkAdapter == null) {
            callOnCreateApi();
        } else {
            spinnerWarehouseNo.setSelection(updateWareHouseNoAdapter.getCount());
            spinnerRemark.setSelection(remarkAdapter.getCount());
        }

        if (arrayAdapterForLepNumber != null) {
            arrayAdapterForLepNumber.clear();
        }
        getALlLepNumberBothra();

        removeErrorMessage();
    }

    private void removeErrorMessage() {
        autoCompleteLepNo.setError(null);
        edtTruckNumber.setError(null);
        edtDriverName.setError(null);
        edtCommodity.setError(null);
        edtGrossWeight.setError(null);
        edtPreviousWareHouseNo.setError(null);
    }

    private void callOnCreateApi() {
        getALlLepNumberBothra();
        getAllWareHouse();
        getAllBothraRemark();
    }

    private boolean validateLoadingAdviseForm() {
        if (autoCompleteLepNo.length() == 0) {
            autoCompleteLepNo.setError("This field is required");
            return false;
        }
        if (edtTruckNumber.length() == 0) {
            edtTruckNumber.setError("This field is required");
            return false;
        }
        if (edtDriverName.length() == 0) {
            edtDriverName.setError("This field is required");
            return false;
        }
        if (edtCommodity.length() == 0) {
            edtCommodity.setError("This field is required");
            return false;
        }
        if (edtGrossWeight.length() == 0) {
            edtGrossWeight.setError("This field is required");
            return false;
        }
        if (edtPreviousWareHouseNo.length() == 0) {
            edtPreviousWareHouseNo.setError("This field is required");
            return false;
        }
        if (!spinnerWarehouseNo.getSelectedItem().toString().equals("Select Warehouse No") && spinnerRemark.getSelectedItem().toString().equals("Select Remarks")) {
            btnSubmit.setEnabled(false);
            Toast.makeText(getActivity(), "Select remarks", Toast.LENGTH_SHORT).show();
            return false;
        }
        removeErrorMessage();
        return true;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle
            savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (isRMGTableRequired == true) {
            mainRecyclerViewLayout.setVisibility(View.VISIBLE);
            recyclerViewRmgNo = getActivity().findViewById(R.id.recycler_view_rmg_no);
            recyclerViewTrip = getActivity().findViewById(R.id.recycler_view_trips);
            setRecyclerView();
        }
    }

    private String getToken() {
        SharedPreferences sp = getActivity().getSharedPreferences("loginCredentials", MODE_PRIVATE);
        String userToken = sp.getString("tokenSPK", null);
        return userToken;
    }

    private void setRecyclerView() {
        recyclerViewRmgNo.setHasFixedSize(false);
        recyclerViewTrip.setHasFixedSize(false);
        RecyclerviewHardcodedData recyclerviewHardcodedData = new RecyclerviewHardcodedData();

        RecyclerView.LayoutManager rmgNoLayoutManager = new LinearLayoutManager(getActivity());
        recyclerViewRmgNo.setLayoutManager(rmgNoLayoutManager);

        RecyclerView.LayoutManager tripLayoutManager = new LinearLayoutManager(getActivity());
        recyclerViewTrip.setLayoutManager(tripLayoutManager);

        RmgDataAdapter rmgDataAdapter = new RmgDataAdapter((Context)
                getActivity(), recyclerviewHardcodedData.initRmgData());
        recyclerViewRmgNo.setAdapter(rmgDataAdapter);

        TripsDataAdapter tripRmgDataAdapter = new TripsDataAdapter((Context)
                getActivity(), recyclerviewHardcodedData.initTripData());
        recyclerViewTrip.setAdapter(tripRmgDataAdapter);
    }

    private boolean getALlLepNumberBothra() {
        Call<TransactionsApiResponse> call = RetrofitController.getInstance().getLoadingAdviseApi().getALlLepNumberBothra("Bearer " + token);
        call.enqueue(new Callback<TransactionsApiResponse>() {
            @Override
            public void onResponse(Call<TransactionsApiResponse> call, Response<TransactionsApiResponse> response) {

                if (!response.isSuccessful()) {
                    alertBuilder(response.errorBody().toString());
                    return;
                }
                Log.i(TAG, "onResponse: getALlLepNumberBothra : responseCode : " + response.code());
                if (response.isSuccessful()) {
                    List<TransactionsDto> transactionsDtoList = response.body().getTransactionsDtos();
                    HashMap<String, Integer> hashMapLepNumber = new HashMap<>();
                    ArrayList<String> arrAutoCompleteLepNo = new ArrayList<>();
                    try {
                        if (transactionsDtoList == null || transactionsDtoList.isEmpty()) {
                            autoCompleteLepNo.setHint("No Lep number available");
                            customToast.toastMessage(getActivity(), EMPTY_LEP_NUMBER_LIST, 0);
                            return;
                        }

                        String strTruckNo = null, strDriverName = null, grossWeight = null, strCommodity = null;
                        String strPreviousWareHouseNo = null;
                        for (int i = 0; i < transactionsDtoList.size(); i++) {
                            String strLepNumber = transactionsDtoList.get(i).getRfidLepIssueModel().getLepNumber();
                            int id = transactionsDtoList.get(i).getRfidLepIssueModel().getId();
                            hashMapLepNumber.put(strLepNumber, id);
                            strDriverName = transactionsDtoList.get(i).getRfidLepIssueModel().getDriverMaster().getDriverName();
                            strTruckNo = transactionsDtoList.get(i).getRfidLepIssueModel().getDailyTransportReportModule().getTruckNumber();
                            strCommodity = transactionsDtoList.get(i).getRfidLepIssueModel().getDailyTransportReportModule().getCommodity();

                            if (transactionsDtoList.get(i).getBothraNetWeight() == null) {
                                Log.i(TAG, "onResponse: if transactionsDtoList.get(i).getBothraNetWeight() : " + transactionsDtoList.get(i).getBothraNetWeight());
                                grossWeight = String.valueOf(transactionsDtoList.get(i).getBothraGrossWeight());
                            } else {
                                grossWeight = String.valueOf(transactionsDtoList.get(i).getGrossWeight());
                                Log.i(TAG, "onResponse:else transactionsDtoList.get(i).getGrossWeight() : " + transactionsDtoList.get(i).getGrossWeight());
                            }
                            strPreviousWareHouseNo = transactionsDtoList.get(i).getFunctionalLocationDestinationMaster().getStrLocationCode();
                            arrAutoCompleteLepNo.add(strLepNumber);
                        }

                        arrayAdapterForLepNumber = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, arrAutoCompleteLepNo);
                        autoCompleteLepNo.setAdapter(arrayAdapterForLepNumber);
                        String finalStrTruckNo = strTruckNo;
                        String finalStrDriverName = strDriverName;
                        String finalStrCommodity = strCommodity;
                        String finalGrossWeight = grossWeight;
                        String finalStrPreviousWareHouse = strPreviousWareHouseNo;
                        autoCompleteLepNo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                selectedLepNumber = arrayAdapterForLepNumber.getItem(i);
                                if (hashMapLepNumber.containsKey(selectedLepNumber)) {
                                    selectedLepNumberId = hashMapLepNumber.get(selectedLepNumber);
                                }
                                previousWarehouseCode = finalStrPreviousWareHouse;

                                if (arrAutoCompleteLepNo.contains(selectedLepNumber)) {
                                    edtTruckNumber.setText(finalStrTruckNo);
                                    edtDriverName.setText(finalStrDriverName);
                                    edtCommodity.setText(finalStrCommodity);
                                    edtGrossWeight.setText(finalGrossWeight);
                                    edtPreviousWareHouseNo.setText(finalStrPreviousWareHouse);
                                }
                            }
                        });
                    } catch (Exception e) {
                        e.getMessage();
                        return;
                    }
                }
            }

            @Override
            public void onFailure(Call<TransactionsApiResponse> call, Throwable t) {
                alertBuilder(t.getMessage());
            }
        });
        return true;
    }

    private boolean getAllWareHouse() {
        Log.i(TAG, "getAllWareHouse: ()");
        Call<RmgNumberApiResponse> call = RetrofitController.getInstance().getLoadingAdviseApi().
                getAllWareHouse("Bearer " + token, loginUserPlantCode);

        call.enqueue(new Callback<RmgNumberApiResponse>() {
            @Override
            public void onResponse(Call<RmgNumberApiResponse> call, Response<RmgNumberApiResponse> response) {
                if (!response.isSuccessful()) {
                    String responseCode = String.valueOf(response.code());
                    Log.i(TAG, "getAllWareHouse: responseCode : " + responseCode);
                    return;
                }
                Log.i(TAG, "onResponse: getAllWareHouse : responseCode : " + response.code());
                if (response.code() != 200) {
                    getAllWareHouse();
                    return;
                }

                if (response.code() == 200) {
                    Log.i(TAG, "onResponse:WareHouse found successfully");
                }
                Log.i(TAG, "onResponse: getAllWareHouse " + response.code());
                List<StorageLocationDto> functionalLocationMasterDtoList = response.body().getStorageLocationDtos();
                HashMap<String, Integer> hashMapUpdateRmgNo = new HashMap<>();
                ArrayList<String> arrDestinationLocation = new ArrayList<>();

                try {
                    if (functionalLocationMasterDtoList == null || functionalLocationMasterDtoList.isEmpty()) {
                        customToast.toastMessage(getActivity(), EMPTY_WAREHOUSE_NUMBER, 0);
                        return;
                    }
                    for (int i = 0; i < functionalLocationMasterDtoList.size(); i++) {
                        String s = functionalLocationMasterDtoList.get(i).getStrLocationCode();
//                        int id = functionalLocationMasterDtoList.get(i).getId();
//                        hashMapUpdateRmgNo.put(s, id);
                        arrDestinationLocation.add(s);
                    }
                    arrDestinationLocation.add("Select Warehouse No");

                    updateWareHouseNoAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, arrDestinationLocation) {
                        @Override
                        public View getView(int position, View convertView, ViewGroup parent) {

                            View v = super.getView(position, convertView, parent);
                            if (position == getCount()) {
                                ((TextView) v.findViewById(android.R.id.text1)).setText("");
                                ((TextView) v.findViewById(android.R.id.text1)).setHint(getItem(getCount())); //"Hint to be displayed"
                            }
                            return v;
                        }

                        @Override
                        public int getCount() {
                            return super.getCount() - 1;
                        }
                    };
                    spinnerWarehouseNo.setAdapter(updateWareHouseNoAdapter);
                    spinnerWarehouseNo.setSelection(updateWareHouseNoAdapter.getCount());

                    spinnerWarehouseNo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            selectedWareHouseNumber = adapterView.getSelectedItem().toString();
                            /*if (hashMapUpdateRmgNo.containsKey(selectedWareHouseNumber)) {
                                selectedWareHouseNumberId = hashMapUpdateRmgNo.get(selectedWareHouseNumber);
                                Log.i(TAG, "onItemSelected: selectedBothraSupervisorId " + selectedWareHouseNumberId);
                            }*/
                            if (!selectedWareHouseNumber.equalsIgnoreCase("Select Warehouse No")) {
                                spinnerRemark.setEnabled(true);
                                spinnerRemark.setClickable(true);
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {
                        }
                    });
                } catch (Exception e) {
                    e.getMessage();
                }
            }

            @Override
            public void onFailure(Call<RmgNumberApiResponse> call, Throwable t) {
                alertBuilder(t.getMessage());
            }
        });
        return true;
    }

    private boolean getAllBothraRemark() {
        Call<RemarkApiResponse> call = RetrofitController.getInstance().getLoadingAdviseApi().
                getAllBothraRemark("Bearer " + token);
        call.enqueue(new Callback<RemarkApiResponse>() {
            @Override
            public void onResponse(Call<RemarkApiResponse> call, Response<RemarkApiResponse> response) {
                if (!response.isSuccessful()) {
                    alertBuilder(response.errorBody().toString());
                    return;
                }
                Log.i(TAG, "onResponse: getAllBothraRemark : responseCode : " + response.code());

                if (response.isSuccessful()) {
                    List<RemarksDto> remarksDtoList = response.body().getRemarksDtos();
                    HashMap<String, Integer> hashMapRemarks = new HashMap<>();
                    ArrayList<String> arrRemarks = new ArrayList<>();

                    try {
                        if (remarksDtoList == null || remarksDtoList.isEmpty()) {
                            customToast.toastMessage(getActivity(), EMPTY_REMARKS, 0);
                            return;
                        }
                        for (int i = 0; i < remarksDtoList.size(); i++) {
                            String s = remarksDtoList.get(i).getRemarks();
                            int id = remarksDtoList.get(i).getId();
                            hashMapRemarks.put(s, id);
                            arrRemarks.add(s);
                        }
                        arrRemarks.add("Select Remarks");

                        remarkAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, arrRemarks) {
                            @Override
                            public View getView(int position, View convertView, ViewGroup parent) {

                                View v = super.getView(position, convertView, parent);
                                if (position == getCount()) {
                                    ((TextView) v.findViewById(android.R.id.text1)).setText("");
                                    ((TextView) v.findViewById(android.R.id.text1)).setHint(getItem(getCount())); //"Hint to be displayed"
                                }
                                return v;
                            }

                            @Override
                            public int getCount() {
                                return super.getCount() - 1;
                            }
                        };
                        spinnerRemark.setEnabled(false);
                        spinnerRemark.setClickable(false);
                        spinnerRemark.setAdapter(remarkAdapter);
                        spinnerRemark.setSelection(remarkAdapter.getCount());
                        spinnerRemark.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                selectedRemarks = adapterView.getSelectedItem().toString();
                                if (hashMapRemarks.containsKey(selectedRemarks)) {
                                    selectedRemarksId = hashMapRemarks.get(selectedRemarks);
                                    Log.i(TAG, "onItemSelected: Selected Remarks Id " + selectedRemarksId);
                                }
                                if (selectedRemarks.equalsIgnoreCase("Update RMG No")) {
                                    spinnerRemark.setEnabled(false);
                                    spinnerRemark.setClickable(false);
                                    spinnerRemark.setFocusable(false);
                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {
                            }
                        });
                    } catch (Exception e) {
                        e.getMessage();
                    }
                }
            }

            @Override
            public void onFailure(Call<RemarkApiResponse> call, Throwable t) {
                alertBuilder(t.getMessage());
            }
        });
        return true;
    }

    private UpdateWareHouseNoRequestDto setData() {
        StorageLocationDto selectedWareHouseNo = null;
        RemarksDto remarksDto = null;
        Integer FLAG = 8;
        AuditEntity auditEntity = new AuditEntity(null, null, loginUserName, null);
        StorageLocationDto previousWareHouseNo = new StorageLocationDto(previousWarehouseCode);
        if (!selectedWareHouseNumber.equals("Select Warehouse No")) {
            selectedWareHouseNo = new StorageLocationDto(selectedWareHouseNumber);
        }
        if (!selectedRemarks.equalsIgnoreCase("Select Remarks")) {
            remarksDto = new RemarksDto(selectedRemarksId);
        }
        RfidLepIssueDto rfidLepIssueDto = new RfidLepIssueDto(selectedLepNumberId);
        UpdateWareHouseNoRequestDto updateWareHouseNoRequestDto = new UpdateWareHouseNoRequestDto(auditEntity, previousWareHouseNo, selectedWareHouseNo, rfidLepIssueDto, remarksDto, FLAG);
        return updateWareHouseNoRequestDto;
    }

    private void updateWareHouseNo(UpdateWareHouseNoRequestDto updateWareHouseNoRequestDto) {
        Log.i(TAG, new Gson().toJson(updateWareHouseNoRequestDto).toString());
        Call<TransactionsApiResponse> call = RetrofitController.getInstance().getLoadingAdviseApi().updateWareHouse("Bearer " + token, updateWareHouseNoRequestDto);
        call.enqueue(new Callback<TransactionsApiResponse>() {
            @Override
            public void onResponse(Call<TransactionsApiResponse> call, Response<TransactionsApiResponse> response) {
                if (!response.isSuccessful()) {
                    alertBuilder(response.errorBody().toString());
                }
                Log.i(TAG, "onResponse: code" + response.code());
                if (response.isSuccessful()) {
                    alertBuilder(response.body().getMessage());
                    progressBar.setVisibility(View.GONE);
                    resetFields();
                }
            }

            @Override
            public void onFailure(Call<TransactionsApiResponse> call, Throwable t) {
                alertBuilder(t.getMessage());
            }
        });
    }

    private void setTvClock() {
        try {
            tvClock.setFormat24Hour("dd-MM-yy hh:mm a");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void alertBuilder(String alertMessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(alertMessage)
                .setCancelable(false)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
}