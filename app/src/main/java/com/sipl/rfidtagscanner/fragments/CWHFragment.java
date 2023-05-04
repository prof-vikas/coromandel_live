package com.sipl.rfidtagscanner.fragments;

import static android.content.Context.MODE_PRIVATE;
import static com.sipl.rfidtagscanner.utils.Config.EMPTY_REMARKS;
import static com.sipl.rfidtagscanner.utils.Config.EMPTY_RMG_NUMBER;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.sipl.rfidtagscanner.MainActivity;
import com.sipl.rfidtagscanner.R;
import com.sipl.rfidtagscanner.RetrofitController;
import com.sipl.rfidtagscanner.dto.dtos.RemarksDto;
import com.sipl.rfidtagscanner.dto.dtos.RfidLepIssueDto;
import com.sipl.rfidtagscanner.dto.dtos.StorageLocationDto;
import com.sipl.rfidtagscanner.dto.request.UpdateRmgRequestDto;
import com.sipl.rfidtagscanner.dto.response.RemarkApiResponse;
import com.sipl.rfidtagscanner.dto.response.RmgNumberApiResponse;
import com.sipl.rfidtagscanner.dto.response.TransactionsApiResponse;
import com.sipl.rfidtagscanner.entites.AuditEntity;
import com.sipl.rfidtagscanner.utils.CustomToast;
import com.sipl.rfidtagscanner.utils.Helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class CWHFragment extends Fragment {

    private final String TAG = "BothraAdvicePage";

    private ArrayAdapter<String> updateRmgNoAdapter;
    private ArrayAdapter<String> remarksAdapter;

    private EditText edtRfidTag, edtLepNo, edtDriverName, edtTruckNumber, edtCommodity, edtGrossWeight, edtPreviousRmgNo;
    private CustomToast customToast = new CustomToast();
    private ProgressBar progressBar;
    private Spinner spinnerUpdateRmgNo, spinnerRemark;
    private TextClock tvClock;
    private Button btnSubmit, btnReset;

    //    userDetails
    private String loginUserName;
    private String token;
    private String loginUserPlantCode;

    private String selectedRemarks;
    private Integer selectedRemarksId;

    private String selectedRmgNo;

    private Integer selectedLepNumberId;

    private String previousRmgNoId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_c_w_h, container, false);
        spinnerRemark = view.findViewById(R.id.cwh_spinner_remark);
        spinnerUpdateRmgNo = view.findViewById(R.id.cwh_spinner_update_rmg_no);

        edtRfidTag = view.findViewById(R.id.cwh_edt_rfid_tag);
        edtLepNo = view.findViewById(R.id.cwh_edt_lep_number);
        edtDriverName = view.findViewById(R.id.cwh_edt_driver_name);
        edtTruckNumber = view.findViewById(R.id.cwh_edt_truck_no);
        edtCommodity = view.findViewById(R.id.cwh_edt_commodity);
        edtGrossWeight = view.findViewById(R.id.cwh_edt_gross_weight);
        edtPreviousRmgNo = view.findViewById(R.id.cwh_edt_previous_rmg_no);
        btnReset = view.findViewById(R.id.cwh_btn_reset);
        btnSubmit = view.findViewById(R.id.cwh_btn_submit);
        progressBar = view.findViewById(R.id.cwh_progressBar);
        tvClock = view.findViewById(R.id.cwh_tv_clock);

        this.token = ((MainActivity) getActivity()).getLoginToken();
        this.loginUserName = ((MainActivity) getActivity()).getLoginUsername();
        this.loginUserPlantCode = ((MainActivity) getActivity()).getLoginUserPlantCode();

        currentTime();
        getLoadingAdviseDetails();
        callOnCreateApi();

        btnSubmit.setOnClickListener(view12 -> {
            if (validateLoadingAdviseForm()) {
                updateRmgNo(setData());
            }
        });
        btnReset.setOnClickListener(view1 -> resetFields());

        return view;
    }

    private boolean validateLoadingAdviseForm() {
        if (edtRfidTag.length() == 0) {
            edtRfidTag.setError("This field is required");
            return false;
        }
        if (edtLepNo.length() == 0) {
            edtLepNo.setError("This field is required");
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
        if (edtPreviousRmgNo.length() == 0) {
            edtPreviousRmgNo.setError("This field is required");
            return false;
        }
        if (!spinnerUpdateRmgNo.getSelectedItem().toString().equals("Update RMG No") && spinnerRemark.getSelectedItem().toString().equals("Select Remarks")) {
            Toast.makeText(getActivity(), "Select remarks", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void resetFields() {
        ((MainActivity) requireActivity()).loadFragment(new ScanFragment(), 1);
    }


/*    private boolean getAllLepNo() {
        progressBar.setVisibility(View.VISIBLE);
        try {
            Call<TransactionsApiResponse> call = RetrofitController.getInstance().getLoadingAdviseApi().getCoromandelWHDetails("Bearer " + token, "4", "3");
            call.enqueue(new Callback<TransactionsApiResponse>() {
                @Override
                public void onResponse(Call<TransactionsApiResponse> call, Response<TransactionsApiResponse> response) {

                    if (!response.isSuccessful()) {
//                        alertBuilder(response.errorBody().toString());
                        progressBar.setVisibility(View.GONE);
                        ((MainActivity) getActivity()).alert(getActivity(), "error", response.errorBody().toString(), null, "OK");
                        return;
                    }
                    if (response.isSuccessful()) {
                        progressBar.setVisibility(View.GONE);
                        Log.i(TAG, "getAllLepNo : response.isSuccessful() : " + response.isSuccessful() + " responseCode : " + response.code() + " responseRaw : " + response.raw());
                        List<TransactionsDto> transactionsDtoList = response.body().getTransactionsDtos();
                        HashMap<String, Integer> hashMapLepNumber = new HashMap<>();
                        HashMap<String, String> hashMapForPreviousRmgNo = new HashMap<>();
                        arrAutoCompleteLepNo = new ArrayList<>();


                        String strTruckNo = null, srtPreviousRmgNoDesc = null, strDriverName = null, grossWeight = null, strCommodity = null, strPreviousRmgNo = null;

                        try {
                            if (transactionsDtoList == null || transactionsDtoList.isEmpty()) {
                                autoCompleteLepNo.setHint("No Lep number available");
                                Toast.makeText(getActivity(), EMPTY_LEP_NUMBER_LIST, Toast.LENGTH_SHORT).show();
                                return;
                            } else {
                                autoCompleteLepNo.setHint("Search Lep Number");
                            }

                            for (int i = 0; i < transactionsDtoList.size(); i++) {
                                String strLepNumber = transactionsDtoList.get(i).getRfidLepIssueModel().getLepNumber();
                                int id = transactionsDtoList.get(i).getRfidLepIssueModel().getId();
                                strDriverName = transactionsDtoList.get(i).getRfidLepIssueModel().getDriverMaster().getDriverName();
                                strTruckNo = transactionsDtoList.get(i).getRfidLepIssueModel().getDailyTransportReportModule().getTruckNumber();
                                strCommodity = transactionsDtoList.get(i).getRfidLepIssueModel().getDailyTransportReportModule().getCommodity();
                                grossWeight = String.valueOf(transactionsDtoList.get(i).getGrossWeight());
                                strPreviousRmgNo = transactionsDtoList.get(i).getFunctionalLocationDestinationMaster().getStrLocationCode();
                                srtPreviousRmgNoDesc = transactionsDtoList.get(i).getFunctionalLocationDestinationMaster().getStrLocationDesc();

                                coromandelWHDto = new CoromandelWHDto(strLepNumber, strTruckNo, strDriverName, strCommodity, grossWeight, strPreviousRmgNo, srtPreviousRmgNoDesc);
                                coromandelWHDtoList.add(coromandelWHDto);
                                arrAutoCompleteLepNo.add(strLepNumber);
                                hashMapLepNumber.put(strLepNumber, id);
                            }

                            arrayAdapterForLepNumber = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, arrAutoCompleteLepNo);
                            autoCompleteLepNo.setAdapter(arrayAdapterForLepNumber);
                            autoCompleteLepNo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                    selectedLepNumber = arrayAdapterForLepNumber.getItem(i);
//                                    previousRmgNoId = finalStrPreviousRmgNo;
                                    if (hashMapLepNumber.containsKey(selectedLepNumber)) {
                                        selectedLepNumberId = hashMapLepNumber.get(selectedLepNumber);
                                    }

                                    if (arrAutoCompleteLepNo.contains(selectedLepNumber)) {
                                        for (CoromandelWHDto d : coromandelWHDtoList) {
                                            if (d.getLepNo().equalsIgnoreCase(selectedLepNumber)) {
                                                edtTruckNumber.setText(d.getTruckNo());
                                                edtDriverName.setText(d.getDriverName());
                                                edtCommodity.setText(d.getCommodity());
                                                edtGrossWeight.setText(d.getGrossWeight());
                                                edtPreviousRmgNo.setText(d.getPreviousRMGNo() + " - " + d.getPreviousRMGNoDesc().toLowerCase());
                                                previousRmgNoId = d.getPreviousRMGNo();
                                            }
                                        }
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
                    progressBar.setVisibility(View.GONE);
//                    alertBuilder(t.getMessage());
                    ((MainActivity) getActivity()).alert(getActivity(), "error", t.getMessage(), null, "OK");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }*/

    private boolean getAllRmgStorage() {
        progressBar.setVisibility(View.VISIBLE);
        Call<RmgNumberApiResponse> call = RetrofitController.getInstance().getLoadingAdviseApi().
                getAllCoromandelRmgNo("Bearer " + token, "coromandel");

        call.enqueue(new Callback<RmgNumberApiResponse>() {
            @Override
            public void onResponse(Call<RmgNumberApiResponse> call, Response<RmgNumberApiResponse> response) {

                if (!response.isSuccessful()) {
                    progressBar.setVisibility(View.GONE);
                    ((MainActivity) getActivity()).alert(getActivity(), "error", response.errorBody().toString(), null, "OK");
                    return;
                }
                Log.i(TAG, "onResponse: getAllUpdateRmgNo : responseCode : " + response.code() + " " + response.raw());

                if (response.isSuccessful()) {
                    progressBar.setVisibility(View.GONE);
                    HashMap<String, String> hashMapLocationCode = new HashMap<>();
                    List<StorageLocationDto> functionalLocationMasterDtoList = response.body().getStorageLocationDtos();
                    ArrayList<String> arrDestinationLocation = new ArrayList<>();
                    ArrayList<String> arrDestinationLocationDesc = new ArrayList<>();
                    try {
                        if (functionalLocationMasterDtoList == null || functionalLocationMasterDtoList.isEmpty()) {
                            customToast.toastMessage(getActivity(), EMPTY_RMG_NUMBER, 0);
                            return;
                        }
                        for (int i = 0; i < functionalLocationMasterDtoList.size(); i++) {
                            String s = functionalLocationMasterDtoList.get(i).getStrLocationCode();
                            String strLocationDesc = functionalLocationMasterDtoList.get(i).getStrLocationDesc();
                            arrDestinationLocation.add(s);
                            String strLocationDescWithCode = s + " - " + strLocationDesc.toLowerCase();
                            arrDestinationLocationDesc.add(strLocationDescWithCode);
                            hashMapLocationCode.put(strLocationDescWithCode, s);
                        }
//                        arrDestinationLocation.add("Update RMG No");
                        arrDestinationLocationDesc.add("Update RMG No");
                        /*for (String a : arrDestinationLocationDesc) {
                            Log.i(TAG, "onResponse: " + a.toLowerCase());
                        }*/
/*

                        String userSourceLocation = loginUserSourceCode;
                        String userSourceLocationDesc = loginUserSourceCodeDesc;
                        String userSourceDesc = userSourceLocation + " - " + userSourceLocationDesc;
                        Log.i(TAG, "onResponse: userSourceDesc : " + userSourceDesc);
                        if (arrDestinationLocationDesc.contains(userSourceDesc)) {
                            Log.i(TAG, "onResponse: in array testing" + arrDestinationLocationDesc.size());
                            arrDestinationLocationDesc.remove(userSourceDesc);
                        }
                            Log.i(TAG, "onResponse: in array testing" + arrDestinationLocationDesc.size());
*/

                        updateRmgNoAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, arrDestinationLocationDesc) {
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
                        spinnerUpdateRmgNo.setAdapter(updateRmgNoAdapter);
                        spinnerUpdateRmgNo.setSelection(updateRmgNoAdapter.getCount());

                        spinnerUpdateRmgNo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                String selectedRmgCode = adapterView.getSelectedItem().toString();
                                Log.i(TAG, "onItemSelected: selectedRmgNo :" + selectedRmgCode);
                                if (hashMapLocationCode.containsKey(selectedRmgCode)) {
                                    selectedRmgNo = hashMapLocationCode.get(selectedRmgCode);
                                    Log.i(TAG, "onItemSelected: selectedRmgNo : " + selectedRmgNo);
                                }

                                if (!selectedRmgCode.equalsIgnoreCase("Update RMG No")) {
                                    spinnerRemark.setEnabled(true);
                                    spinnerRemark.setClickable(true);
                                    spinnerRemark.setFocusable(true);
                                } else {
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
            public void onFailure(Call<RmgNumberApiResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
//                alertBuilder(t.getMessage());
                ((MainActivity) getActivity()).alert(getActivity(), "error", t.getMessage(), null, "OK");
            }
        });

        return true;
    }

    private boolean getRemarks() {
        progressBar.setVisibility(View.VISIBLE);
        Call<RemarkApiResponse> call = RetrofitController.getInstance().getLoadingAdviseApi().
                getAllCoromandelRemark("Bearer " + token);

        call.enqueue(new Callback<RemarkApiResponse>() {
            @Override
            public void onResponse(Call<RemarkApiResponse> call, Response<RemarkApiResponse> response) {
                if (!response.isSuccessful()) {
                    progressBar.setVisibility(View.GONE);
                    ((MainActivity) getActivity()).alert(getActivity(), "error", response.errorBody().toString(), null, "OK");
//                    alertBuilder(response.errorBody().toString());
                    return;
                }
                Log.i(TAG, "onResponse: getAllRemark : responseCode : " + response.code());
                if (response.isSuccessful()) {
                    progressBar.setVisibility(View.GONE);
                    List<RemarksDto> remarksDtoList = response.body().getRemarksDtos();
                    HashMap<String, Integer> hashMapRemarks = new HashMap<>();
                    ArrayList<String> arrRemarks = new ArrayList<>();
                    try {
                        if (remarksDtoList == null || remarksDtoList.isEmpty()) {
                            Toast.makeText(getActivity(), EMPTY_REMARKS, Toast.LENGTH_SHORT).show();
                            return;
                        }
                        for (int i = 0; i < remarksDtoList.size(); i++) {
                            String s = remarksDtoList.get(i).getRemarks();
                            int id = remarksDtoList.get(i).getId();
                            hashMapRemarks.put(s, id);
                            arrRemarks.add(s);
                        }
                        arrRemarks.add("Select Remarks");

                        remarksAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, arrRemarks) {
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
                   /*     spinnerRemark.setEnabled(false);
                        spinnerRemark.setClickable(false);*/
                        spinnerRemark.setAdapter(remarksAdapter);
                        spinnerRemark.setSelection(remarksAdapter.getCount());

                        spinnerRemark.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                selectedRemarks = adapterView.getSelectedItem().toString();
                                if (hashMapRemarks.containsKey(selectedRemarks)) {
                                    selectedRemarksId = hashMapRemarks.get(selectedRemarks);
                                    Log.i(TAG, "onItemSelected: Selected Remarks Id " + selectedRemarksId);
                                }
                              /*  if (selectedRmgNo != null) {
                                    if (selectedRmgNo.equalsIgnoreCase("Update RMG No")) {
                                        spinnerRemark.setEnabled(false);
                                        spinnerRemark.setClickable(false);
                                        spinnerRemark.setFocusable(false);
                                    }
                                }*/
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
                progressBar.setVisibility(View.GONE);
//                alertBuilder(t.getMessage());
                ((MainActivity) requireActivity()).alert(requireActivity(), "error", t.getMessage(), null, "OK");
            }
        });
        return true;
    }

    private void updateRmgNo(UpdateRmgRequestDto updateRmgRequestDto) {
        Log.i(TAG, new Gson().toJson(updateRmgRequestDto).toString());
        Call<TransactionsApiResponse> call = RetrofitController.getInstance().getLoadingAdviseApi().updateRmgNo("Bearer " + token, updateRmgRequestDto);
        progressBar.setVisibility(View.VISIBLE);
        call.enqueue(new Callback<TransactionsApiResponse>() {
            @Override
            public void onResponse(Call<TransactionsApiResponse> call, Response<TransactionsApiResponse> response) {
                if (!response.isSuccessful()) {
                    progressBar.setVisibility(View.GONE);
                    ((MainActivity) getActivity()).alert(getActivity(), "error", response.errorBody().toString(), null, "OK");
//                    alertBuilder(response.errorBody().toString());
                }

                Log.i(TAG, "onResponse: code" + response.code());
               /* if (response.isSuccessful()) {
                    progressBar.setVisibility(View.GONE);
                    ((MainActivity) getActivity()).alert(getActivity(), "success", response.body().getMessage(), null, "OK");
//                    alertBuilder(response.body().getMessage());
                    resetFields();
                }*/

                if (response.body().getStatus().equalsIgnoreCase("OK")) {
                    progressBar.setVisibility(View.GONE);
                    ((MainActivity) getActivity()).alert(getActivity(), "success", response.body().getMessage(), null, "OK");
                    resetFields();
                } else {
                    progressBar.setVisibility(View.GONE);
                    ((MainActivity) getActivity()).alert(getActivity(), "error", response.body().getMessage(), null, "OK");
                    resetFields();
                }
            }

            @Override
            public void onFailure(Call<TransactionsApiResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
//                alertBuilder(t.getMessage());
                ((MainActivity) getActivity()).alert(getActivity(), "error", t.getMessage(), null, "OK");
                t.printStackTrace();
            }
        });
    }

    private UpdateRmgRequestDto setData() {
        StorageLocationDto selectedWareHouseNo = null;
        RemarksDto remarksDto = null;
        final Integer FLAG = 4;
        AuditEntity auditEntity = new AuditEntity(null, null, loginUserName, null);
        StorageLocationDto previousWareHouseNo = new StorageLocationDto(previousRmgNoId);
        if (selectedRmgNo != null) {
            if (!selectedRmgNo.equalsIgnoreCase("Update RMG No")) {
                selectedWareHouseNo = new StorageLocationDto(selectedRmgNo);
            }
            if (!selectedRemarks.equalsIgnoreCase("Select Remarks")) {
                remarksDto = new RemarksDto(selectedRemarksId);
            }
        }
        RfidLepIssueDto rfidLepIssueDto = new RfidLepIssueDto(selectedLepNumberId);
        return new UpdateRmgRequestDto(auditEntity, previousWareHouseNo, selectedWareHouseNo, rfidLepIssueDto, remarksDto, FLAG);
    }


    private void currentTime() {
        try {
            tvClock.setFormat24Hour("dd-MM-yy hh:mm a");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void callOnCreateApi() {
        getAllRmgStorage();
        getRemarks();
    }

    private void getLoadingAdviseDetails() {
        SharedPreferences sp = requireActivity().getSharedPreferences("WareHouseDetails", MODE_PRIVATE);
//        this.selectedLepNumberId = Integer.valueOf(sp.getString("lepNoIdSPK", null));
        String rfidTagId = sp.getString("rfidTagSPK", null);
        String lepNo = sp.getString("lepNoSPK", null);
        String driverName = sp.getString("driverNameSPK", null);
        String truckNo = sp.getString("truckNoSPK", null);
        String commodity = sp.getString("commoditySPK", null);
        String grossWeight = sp.getString("GrossWeightSPK", null);
        String previousRmgNo = sp.getString("previousRmgNoSPK", null);
        this.previousRmgNoId = previousRmgNo;
        String PreviousRmgNoDesc = sp.getString("PreviousRmgNoDescSPK", null);

        saveLoginAdviseData(rfidTagId, lepNo, driverName, truckNo, commodity, grossWeight, previousRmgNo, PreviousRmgNoDesc);
    }

    private void saveLoginAdviseData(String rfidTag, String lepNo, String driverName, String truckNo, String commodity, String grossWeight, String previousRmgNo, String PreviousRmgNoDesc) {
        Log.i(TAG, "saveLoginAdviseData: <<Start>>");
        edtRfidTag.setText(rfidTag);
        edtLepNo.setText(lepNo);
        edtTruckNumber.setText(truckNo);
        edtDriverName.setText(driverName);
        edtCommodity.setText(commodity);
        edtGrossWeight.setText(grossWeight);
        edtPreviousRmgNo.setText(previousRmgNo + " - " + PreviousRmgNoDesc);
        Log.i(TAG, "saveLoginAdviseData: <<end>>");
    }
}