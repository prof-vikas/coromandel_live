package com.sipl.rfidtagscanner.fragments;

import static android.content.Context.MODE_PRIVATE;
import static com.sipl.rfidtagscanner.utils.Config.EMPTY_REMARKS;
import static com.sipl.rfidtagscanner.utils.Config.EMPTY_WAREHOUSE_NUMBER;

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
import com.sipl.rfidtagscanner.dto.request.UpdateWareHouseNoRequestDto;
import com.sipl.rfidtagscanner.dto.response.RemarkApiResponse;
import com.sipl.rfidtagscanner.dto.response.RmgNumberApiResponse;
import com.sipl.rfidtagscanner.dto.response.TransactionsApiResponse;
import com.sipl.rfidtagscanner.entites.AuditEntity;
import com.sipl.rfidtagscanner.utils.CustomToast;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class BWHFragment extends Fragment {

    private final String TAG = "TracingError";

    private final CustomToast customToast = new CustomToast();
    private TextClock tvClock, tvEntryTime, tvExitTime;
    private EditText edtEntryTime;
    private LinearLayout tvEntryTimeClocKLayout, tvEntryTimeEdtLayout, tvLoadingTimeLayout, tvExitTimeLayout;
    private ProgressBar progressBar;
    private Spinner spinnerWarehouseNo, spinnerRemark;
    private EditText edtRfidTag, edtLepNo, edtDriverName, edtTruckNumber, edtCommodity, edtGrossWeight, edtPreviousWareHouseNo;
    private Integer selectedLepNoId;
    private String selectedWhNo;
    private Boolean isSelectedWhHasWB;

    //    userDetails
    private String loginUserName;
    private String token;
    private String selectedRemarks;
    private Integer selectedRemarksId;
    private ArrayAdapter<String> remarkAdapter;
    private ArrayAdapter<String> updateWareHouseNoAdapter;
    private String previousWarehouseCode;
    private String inUnloadingTime = null;
    private String outUnloadingTime = null;

    public BWHFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_b_w_h, container, false);
        spinnerWarehouseNo = view.findViewById(R.id.bwh_spinner_warehouse_no);
        spinnerRemark = view.findViewById(R.id.bwh_spinner_remark);
        tvClock = view.findViewById(R.id.bwh_tv_clock);
        tvEntryTime = view.findViewById(R.id.bwh_tv_entry_time);
        tvExitTime = view.findViewById(R.id.bwh_tv_exit_time);
        edtEntryTime = view.findViewById(R.id.edt_entry_time2);
        tvEntryTimeClocKLayout = view.findViewById(R.id.title_entry_time);
        tvEntryTimeEdtLayout = view.findViewById(R.id.title_entry_time2);
        tvLoadingTimeLayout = view.findViewById(R.id.title_unloading_time);
        tvExitTimeLayout = view.findViewById(R.id.title_exit_time);

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

        setTvClock();
        getLoadingAdviseDetails();
        updateUIBaseOnWareHouseLocation();
        callOnCreateApi();

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

    private void callOnCreateApi() {
        getWareHouseLocation();
        getAllRemarks();
    }

    private boolean validateLoadingData() {
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
        if (edtPreviousWareHouseNo.length() == 0) {
            edtPreviousWareHouseNo.setError("This field is required");
            return false;
        }
        if (!spinnerWarehouseNo.getSelectedItem().toString().equals("Select Warehouse No") && spinnerRemark.getSelectedItem().toString().equals("Select Remarks")) {
            Toast.makeText(getActivity(), "Select remarks", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void getWareHouseLocation() {
        showProgressBar();
        Call<RmgNumberApiResponse> call = RetrofitController.getInstances(requireActivity()).getLoadingAdviseApi().
                getAllWareHouse("Bearer " + token, "bothra");
        call.enqueue(new Callback<RmgNumberApiResponse>() {
            @Override
            public void onResponse(Call<RmgNumberApiResponse> call, Response<RmgNumberApiResponse> response) {
                if (!response.isSuccessful()) {
                    hideProgressBar();
                    ((MainActivity) getActivity()).alert(getActivity(), "error", response.errorBody().toString(), null, "OK");
                    return;
                }
                if (response.isSuccessful()) {
                    List<StorageLocationDto> listWareHouse = response.body().getStorageLocationDtos();
                    HashMap<String, String> hashMapLocationCode = new HashMap<>();
                    SharedPreferences sp = requireActivity().getSharedPreferences("WareHouseDetails", MODE_PRIVATE);
                    String PreviousRmgNoDesc = sp.getString("PreviousRmgNoDescSPK", null);
                    String PreviousRMGRemoved = previousWarehouseCode + " - " + PreviousRmgNoDesc.toLowerCase();
                    HashMap<String, Boolean> HmForWBAvailability = new HashMap<>();
                    ArrayList<String> arrDestinationLocation = new ArrayList<>();
                    ArrayList<String> arrDestinationLocationDesc = new ArrayList<>();
                    try {
                        if (listWareHouse == null || listWareHouse.isEmpty()) {
                            customToast.toastMessage(getActivity(), EMPTY_WAREHOUSE_NUMBER, 0);
                            return;
                        }
                        for (int i = 0; i < listWareHouse.size(); i++) {
                            String s = listWareHouse.get(i).getStrLocationCode();
                            String strLocationDesc = listWareHouse.get(i).getStrLocationDesc();
                            Boolean isWeighBrige = listWareHouse.get(i).getWbAvailable();

                            String strLocationDescWithCode = s + " - " + strLocationDesc.toLowerCase();
                            arrDestinationLocationDesc.add(strLocationDescWithCode);
                            hashMapLocationCode.put(strLocationDescWithCode, s);
                            HmForWBAvailability.put(strLocationDescWithCode, isWeighBrige);
                            arrDestinationLocation.add(s);
                        }
                        if (arrDestinationLocationDesc.contains(PreviousRMGRemoved)) {
                            arrDestinationLocationDesc.remove(PreviousRMGRemoved);
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
                        spinnerWarehouseNo.setSelection(updateWareHouseNoAdapter.getCount());

                        spinnerWarehouseNo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                String selectedRmgCode = adapterView.getSelectedItem().toString();

                                if (hashMapLocationCode.containsKey(selectedRmgCode)) {
                                    selectedWhNo = hashMapLocationCode.get(selectedRmgCode);
                                }
                                if (HmForWBAvailability.containsKey(selectedRmgCode)) {
                                    isSelectedWhHasWB = HmForWBAvailability.get(selectedRmgCode);
                                }
                                if (!selectedRmgCode.equalsIgnoreCase("Select Warehouse No")) {
                                    spinnerRemark.setEnabled(true);
                                    spinnerRemark.setClickable(true);
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
                ((MainActivity) getActivity()).alert(requireActivity(), "error", t.getMessage(), null, "OK");
            }
        });
    }

    private void getAllRemarks() {
        showProgressBar();
        Call<RemarkApiResponse> call = RetrofitController.getInstances(requireActivity()).getLoadingAdviseApi().
                getAllBothraRemark("Bearer " + token);
        call.enqueue(new Callback<RemarkApiResponse>() {
            @Override
            public void onResponse(Call<RemarkApiResponse> call, Response<RemarkApiResponse> response) {
                if (!response.isSuccessful()) {
                    hideProgressBar();
                    ((MainActivity) getActivity()).alert(getActivity(), "error", response.errorBody().toString(), null, "OK");
                    return;
                }
                Log.i(TAG, "onResponse: getAllBothraRemark : responseCode : " + response.code());

                if (response.isSuccessful()) {
                    hideProgressBar();
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
                        spinnerRemark.setAdapter(remarkAdapter);
                        spinnerRemark.setSelection(remarkAdapter.getCount());
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
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<RemarkApiResponse> call, Throwable t) {
                hideProgressBar();
                ((MainActivity) getActivity()).alert(requireActivity(), "error", t.getMessage(), null, "OK");
            }
        });
    }

    private UpdateWareHouseNoRequestDto setData() {
        UpdateWareHouseNoRequestDto updateWareHouseNoRequestDto;
        SharedPreferences sp = requireActivity().getSharedPreferences("WareHouseDetails", MODE_PRIVATE);
        String weighbridgeAvailable = sp.getString("isWeighbridgeAvailableSPK", null);
        Boolean isWeighbridgeAvailable = Boolean.valueOf(weighbridgeAvailable);
        int callFrom = sp.getInt("callFromSPK", 0);

        StorageLocationDto selectedWareHouseNo = null;
        RemarksDto remarksDto = null;
        Integer FLAG = 8;
        AuditEntity auditEntity = new AuditEntity(null, null, loginUserName, String.valueOf(LocalDateTime.now()));
        StorageLocationDto previousWareHouseNo = new StorageLocationDto(previousWarehouseCode, isWeighbridgeAvailable);
        if (selectedWhNo != null) {
            if (!selectedWhNo.equals("Select Warehouse No")) {
                selectedWareHouseNo = new StorageLocationDto(selectedWhNo, isSelectedWhHasWB);
            }
            if (!selectedRemarks.equalsIgnoreCase("Select Remarks")) {
                remarksDto = new RemarksDto(selectedRemarksId);
            }
        }
        RfidLepIssueDto rfidLepIssueDto = new RfidLepIssueDto(selectedLepNoId);
 /*       if (weighbridgeAvailable.equalsIgnoreCase("true")) {
            updateWareHouseNoRequestDto = new UpdateWareHouseNoRequestDto(auditEntity, previousWareHouseNo, selectedWareHouseNo, rfidLepIssueDto, remarksDto, FLAG, null, null);
        } else {
            if (callFrom == 1) {
                updateWareHouseNoRequestDto = new UpdateWareHouseNoRequestDto(auditEntity, previousWareHouseNo, selectedWareHouseNo, rfidLepIssueDto, remarksDto, FLAG, null, String.valueOf(LocalDateTime.now()));
            } else {
                updateWareHouseNoRequestDto = new UpdateWareHouseNoRequestDto(auditEntity, previousWareHouseNo, selectedWareHouseNo, rfidLepIssueDto, remarksDto, FLAG, String.valueOf(LocalDateTime.now()), null);
            }
        }
        return updateWareHouseNoRequestDto;*/
        Log.i(TAG, "setData: inUnloadingTime : " + inUnloadingTime);
        if (inUnloadingTime != null) {
            return new UpdateWareHouseNoRequestDto(auditEntity, previousWareHouseNo, selectedWareHouseNo, rfidLepIssueDto, remarksDto, FLAG,null,null, inUnloadingTime, LocalDateTime.now().toString());
        } else {
            return new UpdateWareHouseNoRequestDto(auditEntity, previousWareHouseNo, selectedWareHouseNo, rfidLepIssueDto, remarksDto, FLAG, null, null, LocalDateTime.now().toString(), null);
        }

    }


  /*  private UpdateRmgRequestDto setData() {
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
        if (inUnloadingTime != null) {
            return new UpdateRmgRequestDto(auditEntity, previousWareHouseNo, selectedWareHouseNo, rfidLepIssueDto, remarksDto, FLAG, inUnloadingTime, LocalDateTime.now().toString());
        } else {
            return new UpdateRmgRequestDto(auditEntity, previousWareHouseNo, selectedWareHouseNo, rfidLepIssueDto, remarksDto, FLAG, LocalDateTime.now().toString(), null);
        }
    }*/

    private void updateWareHouseNo(UpdateWareHouseNoRequestDto updateWareHouseNoRequestDto) {
        Log.i(TAG, new Gson().toJson(updateWareHouseNoRequestDto));
        showProgressBar();
        Call<TransactionsApiResponse> call = RetrofitController.getInstances(requireActivity()).getLoadingAdviseApi().updateWareHouse("Bearer " + token, updateWareHouseNoRequestDto);
        call.enqueue(new Callback<TransactionsApiResponse>() {
            @Override
            public void onResponse(Call<TransactionsApiResponse> call, Response<TransactionsApiResponse> response) {
                if (!response.isSuccessful()) {
                   hideProgressBar();
                    ((MainActivity) requireActivity()).alert(requireActivity(), "error", response.errorBody().toString(), null, "OK");
                }
                Log.i(TAG, "onResponse: updateWareHouseNo : " + response.raw());

                if (response.body().getStatus().equalsIgnoreCase("OK")) {
                    hideProgressBar();
                    ((MainActivity) requireActivity()).alert(requireActivity(), "success", response.body().getMessage(), null, "OK");
                    resetFields();
                } else {
                    hideProgressBar();
                    ((MainActivity) requireActivity()).alert(requireActivity(), "error", response.body().getMessage(), null, "OK");
                }
            }

            @Override
            public void onFailure(Call<TransactionsApiResponse> call, Throwable t) {
                hideProgressBar();
                ((MainActivity) getActivity()).alert(getActivity(), "error", t.getMessage(), null, "OK");
                t.printStackTrace();
            }
        });
    }

    private void setTvClock() {
        try {
            tvClock.setFormat24Hour("dd-MM-yy hh:mm a");
            tvEntryTime.setFormat24Hour("dd-MM-yy hh:mm a");
            tvExitTime.setFormat24Hour("dd-MM-yy hh:mm a");
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Exception in setTvClock : e.getMessage() : " + e.getMessage());
        }
    }

    private void getLoadingAdviseDetails() {
        SharedPreferences sp = requireActivity().getSharedPreferences("WareHouseDetails", MODE_PRIVATE);
        this.selectedLepNoId = Integer.valueOf(sp.getString("lepNoIdSPK", null));
        String rfidTagId = sp.getString("rfidTagSPK", null);
        String lepNo = sp.getString("lepNoSPK", null);
        String driverName = sp.getString("driverNameSPK", null);
        String truckNo = sp.getString("truckNoSPK", null);
        String commodity = sp.getString("commoditySPK", null);
        String sourceGrossWeight = sp.getString("sourceGrossWeightSPK", null);
        String previousRmgNo = sp.getString("previousRmgNoSPK", null);
        String inUnloadingTime = sp.getString("inUnloadingTimeSPK", null);
        String outUnloadingTime = sp.getString("outUnloadingTimeSPK", null);
        this.inUnloadingTime = inUnloadingTime;
        this.outUnloadingTime = outUnloadingTime;
        this.previousWarehouseCode = previousRmgNo;
        String PreviousRmgNoDesc = sp.getString("PreviousRmgNoDescSPK", null);

        saveLoginAdviseData(rfidTagId, lepNo, driverName, truckNo, commodity, sourceGrossWeight, previousRmgNo, PreviousRmgNoDesc);
    }

    //    after new changes
    private void updateUIBaseOnWareHouseLocation() {
        SharedPreferences sp = requireActivity().getSharedPreferences("WareHouseDetails", MODE_PRIVATE);
        String inUnloadingTime = sp.getString("inUnloadingTimeSPK", null);

        if (inUnloadingTime != null){
            tvEntryTimeClocKLayout.setVisibility(View.GONE);
            tvEntryTimeEdtLayout.setVisibility(View.VISIBLE);
            edtEntryTime.setText(inUnloadingTime);
            tvLoadingTimeLayout.setVisibility(View.VISIBLE);
//            tvExitTimeLayout.setVisibility(View.VISIBLE);
        }else {
            tvEntryTimeClocKLayout.setVisibility(View.VISIBLE);
        }
    }

    private void saveLoginAdviseData(String rfidTag, String lepNo, String driverName, String truckNo, String commodity, String sourceGrossWeight, String previousRmgNo, String PreviousRmgNoDesc) {
        edtRfidTag.setText(rfidTag);
        edtLepNo.setText(lepNo);
        edtTruckNumber.setText(truckNo);
        edtDriverName.setText(driverName);
        edtCommodity.setText(commodity);
        edtGrossWeight.setText(sourceGrossWeight);
        edtPreviousWareHouseNo.setText(previousRmgNo + " - " + PreviousRmgNoDesc);
    }

    //before method 22 june version 1.0.2
   /* private void updateUIBaseOnWareHouseLocation() {
        SharedPreferences sp = requireActivity().getSharedPreferences("WareHouseDetails", MODE_PRIVATE);
        String isWeighbridgeAvailable = sp.getString("isWeighbridgeAvailableSPK", null);
        String vehicleInTime = sp.getString("vehicleInTimeSPK", null);
        int callFrom = sp.getInt("callFromSPK", 0);
        if (isWeighbridgeAvailable != null) {
            if (isWeighbridgeAvailable.equalsIgnoreCase("false") && callFrom == 1) {
                tvEntryTimeClocKLayout.setVisibility(View.VISIBLE);
            } else if (isWeighbridgeAvailable.equalsIgnoreCase("false") && callFrom == 2) {
                tvEntryTimeClocKLayout.setVisibility(View.GONE);
                tvEntryTimeEdtLayout.setVisibility(View.VISIBLE);
                edtEntryTime.setText(vehicleInTime);
                tvLoadingTimeLayout.setVisibility(View.VISIBLE);
                tvExitTimeLayout.setVisibility(View.VISIBLE);
            }
        } else {
            Log.i(TAG, "updateUIBaseOnWareHouseLocation: weidgeBride is true");
        }
    }*/



    private void showProgressBar(){
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar(){
        progressBar.setVisibility(View.GONE);
    }

}