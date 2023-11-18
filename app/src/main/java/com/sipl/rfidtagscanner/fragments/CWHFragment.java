package com.sipl.rfidtagscanner.fragments;

import static android.content.Context.MODE_PRIVATE;
import static com.sipl.rfidtagscanner.utils.Config.BTN_OK;
import static com.sipl.rfidtagscanner.utils.Config.DIALOG_ERROR;
import static com.sipl.rfidtagscanner.utils.Config.DIALOG_WARNING;
import static com.sipl.rfidtagscanner.utils.Config.EMPTY_REMARKS;

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
import com.google.gson.reflect.TypeToken;
import com.sipl.rfidtagscanner.MainActivity;
import com.sipl.rfidtagscanner.R;
import com.sipl.rfidtagscanner.RetrofitController;
import com.sipl.rfidtagscanner.dto.dtos.GenericData;
import com.sipl.rfidtagscanner.dto.dtos.RemarksDto;
import com.sipl.rfidtagscanner.dto.dtos.RfidLepIssueDto;
import com.sipl.rfidtagscanner.dto.dtos.StorageLocationDto;
import com.sipl.rfidtagscanner.dto.request.UpdateRmgRequestDto;
import com.sipl.rfidtagscanner.dto.response.RemarkApiResponse;
import com.sipl.rfidtagscanner.dto.response.TransactionsApiResponse;
import com.sipl.rfidtagscanner.entites.AuditEntity;

import java.lang.reflect.Type;
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

    ArrayList<String> arrayList = new ArrayList<>();
    private ArrayAdapter<String> updateAssignDestinationLocation;

    private ArrayAdapter<String> remarksAdapter;

    private LinearLayout llEdtLepLocationActual, llSpinnerLepLocationActual, llSpinnerRemarks;
    private EditText edtLepLocationActual;


    private String inUnloadingTime = null;
    private String userType;
    private EditText edtEntryTime, edtOtherRemarks;
    private TextClock tvClock, tvEntryTime;
    private LinearLayout tvEntryTimeClocKLayout, tvEntryTimeEdtLayout, tvLoadingTimeLayout, tvOtherRemark;

    private EditText edtRfidTag, edtLepNo, edtDriverName, edtTruckNumber, edtCommodity, edtGrossWeight, edtPreviousRmgNo, edtBatchNumber, edtBerthNumber;
    private ProgressBar progressBar;
    private Spinner spinnerUpdateRmgNo, spinnerRemark;
    private Button btnSubmit, btnReset;

    private String loginUserName, token, selectedRemarks, selectedRmgNo, defaultWareHouse, previousRMG, remarks, previousRMGCode, defaulfWareHouseDesc;
    private Integer selectedRemarksId, selectedLepNumberId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_c_w_h, container, false);
        spinnerRemark = view.findViewById(R.id.cwh_spinner_remark);
        spinnerUpdateRmgNo = view.findViewById(R.id.cwh_spinner_update_rmg_no);

        llSpinnerLepLocationActual = view.findViewById(R.id.ll_spinner_rmg_no);
        llSpinnerRemarks = view.findViewById(R.id.cwh_remark);
        llEdtLepLocationActual = view.findViewById(R.id.ll_edt_rmg_no);
        edtLepLocationActual = view.findViewById(R.id.cwh_edt_update_rmg_no);

        edtRfidTag = view.findViewById(R.id.cwh_edt_rfid_tag);
        edtLepNo = view.findViewById(R.id.cwh_edt_lep_number);
        edtDriverName = view.findViewById(R.id.cwh_edt_driver_name);
        edtTruckNumber = view.findViewById(R.id.cwh_edt_truck_no);
        edtCommodity = view.findViewById(R.id.cwh_edt_commodity);
        edtGrossWeight = view.findViewById(R.id.cwh_edt_gross_weight);
        edtPreviousRmgNo = view.findViewById(R.id.cwh_edt_previous_rmg_no);
        edtOtherRemarks = view.findViewById(R.id.cwh_edt_other);
        edtBatchNumber = view.findViewById(R.id.cwh_edt_batch_no);
        edtBerthNumber = view.findViewById(R.id.cwh_edt_berth_no);

        btnReset = view.findViewById(R.id.cwh_btn_reset);
        btnSubmit = view.findViewById(R.id.cwh_btn_submit);
        progressBar = view.findViewById(R.id.cwh_progressBar);

        tvClock = view.findViewById(R.id.bwh_unloading_out_time);
        tvEntryTime = view.findViewById(R.id.cwh_tv_entry_time);
        edtEntryTime = view.findViewById(R.id.edt_entry_time2);
        tvEntryTimeClocKLayout = view.findViewById(R.id.title_entry_time);
        tvEntryTimeEdtLayout = view.findViewById(R.id.bwh_ll_entry_time);
        tvLoadingTimeLayout = view.findViewById(R.id.title_unloading_time);
        tvOtherRemark = view.findViewById(R.id.cwh_other);

        this.token = ((MainActivity) getActivity()).getToken();
        this.loginUserName = ((MainActivity) getActivity()).getUserName();

        displayClock();
        getCWHDetails();
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

    private void displayClock() {
        try {
            tvClock.setFormat24Hour("dd-MM-yyyy HH:mm:ss");
            tvEntryTime.setFormat24Hour("dd-MM-yyyy HH:mm:ss");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateUIBaseOnWareHouseLocation() {
        SharedPreferences sp = requireActivity().getSharedPreferences("WareHouseDetails", MODE_PRIVATE);
        String strInUnloadingTime = sp.getString("inUnloadingTimeSPK", null);
        String inUnloadingTime = null;
        if (strInUnloadingTime != null) {
            spinnerRemark.setEnabled(false);
            LocalDateTime aLDT = LocalDateTime.parse(strInUnloadingTime);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
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
        if (!userType.equalsIgnoreCase("authorized")) {
            if (spinnerUpdateRmgNo.getSelectedItem().toString().equalsIgnoreCase("Update LEP Location")) {
                Toast.makeText(getActivity(), "Please select LEP location (Actual)", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        if (arrayList.size() > 0) {
            if (!spinnerUpdateRmgNo.getSelectedItem().toString().equals("Update LEP Location") && spinnerRemark.getSelectedItem().toString().equals("Select Remarks")) {
                Toast.makeText(getActivity(), "Select remarks", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }

    private void resetFields() {
        ((MainActivity) requireActivity()).loadFragment(new ScanFragment(), 1);
    }

    private void getUserMappedStorage() {
        String storageLocation = ((MainActivity) requireActivity()).destinationLocationDtoList();
        spinnerRemark.setEnabled(false);
        if (storageLocation != null) {

            HashMap<String, String> hashMapForDestinationLocation = new HashMap<>();
            Type listType = new TypeToken<List<GenericData>>() {
            }.getType();
            List<GenericData> parsedDestinationList = new Gson().fromJson(storageLocation, listType);
            for (GenericData name : parsedDestinationList) {
                String s = name.getName();
                String v = name.getValue();
                arrayList.add(s);
                hashMapForDestinationLocation.put(s, v);
            }

            if (arrayList.size() > 0) {
                int counter = 1;
                for (String s: arrayList) {
                    Log.e(TAG, "getUserMappedStorage: Stoorage location : " + counter + " : " + s );
                    counter ++ ;
                }
                llEdtLepLocationActual.setVisibility(View.GONE);
                llSpinnerLepLocationActual.setVisibility(View.VISIBLE);
                arrayList.add("Update LEP Location");

                updateAssignDestinationLocation = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, arrayList) {
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

                spinnerUpdateRmgNo.setAdapter(updateAssignDestinationLocation);

                if (inUnloadingTime != null) {
                    if (previousRMG.equalsIgnoreCase(defaulfWareHouseDesc)) {
                        spinnerUpdateRmgNo.setEnabled(false);
                        spinnerRemark.setEnabled(false);
                        spinnerUpdateRmgNo.setBackgroundResource(R.drawable.rectangle_edt_read_only_field);
                        spinnerUpdateRmgNo.setSelection(updateAssignDestinationLocation.getCount());
                    } else {
                        int position = -1;
                        if (arrayList.contains(defaulfWareHouseDesc)) {
                            for (int i = 0; i < updateAssignDestinationLocation.getCount(); i++) {
                                String item = updateAssignDestinationLocation.getItem(i);
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
                            ((MainActivity)requireActivity()).alert(requireContext(), DIALOG_WARNING, "Please log in again. It appears that your user receiving location has been updated", null, BTN_OK, true);
                            Log.i(TAG, "onResponse: not contain storage location " + defaulfWareHouseDesc);
                            return;
                        }
                    }
                } else {
                    spinnerUpdateRmgNo.setSelection(updateAssignDestinationLocation.getCount());
                }

                spinnerUpdateRmgNo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        String selectedRmgCode = adapterView.getSelectedItem().toString();

                        if (hashMapForDestinationLocation.containsKey(selectedRmgCode)) {
                            selectedRmgNo = hashMapForDestinationLocation.get(selectedRmgCode);
                        }

                        if (!selectedRmgCode.equalsIgnoreCase("Update LEP Location")) {
                            spinnerRemark.setEnabled(true);
                        } else {
                            spinnerRemark.setEnabled(false);
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {
                    }
                });
           /* } else if (arrayList.size() == 1 && userType.equalsIgnoreCase("unAuthorizedUser")) {
                llEdtLepLocationActual.setVisibility(View.VISIBLE);
                llSpinnerLepLocationActual.setVisibility(View.GONE);
                llSpinnerRemarks.setVisibility(View.VISIBLE);

                spinnerRemark.setEnabled(true);
                edtLepLocationActual.setText(arrayList.get(0));
                edtLepLocationActual.setEnabled(false);
                if (hashMapForDestinationLocation.containsKey(arrayList.get(0))) {
                    selectedRmgNo = hashMapForDestinationLocation.get(arrayList.get(0));
                }
                */
            } else {
                Log.e(TAG, "getUserMappedStorage: in else as per sudhir" );
                llEdtLepLocationActual.setVisibility(View.VISIBLE);
                llSpinnerLepLocationActual.setVisibility(View.GONE);
                llSpinnerRemarks.setVisibility(View.GONE);

                spinnerRemark.setEnabled(false);
                edtLepLocationActual.setText(arrayList.get(0));
                edtLepLocationActual.setEnabled(false);
                if (hashMapForDestinationLocation.containsKey(arrayList.get(0))) {
                    selectedRmgNo = hashMapForDestinationLocation.get(arrayList.get(0));
                }
            }
        }
    }

    private void getRemarks() {
        progressBar.setVisibility(View.VISIBLE);
        Call<RemarkApiResponse> call = RetrofitController.getInstances(requireContext()).getLoadingAdviseApi().
                getRemarks("Bearer " + token);

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
                        /*    @Override
                            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                               *//* if (convertView == null) {
                                    convertView = getLayoutInflater().inflate(R.layout.custom_spinner_dropdown_item, parent, false);
                                }*//*

                             *//*  TextView text = convertView.findViewById(R.id.spinner_item_text);
                                text.setText(getItem(position));*//*
                                TextView text = convertView.findViewById(R.id.spinner_selected_item_text);
                                text.setText(getItem(position));

                                return convertView;
                            }*/

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
        Call<TransactionsApiResponse> call = RetrofitController.getInstances(requireContext()).getLoadingAdviseApi().updateCilWarehouse("Bearer " + token, updateRmgRequestDto);
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
                        } else {
                            progressBar.setVisibility(View.GONE);
                            ((MainActivity) getActivity()).alert(getActivity(), "error", response.body().getMessage(), null, "OK", false);
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
            if (arrayList.size() > 0) {
                if (!selectedRmgNo.equalsIgnoreCase("Update RMG No")) {
                    selectedWareHouseNo = new StorageLocationDto(selectedRmgNo);
                }
                if (!selectedRemarks.equalsIgnoreCase("Select Remarks")) {
                    remarksDto = new RemarksDto(selectedRemarksId);
                }
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

    private void callOnCreateApi() {
        getUserMappedStorage();
        getRemarks();
    }

    private void getCWHDetails() {
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
        String wareHouseCode = sp.getString("wareHouseCodeSPK", null);
        String wareHouseDesc = sp.getString("wareHouseCodeDescSPK", null);
        String remarks = sp.getString("remarksSPK", null);
        String batchNumber = sp.getString("batchNumberSPK", null);
        String userType = sp.getString("userTypeSPK", null);
        String berthNumber = sp.getString("berthNumberSPK", null);
        this.userType = userType;
        String wareHouse = wareHouseCode + " - " + wareHouseDesc;
        String previousRMG = previousRmgNo + " - " + PreviousRmgNoDesc;
        this.defaultWareHouse = wareHouseCode;
        this.remarks = remarks;
        this.defaulfWareHouseDesc = wareHouse.toUpperCase();
        this.previousRMG = previousRMG;
        this.previousRMGCode = previousRmgNo;
        this.inUnloadingTime = inUnloadingTime;

        if (inUnloadingTime != null) {
            spinnerUpdateRmgNo.setEnabled(false);
            spinnerRemark.setEnabled(false);
        }
        saveLoginAdviseData(rfidTagId, lepNo, driverName, truckNo, commodity, grossWeight, previousRmgNo, PreviousRmgNoDesc, wareHouse, batchNumber, berthNumber);
    }

    private void saveLoginAdviseData(String rfidTag, String lepNo, String driverName, String truckNo, String commodity, String grossWeight, String previousRmgNo, String PreviousRmgNoDesc, String wareHouseCode, String batchNumber, String berthNumber) {
        edtRfidTag.setText(rfidTag.toUpperCase());
        edtLepNo.setText(lepNo.toUpperCase());
        edtTruckNumber.setText(truckNo.toUpperCase());
        edtDriverName.setText(driverName.toUpperCase());
        edtCommodity.setText(commodity.toUpperCase());
        edtGrossWeight.setText(grossWeight.toUpperCase());
        edtBatchNumber.setText(batchNumber.toUpperCase());
        edtBerthNumber.setText(berthNumber);
        if (inUnloadingTime != null) {
            edtPreviousRmgNo.setText(previousRmgNo.toUpperCase() + " - " + PreviousRmgNoDesc.toUpperCase());
        } else {
            edtPreviousRmgNo.setText(wareHouseCode.toUpperCase());
        }
    }
}