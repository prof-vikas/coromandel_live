package com.sipl.rfidtagscanner.fragments;

import static android.content.Context.MODE_PRIVATE;
import static android.content.Context.VIBRATOR_SERVICE;
import static com.sipl.rfidtagscanner.utils.Config.PLANT_BOTHRA;
import static com.sipl.rfidtagscanner.utils.Config.ROLES_BWH;
import static com.sipl.rfidtagscanner.utils.Config.ROLES_CWH;
import static com.sipl.rfidtagscanner.utils.Config.ROLES_LAO;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import com.sipl.rfidtagscanner.LoginActivity;
import com.sipl.rfidtagscanner.MainActivity;
import com.sipl.rfidtagscanner.R;
import com.sipl.rfidtagscanner.RetrofitController;
import com.sipl.rfidtagscanner.RfidHandler;
import com.sipl.rfidtagscanner.dto.dtos.RfidLepIssueDto;
import com.sipl.rfidtagscanner.dto.dtos.TransactionsDto;
import com.sipl.rfidtagscanner.dto.response.RfidLepApiResponse;
import com.sipl.rfidtagscanner.dto.response.TransactionsApiResponse;
import com.sipl.rfidtagscanner.interf.MyListener;
import com.sipl.rfidtagscanner.interf.RFIDDataModel;
import com.sipl.rfidtagscanner.interf.RfidUiDataDto;
import com.zebra.rfid.api3.TagData;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScanFragment extends Fragment implements MyListener {


    private static final String TAG = "ConnectFragment";
    private String loginUserPlantCode;
    private ProgressBar progressBar;
    private TextView txtDeviceName, txtSerialNo, txtStatus;
    private LinearLayout llShowDeviceInfo;
    private EditText edtRfidTagId;
    private final Observer<RfidUiDataDto> currentRFIDObserver = rfidUiDataDto -> {
        if (rfidUiDataDto.isReaderConnected()) {
            TagData[] tagDataArray = rfidUiDataDto.getTagData();
            if (tagDataArray != null && tagDataArray.length > 0) {
                for (TagData tagData : rfidUiDataDto.getTagData()) {
                    edtRfidTagId.setText(tagData.getTagID());
                    Log.i(TAG, "tag : " + edtRfidTagId.getText().toString());
                }
            }
        }
    };
    private CheckBox chkShowDeviceInfo;
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
        progressBar = view.findViewById(R.id.login_progressBar);
        this.loginUserRole = ((MainActivity) getActivity()).getLoginUserRole();
        this.loginUserToken = ((MainActivity) getActivity()).getLoginToken();
        this.loginUserPlantCode = ((MainActivity) getActivity()).getLoginUserPlantCode();

        Button btnVerify = view.findViewById(R.id.sf_btn_verify);

        rfidHandler = new RfidHandler(requireActivity());
        rfidHandler.InitSDK(this);

        isCheckBoxChecked();


        btnVerify.setOnClickListener(view1 -> {
            vibrate();
            RfidDetailsLoadingAdvise();
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

    public void updateButtonStatus(boolean triggerStatus) {
        requireActivity().runOnUiThread(() -> {
            if (triggerStatus) {
                rfidHandler.performInventory();
                return;
            }
            rfidHandler.stopInventory();
        });
    }

    private void getRfidTagDetailCoromandelLA() {
        progressBar.setVisibility(View.VISIBLE);
        try {
            Call<RfidLepApiResponse> call = RetrofitController.getInstance().getLoadingAdviseApi().getRfidTagDetailCoromandelLA("Bearer " + loginUserToken, edtRfidTagId.getText().toString());
            call.enqueue(new Callback<RfidLepApiResponse>() {
                @Override
                public void onResponse(Call<RfidLepApiResponse> call, Response<RfidLepApiResponse> response) {
                    if (!response.isSuccessful()) {
                        progressBar.setVisibility(View.GONE);
                        ((MainActivity) getActivity()).alert(getActivity(), "error", response.errorBody().toString(), null, "OK");
                        return;
                    }

                    if (response.body().getStatus().equalsIgnoreCase("FOUND")) {
                        vibrate();
                        progressBar.setVisibility(View.GONE);
                        Log.i(TAG, "getAllLepNumber : response.isSuccessful() : " + response.isSuccessful() + " responseCode : " + response.code() + " responseRaw : " + response.raw());
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

                            String role = ((MainActivity) requireActivity()).getLoginUserRole();
                            if (role.equalsIgnoreCase(ROLES_LAO)) {
                                Log.i(TAG, "onResponse: before share pref");
                                saveLoadingAdivseDetails(rfidTag, lepNo, lepNoId, driverName, driverMobileNo, driverLicenseNo, truckNo, sapGrNo, vesselName, truckCapacity, commodity);
                                ((MainActivity) requireActivity()).loadFragment(new LoadingAdviseFragment(), 1);
                            }

                        } catch (Exception e) {
                            e.getMessage();
                            return;
                        }
                    }
                }

                @Override
                public void onFailure(Call<RfidLepApiResponse> call, Throwable t) {
                    progressBar.setVisibility(View.GONE);
                    ((MainActivity) getActivity()).alert(getActivity(), "error", t.getMessage(), null, "OK");
                    t.printStackTrace();
                }
            });

        } catch (Exception e) {
            Log.i(TAG, "getALlLepNumberWithFlag: " + e.getMessage());
        }
    }

    private void saveLoadingAdivseDetails(String rfidTag, String lepNo, String lepNoId, String driverName, String driverMobileNo, String driverLicenseNo, String truckNo, String sapGrNo, String vesselName, String truckCapacity, String commodity) {
        SharedPreferences sp = requireActivity().getSharedPreferences("loadingAdviceDetails", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("rfidTagSPK", rfidTag).apply();
        editor.putString("lepNoSPK", lepNo).apply();
        editor.putString("lepNoIdSPK", lepNoId).apply();
        editor.putString("driverNameSPK", driverName).apply();
        editor.putString("driverMobileNoSPK", driverMobileNo).apply();
        editor.putString("driverLicenseNoSPK", driverLicenseNo).apply();
        editor.putString("truckNoSPK", truckNo).apply();
        editor.putString("sapGrNoSPK", sapGrNo).apply();
        editor.putString("vesselNameSPK", vesselName).apply();
        editor.putString("truckCapacitySPK", truckCapacity).apply();
        editor.putString("commoditySPK", commodity).apply();
        editor.apply();
    }

    private void saveWareHouseDetials(String lepNo, String lepNoId, String rfidTag, String driverName, String truckNo, String commodity, String GrossWeight, String previousRmgNo, String PreviousRmgNoDesc, String sourceGrossWeight) {
        SharedPreferences sp = requireActivity().getSharedPreferences("WareHouseDetails", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("rfidTagSPK", rfidTag).apply();
        editor.putString("lepNoSPK", lepNo).apply();
        editor.putString("lepNoIdSPK", lepNoId).apply();
        editor.putString("driverNameSPK", driverName).apply();
        editor.putString("truckNoSPK", truckNo).apply();
        editor.putString("commoditySPK", commodity).apply();
        editor.putString("GrossWeightSPK", GrossWeight).apply();
        editor.putString("previousRmgNoSPK", previousRmgNo).apply();
        editor.putString("PreviousRmgNoDescSPK", PreviousRmgNoDesc).apply();
        editor.putString("sourceGrossWeightSPK", sourceGrossWeight).apply();
        editor.apply();
    }

    private void getWareHouseDetails() {
        progressBar.setVisibility(View.VISIBLE);
        try {
            Call<TransactionsApiResponse> call = null;
            if (loginUserRole.equalsIgnoreCase(ROLES_BWH)) {
                call = RetrofitController.getInstance().getLoadingAdviseApi().getBothraWHDetails("Bearer " + loginUserToken, edtRfidTagId.getText().toString());
            } else if (loginUserRole.equalsIgnoreCase(ROLES_CWH)) {
                call = RetrofitController.getInstance().getLoadingAdviseApi().getCoromandelWHDetails("Bearer " + loginUserToken, edtRfidTagId.getText().toString());
            }
            call.enqueue(new Callback<TransactionsApiResponse>() {
                @Override
                public void onResponse(Call<TransactionsApiResponse> call, Response<TransactionsApiResponse> response) {
                    if (!response.isSuccessful()) {
                        progressBar.setVisibility(View.GONE);
                        ((MainActivity) getActivity()).alert(getActivity(), "error", response.errorBody().toString(), null, "OK");
                        return;
                    }

                    if (response.body().getStatus().equalsIgnoreCase("FOUND")) {
                        progressBar.setVisibility(View.GONE);
                        vibrate();
                        TransactionsDto transactionsDto = response.body().getTransactionsDto();
                        try {
                            String lepNo = transactionsDto.getRfidLepIssueModel().getLepNumber();
                            String lepNoId = String.valueOf(transactionsDto.getRfidLepIssueModel().getId());
                            String rfidTag = transactionsDto.getRfidLepIssueModel().getRfidNumber();
                            String driverName = transactionsDto.getRfidLepIssueModel().getDriverMaster().getDriverName();
                            String truckNo = transactionsDto.getRfidLepIssueModel().getDailyTransportReportModule().getTruckNumber();
                            String commodity = transactionsDto.getRfidLepIssueModel().getDailyTransportReportModule().getCommodity();
                            String previousRmgNo = transactionsDto.getFunctionalLocationDestinationMaster().getStrLocationCode();
                            String PreviousRmgNoDesc = transactionsDto.getFunctionalLocationDestinationMaster().getStrLocationDesc();

                            if (loginUserRole.equalsIgnoreCase(ROLES_CWH)) {
                                String GrossWeight = String.valueOf(transactionsDto.getGrossWeight());
                                saveWareHouseDetials(lepNo, lepNoId, rfidTag, driverName, truckNo, commodity, GrossWeight, previousRmgNo, PreviousRmgNoDesc, null);
                                ((MainActivity) requireActivity()).loadFragment(new CWHFragment(), 1);
                            } else if (loginUserRole.equalsIgnoreCase(ROLES_BWH)) {
                                String sourceGrossWeight = String.valueOf(transactionsDto.getSourceGrossWeight());
                                saveWareHouseDetials(lepNo, lepNoId, rfidTag, driverName, truckNo, commodity, null, previousRmgNo, PreviousRmgNoDesc, sourceGrossWeight);
                                ((MainActivity) requireActivity()).loadFragment(new BWHFragment(), 1);
                            } else {
                                ((MainActivity) requireActivity()).alert(requireActivity(), "ERROR", "Invalid roles", null, "OK");
                                Intent id = new Intent(requireActivity(), LoginActivity.class);
                                startActivity(id);
                                requireActivity().finish();
                            }

                        } catch (Exception e) {
                            e.getMessage();
                            return;
                        }
                    } else {
                        progressBar.setVisibility(View.GONE);
                        ((MainActivity) getActivity()).alert(getActivity(), "warning", "No LEP no is available", null, "OK");
                    }
                }

                @Override
                public void onFailure(Call<TransactionsApiResponse> call, Throwable t) {
                    progressBar.setVisibility(View.GONE);
                    ((MainActivity) getActivity()).alert(getActivity(), "error", t.getMessage(), null, "OK");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void RfidDetailsLoadingAdvise() {
        if (loginUserRole.equalsIgnoreCase(ROLES_LAO)) {
            if ((loginUserPlantCode.equalsIgnoreCase(PLANT_BOTHRA))) {
                getRfidTagDetailBothraLA();
            } else {
                getRfidTagDetailCoromandelLA();
            }
        } else {
            getWareHouseDetails();
        }
    }

    private void getRfidTagDetailBothraLA() {
        progressBar.setVisibility(View.VISIBLE);
        try {
            Call<TransactionsApiResponse> call = RetrofitController.getInstance().getLoadingAdviseApi().getRfidTagDetailBothraLA("Bearer " + loginUserToken, edtRfidTagId.getText().toString());

            call.enqueue(new Callback<TransactionsApiResponse>() {
                @Override
                public void onResponse(Call<TransactionsApiResponse> call, Response<TransactionsApiResponse> response) {
                    if (!response.isSuccessful()) {
                        progressBar.setVisibility(View.GONE);
                        ((MainActivity) getActivity()).alert(getActivity(), "error", response.errorBody().toString(), null, "OK");
                        return;
                    }

                    if (response.body().getStatus().equalsIgnoreCase("FOUND")) {
                        vibrate();
                        progressBar.setVisibility(View.GONE);
                        TransactionsDto transactionsDto = response.body().getTransactionsDto();
                        try {
                            String lepNo = transactionsDto.getRfidLepIssueModel().getLepNumber();
                            String lepNoId = String.valueOf(transactionsDto.getRfidLepIssueModel().getId());
                            String rfidTag = transactionsDto.getRfidLepIssueModel().getRfidNumber();
                            String driverName = transactionsDto.getRfidLepIssueModel().getDriverMaster().getDriverName();
                            String driverMobileNo = transactionsDto.getRfidLepIssueModel().getDriverMaster().getDriverMobileNo();
                            String driverLicenseNo = transactionsDto.getRfidLepIssueModel().getDriverMaster().getDriverLicenseNo();
                            String truckNo = transactionsDto.getRfidLepIssueModel().getDailyTransportReportModule().getTruckNumber();
                            String sapGrNo = String.valueOf(transactionsDto.getRfidLepIssueModel().getDailyTransportReportModule().getSapGrNumber());
                            String vesselName = transactionsDto.getRfidLepIssueModel().getDailyTransportReportModule().getVesselName();
                            String truckCapacity = String.valueOf(transactionsDto.getRfidLepIssueModel().getDailyTransportReportModule().getTruckCapacity());
                            String commodity = transactionsDto.getRfidLepIssueModel().getDailyTransportReportModule().getCommodity();

                            if (loginUserRole.equalsIgnoreCase(ROLES_LAO)) {
                                saveLoadingAdivseDetails(rfidTag, lepNo, lepNoId, driverName, driverMobileNo, driverLicenseNo, truckNo, sapGrNo, vesselName, truckCapacity, commodity);
                                ((MainActivity) requireActivity()).loadFragment(new LoadingAdviseFragment(), 1);
                            }
                        } catch (Exception e) {
                            e.getMessage();
                            return;
                        }
                    } else {
                        progressBar.setVisibility(View.GONE);
                        ((MainActivity) getActivity()).alert(getActivity(), "warning", "No LEP no is available", null, "OK");
                    }
                }

                @Override
                public void onFailure(Call<TransactionsApiResponse> call, Throwable t) {
                    progressBar.setVisibility(View.GONE);
                    ((MainActivity) getActivity()).alert(getActivity(), "error", t.getMessage(), null, "OK");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}