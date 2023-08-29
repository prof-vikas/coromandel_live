package com.sipl.rfidtagscanner.fragments;

import static android.content.Context.MODE_PRIVATE;
import static com.sipl.rfidtagscanner.utils.Config.DIALOG_ERROR;
import static com.sipl.rfidtagscanner.utils.Config.DIALOG_WARNING;
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
import com.sipl.rfidtagscanner.dto.request.UpdateWareHouseNoRequestDto;
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


public class BWHFragment extends Fragment {

    private final String TAG = "TracingError";

    private final CustomToast customToast = new CustomToast();
    private TextClock tvExitTime, tvEntryTime;
    private EditText edtEntryTime;
    private LinearLayout tvEntryTimeClocKLayout, tvEntryTimeEdtLayout, tvLoadingTimeLayout;
    private ProgressBar progressBar;
    private Spinner spinnerWarehouseNo, spinnerRemark;
    private EditText edtRfidTag, edtLepNo, edtDriverName, edtTruckNumber, edtCommodity, edtGrossWeight, edtPreviousWareHouseNo;
    private Integer selectedLepNoId;

    private String previousRMGCode;
    private String selectedRemarks;
    private Integer selectedRemarksId;
    private String selectedRmgNo;

    //    userDetails
    private String loginUserName;
    private String token;
    private ArrayAdapter<String> remarkAdapter;
    private ArrayAdapter<String> updateWareHouseNoAdapter;
    private String inUnloadingTime = null;


    private String defaultWareHouse;
    private String previousRMG;
    private String remarks;
    private String defaulfWareHouseDesc;

    public BWHFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_b_w_h, container, false);
        spinnerWarehouseNo = view.findViewById(R.id.bwh_spinner_warehouse_no);
        spinnerRemark = view.findViewById(R.id.bwh_spinner_remark);
        tvExitTime = view.findViewById(R.id.bwh_exit_time);
        tvEntryTime = view.findViewById(R.id.bwh_tv_entry_time);
        edtEntryTime = view.findViewById(R.id.edt_entry_time2);
        tvEntryTimeClocKLayout = view.findViewById(R.id.title_entry_time);
        tvEntryTimeEdtLayout = view.findViewById(R.id.bwh_ll_entry_time);
        tvLoadingTimeLayout = view.findViewById(R.id.title_unloading_time);

        edtRfidTag = view.findViewById(R.id.bwh_edt_rfid_tag);
        edtLepNo = view.findViewById(R.id.bwh_edt_lep_number);
        edtDriverName = view.findViewById(R.id.bwh_edt_driver_name);
        edtTruckNumber = view.findViewById(R.id.bwh_edt_truck_no);
        edtCommodity = view.findViewById(R.id.bwh_edt_commodity);
        edtGrossWeight = view.findViewById(R.id.bwh_edt_gross_weight);
        edtPreviousWareHouseNo = view.findViewById(R.id.bwh_edt_previous_ware_house_no);
        progressBar = view.findViewById(R.id.bwh_progressBar);
        Button btnReset = view.findViewById(R.id.bwh_btn_reset);
        Button btnSubmit = view.findViewById(R.id.bwh_btn_submit);

        this.token = ((MainActivity) requireActivity()).getLoginToken();
        this.loginUserName = ((MainActivity) requireActivity()).getLoginUsername();

        setLocalTime();
        getBothraWHDetails();
        updateUIBaseOnVehicleInTime();
//        getWareHouseLocation();
        getWarehouseLocation();
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
        if (!spinnerWarehouseNo.getSelectedItem().toString().equals("Select Warehouse No") && spinnerRemark.getSelectedItem().toString().equals("Select Remarks")) {
            Toast.makeText(getActivity(), "Select remarks", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

/*    private void getWareHouseLocation() {
        progressBar.setVisibility(View.VISIBLE);
        Call<RmgNumberApiResponse> call = RetrofitController.getInstances(requireContext()).getLoadingAdviseApi().
                getAllCoromandelRmgNo("Bearer " + token, "bothra");

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
                                    } else {
                                        Log.i(TAG, "onResponse:  in position else");
                                    }
                                } else {
                                    Log.i(TAG, "onResponse: not contain storage location " + defaulfWareHouseDesc);
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
    }*/

    private void getWarehouseLocation() {
        progressBar.setVisibility(View.VISIBLE);
        Call<RmgNumberApiResponse> call = RetrofitController.getInstances(requireContext())
                .getLoadingAdviseApi()
                .getAllCoromandelRmgNo("Bearer " + token, "bothra");

        call.enqueue(new Callback<RmgNumberApiResponse>() {
            @Override
            public void onResponse(Call<RmgNumberApiResponse> call, Response<RmgNumberApiResponse> response) {
                progressBar.setVisibility(View.GONE);

                if (!response.isSuccessful()) {
                    String errorBody = response.errorBody() != null ? response.errorBody().toString() : "";
                    ((MainActivity) requireActivity()).alert(getActivity(), DIALOG_ERROR, errorBody, null, "OK", false);
                    return;
                }

                Log.i(TAG, "onResponse: getAllUpdateRmgNo : responseCode : " + response.code() + " " + response.raw());

                if (response.isSuccessful()) {
                    HashMap<String, String> hashMapLocationCode = new HashMap<>();
                    List<StorageLocationDto> functionalLocationMasterDtoList = response.body().getStorageLocationDtos();
                    ArrayList<String> arrDestinationLocation = new ArrayList<>();
                    ArrayList<String> arrDestinationLocationDesc = new ArrayList<>();
                    SharedPreferences sp = requireActivity().getSharedPreferences("WareHouseDetails", MODE_PRIVATE);
                    String previousRmgNoDesc = sp.getString("PreviousRmgNoDescSPK", null);
                    String removedPreviousRmgCode = defaultWareHouse + " - " + previousRmgNoDesc;

                    try {
                        if (functionalLocationMasterDtoList == null || functionalLocationMasterDtoList.isEmpty()) {
                            customToast.toastMessage(getActivity(), EMPTY_RMG_NUMBER, 0);
                            return;
                        }

                        for (StorageLocationDto locationDto : functionalLocationMasterDtoList) {
                            String s = locationDto.getStrLocationCode();
                            String strLocationDesc = locationDto.getStrLocationDesc();
                            arrDestinationLocation.add(s);
                            String strLocationDescWithCode = s + " - " + strLocationDesc.toUpperCase();
                            arrDestinationLocationDesc.add(strLocationDescWithCode);
                            hashMapLocationCode.put(strLocationDescWithCode, s);
                        }

                        arrDestinationLocationDesc.remove(removedPreviousRmgCode);
                        arrDestinationLocationDesc.add("Select Warehouse No");

                        updateWareHouseNoAdapter = new ArrayAdapter<String>(getActivity(),
                                android.R.layout.simple_spinner_dropdown_item, arrDestinationLocationDesc) {
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
                                int position = arrDestinationLocationDesc.indexOf(defaulfWareHouseDesc);
                                if (position != -1) {
                                    spinnerWarehouseNo.setBackgroundResource(R.drawable.rectangle_edt_read_only_field);
                                    spinnerWarehouseNo.setSelection(position);
                                    spinnerWarehouseNo.setEnabled(false);
                                } else {
                                    Log.i(TAG, "onResponse: in position else");
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

                                spinnerRemark.setEnabled(!selectedRmgCode.equalsIgnoreCase("Update RMG No"));
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {
                            }
                        });
                    } catch (Exception e) {
                        Log.e(TAG, "Exception in RMG location: " + e.getMessage());
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
    }


    private void getAllRemarks() {
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
                progressBar.setVisibility(View.GONE);
                ((MainActivity) requireActivity()).alert(requireActivity(), "error", t.getMessage(), null, "OK", false);
            }
        });
    }

    private UpdateWareHouseNoRequestDto setData() {
        StorageLocationDto selectedWareHouseNo = null;
        RemarksDto remarksDto = null;
        Integer FLAG = 8;
        AuditEntity auditEntity = new AuditEntity(null, null, loginUserName, String.valueOf(LocalDateTime.now()));
        StorageLocationDto previousWareHouseNo = new StorageLocationDto(defaultWareHouse);
        if (selectedRmgNo != null) {
            if (!selectedRmgNo.equalsIgnoreCase("Select Warehouse No")) {
                selectedWareHouseNo = new StorageLocationDto(selectedRmgNo);
            }
            if (!selectedRemarks.equalsIgnoreCase("Select Remarks")) {
                remarksDto = new RemarksDto(selectedRemarksId);
            }
        } else {
            selectedWareHouseNo = new StorageLocationDto(defaultWareHouse);
        }
        RfidLepIssueDto rfidLepIssueDto = new RfidLepIssueDto(selectedLepNoId);
        if (inUnloadingTime != null) {
            StorageLocationDto previousWareHouseNo2 = new StorageLocationDto(previousRMGCode);
            return new UpdateWareHouseNoRequestDto(auditEntity, previousWareHouseNo2, selectedWareHouseNo, rfidLepIssueDto, remarksDto, FLAG, null, null, inUnloadingTime, LocalDateTime.now().toString());
        } else {
            return new UpdateWareHouseNoRequestDto(auditEntity, previousWareHouseNo, selectedWareHouseNo, rfidLepIssueDto, remarksDto, FLAG, null, null, LocalDateTime.now().toString(), null);
        }
    }


    private void updateWareHouseNo(UpdateWareHouseNoRequestDto updateWareHouseNoRequestDto) {
        Log.i(TAG, new Gson().toJson(updateWareHouseNoRequestDto));
        showProgressBar();
        Call<TransactionsApiResponse> call = RetrofitController.getInstances(requireActivity()).getLoadingAdviseApi().updateWareHouse("Bearer " + token, updateWareHouseNoRequestDto);
        call.enqueue(new Callback<TransactionsApiResponse>() {
            @Override
            public void onResponse(Call<TransactionsApiResponse> call, Response<TransactionsApiResponse> response) {
                if (!response.isSuccessful()) {
                    hideProgressBar();
                    ((MainActivity) requireActivity()).alert(requireActivity(), "error", response.errorBody().toString(), null, "OK", false);
                }
                Log.i(TAG, "onResponse: updateWareHouseNo : " + response.raw());

                if (response.isSuccessful()) {
                    if (response.body().getStatus().equalsIgnoreCase("OK")) {
                        hideProgressBar();
                        ((MainActivity) requireActivity()).alert(requireActivity(), "success", response.body().getMessage(), null, "OK", true);
                    } else {
                        hideProgressBar();
                        ((MainActivity) requireActivity()).alert(requireActivity(), "error", response.body().getMessage(), null, "OK", false);
                    }
                }
            }

            @Override
            public void onFailure(Call<TransactionsApiResponse> call, Throwable t) {
                hideProgressBar();
                ((MainActivity) getActivity()).alert(getActivity(), "error", t.getMessage(), null, "OK", false);
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

    private void saveLoginAdviseData(String rfidTag, String lepNo, String driverName, String truckNo, String commodity, String sourceGrossWeight, String previousRmgNo, String PreviousRmgNoDesc, String wareHouseCode) {
        edtRfidTag.setText(rfidTag);
        edtLepNo.setText(lepNo);
        edtTruckNumber.setText(truckNo);
        edtDriverName.setText(driverName);
        edtCommodity.setText(commodity);
        edtGrossWeight.setText(sourceGrossWeight);

        if (inUnloadingTime != null) {
            edtPreviousWareHouseNo.setText(previousRmgNo + " - " + PreviousRmgNoDesc);
        } else {
            edtPreviousWareHouseNo.setText(wareHouseCode);
        }
    }

    private void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }


    private void getBothraWHDetails() {
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
        String wareHouse = wareHouseCode + " - " + wareHouseDesc;
        String previousRMG = previousRmgNo + " - " + PreviousRmgNoDesc;
        this.defaultWareHouse = wareHouseCode;
        this.remarks = remarks;
        this.previousRMGCode = previousRmgNo;
        this.defaulfWareHouseDesc = wareHouse.toUpperCase();
        this.previousRMG = previousRMG;
        this.inUnloadingTime = inUnloadingTime;
//        if (rfidTagId != null && lepNo != null && driverName != null && truckNo != null && commodity != null && sourceGrossWeight != null && previousRmgNo != null && PreviousRmgNoDesc != null){
        saveLoginAdviseData(rfidTagId,lepNo,driverName,truckNo,commodity,sourceGrossWeight,previousRmgNo, PreviousRmgNoDesc, wareHouse);
//        }else {
//            ((MainActivity) requireActivity()).alert(requireActivity(), DIALOG_WARNING, "Oops ! Encounter Null value, cannot process forward", null, "OK", true);
//        }
    }

}