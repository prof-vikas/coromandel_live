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
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
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
import com.sipl.rfidtagscanner.entites.BothraWHDto;
import com.sipl.rfidtagscanner.utils.CustomToast;
import com.sipl.rfidtagscanner.utils.Helper;

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
    BothraWHDto bothraWHDto;
    List<BothraWHDto> bothraWHDtoList = new ArrayList<>();
    ArrayList<String> arrAutoCompleteLepNo;
    private TextClock tvClock;
    private ProgressBar progressBar;
    private TextView tvLepNumber;
    private Spinner spinnerWarehouseNo, spinnerRemark;
    private EditText edtRfidTag, edtLepNo, edtDriverName, edtTruckNumber, edtCommodity, edtGrossWeight, edtPreviousWareHouseNo;
    private Button btnSubmit, btnReset;
    private Integer selectedLepNumberId;
    private String selectedWareHouseNumber;

    //    userDetails
    private String loginUserName;
    private String token;
    private String loginUserPlantCode;
    private String loginUserSourceCode;
    private String loginUserSourceCodeDesc;

    private String selectedRemarks;
    private Integer selectedRemarksId;
    private ArrayAdapter<String> remarkAdapter;
    private ArrayAdapter<String> updateWareHouseNoAdapter;
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

        edtRfidTag = view.findViewById(R.id.bwh_edt_rfid_tag);
        edtLepNo = view.findViewById(R.id.bwh_edt_lep_number);
        edtDriverName = view.findViewById(R.id.bwh_edt_driver_name);
        edtTruckNumber = view.findViewById(R.id.bwh_edt_truck_no);
        edtCommodity = view.findViewById(R.id.bwh_edt_commodity);
        edtGrossWeight = view.findViewById(R.id.bwh_edt_gross_weight);
        edtPreviousWareHouseNo = view.findViewById(R.id.bwh_edt_previous_ware_house_no);
        progressBar = view.findViewById(R.id.bwh_progressBar);
        btnReset = view.findViewById(R.id.bwh_btn_reset);
        btnSubmit = view.findViewById(R.id.bwh_btn_submit);

        this.token = ((MainActivity) getActivity()).getLoginToken();
        this.loginUserName = ((MainActivity) getActivity()).getLoginUsername();
        this.loginUserPlantCode = ((MainActivity) getActivity()).getLoginUserPlantCode();
        this.loginUserSourceCode = ((MainActivity) getActivity()).getLoginUserStorageCode();
        this.loginUserSourceCodeDesc = ((MainActivity) getActivity()).getLoginUserSourceLocationDesc();


        setTvClock();
        getLoadingAdviseDetails();
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
        ((MainActivity) requireActivity()).loadFragment(new ScanFragment(), 1);
    }


    private void callOnCreateApi() {
        getWareHouseStorage();
        getAllBothraRemark();
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

    private boolean getWareHouseStorage() {
        Log.i("getWareHouseStorage", "getAllWareHouse: ()");
        progressBar.setVisibility(View.VISIBLE);
        Call<RmgNumberApiResponse> call = RetrofitController.getInstance().getLoadingAdviseApi().
                getAllWareHouse("Bearer " + token, "bothra");

        call.enqueue(new Callback<RmgNumberApiResponse>() {
            @Override
            public void onResponse(Call<RmgNumberApiResponse> call, Response<RmgNumberApiResponse> response) {
                if (!response.isSuccessful()) {
                    progressBar.setVisibility(View.GONE);
                    ((MainActivity) getActivity()).alert(getActivity(), "error", response.errorBody().toString(), null, "OK");
                    return;
                }
                Log.i(TAG, "onResponse: getAllWareHouse : responseCode : " + response.code());
                if (response.isSuccessful()) {
                    Log.i(TAG, "onResponse: getAllWareHouse " + response.code());
                    List<StorageLocationDto> functionalLocationMasterDtoList = response.body().getStorageLocationDtos();
                    HashMap<String, String> hashMapLocationCode = new HashMap<>();
                    HashMap<String, Integer> hashMapUpdateRmgNo = new HashMap<>();
                    ArrayList<String> arrDestinationLocation = new ArrayList<>();
                    ArrayList<String> arrDestinationLocationDesc = new ArrayList<>();
                    try {
                        if (functionalLocationMasterDtoList == null || functionalLocationMasterDtoList.isEmpty()) {
                            customToast.toastMessage(getActivity(), EMPTY_WAREHOUSE_NUMBER, 0);
                            return;
                        }
                        for (int i = 0; i < functionalLocationMasterDtoList.size(); i++) {
                            String s = functionalLocationMasterDtoList.get(i).getStrLocationCode();
                            String strLocationDesc = functionalLocationMasterDtoList.get(i).getStrLocationDesc();
//                        int id = functionalLocationMasterDtoList.get(i).getId();
//                        hashMapUpdateRmgNo.put(s, id);
                            String strLocationDescWithCode = s + " - " + strLocationDesc.toLowerCase();
                            arrDestinationLocationDesc.add(strLocationDescWithCode);
                            hashMapLocationCode.put(strLocationDescWithCode, s);
                            arrDestinationLocation.add(s);
                        }
//                    arrDestinationLocation.add("Select Warehouse No");
                        arrDestinationLocationDesc.add("Select Warehouse No");


                       /* for (String a : arrDestinationLocationDesc) {
                            Log.i(TAG, "onResponse: " + a.toLowerCase());
                        }

                        for (Map.Entry<String, String> entry : hashMapLocationCode.entrySet()) {
                            String key = entry.getKey();
                            String value = entry.getValue();
                            Log.i(TAG, "onResponse: hashMapLocationCode : key : " + key + " --- Value : " + value);
                        }
*/
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
                            /*if (hashMapUpdateRmgNo.containsKey(selectedWareHouseNumber)) {
                                selectedWareHouseNumberId = hashMapUpdateRmgNo.get(selectedWareHouseNumber);
                                Log.i(TAG, "onItemSelected: selectedBothraSupervisorId " + selectedWareHouseNumberId);
                            }*/

                                Log.i(TAG, "onItemSelected: selectedRmgNo :" + selectedRmgCode);
                                if (hashMapLocationCode.containsKey(selectedRmgCode)) {
                                    selectedWareHouseNumber = hashMapLocationCode.get(selectedRmgCode);
                                    Log.i(TAG, "onItemSelected: selectedRmgNo : " + selectedWareHouseNumber);
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
//                alertBuilder(t.getMessage());
                progressBar.setVisibility(View.GONE);
                ((MainActivity) getActivity()).alert(getActivity(), "error", t.getMessage(), null, "OK");
            }
        });
        return true;
    }

    private boolean getAllBothraRemark() {
        progressBar.setVisibility(View.VISIBLE);
        Call<RemarkApiResponse> call = RetrofitController.getInstance().getLoadingAdviseApi().
                getAllBothraRemark("Bearer " + token);
        call.enqueue(new Callback<RemarkApiResponse>() {
            @Override
            public void onResponse(Call<RemarkApiResponse> call, Response<RemarkApiResponse> response) {
                if (!response.isSuccessful()) {
                    progressBar.setVisibility(View.GONE);
//                    alertBuilder(response.errorBody().toString());
                    ((MainActivity) getActivity()).alert(getActivity(), "error", response.errorBody().toString(), null, "OK");
                    return;
                }
                Log.i(TAG, "onResponse: getAllBothraRemark : responseCode : " + response.code());

                if (response.isSuccessful()) {
                    progressBar.setVisibility(View.GONE);
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
                                    Log.i(TAG, "onItemSelected: Selected Remarks Id " + selectedRemarksId);
                                }
                               /* if (selectedRemarks.equalsIgnoreCase("Update RMG No")) {
                                    spinnerRemark.setEnabled(false);
                                    spinnerRemark.setClickable(false);
                                    spinnerRemark.setFocusable(false);
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
//                alertBuilder(t.getMessage());
                progressBar.setVisibility(View.GONE);
                ((MainActivity) getActivity()).alert(getActivity(), "error", t.getMessage(), null, "OK");
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
        if (selectedWareHouseNumber != null) {
            Log.i(TAG, "setData: " + selectedWareHouseNumber);
            if (!selectedWareHouseNumber.equals("Select Warehouse No")) {
                selectedWareHouseNo = new StorageLocationDto(selectedWareHouseNumber);
            }
            if (!selectedRemarks.equalsIgnoreCase("Select Remarks")) {
                remarksDto = new RemarksDto(selectedRemarksId);
            }
        }
        RfidLepIssueDto rfidLepIssueDto = new RfidLepIssueDto(selectedLepNumberId);
        UpdateWareHouseNoRequestDto updateWareHouseNoRequestDto = new UpdateWareHouseNoRequestDto(auditEntity, previousWareHouseNo, selectedWareHouseNo, rfidLepIssueDto, remarksDto, FLAG);
        return updateWareHouseNoRequestDto;
    }

    private void updateWareHouseNo(UpdateWareHouseNoRequestDto updateWareHouseNoRequestDto) {
        Log.i(TAG, new Gson().toJson(updateWareHouseNoRequestDto).toString());
        progressBar.setVisibility(View.VISIBLE);
        Call<TransactionsApiResponse> call = RetrofitController.getInstance().getLoadingAdviseApi().updateWareHouse("Bearer " + token, updateWareHouseNoRequestDto);
        call.enqueue(new Callback<TransactionsApiResponse>() {
            @Override
            public void onResponse(Call<TransactionsApiResponse> call, Response<TransactionsApiResponse> response) {
                if (!response.isSuccessful()) {
//                    alertBuilder(response.errorBody().toString());
                    progressBar.setVisibility(View.GONE);
                    ((MainActivity) getActivity()).alert(getActivity(), "error", response.errorBody().toString(), null, "OK");
                }
                Log.i(TAG, "onResponse: code" + response.code() + "status : " + response.body().getStatus());
             /*   if (response.isSuccessful()) {
                    progressBar.setVisibility(View.GONE);
                    ((MainActivity) getActivity()).alert(getActivity(), "success", response.body().getMessage(), null, "OK");
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
//                alertBuilder(t.getMessage());
                ((MainActivity) getActivity()).alert(getActivity(), "error", t.getMessage(), null, "OK");
                t.printStackTrace();
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

    private void getLoadingAdviseDetails() {
        SharedPreferences sp = requireActivity().getSharedPreferences("WareHouseDetails", MODE_PRIVATE);
//        this.selectedLepNumberId = Integer.valueOf(sp.getString("lepNoIdSPK", null));
        String rfidTagId = sp.getString("rfidTagSPK", null);
        String lepNo = sp.getString("lepNoSPK", null);
        String driverName = sp.getString("driverNameSPK", null);
        String truckNo = sp.getString("truckNoSPK", null);
        String commodity = sp.getString("commoditySPK", null);
        String sourceGrossWeight = sp.getString("sourceGrossWeightSPK", null);
        String previousRmgNo = sp.getString("previousRmgNoSPK", null);
        this.previousWarehouseCode = previousRmgNo;
        String PreviousRmgNoDesc = sp.getString("PreviousRmgNoDescSPK", null);

        saveLoginAdviseData(rfidTagId, lepNo, driverName, truckNo, commodity, sourceGrossWeight, previousRmgNo, PreviousRmgNoDesc);
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
}