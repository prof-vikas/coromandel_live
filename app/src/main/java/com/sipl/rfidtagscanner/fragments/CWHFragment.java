package com.sipl.rfidtagscanner.fragments;

import static android.content.Context.MODE_PRIVATE;
import static com.sipl.rfidtagscanner.utils.Config.DIALOG_ERROR;
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
import android.widget.LinearLayout;
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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
    private String inUnloadingTime = null;
    private EditText edtEntryTime;
    ;
    private TextClock tvClock;
    private LinearLayout tvEntryTimeClocKLayout, tvEntryTimeEdtLayout, tvLoadingTimeLayout;

    private EditText edtRfidTag, edtLepNo, edtDriverName, edtTruckNumber, edtCommodity, edtGrossWeight, edtPreviousRmgNo;
    private CustomToast customToast = new CustomToast();
    private ProgressBar progressBar;
    private Spinner spinnerUpdateRmgNo, spinnerRemark;
    private Button btnSubmit, btnReset;

    private String loginUserName;
    private String token;
    private String selectedRemarks;
    private Integer selectedRemarksId;
    private String selectedRmgNo;
    private Integer selectedLepNumberId;
    private String defaultWareHouse;
    private String previousRMG;
    private String remarks;
    private String previousRMGCode;
    private String defaulfWareHouseDesc;

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

        tvClock = view.findViewById(R.id.bwh_tv_clock);
//        tvEntryTime = view.findViewById(R.id.bwh_tv_entry_time);
        edtEntryTime = view.findViewById(R.id.edt_entry_time2);
        tvEntryTimeClocKLayout = view.findViewById(R.id.title_entry_time);
        tvEntryTimeEdtLayout = view.findViewById(R.id.bwh_ll_entry_time);
        tvLoadingTimeLayout = view.findViewById(R.id.title_unloading_time);

        this.token = ((MainActivity) getActivity()).getLoginToken();
        this.loginUserName = ((MainActivity) getActivity()).getLoginUsername();

        currentTime();
        getLoadingAdviseDetails();
        updateUIBaseOnWareHouseLocation();
        callOnCreateApi();

        btnSubmit.setOnClickListener(view12 -> {
            if (validateLoadingAdviseForm()) {
                updateRmgNo(setData());
            }
        });
        btnReset.setOnClickListener(view1 -> resetFields());

        return view;
    }

    private void updateUIBaseOnWareHouseLocation() {
        SharedPreferences sp = requireActivity().getSharedPreferences("WareHouseDetails", MODE_PRIVATE);
        String strInUnloadingTime = sp.getString("inUnloadingTimeSPK", null);
        String inUnloadingTime = null;
        if (strInUnloadingTime != null) {
            LocalDateTime aLDT = LocalDateTime.parse(strInUnloadingTime);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss a");
            inUnloadingTime = aLDT.format(formatter);
        }

        if (inUnloadingTime != null) {
            tvEntryTimeClocKLayout.setVisibility(View.GONE);
            tvEntryTimeEdtLayout.setVisibility(View.VISIBLE);
            edtEntryTime.setText(inUnloadingTime);
            tvLoadingTimeLayout.setVisibility(View.VISIBLE);
        } else {
            tvEntryTimeClocKLayout.setVisibility(View.VISIBLE);
        }
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

    private boolean getAllRmgStorage() {
        progressBar.setVisibility(View.VISIBLE);
        Call<RmgNumberApiResponse> call = RetrofitController.getInstances(requireContext()).getLoadingAdviseApi().
                getAllCoromandelRmgNo("Bearer " + token, "coromandel");

        call.enqueue(new Callback<RmgNumberApiResponse>() {
            @Override
            public void onResponse(Call<RmgNumberApiResponse> call, Response<RmgNumberApiResponse> response) {

                if (!response.isSuccessful()) {
                    progressBar.setVisibility(View.GONE);
                    ((MainActivity) getActivity()).alert(getActivity(), DIALOG_ERROR, response.errorBody().toString(), null, "OK", false);
                    return;
                }
                Log.i(TAG, "onResponse: getAllUpdateRmgNo : responseCode : " + response.code() + " " + response.raw());

                if (response.isSuccessful()) {
                    progressBar.setVisibility(View.GONE);
                    HashMap<String, String> hashMapLocationCode = new HashMap<>();
                    List<StorageLocationDto> functionalLocationMasterDtoList = response.body().getStorageLocationDtos();
                    ArrayList<String> arrDestinationLocation = new ArrayList<>();
                    ArrayList<String> arrDestinationLocationDesc = new ArrayList<>();
                    SharedPreferences sp = requireActivity().getSharedPreferences("WareHouseDetails", MODE_PRIVATE);
                    String PreviousRmgNoDesc = sp.getString("PreviousRmgNoDescSPK", null);
                    String removedPreviousRmgCode = defaultWareHouse + " - " + PreviousRmgNoDesc;
                    try {
                        if (functionalLocationMasterDtoList == null || functionalLocationMasterDtoList.isEmpty()) {
                            customToast.toastMessage(getActivity(), EMPTY_RMG_NUMBER, 0);
                            return;
                        }
                        for (int i = 0; i < functionalLocationMasterDtoList.size(); i++) {
                            String s = functionalLocationMasterDtoList.get(i).getStrLocationCode();
                            String strLocationDesc = functionalLocationMasterDtoList.get(i).getStrLocationDesc();
                            arrDestinationLocation.add(s);
                            String strLocationDescWithCode = s + " - " + strLocationDesc.toUpperCase();
                            arrDestinationLocationDesc.add(strLocationDescWithCode);
                            hashMapLocationCode.put(strLocationDescWithCode, s);
                        }
                        if (arrDestinationLocationDesc.contains(removedPreviousRmgCode)) {
                            arrDestinationLocationDesc.remove(removedPreviousRmgCode);
                        }
                        arrDestinationLocationDesc.add("Update RMG No");
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

                        if (inUnloadingTime != null) {
                            if (previousRMG.equalsIgnoreCase(defaulfWareHouseDesc)) {
                                spinnerUpdateRmgNo.setEnabled(false);
                                spinnerUpdateRmgNo.setBackgroundResource(R.drawable.rectangle_edt_read_only_field);
                                spinnerUpdateRmgNo.setSelection(updateRmgNoAdapter.getCount());
                            } else {
                                int position = -1;
                                if (arrDestinationLocationDesc.contains(defaulfWareHouseDesc)) {
                                    for (int i = 0; i < updateRmgNoAdapter.getCount(); i++) {
                                        String item = updateRmgNoAdapter.getItem(i);
                                        if (defaulfWareHouseDesc.trim().equalsIgnoreCase(item.trim())) {
                                            position = i;
                                            break;
                                        }
                                    }
                                    if (position != -1) {
                                        spinnerUpdateRmgNo.setBackgroundResource(R.drawable.rectangle_edt_read_only_field);
                                        spinnerUpdateRmgNo.setSelection(position);
                                        spinnerUpdateRmgNo.setEnabled(false);
                                    } else {
                                        Log.i(TAG, "onResponse:  in position else");
                                    }
                                } else {
                                    Log.i(TAG, "onResponse: not contain storage location " + defaulfWareHouseDesc);
                                }
                            }
                        } else {
                            spinnerUpdateRmgNo.setSelection(updateRmgNoAdapter.getCount());
                        }

                        spinnerUpdateRmgNo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                String selectedRmgCode = adapterView.getSelectedItem().toString();

                                if (hashMapLocationCode.containsKey(selectedRmgCode)) {
                                    selectedRmgNo = hashMapLocationCode.get(selectedRmgCode);
                                }

                                if (!selectedRmgCode.equalsIgnoreCase("Update RMG No")) {
                                    spinnerRemark.setEnabled(true);
                                } else {
                                    spinnerRemark.setEnabled(false);
                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {
                            }
                        });
                    } catch (Exception e) {
                        Log.e(TAG, "Exception in RMG location : " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<RmgNumberApiResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                ((MainActivity) getActivity()).alert(getActivity(), DIALOG_ERROR, t.getMessage(), null, "OK", false);
            }
        });

        return true;
    }

    private void getRemarks() {
        progressBar.setVisibility(View.VISIBLE);
        Call<RemarkApiResponse> call = RetrofitController.getInstances(requireContext()).getLoadingAdviseApi().
                getAllCoromandelRemark("Bearer " + token);

        call.enqueue(new Callback<RemarkApiResponse>() {
            @Override
            public void onResponse(Call<RemarkApiResponse> call, Response<RemarkApiResponse> response) {
                if (!response.isSuccessful()) {
                    progressBar.setVisibility(View.GONE);
                    ((MainActivity) getActivity()).alert(getActivity(), DIALOG_ERROR, response.errorBody().toString(), null, "OK", false);
                    return;
                }
                Log.i(TAG, "onResponse: getAllRemark : responseCode : " + response.code() + response.raw());
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

                        remarksAdapter = new ArrayAdapter<String>(requireActivity(), android.R.layout.simple_spinner_dropdown_item, arrRemarks) {
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

                        spinnerRemark.setAdapter(remarksAdapter);
                        spinnerRemark.setSelection(remarksAdapter.getCount());
                        if (inUnloadingTime != null) {
                            if (previousRMG.equalsIgnoreCase(defaulfWareHouseDesc)) {
                                spinnerRemark.setSelection(remarksAdapter.getCount());
                                spinnerRemark.setBackgroundResource(R.drawable.rectangle_edt_read_only_field);
                                spinnerRemark.setEnabled(false);
                            } else {
                                int position = -1;
                                if (arrRemarks.contains(remarks)) {
                                    for (int i = 0; i < remarksAdapter.getCount(); i++) {
                                        String item = remarksAdapter.getItem(i);
                                        if (remarks.trim().equalsIgnoreCase(item.trim())) {
                                            position = i;
                                            break;
                                        }
                                    }
                                    if (position != -1) {
                                        spinnerRemark.setBackgroundResource(R.drawable.rectangle_edt_read_only_field);
                                        spinnerRemark.setSelection(position);
                                        spinnerRemark.setEnabled(false);
                                    }
                                }
                            }
                        } else {
                            spinnerRemark.setSelection(remarksAdapter.getCount());
                        }

                        spinnerRemark.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                selectedRemarks = adapterView.getSelectedItem().toString();
                                if (hashMapRemarks.containsKey(selectedRemarks)) {
                                    selectedRemarksId = hashMapRemarks.get(selectedRemarks);
                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {
                            }
                        });
                    } catch (Exception e) {
                        Log.e(TAG, "onResponse: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<RemarkApiResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                ((MainActivity) requireActivity()).alert(requireActivity(), "error", t.getMessage(), null, "OK", false);
            }
        });
    }

    private void updateRmgNo(UpdateRmgRequestDto updateRmgRequestDto) {
        Log.i(TAG, new Gson().toJson(updateRmgRequestDto).toString());
        Call<TransactionsApiResponse> call = RetrofitController.getInstances(requireContext()).getLoadingAdviseApi().updateRmgNo("Bearer " + token, updateRmgRequestDto);
        progressBar.setVisibility(View.VISIBLE);
        call.enqueue(new Callback<TransactionsApiResponse>() {
            @Override
            public void onResponse(Call<TransactionsApiResponse> call, Response<TransactionsApiResponse> response) {
                if (!response.isSuccessful()) {
                    progressBar.setVisibility(View.GONE);
                    ((MainActivity) getActivity()).alert(getActivity(), "error", response.errorBody().toString(), null, "OK", false);
                }

                Log.i(TAG, "onResponse: code" + response.code() + response.raw());
                if (response.isSuccessful()) {
                    if (response.body().getStatus() != null) {
                        if (response.body().getStatus().equalsIgnoreCase("OK")) {
                            progressBar.setVisibility(View.GONE);
                            ((MainActivity) getActivity()).alert(getActivity(), "success", response.body().getMessage(), null, "OK", true);
//                            resetFields();
                        } else {
                            progressBar.setVisibility(View.GONE);
                            ((MainActivity) getActivity()).alert(getActivity(), "error", response.body().getMessage(), null, "OK", false);
//                            resetFields();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<TransactionsApiResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Log.i(TAG, "onFailure: " + t.getMessage());
                ((MainActivity) getActivity()).alert(getActivity(), "error", t.getMessage(), null, "OK", false);
                t.printStackTrace();
            }
        });
    }

    private UpdateRmgRequestDto setData() {
        StorageLocationDto selectedWareHouseNo = null;
        RemarksDto remarksDto = null;
        final Integer FLAG = 4;
        AuditEntity auditEntity = new AuditEntity(null, null, loginUserName, null);
        StorageLocationDto previousWareHouseNo = new StorageLocationDto(defaultWareHouse);
        if (selectedRmgNo != null) {
            if (!selectedRmgNo.equalsIgnoreCase("Update RMG No")) {
                selectedWareHouseNo = new StorageLocationDto(selectedRmgNo);
            }
            if (!selectedRemarks.equalsIgnoreCase("Select Remarks")) {
                remarksDto = new RemarksDto(selectedRemarksId);
            }
        } else {
            selectedWareHouseNo = new StorageLocationDto(defaultWareHouse);
        }

        RfidLepIssueDto rfidLepIssueDto = new RfidLepIssueDto(selectedLepNumberId);
        if (inUnloadingTime != null) {
            StorageLocationDto previousWareHouseNo2 = new StorageLocationDto(previousRMGCode);
            return new UpdateRmgRequestDto(auditEntity, previousWareHouseNo2, selectedWareHouseNo, rfidLepIssueDto, remarksDto, FLAG, inUnloadingTime, LocalDateTime.now().toString());
        } else {
            return new UpdateRmgRequestDto(auditEntity, previousWareHouseNo, selectedWareHouseNo, rfidLepIssueDto, remarksDto, FLAG, LocalDateTime.now().toString(), null);
        }
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
        this.selectedLepNumberId = Integer.valueOf(sp.getString("lepNoIdSPK", null));
        String rfidTagId = sp.getString("rfidTagSPK", null);
        String lepNo = sp.getString("lepNoSPK", null);
        String driverName = sp.getString("driverNameSPK", null);
        String truckNo = sp.getString("truckNoSPK", null);
        String commodity = sp.getString("commoditySPK", null);
        String grossWeight = sp.getString("GrossWeightSPK", null);
        String previousRmgNo = sp.getString("previousRmgNoSPK", null);
        String PreviousRmgNoDesc = sp.getString("PreviousRmgNoDescSPK", null);
        String inUnloadingTime = sp.getString("inUnloadingTimeSPK", null);
//        String outUnloadingTime = sp.getString("outUnloadingTimeSPK", null);
        String wareHouseCode = sp.getString("wareHouseCodeSPK", null);
        String wareHouseDesc = sp.getString("wareHouseCodeDescSPK", null);
        String remarks = sp.getString("remarksSPK", null);
        String wareHouse = wareHouseCode + " - " + wareHouseDesc;
        String previousRMG = previousRmgNo + " - " + PreviousRmgNoDesc;
        Log.i(TAG, "getLoadingAdviseDetails: previous : " + previousRmgNo + " wareHouse : " + wareHouseCode);
        this.defaultWareHouse = wareHouseCode;
        this.remarks = remarks;
        this.defaulfWareHouseDesc = wareHouse.toUpperCase();
        this.previousRMG = previousRMG;
        this.previousRMGCode = previousRmgNo;
        this.inUnloadingTime = inUnloadingTime;
//        this.outUnloadingTime = outUnloadingTime;
        saveLoginAdviseData(rfidTagId, lepNo, driverName, truckNo, commodity, grossWeight, previousRmgNo, PreviousRmgNoDesc, wareHouse);
    }

    private void saveLoginAdviseData(String rfidTag, String lepNo, String driverName, String truckNo, String commodity, String grossWeight, String previousRmgNo, String PreviousRmgNoDesc, String wareHouseCode) {
        Log.i(TAG, "saveLoginAdviseData: <<Start>>");
        edtRfidTag.setText(rfidTag);
        edtLepNo.setText(lepNo);
        edtTruckNumber.setText(truckNo);
        edtDriverName.setText(driverName);
        edtCommodity.setText(commodity);
        edtGrossWeight.setText(grossWeight);
        if (inUnloadingTime != null) {
            Log.i(TAG, "saveLoginAdviseData: in if");
            edtPreviousRmgNo.setText(previousRmgNo + " - " + PreviousRmgNoDesc);
        } else {
            Log.i(TAG, "saveLoginAdviseData:  in else");
            edtPreviousRmgNo.setText(wareHouseCode);
        }
        Log.i(TAG, "saveLoginAdviseData: <<end>>");
    }
}