package com.sipl.rfidtagscanner.fragments;

import static android.content.Context.MODE_PRIVATE;
import static com.sipl.rfidtagscanner.utils.Config.BTN_OK;
import static com.sipl.rfidtagscanner.utils.Config.DIALOG_ERROR;
import static com.sipl.rfidtagscanner.utils.Config.DIALOG_SUCCESS;
import static com.sipl.rfidtagscanner.utils.Config.DIALOG_WARNING;
import static com.sipl.rfidtagscanner.utils.Config.RESPONSE_OK;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
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
import com.sipl.rfidtagscanner.dto.request.UpdateWareHouseNoRequestDto;
import com.sipl.rfidtagscanner.dto.response.RemarkApiResponse;
import com.sipl.rfidtagscanner.dto.response.TransactionsApiResponse;
import com.sipl.rfidtagscanner.entites.AuditEntity;
import com.sipl.rfidtagscanner.utils.CustomErrorMessage;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class BWHFragment extends Fragment {

    private final String TAG = "TracingError";

    ArrayList<String> arrayList = new ArrayList<>();
    private ArrayAdapter<String> updateAssignDestinationLocation;


    private LinearLayout llEdtLepLocationActual, llSpinnerLepLocationActual, llSpinnerRemarks;
    private EditText edtLepLocationActual;
    private String userType;

    private TextClock tvExitTime, tvEntryTime;
    private LinearLayout tvEntryTimeClocKLayout, tvEntryTimeEdtLayout, tvLoadingTimeLayout;
    private ProgressBar progressBar;
    private Spinner spinnerWarehouseNo, spinnerRemark;
    private EditText edtRfidTag, edtLepNo, edtDriverName, edtTruckNumber, edtCommodity, edtGrossWeight, edtPreviousWareHouseNo, edtEntryTime, edtBatchNumber, edtBerthNumber;
    private Integer selectedLepNoId, selectedRemarksId;
    private String previousRMGCode, inUnloadingTime, selectedRemarks, selectedRmgNo, loginUserName, token, defaultWareHouse, previousRMG, remarks, defaulfWareHouseDesc;
    private ArrayAdapter<String> remarkAdapter, updateWareHouseNoAdapter;

    public BWHFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_b_w_h, container, false);

        llEdtLepLocationActual = view.findViewById(R.id.ll_bwh_edt_rmg_no);
        llSpinnerRemarks = view.findViewById(R.id.cwh_remark);
        llSpinnerLepLocationActual = view.findViewById(R.id.ll_bwh_spinner_rmg_no);
        edtLepLocationActual = view.findViewById(R.id.bwh_edt_warehouse_no);

        spinnerWarehouseNo = view.findViewById(R.id.bwh_spinner_warehouse_no);
        spinnerRemark = view.findViewById(R.id.bwh_spinner_remark);
        tvExitTime = view.findViewById(R.id.bwh_exit_time);
        tvEntryTime = view.findViewById(R.id.bwh_tv_entry_time);
        edtEntryTime = view.findViewById(R.id.edt_entry_time2);
        tvEntryTimeClocKLayout = view.findViewById(R.id.title_entry_time);
        tvEntryTimeEdtLayout = view.findViewById(R.id.bwh_ll_entry_time);
        tvLoadingTimeLayout = view.findViewById(R.id.title_unloading_time);
        edtBatchNumber = view.findViewById(R.id.edt_bwh_batch_no);

        edtRfidTag = view.findViewById(R.id.bwh_edt_rfid_tag);
        edtLepNo = view.findViewById(R.id.bwh_edt_lep_number);
        edtDriverName = view.findViewById(R.id.bwh_edt_driver_name);
        edtTruckNumber = view.findViewById(R.id.bwh_edt_truck_no);
        edtCommodity = view.findViewById(R.id.bwh_edt_commodity);
        edtGrossWeight = view.findViewById(R.id.bwh_edt_gross_weight);
        edtBerthNumber = view.findViewById(R.id.edt_bwh_berth_no);
        edtPreviousWareHouseNo = view.findViewById(R.id.bwh_edt_previous_ware_house_no);
        progressBar = view.findViewById(R.id.bwh_progressBar);
        Button btnReset = view.findViewById(R.id.bwh_btn_reset);
        Button btnSubmit = view.findViewById(R.id.bwh_btn_submit);

        this.token = ((MainActivity) requireActivity()).getToken();
        this.loginUserName = ((MainActivity) requireActivity()).getUserName();

        setLocalTime();
        getBWHDetails();
        updateUIBaseOnVehicleInTime();
        getUserMappedStorage();
        getAllRemarks();

        btnSubmit.setOnClickListener(view12 -> {
            if (validateLoadingData()) {
                updateWareHouseNo(setData());
            }
        });

        btnReset.setOnClickListener(view1 -> resetFields());
        return view;
    }

    private void resetFields() {
        ((MainActivity) requireActivity()).loadFragment(new ScanFragment(), 1);
    }

    private boolean validateLoadingData() {
        if (!userType.equalsIgnoreCase("authorized")) {
            if (spinnerWarehouseNo.getSelectedItem().toString().equalsIgnoreCase("Update LEP Location")) {
                Toast.makeText(getActivity(), "Please select LEP location (Actual)", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        if (arrayList.size() > 0) {
            if (!spinnerWarehouseNo.getSelectedItem().toString().equals("Update LEP Location") && spinnerRemark.getSelectedItem().toString().equals("Select Remarks")) {
                Toast.makeText(getActivity(), "Select remarks", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
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

                spinnerWarehouseNo.setAdapter(updateAssignDestinationLocation);

                if (inUnloadingTime != null) {
                    if (previousRMG.equalsIgnoreCase(defaulfWareHouseDesc)) {
                        spinnerWarehouseNo.setEnabled(false);
                        spinnerRemark.setEnabled(false);
                        spinnerWarehouseNo.setBackgroundResource(R.drawable.rectangle_edt_read_only_field);
                        spinnerWarehouseNo.setSelection(updateAssignDestinationLocation.getCount());
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
                                spinnerWarehouseNo.setBackgroundResource(R.drawable.rectangle_edt_read_only_field);
                                spinnerWarehouseNo.setSelection(position);
                                spinnerWarehouseNo.setEnabled(false);
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
                    spinnerWarehouseNo.setSelection(updateAssignDestinationLocation.getCount());
                }

                spinnerWarehouseNo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
            } else {
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

  /*  private void getBssLocation() {
        showProgress();
        Call<RmgNumberApiResponse> call = RetrofitController.getInstances(requireContext()).getLoadingAdviseApi().
                getAllCoromandelRmgNo("Bearer " + token, "bothra");

        call.enqueue(new Callback<RmgNumberApiResponse>() {
            @Override
            public void onResponse(Call<RmgNumberApiResponse> call, Response<RmgNumberApiResponse> response) {
                hideProgress();
                if (!response.isSuccessful()) {
                    ((MainActivity) requireActivity()).alert(requireContext(), DIALOG_ERROR, response.errorBody() != null ? response.errorBody().toString() : "An error occurs when attempting to get location information", null, "OK", false);
                    return;
                }
                Log.i(TAG, "onResponse: getBssLocation :  " + response.raw());

                HashMap<String, String> hashMapLocationCode = new HashMap<>();
                ArrayList<String> arrDestinationLocation = new ArrayList<>();
                ArrayList<String> arrDestinationLocationDesc = new ArrayList<>();
                SharedPreferences sp = requireActivity().getSharedPreferences("WareHouseDetails", MODE_PRIVATE);
                String PreviousRmgNoDesc = sp.getString("PreviousRmgNoDescSPK", null);
                String removedPreviousRmgCode = defaultWareHouse + " - " + PreviousRmgNoDesc;

                try {
                    if (response.body() != null && response.body().getStorageLocationDtos() != null) {
                        List<StorageLocationDto> bssLocationList = response.body().getStorageLocationDtos();
                        for (int i = 0; i < bssLocationList.size(); i++) {
                            String locationCode = bssLocationList.get(i).getStrLocationCode();
                            String strLocationDesc = bssLocationList.get(i).getStrLocationDesc();
                            arrDestinationLocation.add(locationCode);
                            String strLocationDescWithCode = locationCode + " - " + strLocationDesc.toUpperCase();
                            arrDestinationLocationDesc.add(strLocationDescWithCode);
                            hashMapLocationCode.put(strLocationDescWithCode, locationCode);
                        }
                        if (arrDestinationLocationDesc.contains(removedPreviousRmgCode)) {
                            arrDestinationLocationDesc.remove(removedPreviousRmgCode);
                        }
                        arrDestinationLocationDesc.add("Select Warehouse No");
                        updateWareHouseNoAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, arrDestinationLocationDesc) {
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

                        if (inUnloadingTime != null) {
                            if (previousRMG.equalsIgnoreCase(defaulfWareHouseDesc)) {
                                spinnerWarehouseNo.setEnabled(false);
                                spinnerWarehouseNo.setBackgroundResource(R.drawable.rectangle_edt_read_only_field);
                                spinnerWarehouseNo.setSelection(updateWareHouseNoAdapter.getCount());
                            } else {
                                int position = -1;
                                if (arrDestinationLocationDesc.contains(defaulfWareHouseDesc)) {
                                    for (int i = 0; i < updateWareHouseNoAdapter.getCount(); i++) {
                                        String item = updateWareHouseNoAdapter.getItem(i);
                                        if (defaulfWareHouseDesc.trim().equalsIgnoreCase(item.trim())) {
                                            position = i;
                                            break;
                                        }
                                    }
                                    if (position != -1) {
                                        spinnerWarehouseNo.setBackgroundResource(R.drawable.rectangle_edt_read_only_field);
                                        spinnerWarehouseNo.setSelection(position);
                                        spinnerWarehouseNo.setEnabled(false);
                                    }
                                }
                            }
                        } else {
                            spinnerWarehouseNo.setSelection(updateWareHouseNoAdapter.getCount());
                        }

                        spinnerWarehouseNo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
                    } else {
                        ((MainActivity) requireActivity()).alert(requireActivity(), DIALOG_ERROR, "No BSS location found", null, BTN_OK, false);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Exception in getBssLocation : " + e.getMessage());
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<RmgNumberApiResponse> call, Throwable t) {
                hideProgress();
                ((MainActivity) getActivity()).alert(getActivity(), DIALOG_ERROR, CustomErrorMessage.setErrorMessage(t.getMessage()), null, "OK", false);
            }
        });
    }*/

    private void getAllRemarks() {
        showProgress();
        Call<RemarkApiResponse> call = RetrofitController.getInstances(requireContext()).getLoadingAdviseApi().
                getRemarks("Bearer " + token);

        call.enqueue(new Callback<RemarkApiResponse>() {
            @Override
            public void onResponse(Call<RemarkApiResponse> call, Response<RemarkApiResponse> response) {
                hideProgress();
                if (!response.isSuccessful()) {
                    ((MainActivity) getActivity()).alert(getActivity(), DIALOG_ERROR, response.errorBody() != null ? response.errorBody().toString() : "An error occurs when attempting to get remarks", null, BTN_OK, false);
                }
                Log.i(TAG, "onResponse: getAllRemark : responseCode : " + response.code() + response.raw());

                if (response.body() != null) {
                    List<RemarksDto> remarksDtoList = response.body().getRemarksDtos();
                    HashMap<String, Integer> hashMapRemarks = new HashMap<>();
                    ArrayList<String> arrRemarks = new ArrayList<>();
                    try {
                        for (int i = 0; i < remarksDtoList.size(); i++) {
                            String s = remarksDtoList.get(i).getRemarks();
                            int id = remarksDtoList.get(i).getId();
                            hashMapRemarks.put(s, id);
                            arrRemarks.add(s);
                        }
                        arrRemarks.add("Select Remarks");

                        remarkAdapter = new ArrayAdapter<String>(requireActivity(), android.R.layout.simple_spinner_dropdown_item, arrRemarks) {
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

                        spinnerRemark.setAdapter(remarkAdapter);
                        spinnerRemark.setSelection(remarkAdapter.getCount());
                        if (inUnloadingTime != null) {
                            if (previousRMG.equalsIgnoreCase(defaulfWareHouseDesc)) {
                                spinnerRemark.setSelection(remarkAdapter.getCount());
                                spinnerRemark.setBackgroundResource(R.drawable.rectangle_edt_read_only_field);
                                spinnerRemark.setEnabled(false);
                            } else {
                                int position = -1;
                                if (arrRemarks.contains(remarks)) {
                                    for (int i = 0; i < remarkAdapter.getCount(); i++) {
                                        String item = remarkAdapter.getItem(i);
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
                            spinnerRemark.setSelection(remarkAdapter.getCount());
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
                hideProgress();
                ((MainActivity) requireActivity()).alert(requireActivity(), DIALOG_ERROR, CustomErrorMessage.setErrorMessage(t.getMessage()), null, BTN_OK, false);
            }
        });
    }

    private UpdateWareHouseNoRequestDto setData() {
        StorageLocationDto selectedWareHouseNo = null;
        RemarksDto remarksDto = null;
        AuditEntity auditEntity = new AuditEntity(null, null, loginUserName, String.valueOf(LocalDateTime.now()));
        StorageLocationDto previousWareHouseNo = new StorageLocationDto(defaultWareHouse);
        if (selectedRmgNo != null) {
            if (arrayList.size() > 0) {
                if (!selectedRmgNo.equalsIgnoreCase("Update LEP Location")) {
                    selectedWareHouseNo = new StorageLocationDto(selectedRmgNo);
                }
                if (!selectedRemarks.equalsIgnoreCase("Select Remarks")) {
                    remarksDto = new RemarksDto(selectedRemarksId);
                }
            }
        } else {
            selectedWareHouseNo = new StorageLocationDto(defaultWareHouse);
        }
        RfidLepIssueDto rfidLepIssueDto = new RfidLepIssueDto(selectedLepNoId);
        if (inUnloadingTime != null) {
            StorageLocationDto previousWareHouseNo2 = new StorageLocationDto(previousRMGCode);
            return new UpdateWareHouseNoRequestDto(auditEntity, previousWareHouseNo2, selectedWareHouseNo, rfidLepIssueDto, remarksDto, 8, null, null, inUnloadingTime, LocalDateTime.now().toString());
        } else {
            return new UpdateWareHouseNoRequestDto(auditEntity, previousWareHouseNo, selectedWareHouseNo, rfidLepIssueDto, remarksDto, 8, null, null, LocalDateTime.now().toString(), null);
        }
    }


    private void updateWareHouseNo(UpdateWareHouseNoRequestDto updateWareHouseNoRequestDto) {
        Log.i(TAG, new Gson().toJson(updateWareHouseNoRequestDto));
        showProgress();
        Call<TransactionsApiResponse> call = RetrofitController.getInstances(requireActivity()).getLoadingAdviseApi().updateBothraWarehouse("Bearer " + token, updateWareHouseNoRequestDto);
        call.enqueue(new Callback<TransactionsApiResponse>() {
            @Override
            public void onResponse(Call<TransactionsApiResponse> call, Response<TransactionsApiResponse> response) {
                hideProgress();
                if (!response.isSuccessful()) {
                    ((MainActivity) requireActivity()).alert(requireActivity(), DIALOG_ERROR, response.errorBody() != null ? response.errorBody().toString() : "Error occurs while updating transaction", null, BTN_OK, false);
                }
                Log.i(TAG, "onResponse: updateWareHouseNo : " + response.raw());

                if (response.isSuccessful()) {
                    if (response.body().getStatus().equalsIgnoreCase(RESPONSE_OK)) {
                        ((MainActivity) requireActivity()).alert(requireActivity(), DIALOG_SUCCESS, response.body().getMessage(), null, BTN_OK, true);
                    } else {
                        ((MainActivity) requireActivity()).alert(requireActivity(), DIALOG_ERROR, response.body().getMessage(), null, BTN_OK, false);
                    }
                }
            }

            @Override
            public void onFailure(Call<TransactionsApiResponse> call, Throwable t) {
                hideProgress();
                ((MainActivity) requireActivity()).alert(getActivity(), DIALOG_ERROR, t.getMessage(), null, BTN_OK, false);
                t.printStackTrace();
            }
        });
    }

    private void setLocalTime() {
        try {
            tvEntryTime.setFormat24Hour("dd-MM-yyyy HH:mm:ss");
            tvExitTime.setFormat24Hour("dd-MM-yyyy HH:mm:ss");
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Exception in setTvClock : Message : " + e.getMessage());
        }
    }


    private void updateUIBaseOnVehicleInTime() {
        SharedPreferences sp = requireActivity().getSharedPreferences("WareHouseDetails", MODE_PRIVATE);
        String inUnloadingTime = sp.getString("inUnloadingTimeSPK", null);
        String strInUnloadingTime = null;
        if (inUnloadingTime != null) {
            spinnerRemark.setEnabled(false);
            LocalDateTime localDateTime = LocalDateTime.parse(inUnloadingTime);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
            strInUnloadingTime = localDateTime.format(formatter);
        }

        if (inUnloadingTime != null) {
            tvEntryTimeClocKLayout.setVisibility(View.GONE);
            tvEntryTimeEdtLayout.setVisibility(View.VISIBLE);
            edtEntryTime.setText(strInUnloadingTime);
            tvLoadingTimeLayout.setVisibility(View.VISIBLE);
        } else {
            tvEntryTimeClocKLayout.setVisibility(View.VISIBLE);
        }
    }

    private void showDataOnScreen(String rfidTag, String lepNo, String driverName, String truckNo, String commodity, String sourceGrossWeight, String previousRmgNo, String PreviousRmgNoDesc, String wareHouseCode, String batchNumber, String berthNumber) {
        edtRfidTag.setText(rfidTag.toUpperCase());
        edtLepNo.setText(lepNo.toUpperCase());
        edtTruckNumber.setText(truckNo.toUpperCase());
        edtDriverName.setText(driverName.toUpperCase());
        edtCommodity.setText(commodity.toUpperCase());
        edtGrossWeight.setText(sourceGrossWeight.toUpperCase());
        edtBatchNumber.setText(batchNumber.toUpperCase());
        edtBerthNumber.setText(berthNumber);

        if (inUnloadingTime != null) {
            String previousRmg = previousRmgNo + PreviousRmgNoDesc.toUpperCase();
            edtPreviousWareHouseNo.setText(previousRmg);
        } else {
            edtPreviousWareHouseNo.setText(wareHouseCode.toUpperCase());
        }
    }

    private void getBWHDetails() {
        SharedPreferences sp = requireActivity().getSharedPreferences("WareHouseDetails", MODE_PRIVATE);
        this.selectedLepNoId = Integer.valueOf(sp.getString("lepNoIdSPK", null));
        String rfidTagId = sp.getString("rfidTagSPK", null);
        String lepNo = sp.getString("lepNoSPK", null);
        String driverName = sp.getString("driverNameSPK", null);
        String truckNo = sp.getString("truckNoSPK", null);
        String commodity = sp.getString("commoditySPK", null);
        String sourceGrossWeight = sp.getString("sourceGrossWeightSPK", null);
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
        String wareHouse = wareHouseCode + " - " + wareHouseDesc.trim();
        String previousRMG = previousRmgNo + " - " + PreviousRmgNoDesc.trim();
        this.defaultWareHouse = wareHouseCode;
        this.remarks = remarks;
        this.previousRMGCode = previousRmgNo;
        this.defaulfWareHouseDesc = wareHouse.toUpperCase();
        this.previousRMG = previousRMG;
        this.inUnloadingTime = inUnloadingTime;

        if (inUnloadingTime != null) {
            spinnerWarehouseNo.setEnabled(false);
            spinnerRemark.setEnabled(false);
        }
        showDataOnScreen(rfidTagId, lepNo, driverName, truckNo, commodity, sourceGrossWeight, previousRmgNo, PreviousRmgNoDesc, wareHouse, batchNumber, berthNumber);
    }

    private void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
        requireActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void hideProgress() {
        progressBar.setVisibility(View.GONE);
        requireActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

}