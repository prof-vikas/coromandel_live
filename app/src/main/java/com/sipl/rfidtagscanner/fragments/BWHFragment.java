package com.sipl.rfidtagscanner.fragments;

import static android.content.Context.MODE_PRIVATE;
import static com.sipl.rfidtagscanner.utils.ToastConstants.EMPTY_LEP_NUMBER_LIST;
import static com.sipl.rfidtagscanner.utils.ToastConstants.EMPTY_REMARKS;
import static com.sipl.rfidtagscanner.utils.ToastConstants.EMPTY_WAREHOUSE_NUMBER;
import static com.sipl.rfidtagscanner.utils.ToastConstants.FAILED_CONNECTION;
import static com.sipl.rfidtagscanner.utils.ToastConstants.RESPONSE_NOT_200;
import static com.sipl.rfidtagscanner.utils.ToastConstants.isRMGTableRequired;

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
import com.google.gson.Gson;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class BWHFragment extends Fragment {

    private final String TAG = "TracingError";

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
    private Integer selectedWareHouseNumberId;
    private String selectedRemarks;
    private Integer selectedRemarksId;
    private ArrayAdapter<String> remarkAdapter;
    private ArrayAdapter<String> updateWareHouseNoAdapter;
    private ArrayAdapter<String> arrayAdapterForLepNumber;

    private int updateWHFailCounters = 0;
    private int updateWareHouseFailCounter = 3;
    private int getAllLepNumberCounterFail = 6;
    private int getAllWareHouseCounterFail = 6;
    private int getAllRemarksCounterFail = 6;
    private String previousWarehouseId;

    private final Helper helper = new Helper();
    private final CustomToast customToast = new CustomToast();

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

        setTvClock();
        callOnCreateApi();

        btnSubmit.setOnClickListener(view12 -> {
            if (validateLoadingAdviseForm()) {
                Log.i(TAG, "onClick: submit");
                updateWareHouseNo(setData());
//                callOnCreateApi();
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

        if (!getALlLepNumberBothra()) {
            getALlLepNumberBothra();
        }

        removeErrorMessage();
    }

    private void removeErrorMessage() {
        autoCompleteLepNo.setError(null);
        edtTruckNumber.setError(null);
        edtTruckNumber.setHint(null);
        edtDriverName.setError(null);
        edtDriverName.setHint(null);
        edtCommodity.setError(null);
        edtCommodity.setHint(null);
        edtGrossWeight.setError(null);
        edtGrossWeight.setHint(null);
        edtPreviousWareHouseNo.setError(null);
        edtPreviousWareHouseNo.setHint(null);
    }

    private boolean callOnCreateApi() {
        if (!getALlLepNumberBothra()) {
            getALlLepNumberBothra();
            return false;
        }

        if (!getAllWareHouse()) {
            getAllWareHouse();
            return false;
        }

        if (!getAllBothraRemark()) {
            getAllBothraRemark();
            return false;
        }
        return true;
    }

    private boolean validateLoadingAdviseForm() {
        if (autoCompleteLepNo.length() == 0) {
            autoCompleteLepNo.setError("This field is required");
            return false;
        }

        if (edtTruckNumber.length() == 0) {
            edtTruckNumber.setError("This field is required");
            edtTruckNumber.setHint("This field is required");
            return false;
        }

        if (edtDriverName.length() == 0) {
            edtDriverName.setError("This field is required");
            edtDriverName.setHint("This field is required");
            return false;
        }

        if (edtCommodity.length() == 0) {
            edtCommodity.setError("This field is required");
            edtCommodity.setHint("This field is required");
            return false;
        }
        if (edtGrossWeight.length() == 0) {
            edtGrossWeight.setError("This field is required");
            edtGrossWeight.setHint("This field is required");
            return false;
        }

        if (edtPreviousWareHouseNo.length() == 0) {
            edtPreviousWareHouseNo.setError("This field is required");
            edtPreviousWareHouseNo.setHint("This field is required");
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

    private String getLoginSupervisor() {
        SharedPreferences sp = getActivity().getSharedPreferences("credentials", MODE_PRIVATE);
        return sp.getString("username", null);
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
        Log.i(TAG, "getAllLepNumber: ");
        try {
            Call<TransactionsApiResponse> call = RetrofitController.getInstance().getLoadingAdviseApi().getALlLepNumberBothra("Bearer " + getToken());

            call.enqueue(new Callback<TransactionsApiResponse>() {
                @Override
                public void onResponse(Call<TransactionsApiResponse> call, Response<TransactionsApiResponse> response) {

                    if (!response.isSuccessful()) {
                        String responseCode = String.valueOf(response.code());
                        Log.i(TAG, "getALlLepNumberBothra: responseCode : " + responseCode);
                        return;
                    }
                    Log.i(TAG, "onResponse: getALlLepNumberBothra : responseCode : " + response.code());
                    if (response.code() != 200) {
                        getALlLepNumberBothra();
                        return;
                    }

                    if (response.code() == 200) {
                        Log.i(TAG, "onResponse: Lep no found successfully");
                    }
                    List<TransactionsDto> transactionsDtoList = response.body().getTransactionsDtos();
                    HashMap<String, Integer> hashMapLepNumber = new HashMap<>();
                    HashMap<String, String> hashMapModifiedTime = new HashMap<>();
                    HashMap<String, String> hashMapModifiedBy = new HashMap<>();
                    HashMap<String, Integer> hashMapRmgNo = new HashMap<>();
                    ArrayList<String> arrAutoCompleteLepNo = new ArrayList<>();
                    try {
                        if (transactionsDtoList == null || transactionsDtoList.isEmpty()) {
                            autoCompleteLepNo.setHint("No Lep number available");
                            customToast.toastMessage(getActivity(), EMPTY_LEP_NUMBER_LIST, 0);
                            return;
                        }

                        Log.i(TAG, "onResponse: transactionsDtoList.size " + transactionsDtoList.size());
                        String strLepNumber, strTruckNo = null, strDriverName = null, grossWeight = null, strCommodity = null;
                        String strPreviousWareHouseNo = null;
                        for (int i = 0; i < transactionsDtoList.size(); i++) {
                            int transId = transactionsDtoList.get(i).getId();
                            Log.i(TAG, "onResponse: transId : <<>>  " + transId);
                            strLepNumber = transactionsDtoList.get(i).getRfidLepIssueModel().getLepNumber();
                            Log.i(TAG, "onResponse: strLepNumber " + strLepNumber);
                            int id = transactionsDtoList.get(i).getRfidLepIssueModel().getId();
                            String modifiedBy = transactionsDtoList.get(i).getAuditEntity().getModifiedBy();
                            String modifiedTime = transactionsDtoList.get(i).getAuditEntity().getModifiedTime();
                            hashMapLepNumber.put(strLepNumber, id);
                            hashMapModifiedBy.put(strLepNumber, modifiedBy);
                            hashMapModifiedTime.put(strLepNumber, modifiedTime);

                            strDriverName = transactionsDtoList.get(i).getRfidLepIssueModel().getDriverMaster().getDriverName();
                            strTruckNo = transactionsDtoList.get(i).getRfidLepIssueModel().getDailyTransportReportModule().getTruckNumber();
                            strCommodity = transactionsDtoList.get(i).getRfidLepIssueModel().getDailyTransportReportModule().getCommodity();
                            grossWeight = String.valueOf(transactionsDtoList.get(i).getGrossWeight());

                            strPreviousWareHouseNo = transactionsDtoList.get(i).getFunctionalLocationDestinationMaster().getStrLocationCode();
//                            int intPreviousWareHouseNoId = transactionsDtoList.get(i).getWarehouse().getStrLocationCode();
//                            hashMapRmgNo.put(strPreviousWareHouseNo, intPreviousWareHouseNoId);
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
                               /* if (hashMapLepNumber.containsKey(selectedLepNumber)) {
                                    selectedLepNumberId = hashMapLepNumber.get(selectedLepNumber);
                                }*/
                                previousWarehouseId = finalStrPreviousWareHouse;

                              /*  if (hashMapRmgNo.containsKey(finalStrPreviousWareHouse)) {
                                    Log.i(TAG, "onItemClick: finalStrPreviousRmgNo1 : " + finalStrPreviousWareHouse);
                                    previousWarehouseId = hashMapRmgNo.get(finalStrPreviousWareHouse);
                                }*/

                                if (arrAutoCompleteLepNo.contains(selectedLepNumber)) {
                                    edtTruckNumber.setText(finalStrTruckNo);
                                    edtDriverName.setText(finalStrDriverName);
                                    edtCommodity.setText(finalStrCommodity);
                                    edtGrossWeight.setText(finalGrossWeight);
                                    edtPreviousWareHouseNo.setText(finalStrPreviousWareHouse);
                                    Log.i(TAG, "onItemClick: " + finalStrPreviousWareHouse);
                                }
                            }
                        });
                    } catch (Exception e) {
                        e.getMessage();
                        return;
                    }
                }

                @Override
                public void onFailure(Call<TransactionsApiResponse> call, Throwable t) {
                    getAllLepNumberCounterFail--;
                    if (getAllLepNumberCounterFail == 0) {
                        t.getMessage();
                        return;
                    }
                    if (getAllLepNumberCounterFail != 0) {
                        customToast.toastMessage(getActivity(), FAILED_CONNECTION + t.getMessage(), 0);
                        getALlLepNumberBothra();
                    }
                }
            });
        } catch (Exception e) {
            getALlLepNumberBothra();
            Log.i(TAG, "getALlLepNumberWithFlag: " + e.getMessage());

            e.printStackTrace();
        }
        return true;
    }

    private boolean getAllWareHouse() {
        Log.i(TAG, "getAllWareHouse: ()");
        Call<RmgNumberApiResponse> call = RetrofitController.getInstance().getLoadingAdviseApi().
                getAllWareHouse("Bearer " + getToken(), getUserPlantLocation());

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
                            if (hashMapUpdateRmgNo.containsKey(selectedWareHouseNumber)) {
                                selectedWareHouseNumberId = hashMapUpdateRmgNo.get(selectedWareHouseNumber);
                                Log.i(TAG, "onItemSelected: selectedBothraSupervisorId " + selectedWareHouseNumberId);
                            }
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
                getAllWareHouseCounterFail--;
                if (getAllWareHouseCounterFail == 0) {
                    t.getMessage();
                    return;
                }
                if (getAllWareHouseCounterFail != 0) {
                    customToast.toastMessage(getActivity(), FAILED_CONNECTION + t.getMessage(), 0);
                    getAllWareHouse();
                }
            }
        });
        return true;
    }

    private boolean getAllBothraRemark() {
        Call<RemarkApiResponse> call = RetrofitController.getInstance().getLoadingAdviseApi().
                getAllBothraRemark("Bearer " + getToken());

        call.enqueue(new Callback<RemarkApiResponse>() {
            @Override
            public void onResponse(Call<RemarkApiResponse> call, Response<RemarkApiResponse> response) {
                if (!response.isSuccessful()) {
                    String responseCode = String.valueOf(response.code());
                    Log.i(TAG, "getAllBothraRemark: responseCode : " + responseCode);
                    return;
                }
                Log.i(TAG, "onResponse: getAllBothraRemark : responseCode : " + response.code());
                if (response.code() != 200) {
                    getAllBothraRemark();
                    return;
                }

                if (response.code() == 200) {
                    Log.i(TAG, "onResponse : Remarks found successfully");
                }
                Log.i(TAG, "onResponse: getAllBothraRemark " + response.code());
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

            @Override
            public void onFailure(Call<RemarkApiResponse> call, Throwable t) {
                getAllRemarksCounterFail--;
                if (getAllRemarksCounterFail == 0) {
                    t.getMessage();
                    return;
                }
                if (getAllRemarksCounterFail != 0) {
                    customToast.toastMessage(getActivity(), FAILED_CONNECTION + t.getMessage(), 0);
                    getAllBothraRemark();
                }
            }
        });
        return true;
    }

    private UpdateWareHouseNoRequestDto setData() {
        Integer FLAG = 8;
        AuditEntity auditEntity = new AuditEntity(null, null, getLoginSupervisor(), LocalDateTime.now().toString());
//        FunctionalLocationMasterDto previousWareHouseNo = new FunctionalLocationMasterDto(previousWarehouseId);
//        FunctionalLocationMasterDto selectedWareHouseNo = new FunctionalLocationMasterDto(selectedWareHouseNumberId);

        StorageLocationDto previousWareHouseNo = new StorageLocationDto(previousWarehouseId);
        StorageLocationDto selectedWareHouseNo = new StorageLocationDto(selectedWareHouseNumber);

        RfidLepIssueDto rfidLepIssueDto = new RfidLepIssueDto(selectedLepNumberId);
        UpdateWareHouseNoRequestDto updateWareHouseNoRequestDto = new UpdateWareHouseNoRequestDto(auditEntity, previousWareHouseNo, selectedWareHouseNo, rfidLepIssueDto, selectedRemarksId, FLAG, LocalDateTime.now().toString());

        auditEntity.setModifiedBy(getLoginSupervisor());
        auditEntity.setModifiedTime(LocalDateTime.now().toString());
    /*    previousWareHouseNo.setId(previousWarehouseId);
        selectedWareHouseNo.setId(selectedWareHouseNumberId);*/
        rfidLepIssueDto.setId(selectedLepNumberId);
        updateWareHouseNoRequestDto.setWhSupervisorRemark(selectedRemarksId);
        updateWareHouseNoRequestDto.setTransactionFlag(FLAG);
        updateWareHouseNoRequestDto.setUnloadingTime(LocalDateTime.now().toString());

        return updateWareHouseNoRequestDto;
    }

    private void updateWareHouseNo(UpdateWareHouseNoRequestDto updateWareHouseNoRequestDto) {

        Log.i(TAG, new Gson().toJson(updateWareHouseNoRequestDto).toString());
        Call<TransactionsApiResponse> call = RetrofitController.getInstance().getLoadingAdviseApi().updateWareHouse("Bearer " + getToken(), updateWareHouseNoRequestDto);

        call.enqueue(new Callback<TransactionsApiResponse>() {
            @Override
            public void onResponse(Call<TransactionsApiResponse> call, Response<TransactionsApiResponse> response) {
                if (!response.isSuccessful()) {
                    Log.i(TAG, "onResponse: " + response.code());
                    customToast.toastMessage(getActivity(), RESPONSE_NOT_200 + response.code(), 0);
                }

                Log.i(TAG, "onResponse: code" + response.code());
                if (response.code() == 200) {
//                    customToast.toastMessage(getActivity(), RESPONSE_200, 0);
//                    arrayAdapterForLepNumber.remove(selectedLepNumber);
                    alertBuilder(response.body().getMessage());
                    progressBar.setVisibility(View.GONE);
                    resetFields();
                }

                if (response.code() != 200) {
                    if (updateWareHouseFailCounter != 0) {
                        updateWareHouseFailCounter--;
                        updateWareHouseNo(setData());
                        return;
                    }
                    customToast.errorToastMessage(getActivity(), RESPONSE_NOT_200, 0);
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<TransactionsApiResponse> call, Throwable t) {
                updateWHFailCounters++;
                updateWareHouseNo(setData());
                customToast.toastMessage(getActivity(), FAILED_CONNECTION + updateWHFailCounters, 0);
                if (updateWHFailCounters == 4) {
                    customToast.toastMessage(getActivity(), "Connection failed", 0);
                    t.printStackTrace();
                    return;
                }
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

    private String getUserSourceLocation() {
        SharedPreferences sp = getActivity().getSharedPreferences("loginCredentials", MODE_PRIVATE);
        String userSourceLocation = sp.getString("UserSourceLocationSPK", null);
        return userSourceLocation;
    }

    private String getUserPlantLocation() {
        SharedPreferences sp = getActivity().getSharedPreferences("loginCredentials", MODE_PRIVATE);
        String userPlantLocation = sp.getString("userPlantLocationSPK", null);
        return userPlantLocation;
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