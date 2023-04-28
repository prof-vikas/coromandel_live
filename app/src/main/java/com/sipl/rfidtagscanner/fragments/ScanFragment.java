package com.sipl.rfidtagscanner.fragments;

import static android.content.Context.MODE_PRIVATE;
import static android.content.Context.VIBRATOR_SERVICE;

import static com.sipl.rfidtagscanner.utils.Config.EMPTY_LEP_NUMBER_LIST;
import static com.sipl.rfidtagscanner.utils.Config.PLANT_BOTHRA;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import com.sipl.rfidtagscanner.MainActivity;
import com.sipl.rfidtagscanner.R;
import com.sipl.rfidtagscanner.RetrofitController;
import com.sipl.rfidtagscanner.RfidHandler;
import com.sipl.rfidtagscanner.dto.dtos.RfidLepIssueDto;
import com.sipl.rfidtagscanner.dto.response.RfidLepApiResponse;
import com.sipl.rfidtagscanner.entites.LoadingAdviseLepDto;
import com.sipl.rfidtagscanner.interf.MyListener;
import com.sipl.rfidtagscanner.interf.RFIDDataModel;
import com.sipl.rfidtagscanner.interf.RfidUiDataDto;
import com.zebra.rfid.api3.TagData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScanFragment extends Fragment implements MyListener {

    ArrayList<String> arrAutoCompleteLepNo;



    private static final String TAG = "ConnectFragment";
    private TextView txtDeviceName, txtSerialNo, txtStatus;
    private LinearLayout llShowDeviceInfo;
    private EditText edtRfidTagId;

    private CheckBox chkShowDeviceInfo;
    private final Observer<RfidUiDataDto> currentRFIDObserver = rfidUiDataDto -> {
        if (rfidUiDataDto.isReaderConnected()) {
            TagData[] tagDataArray = rfidUiDataDto.getTagData();
            if (tagDataArray != null && tagDataArray.length > 0) {
                for (TagData tagData : rfidUiDataDto.getTagData()) {
                    edtRfidTagId.setText(tagData.getTagID());
                }
            }
        }
    };
    private Vibrator vibrator;
    private RfidHandler rfidHandler;

    private String loginUserRole;
    private String loginUserToken;


    public ScanFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scan, container, false);
        txtDeviceName = view.findViewById(R.id.sf_txt_device_name);
        txtSerialNo = view.findViewById(R.id.sf_txt_serial_no);
        txtStatus = view.findViewById(R.id.sf_txt_status);
        edtRfidTagId = view.findViewById(R.id.sf_edt_rfid_tag);
        llShowDeviceInfo = view.findViewById(R.id.rf_ll_show_device_info);
        chkShowDeviceInfo = view.findViewById(R.id.chk_rf_show_device_details);
        this.loginUserRole = ((MainActivity) getActivity()) .getLoginUserRole();
        this.loginUserToken = ((MainActivity) getActivity()) .getLoginToken();

        Button btnVerify = view.findViewById(R.id.sf_btn_verify);

        vibrator = (Vibrator) requireActivity().getSystemService(VIBRATOR_SERVICE);

        rfidHandler = new RfidHandler(requireActivity());
        rfidHandler.InitSDK(this);

        isCheckBoxChecked();


        btnVerify.setOnClickListener(view1 -> {
            vibrate();
//            verifyRFIDTagApi();
        });

        chkShowDeviceInfo.setOnCheckedChangeListener((compoundButton, b) -> {
            if (compoundButton.isChecked()) {
                Log.i(TAG, "onCreateView: " + compoundButton.isChecked());
                llShowDeviceInfo.setVisibility(View.VISIBLE);
                SharedPreferences sp = requireActivity().getSharedPreferences("showDeviceInfo", MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("remember", "true").apply();
            } else {
                llShowDeviceInfo.setVisibility(View.GONE);
                SharedPreferences sp = requireActivity().getSharedPreferences("showDeviceInfo", MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("remember", "false").apply();
            }
        });
        RFIDDataModel.getInstance().getRFIDStatus().observe(getViewLifecycleOwner(), currentRFIDObserver);
        return view;
    }

    private void vibrate() {
        if (Build.VERSION.SDK_INT >= 26) {
            ((Vibrator) requireActivity().getSystemService(VIBRATOR_SERVICE)).vibrate(VibrationEffect.createOneShot(100, 10));
        } else {
            ((Vibrator) requireActivity().getSystemService(VIBRATOR_SERVICE)).vibrate(100);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void isCheckBoxChecked() {
        SharedPreferences sp = requireActivity().getSharedPreferences("showDeviceInfo", MODE_PRIVATE);
        String checkBox = sp.getString("remember", "false");
        Log.i(TAG, "isCheckBoxChecked: " + checkBox);
        if (checkBox.equalsIgnoreCase("false")) {
            llShowDeviceInfo.setVisibility(View.GONE);
            chkShowDeviceInfo.setChecked(false);

        } else {
            llShowDeviceInfo.setVisibility(View.VISIBLE);
            chkShowDeviceInfo.setChecked(true);

        }
    }


    @Override
    public void onPause() {
        rfidHandler.onPause();
        super.onPause();
    }

    @Override
    public void onResume() {
        rfidHandler.onResume();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        rfidHandler.onDestroy();
        vibrator.cancel();
        super.onDestroy();
    }

    @Override
    public void onTextUpdated(String name, String serialNo, String status) {
        try {
            if (serialNo != null && name != null && status != null) {
                txtSerialNo.setText(serialNo);
                txtDeviceName.setText(name);
                txtStatus.setText(status);
            } else {
                Log.i(TAG, "setData: data is null");
            }
        } catch (Exception e) {
            Log.e(TAG, "setdData: ", e);
        }
    }

/*    private void verifyRFIDTagApi() {

        Log.i(TAG, "verifyRFIDTagApi: in api call");
        Call<RFIDResponseDtoDemo> call = RetrofitInstance.getInstance().getControllerApi().getRFIDDetails(edtRfidTagId.getText().toString());
        call.enqueue(new Callback<RFIDResponseDtoDemo>() {
            @Override
            public void onResponse(Call<RFIDResponseDtoDemo> call, Response<RFIDResponseDtoDemo> response) {
                if (!response.isSuccessful()) {
                    Log.i(TAG, "onResponse: responseCode : " + response.code() + response.errorBody() + response.message());
                }
                Log.i(TAG, "onResponse: responseCode : " + response.code() + " responseRaw : " + response.raw());

                if (response.isSuccessful()) {

                    assert response.body() != null;
                    if (response.body().getStatus().equalsIgnoreCase("FOUND")) {
                        storeDataBaseOnUser(response.body().getTAG(), response.body().getDriverName(), response.body().getDriverMobileNo());
                    } else {
                        Log.i(TAG, "onResponse: No rfid tag found ");
                        return;
                    }
                }
            }

            @Override
            public void onFailure(Call<RFIDResponseDtoDemo> call, Throwable t) {
                t.printStackTrace();
                Log.i(TAG, "onFailure: " + t.getMessage());
            }
        });
    }*/

    public void updateButtonStatus(boolean triggerStatus) {
        requireActivity().runOnUiThread(() -> {
            if (triggerStatus) {
                rfidHandler.performInventory();
                return;
            }
            rfidHandler.stopInventory();
        });
    }

    private boolean getAllLepNumber() {
//        progressBar.setVisibility(View.VISIBLE);
        HashMap<String, Integer> hashMapLepNumber = new HashMap<>();
        arrAutoCompleteLepNo = new ArrayList<>();
        try {
           /* if ((loginUserPlantCode.equalsIgnoreCase(PLANT_BOTHRA))) {
                getAllLepNo();
            } else {*/
                Call<RfidLepApiResponse> call = RetrofitController.getInstance().getLoadingAdviseApi().getRfidDetailsByRfidTag("Bearer " + loginUserToken, edtRfidTagId.getText().toString());
                call.enqueue(new Callback<RfidLepApiResponse>() {
                    @Override
                    public void onResponse(Call<RfidLepApiResponse> call, Response<RfidLepApiResponse> response) {
                        if (!response.isSuccessful()) {
//                        alertBuilder(response.errorBody().toString());
//                            progressBar.setVisibility(View.GONE);
                            ((MainActivity) getActivity()).alert(getActivity(), "error", response.errorBody().toString(), null, "OK");
                            return;
                        }

                        if (response.isSuccessful()) {
//                            progressBar.setVisibility(View.GONE);
                            Log.i(TAG, "getAllLepNumber : response.isSuccessful() : " + response.isSuccessful() + " responseCode : " + response.code() + " responseRaw : " + response.raw());
                            RfidLepIssueDto rfidLepIssueDtoList = response.body().getRfidLepIssueDto();
                            RfidLepIssueDto rfidLepIssueDto = response.body().getRfidLepIssueDto();
                            try {
                                String rfidTag = rfidLepIssueDto.getRfidNumber();
                                String lepNo = rfidLepIssueDto.getLepNumber();
                                String lepNoId = String.valueOf(rfidLepIssueDto.getId());
                                String driverName = rfidLepIssueDto.getDriverMaster().getDriverName();
                                String driverMobileNo = rfidLepIssueDto.getDriverMaster().getDriverMobileNo();
                                String driverLicenseNo = rfidLepIssueDto.getDriverMaster().getDriverLicenseNo();
                                String truckNo = rfidLepIssueDto.getDailyTransportReportModule().getTruckNumber();
                                String sapGrNo = String.valueOf(rfidLepIssueDto.getDailyTransportReportModule().getSapGrNumber());
                                String vesselName = rfidLepIssueDto.getDailyTransportReportModule().getVesselName();
                                String truckCapacity = String.valueOf(rfidLepIssueDto.getDailyTransportReportModule().getTruckCapacity());
                                String commodity = rfidLepIssueDto.getDailyTransportReportModule().getCommodity();


                                String strSapGrNo = null, strTruckNo = null, strDriverName = null, strDriverMobileNo = null, strDriverLicenseNo = null, strVesselName = null, strCommodity = null;
                                Integer strTruckCapacity = null;
                                for (int i = 0; i < rfidLepIssueDtoList.size(); i++) {
                                    String strLepNumber = rfidLepIssueDtoList.get(i).getLepNumber();
                                    int id = rfidLepIssueDtoList.get(i).getId();
                                    strDriverName = String.valueOf(rfidLepIssueDtoList.get(i).getDriverMaster().getDriverName());
                                    strDriverMobileNo = String.valueOf(rfidLepIssueDtoList.get(i).getDriverMaster().getDriverMobileNo());
                                    strDriverLicenseNo = String.valueOf(rfidLepIssueDtoList.get(i).getDriverMaster().getDriverLicenseNo());
                                    strSapGrNo = String.valueOf(rfidLepIssueDtoList.get(i).getDailyTransportReportModule().getSapGrNumber());
                                    strTruckNo = String.valueOf(rfidLepIssueDtoList.get(i).getDailyTransportReportModule().getTruckNumber());
                                    strVesselName = String.valueOf(rfidLepIssueDtoList.get(i).getDailyTransportReportModule().getVesselName());
                                    strTruckCapacity = rfidLepIssueDtoList.get(i).getDailyTransportReportModule().getTruckCapacity();
                                    strCommodity = String.valueOf(rfidLepIssueDtoList.get(i).getDailyTransportReportModule().getCommodity());

                                    loadingAdviseLepDto = new LoadingAdviseLepDto(strLepNumber,strDriverName,strDriverMobileNo,strDriverLicenseNo,strSapGrNo,strTruckNo,strVesselName,strTruckCapacity,strCommodity);
                                    loadingAdviseLepDtoList.add(loadingAdviseLepDto);

                                    arrAutoCompleteLepNo.add(strLepNumber);
                                    hashMapLepNumber.put(strLepNumber, id);
                                }
                                arrayAdapterForLepNumber = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, arrAutoCompleteLepNo);
                                autoCompleteLepNumber.setAdapter(arrayAdapterForLepNumber);
                           /*     String finalStrSapGrNo = strSapGrNo;
                                String finalStrTruckNo = strTruckNo;
                                String finalStrDriverName = strDriverName;
                                String finalStrDriverMobileNo = strDriverMobileNo;
                                String finalStrDriverLicenseNo = strDriverLicenseNo;
                                String finalStrVesselName = strVesselName;
                                Integer finalStrTruckCapacity = strTruckCapacity;
                                String finalStrCommodity = strCommodity;*/
                                autoCompleteLepNumber.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                        SelectedLepNo = arrayAdapterForLepNumber.getItem(i);
                                        if (hashMapLepNumber.containsKey(SelectedLepNo)) {
                                            selectedLepNumberId = hashMapLepNumber.get(SelectedLepNo);
                                        }
                                        Log.i(TAG, "onItemClick: SelectedLepNo : " + SelectedLepNo);

                                        if (arrAutoCompleteLepNo.contains(SelectedLepNo)) {
                                            for (LoadingAdviseLepDto d : loadingAdviseLepDtoList) {
                                                Log.i(TAG, "onItemClick: in foreach leoop" );
                                                if (SelectedLepNo.equalsIgnoreCase(d.getLepNumber())) {
                                                    edtSapGrNo.setText(d.getSapGrnNo());
                                                    edtTruckNumber.setText(d.getTruckNo());
                                                    edtDriverName.setText(d.getDriverName());
                                                    edtDriverMobileNo.setText(d.getDriverMobileNo());
                                                    edtDriverLicenseNo.setText(d.getDriverLicenseNo());
                                                    edtVesselName.setText(d.getVesselName());
                                                    edtCommodity.setText(d.getCommodity());
                                                    edtTruckCapacity.setText(String.valueOf(d.getTruckCapacity()));
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
                    public void onFailure(Call<RfidLepApiResponse> call, Throwable t) {
//                        progressBar.setVisibility(View.GONE);
//                    alertBuilder(t.getMessage());
                        ((MainActivity) getActivity()).alert(getActivity(), "error", t.getMessage(), null, "OK");
                        t.printStackTrace();
                    }
                });
//            }
        } catch (Exception e) {
            Log.i(TAG, "getALlLepNumberWithFlag: " + e.getMessage());
        }
        return true;
    }

}