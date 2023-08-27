package com.sipl.rfidtagscanner.fragments;

import static android.content.Context.MODE_PRIVATE;
import static android.content.Context.VIBRATOR_SERVICE;
import static com.sipl.rfidtagscanner.utils.Config.DIALOG_ERROR;
import static com.sipl.rfidtagscanner.utils.Config.DIALOG_WARNING;
import static com.sipl.rfidtagscanner.utils.Config.ROLES_ADMIN_PLANT;
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
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.preference.PreferenceManager;

import com.sipl.rfidtagscanner.LoginActivity;
import com.sipl.rfidtagscanner.MainActivity;
import com.sipl.rfidtagscanner.R;
import com.sipl.rfidtagscanner.RetrofitController;
import com.sipl.rfidtagscanner.RfidHandler;
import com.sipl.rfidtagscanner.dto.dtos.RfidLepIssueDto;
import com.sipl.rfidtagscanner.dto.dtos.StorageLocationDto;
import com.sipl.rfidtagscanner.dto.dtos.TransactionsDto;
import com.sipl.rfidtagscanner.dto.response.RfidLepApiResponse;
import com.sipl.rfidtagscanner.dto.response.RmgNumberApiResponse;
import com.sipl.rfidtagscanner.dto.response.TransactionsApiResponse;
import com.sipl.rfidtagscanner.interf.HandleStatusInterface;
import com.sipl.rfidtagscanner.interf.RFIDDataModel;
import com.sipl.rfidtagscanner.interf.RfidUiDataDto;
import com.zebra.rfid.api3.TagData;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScanFragment extends Fragment implements HandleStatusInterface {

    private static final String TAG = "ConnectFragment";
    private ArrayList<String> arrDestinationLocation;
    private ProgressBar progressBar;
    private EditText edtRfidTagId;
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
    private RfidHandler rfidHandler;
    private String loginUserRole;
    private String loginUserToken;
    private String loginUserStorageLocation;
    private TextView errorHandle;
    private LinearLayout error_layout;
    private String admin_selected_nav_screen = null;

    public ScanFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scan, container, false);
        edtRfidTagId = view.findViewById(R.id.sf_edt_rfid_tag);
        errorHandle = view.findViewById(R.id.sf_error);
        error_layout = view.findViewById(R.id.error_layout);
        progressBar = view.findViewById(R.id.login_progressBar);
        this.loginUserRole = ((MainActivity) getActivity()).getLoginUserRole();
        this.loginUserToken = ((MainActivity) getActivity()).getLoginToken();
        this.admin_selected_nav_screen = getScreenDetails();
        this.loginUserStorageLocation = ((MainActivity) getActivity()).getLoginUserStorageCode();

        Button btnVerify = view.findViewById(R.id.sf_btn_verify);
        getWareHouseStorage();
        checkInitialRFIDEnableStatus();

        btnVerify.setOnClickListener(view1 -> {
            if (edtRfidTagId.length() != 0) {
                vibrate();
                getRFIDDetails();
            } else {
                edtRfidTagId.setError("This field is required");
            }
        });

        RFIDDataModel.getInstance().getRFIDStatus().observe(getViewLifecycleOwner(), currentRFIDObserver);
        return view;
    }

    /*
     * This initial check weather RFID is enable or not and perform activity
     * */
    private void checkInitialRFIDEnableStatus() {
        Boolean value = isRFIDHandleEnable();
        try {
            if (value != null) {
                if (value) {
                    edtRfidTagId.setEnabled(false);
                    rfidHandler = new RfidHandler(requireActivity());
                    rfidHandler.InitSDK(this);
                } else {
                    edtRfidTagId.setEnabled(true);
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
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

    @Override
    public void onPause() {
        if (isRFIDHandleEnable()) {
            rfidHandler.onPause();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        if (isRFIDHandleEnable()) {
            rfidHandler.onResume();
        }
        super.onResume();
    }

    @Override
    public void onDestroy() {
        if (isRFIDHandleEnable()) {
            rfidHandler.onDestroy();
        }
        super.onDestroy();
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

    /*
     * Method call on verify
     * */
    private void getRFIDDetails() {
        if (loginUserRole.equalsIgnoreCase(ROLES_LAO) || (loginUserRole.equalsIgnoreCase(ROLES_ADMIN_PLANT)) && (admin_selected_nav_screen.equalsIgnoreCase("loadingAdvise"))) {
            if (arrDestinationLocation.contains(loginUserStorageLocation)) {
                getRfidTagDetailBothraLA();
            } else {
                getRfidDetailCoromandelLA();
            }
        } else {
            getAllWareHouseDetails();
        }
    }

    private void getRfidDetailCoromandelLA() {
        Log.i(TAG, "getRfidTagDetailCoromandelLA: ");
        progressBar.setVisibility(View.VISIBLE);
        try {
            Call<RfidLepApiResponse> call = RetrofitController.getInstances(requireContext()).getLoadingAdviseApi().getRfidTagDetailCoromandelLA("Bearer " + loginUserToken, edtRfidTagId.getText().toString());
            call.enqueue(new Callback<RfidLepApiResponse>() {
                @Override
                public void onResponse(Call<RfidLepApiResponse> call, Response<RfidLepApiResponse> response) {
                    if (!response.isSuccessful()) {
                        progressBar.setVisibility(View.GONE);
                        ((MainActivity) getActivity()).alert(getActivity(), "error", response.errorBody().toString(), null, "OK", false);
                        return;
                    }
                    Log.i(TAG, "onResponse: response.raw : getRfidTagDetailCoromandelLA : " + response.raw());

                    if (response.body().getStatus().equalsIgnoreCase("FOUND")) {
                        Log.i(TAG, "onResponse: getRfidTagDetailCoromandelLA : <<Start >>");
                        vibrate();
                        progressBar.setVisibility(View.GONE);
                        RfidLepIssueDto rfidLepIssueDto = response.body().getRfidLepIssueDto();
                        try {
                            String rfidTag = rfidLepIssueDto.getRfidMaster().getRfidNumber();
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
                            String destinationLocation = rfidLepIssueDto.getDestinationLocation().getStrLocationCode();
                            String berthLocation = rfidLepIssueDto.getBerthMaster().getBerthNumber();
                            String destinationLocationDesc = rfidLepIssueDto.getDestinationLocation().getStrLocationDesc();
                            Log.i(TAG, "onResponse: in rstat : " + response.body().getRfidLepIssueDto().getrStat());
                            if (rfidLepIssueDto.getrStat() == 0) {
                                getRFIDBothraLA();
                                return;
                            } else {
                                Log.i(TAG, "onResponse: in else");
                            }

                            String role = ((MainActivity) requireActivity()).getLoginUserRole();
                            Log.i(TAG, "onResponse: role " + role);
                            if (role.equalsIgnoreCase(ROLES_LAO)) {
                                Log.i(TAG, "onResponse:  in before saving in shp");
                                saveLADetails(rfidTag, lepNo, lepNoId, driverName, driverMobileNo, driverLicenseNo, truckNo, sapGrNo, vesselName, truckCapacity, commodity, destinationLocation, destinationLocationDesc, null, null, null, null, berthLocation);
                                Log.i(TAG, "onResponse: after saving data");
                                ((MainActivity) requireActivity()).loadFragment(new LoadingAdviseFragment(), 1);
                                return;
                            }
                            Log.i(TAG, "onResponse:  out of reach");

                        } catch (Exception e) {
                            Log.i(TAG, "onResponse: " + e.getMessage());
                            e.getMessage();
                            return;
                        }
                        Log.i(TAG, "onResponse: getRfidTagDetailCoromandelLA : <<END >>");
                    } else if (response.body().getStatus().equalsIgnoreCase("NOT_FOUND")) {
                        Log.i(TAG, "onResponse: NOT_FOUND");
                        progressBar.setVisibility(View.GONE);
                        getRFIDBothraLA();
                    } else {
                        Log.i(TAG, "onResponse: NOT_FOUND &&& else");
                        progressBar.setVisibility(View.GONE);
                        Log.i(TAG, "onResponse: " + response.raw());
                        ((MainActivity) requireActivity()).alert(requireContext(), "WARNING", response.body().getMessage(), null, "OK", false);
                    }
                }

                @Override
                public void onFailure(Call<RfidLepApiResponse> call, Throwable t) {
                    progressBar.setVisibility(View.GONE);
                    ((MainActivity) getActivity()).alert(getActivity(), "error", t.getMessage(), null, "OK", false);
                    Log.i(TAG, "onFailure: " + t.getMessage());
                    t.printStackTrace();
                }
            });

        } catch (Exception e) {
            Log.i(TAG, "getALlLepNumberWithFlag: " + e.getMessage());
        }
    }

    private void getRFIDBothraLA() {
        Log.i(TAG, "getRFIDCoromandelLa_2(): <<Start>>");
        progressBar.setVisibility(View.VISIBLE);
        try {
            Call<TransactionsApiResponse> call = RetrofitController.getInstances(requireContext()).getLoadingAdviseApi().getRfidTagDetailBothraLA("Bearer " + loginUserToken, "1", "0", edtRfidTagId.getText().toString());
            call.enqueue(new Callback<TransactionsApiResponse>() {
                @Override
                public void onResponse(Call<TransactionsApiResponse> call, Response<TransactionsApiResponse> response) {
                    if (!response.isSuccessful()) {
                        progressBar.setVisibility(View.GONE);
                        ((MainActivity) getActivity()).alert(getActivity(), DIALOG_ERROR, response.errorBody().toString(), null, "OK", false);
                        return;
                    }
                    Log.i(TAG, "onResponse: getRFIDBothraLA : response.raw() : " + response.raw());
                    if (response.isSuccessful()) {
                        if (response.body().getStatus().equalsIgnoreCase("FOUND")) {
                            vibrate();
                            progressBar.setVisibility(View.GONE);
                            TransactionsDto transactionsDto = response.body().getTransactionsDto();

                            try {
                                String lepNo = transactionsDto.getRfidLepIssueModel().getLepNumber();
                                String lepNoId = String.valueOf(transactionsDto.getRfidLepIssueModel().getId());
                                String rfidTag = transactionsDto.getRfidLepIssueModel().getRfidMaster().getRfidNumber();
                                String driverName = transactionsDto.getRfidLepIssueModel().getDriverMaster().getDriverName();
                                String driverMobileNo = transactionsDto.getRfidLepIssueModel().getDriverMaster().getDriverMobileNo();
                                String driverLicenseNo = transactionsDto.getRfidLepIssueModel().getDriverMaster().getDriverLicenseNo();
                                String truckNo = transactionsDto.getRfidLepIssueModel().getDailyTransportReportModule().getTruckNumber();
                                String sapGrNo = String.valueOf(transactionsDto.getRfidLepIssueModel().getDailyTransportReportModule().getSapGrNumber());
                                String vesselName = transactionsDto.getRfidLepIssueModel().getDailyTransportReportModule().getVesselName();
                                String truckCapacity = String.valueOf(transactionsDto.getRfidLepIssueModel().getDailyTransportReportModule().getTruckCapacity());
                                String commodity = transactionsDto.getRfidLepIssueModel().getDailyTransportReportModule().getCommodity();
                                String destinationLocation = transactionsDto.getFunctionalLocationDestinationMaster().getStrLocationCode();
                                String destinationLocationDesc = transactionsDto.getFunctionalLocationDestinationMaster().getStrLocationDesc();
                                String berthNumber = transactionsDto.getRfidLepIssueModel().getBerthMaster().getBerthNumber();

                             /*   String wareHouseCode = transactionsDto.getWarehouse().getStrLocationCode();
                                String wareHouseCodeDesc = transactionsDto.getWarehouse().getStrLocationDesc();*/


                                String isgetInLoadingTime;
                                String getInLoadingTime = null;
                                String pinnacleSupervisor = null;
                                String bothraSupervisor = null;

                                if (transactionsDto.getInLoadingTime() != null) {
                                    isgetInLoadingTime = "true";
                                    String entryTime = transactionsDto.getInLoadingTime();
                                    LocalDateTime aLDT = LocalDateTime.parse(entryTime);
                                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
                                    getInLoadingTime = aLDT.format(formatter);
                                    pinnacleSupervisor = transactionsDto.getStrPinnacleLoadingSupervisor();
                                    bothraSupervisor = transactionsDto.getStrBothraLoadingSupervisor();
                                } else {
                                    isgetInLoadingTime = "false";
                                }


                                if (loginUserRole.equalsIgnoreCase(ROLES_LAO)) {
                                    saveLADetails(rfidTag, lepNo, lepNoId, driverName, driverMobileNo, driverLicenseNo, truckNo, sapGrNo, vesselName, truckCapacity, commodity, destinationLocation, destinationLocationDesc, isgetInLoadingTime, getInLoadingTime, pinnacleSupervisor, bothraSupervisor, berthNumber);
                                    ((MainActivity) requireActivity()).loadFragment(new LoadingAdviseFragment(), 1);
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "onResponse: Exception in getRFIDCoromandelSecondURL" + e.getMessage());
                                e.printStackTrace();
                            }
                        } else {
                            progressBar.setVisibility(View.GONE);
                            ((MainActivity) getActivity()).alert(getActivity(), DIALOG_ERROR, response.body().getMessage(), null, "OK", false);
                        }
                    }
                }

                @Override
                public void onFailure(Call<TransactionsApiResponse> call, Throwable t) {
                    progressBar.setVisibility(View.GONE);
                    ((MainActivity) getActivity()).alert(getActivity(), DIALOG_ERROR, t.getMessage(), null, "OK", false);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void saveLADetails(String rfidTag, String lepNo, String lepNoId, String driverName, String driverMobileNo, String driverLicenseNo, String truckNo, String sapGrNo, String vesselName, String truckCapacity, String commodity, String strDestinationCode, String strDestinationDesc, String isgetInLoadingTime, String getInloadingTime, String pinnacleSupervisor, String bothraSupervisor, String BerthNumber) {
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
        editor.putString("strDestinationCodeSPK", strDestinationCode).apply();
        editor.putString("isgetInLoadingTimeSPK", isgetInLoadingTime).apply();
        editor.putString("getInloadingTimeSPK", getInloadingTime).apply();
        editor.putString("pinnacleSupervisorSPK", pinnacleSupervisor).apply();
        editor.putString("bothraSupervisorSPK", bothraSupervisor).apply();
        editor.putString("BerthNumberSPK", BerthNumber).apply();
        Log.i(TAG, "saveLADataSharedPref: strDestinationCode : " + strDestinationCode);
        Log.i(TAG, "saveLADataSharedPref: strDestinationDesc : " + strDestinationDesc);
        editor.putString("strDestinationDescSPK", strDestinationDesc).apply();
        editor.apply();
    }

    private void saveWHDetails(String lepNo, String lepNoId, String rfidTag, String driverName, String truckNo, String commodity, String GrossWeight, String previousRmgNo, String PreviousRmgNoDesc, String sourceGrossWeight, String isWeighbridgeAvailable, Integer callFrom, String vehicleInTime, String outUnloadingTime, String inUnloadingTime) {
        SharedPreferences sp = requireActivity().getSharedPreferences("WareHouseDetails", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("rfidTagSPK", rfidTag).apply();
        editor.putString("lepNoSPK", lepNo).apply();
        editor.putString("inUnloadingTimeSPK", inUnloadingTime).apply();
        editor.putString("outUnloadingTimeSPK", outUnloadingTime).apply();
        editor.putString("lepNoIdSPK", lepNoId).apply();
        editor.putString("driverNameSPK", driverName).apply();
        editor.putString("truckNoSPK", truckNo).apply();
        editor.putString("commoditySPK", commodity).apply();
        editor.putString("GrossWeightSPK", GrossWeight).apply();
        editor.putString("previousRmgNoSPK", previousRmgNo).apply();
        editor.putString("PreviousRmgNoDescSPK", PreviousRmgNoDesc).apply();
        editor.putString("sourceGrossWeightSPK", sourceGrossWeight).apply();
        editor.putString("isWeighbridgeAvailableSPK", isWeighbridgeAvailable).apply();
        editor.putInt("callFromSPK", callFrom).apply();
        editor.putString("vehicleInTimeSPK", vehicleInTime).apply();
        editor.apply();
    }

    private void saveWHDetailsCoro(String lepNo, String lepNoId, String rfidTag, String driverName, String truckNo, String commodity, String GrossWeight, String previousRmgNo, String PreviousRmgNoDesc, String sourceGrossWeight, String vehicleInTime, String outUnloadingTime, String inUnloadingTime, String wareHouseCode, String wareHouseDesc, String remarks) {
        SharedPreferences sp = requireActivity().getSharedPreferences("WareHouseDetails", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("rfidTagSPK", rfidTag).apply();
        editor.putString("lepNoSPK", lepNo).apply();
        editor.putString("inUnloadingTimeSPK", inUnloadingTime).apply();
        editor.putString("outUnloadingTimeSPK", outUnloadingTime).apply();
        editor.putString("lepNoIdSPK", lepNoId).apply();
        editor.putString("driverNameSPK", driverName).apply();
        editor.putString("truckNoSPK", truckNo).apply();
        editor.putString("commoditySPK", commodity).apply();
        editor.putString("GrossWeightSPK", GrossWeight).apply();
        editor.putString("previousRmgNoSPK", previousRmgNo).apply();
        editor.putString("PreviousRmgNoDescSPK", PreviousRmgNoDesc).apply();
        editor.putString("sourceGrossWeightSPK", sourceGrossWeight).apply();
        editor.putString("wareHouseCodeSPK", wareHouseCode).apply();
        editor.putString("wareHouseCodeDescSPK", wareHouseDesc).apply();
        editor.putString("vehicleInTimeSPK", vehicleInTime).apply();
        editor.putString("remarksSPK", remarks).apply();
        editor.apply();
    }


    private void saveWHDetailsBoro(String lepNo, String lepNoId, String rfidTag, String driverName, String truckNo, String commodity, String GrossWeight, String previousRmgNo, String PreviousRmgNoDesc, String sourceGrossWeight, String vehicleInTime, String outUnloadingTime, String inUnloadingTime, String wareHouseCode, String wareHouseDesc, String remarks) {
        SharedPreferences sp = requireActivity().getSharedPreferences("WareHouseDetails", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("rfidTagSPK", rfidTag).apply();
        editor.putString("lepNoSPK", lepNo).apply();
        editor.putString("inUnloadingTimeSPK", inUnloadingTime).apply();
        editor.putString("outUnloadingTimeSPK", outUnloadingTime).apply();
        editor.putString("lepNoIdSPK", lepNoId).apply();
        editor.putString("driverNameSPK", driverName).apply();
        editor.putString("truckNoSPK", truckNo).apply();
        editor.putString("commoditySPK", commodity).apply();
        editor.putString("GrossWeightSPK", GrossWeight).apply();
        editor.putString("previousRmgNoSPK", previousRmgNo).apply();
        editor.putString("PreviousRmgNoDescSPK", PreviousRmgNoDesc).apply();
        editor.putString("sourceGrossWeightSPK", sourceGrossWeight).apply();
        editor.putString("wareHouseCodeSPK", wareHouseCode).apply();
        editor.putString("wareHouseCodeDescSPK", wareHouseDesc).apply();
        editor.putString("vehicleInTimeSPK", vehicleInTime).apply();
        editor.putString("remarksSPK", remarks).apply();
        editor.apply();
    }


    private void getAllWareHouseDetails() {
        if (loginUserRole.equalsIgnoreCase(ROLES_BWH) || (loginUserRole.equalsIgnoreCase(ROLES_ADMIN_PLANT) && (admin_selected_nav_screen.equalsIgnoreCase("bothra")))) {
            getBothraInUnLoadingDetails();
        } else if (loginUserRole.equalsIgnoreCase(ROLES_CWH) || (loginUserRole.equalsIgnoreCase(ROLES_ADMIN_PLANT) && (admin_selected_nav_screen.equalsIgnoreCase("coromandel")))) {
            getCoromandelWareHouseDetails();
        }
    }

    private void getCoromandelWareHouseDetails() {
        showProgress();
        try {
            Call<TransactionsApiResponse> call = RetrofitController.getInstances(requireContext()).getLoadingAdviseApi().getCoromandelWHDetails("Bearer " + loginUserToken, "4", "3", edtRfidTagId.getText().toString());
            call.enqueue(new Callback<TransactionsApiResponse>() {
                @Override
                public void onResponse(Call<TransactionsApiResponse> call, Response<TransactionsApiResponse> response) {
                    hideProgress();
                    if (!response.isSuccessful()) {
                        ((MainActivity) getActivity()).alert(getActivity(), DIALOG_ERROR, response.errorBody().toString(), null, "OK", false);
                        return;
                    }
                    Log.i(TAG, "onResponse: getCoromandelWareHouseDetails : raw " + response.raw());
                    if (response.body().getStatus() != null) {
                        if (response.body().getStatus().equalsIgnoreCase("FOUND")) {
                            vibrate();
                            TransactionsDto transactionsDto = response.body().getTransactionsDto();
                            try {
                                String lepNo = transactionsDto.getRfidLepIssueModel().getLepNumber();
                                String lepNoId = String.valueOf(transactionsDto.getRfidLepIssueModel().getId());
                                String rfidTag = transactionsDto.getRfidLepIssueModel().getRfidMaster().getRfidNumber();
                                String driverName = transactionsDto.getRfidLepIssueModel().getDriverMaster().getDriverName();
                                String truckNo = transactionsDto.getRfidLepIssueModel().getDailyTransportReportModule().getTruckNumber();
                                String commodity = transactionsDto.getRfidLepIssueModel().getDailyTransportReportModule().getCommodity();
                                String previousRmgNo = null;
                                String PreviousRmgNoDesc = null;
                                String remarks = null;
                                String wareHouseCode = transactionsDto.getWarehouse().getStrLocationCode();
                                String wareHouseDesc = transactionsDto.getWarehouse().getStrLocationDesc();
                                String strInUnloadingTime = transactionsDto.getInUnLoadingTime();
                                String outUnloadingTime = transactionsDto.getOutUnLoadingTime();

                                if (strInUnloadingTime != null) {
                                    if (transactionsDto.getPriviousWarehouse().getStrLocationCode() != null && transactionsDto.getPriviousWarehouse().getStrLocationDesc() != null) {
                                        previousRmgNo = transactionsDto.getPriviousWarehouse().getStrLocationCode();
                                        PreviousRmgNoDesc = transactionsDto.getPriviousWarehouse().getStrLocationDesc();
                                        if (transactionsDto.getRemarkMaster() != null) {
                                            remarks = transactionsDto.getRemarkMaster().getRemarks();
                                        } else {
                                            remarks = null;
                                        }
                                    }
                                } else {
                                    previousRmgNo = null;
                                    PreviousRmgNoDesc = null;
                                    remarks = null;
                                }

                                if (loginUserRole.equalsIgnoreCase(ROLES_CWH)) {
                                    String GrossWeight = String.valueOf(transactionsDto.getGrossWeight());
                                    saveWHDetailsCoro(lepNo, lepNoId, rfidTag, driverName, truckNo, commodity, GrossWeight, previousRmgNo, PreviousRmgNoDesc, null, null, outUnloadingTime, strInUnloadingTime, wareHouseCode, wareHouseDesc, remarks);
                                    ((MainActivity) requireActivity()).loadFragment(new CWHFragment(), 1);
                                } else {
                                    ((MainActivity) requireActivity()).alert(requireActivity(), DIALOG_ERROR, "Invalid roles", null, "OK", false);
                                    Intent id = new Intent(requireActivity(), LoginActivity.class);
                                    startActivity(id);
                                    requireActivity().finish();
                                }

                            } catch (Exception e) {
                                Log.i(TAG, "onResponse: Exception in coromandel warehouse : " + e.getMessage());
                                e.printStackTrace();
                            }
                        } else {
                            progressBar.setVisibility(View.GONE);
                            ((MainActivity) getActivity()).alert(getActivity(), DIALOG_WARNING, response.body().getMessage(), null, "OK", false);
                        }
                    }
                }

                @Override
                public void onFailure(Call<TransactionsApiResponse> call, Throwable t) {
                    hideProgress();
                    ((MainActivity) getActivity()).alert(getActivity(), DIALOG_ERROR, t.getMessage(), null, "OK", false);
                }
            });
        } catch (Exception e) {
            Log.i(TAG, "getCoromandelWareHouseDetails: Exception in coromandelWareHouse : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void getBothraInUnLoadingDetails() {
        progressBar.setVisibility(View.VISIBLE);
        try {
            Call<TransactionsApiResponse> call = RetrofitController.getInstances(requireContext()).getLoadingAdviseApi().getBothraWHDetails("Bearer " + loginUserToken, "8", "7", edtRfidTagId.getText().toString());
            call.enqueue(new Callback<TransactionsApiResponse>() {
                @Override
                public void onResponse(Call<TransactionsApiResponse> call, Response<TransactionsApiResponse> response) {
                    if (!response.isSuccessful()) {
                        progressBar.setVisibility(View.GONE);
                        return;
                    }
                    Log.i(TAG, "onResponse: response.raw : " + response.raw());
                    if (response.isSuccessful()) {
                        if (response.body().getStatus().equalsIgnoreCase("FOUND")) {
                            progressBar.setVisibility(View.GONE);
                            vibrate();
                            TransactionsDto transactionsDto = response.body().getTransactionsDto();
                            try {
                                String lepNo = transactionsDto.getRfidLepIssueModel().getLepNumber();
                                String lepNoId = String.valueOf(transactionsDto.getRfidLepIssueModel().getId());
                                String rfidTag = transactionsDto.getRfidLepIssueModel().getRfidMaster().getRfidNumber();
                                String driverName = transactionsDto.getRfidLepIssueModel().getDriverMaster().getDriverName();
                                String truckNo = transactionsDto.getRfidLepIssueModel().getDailyTransportReportModule().getTruckNumber();
                                String commodity = transactionsDto.getRfidLepIssueModel().getDailyTransportReportModule().getCommodity();
                                String previousRmgNo = null;
                                String PreviousRmgNoDesc = null;
                                String remarks = null;
                                String wareHouseCode = transactionsDto.getWarehouse().getStrLocationCode();
                                String wareHouseDesc = transactionsDto.getWarehouse().getStrLocationDesc();
                                String strInUnloadingTime = transactionsDto.getInUnLoadingTime();
                                String outUnloadingTime = transactionsDto.getOutUnLoadingTime();

                                if (strInUnloadingTime != null) {
                                    if (transactionsDto.getPriviousWarehouse().getStrLocationCode() != null && transactionsDto.getPriviousWarehouse().getStrLocationDesc() != null) {
                                        previousRmgNo = transactionsDto.getPriviousWarehouse().getStrLocationCode();
                                        PreviousRmgNoDesc = transactionsDto.getPriviousWarehouse().getStrLocationDesc();
                                        if (transactionsDto.getRemarkMaster() != null) {
                                            remarks = transactionsDto.getRemarkMaster().getRemarks();
                                        } else {
                                            remarks = null;
                                        }
                                    }
                                } else {
                                    previousRmgNo = null;
                                    PreviousRmgNoDesc = null;
                                    remarks = null;
                                }



                                if (loginUserRole.equalsIgnoreCase(ROLES_BWH)) {
                                    String sourceGrossWeight;
                                    if (transactionsDto.getSourceGrossWeight() != null) {
                                        sourceGrossWeight = String.valueOf(transactionsDto.getSourceGrossWeight());
                                    } else {
                                        sourceGrossWeight = String.valueOf(transactionsDto.getGrossWeight());
                                    }
                                    Log.i(TAG, "onResponse: lepno : " + lepNo + "\nlepNoId : " + lepNoId + "\nrfidTag : " + rfidTag + "\ndriverName : " + driverName + "truckNo : " + truckNo + "\ncommodity : " + commodity + "\npreviousRmg : "+ previousRmgNo + "\nPreviousRmgNoDesc : " + PreviousRmgNoDesc + "\nSourceGrossWeight : " + sourceGrossWeight + "\noutUnloadingTime : " + outUnloadingTime + "\nstrInUnloadingTime : " + strInUnloadingTime + "\nwareHouseCode : " + wareHouseCode + "\nwareHouseDesc : " + wareHouseDesc + "\nremarks : " + remarks);
                                    saveWHDetailsBoro(lepNo, lepNoId, rfidTag, driverName, truckNo, commodity, null, previousRmgNo, PreviousRmgNoDesc, sourceGrossWeight, null, outUnloadingTime, strInUnloadingTime, wareHouseCode, wareHouseDesc, remarks);
                                    ((MainActivity) requireActivity()).loadFragment(new BWHFragment(), 1);
                                } else {
                                    ((MainActivity) requireActivity()).alert(requireActivity(), DIALOG_ERROR, "Invalid roles", null, "OK", false);
                                    Intent id = new Intent(requireActivity(), LoginActivity.class);
                                    startActivity(id);
                                    requireActivity().finish();
                                }
                            } catch (Exception e) {
                                Log.i(TAG, "onResponse : Exception in bothraWarehouse vehicle in case : " + e.getMessage());
                                e.printStackTrace();
                            }
                        } else {
                            progressBar.setVisibility(View.GONE);
                            getBothraOutUnLoadingDetails();
                        }
                    }
                }

                @Override
                public void onFailure(Call<TransactionsApiResponse> call, Throwable t) {
                    progressBar.setVisibility(View.GONE);
                    ((MainActivity) getActivity()).alert(getActivity(), DIALOG_ERROR, t.getMessage(), null, "OK", false);
                }
            });
        } catch (Exception e) {
            Log.i(TAG, "getBothraInUnLoadingDetails: Exception in bothraWarehouse vehicle in case : " + e.getMessage());
            e.printStackTrace();
        }

    }

    /*
     * This method is call to fetch RFID details for vehicle out time and save data in shared preferences
     * */
    private void getBothraOutUnLoadingDetails() {
        progressBar.setVisibility(View.VISIBLE);
        try {
            Call<TransactionsApiResponse> call = RetrofitController.getInstances(requireContext()).getLoadingAdviseApi().getBothraWHDetailsForExit("Bearer " + loginUserToken, "8", edtRfidTagId.getText().toString());
            call.enqueue(new Callback<TransactionsApiResponse>() {
                @Override
                public void onResponse(Call<TransactionsApiResponse> call, Response<TransactionsApiResponse> response) {
                    if (!response.isSuccessful()) {
                        progressBar.setVisibility(View.GONE);
                        ((MainActivity) getActivity()).alert(getActivity(), DIALOG_ERROR, response.errorBody().toString(), null, "OK", false);
                        return;
                    }
                    Log.i(TAG, "onResponse: response.raw : " + response.raw());
                    if (response.body().getStatus().equalsIgnoreCase("FOUND")) {
                        progressBar.setVisibility(View.GONE);
                        vibrate();
                        TransactionsDto transactionsDto = response.body().getTransactionsDto();
                        try {
                            String lepNo = transactionsDto.getRfidLepIssueModel().getLepNumber();
                            String lepNoId = String.valueOf(transactionsDto.getRfidLepIssueModel().getId());
                            String rfidTag = transactionsDto.getRfidLepIssueModel().getRfidMaster().getRfidNumber();
                            String driverName = transactionsDto.getRfidLepIssueModel().getDriverMaster().getDriverName();
                            String truckNo = transactionsDto.getRfidLepIssueModel().getDailyTransportReportModule().getTruckNumber();
                            String commodity = transactionsDto.getRfidLepIssueModel().getDailyTransportReportModule().getCommodity();
                 /*           String previousRmgNo = transactionsDto.getFunctionalLocationDestinationMaster().getStrLocationCode();
                            String PreviousRmgNoDesc = transactionsDto.getFunctionalLocationDestinationMaster().getStrLocationDesc();
                            String isWeighBridgeAvailble = String.valueOf(transactionsDto.getFunctionalLocationDestinationMaster().getWbAvailable());
                            String strWareHouseCode = transactionsDto.getWarehouse().getStrLocationCode();
                            String strWareHouseCodeDesc = transactionsDto.getWarehouse().getStrLocationDesc();
                            String strWbAvailable = String.valueOf(transactionsDto.getWarehouse().getWbAvailable());*/

                        /*    String strEntryTime = transactionsDto.getVehicleInTime();
                            LocalDateTime aLDT = LocalDateTime.parse(strEntryTime);
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
                            String entryTime = aLDT.format(formatter);*/

                      /*      if (loginUserRole.equalsIgnoreCase(ROLES_BWH)) {
                                String sourceGrossWeight = String.valueOf(transactionsDto.getSourceGrossWeight());
                                if (strWareHouseCode != null) {
                                    saveWHDetails(lepNo, lepNoId, rfidTag, driverName, truckNo, commodity, null, strWareHouseCode, strWareHouseCodeDesc, sourceGrossWeight, strWbAvailable, 2, entryTime, null, null);
                                } else {
                                    saveWHDetails(lepNo, lepNoId, rfidTag, driverName, truckNo, commodity, null, previousRmgNo, PreviousRmgNoDesc, sourceGrossWeight, isWeighBridgeAvailble, 2, entryTime, null, null);
                                }
                                ((MainActivity) requireActivity()).loadFragment(new BWHFragment(), 1);*/

                            String previousRmgNo = null;
                            String PreviousRmgNoDesc = null;
                            String remarks = null;
                            String wareHouseCode = transactionsDto.getWarehouse().getStrLocationCode();
                            String wareHouseDesc = transactionsDto.getWarehouse().getStrLocationDesc();
                            String strInUnloadingTime = transactionsDto.getInUnLoadingTime();
                            String outUnloadingTime = transactionsDto.getOutUnLoadingTime();

                            if (strInUnloadingTime != null) {
                                if (transactionsDto.getPriviousWarehouse().getStrLocationCode() != null && transactionsDto.getPriviousWarehouse().getStrLocationDesc() != null) {
                                    previousRmgNo = transactionsDto.getPriviousWarehouse().getStrLocationCode();
                                    PreviousRmgNoDesc = transactionsDto.getPriviousWarehouse().getStrLocationDesc();
                                    if (transactionsDto.getRemarkMaster() != null) {
                                        remarks = transactionsDto.getRemarkMaster().getRemarks();
                                    } else {
                                        remarks = null;
                                    }
                                }
                            } else {
                                previousRmgNo = null;
                                PreviousRmgNoDesc = null;
                                remarks = null;
                            }



                            if (loginUserRole.equalsIgnoreCase(ROLES_BWH)) {
                                String sourceGrossWeight;
                                if (transactionsDto.getSourceGrossWeight() != null) {
                                    sourceGrossWeight = String.valueOf(transactionsDto.getSourceGrossWeight());
                                } else {
                                    sourceGrossWeight = String.valueOf(transactionsDto.getGrossWeight());
                                }
                                Log.i(TAG, "onResponse: lepno : " + lepNo + "\nlepNoId : " + lepNoId + "\nrfidTag : " + rfidTag + "\ndriverName : " + driverName + "truckNo : " + truckNo + "\ncommodity : " + commodity + "\npreviousRmg : "+ previousRmgNo + "\nPreviousRmgNoDesc : " + PreviousRmgNoDesc + "\nSourceGrossWeight : " + sourceGrossWeight + "\noutUnloadingTime : " + outUnloadingTime + "\nstrInUnloadingTime : " + strInUnloadingTime + "\nwareHouseCode : " + wareHouseCode + "\nwareHouseDesc : " + wareHouseDesc + "\nremarks : " + remarks);
                                saveWHDetailsBoro(lepNo, lepNoId, rfidTag, driverName, truckNo, commodity, null, previousRmgNo, PreviousRmgNoDesc, sourceGrossWeight, null, outUnloadingTime, strInUnloadingTime, wareHouseCode, wareHouseDesc, remarks);
                                ((MainActivity) requireActivity()).loadFragment(new BWHFragment(), 1);
                            } else {
                                ((MainActivity) requireActivity()).alert(requireActivity(), DIALOG_ERROR, "Invalid roles", null, "OK", false);
                                Intent id = new Intent(requireActivity(), LoginActivity.class);
                                startActivity(id);
                                requireActivity().finish();
                            }

                        } catch (Exception e) {
                            Log.i(TAG, "onResponse: Exception in bothraWarehouse vehicle out case : " + e.getMessage());
                            e.printStackTrace();
                            return;
                        }
                    } else {
                        progressBar.setVisibility(View.GONE);
                        ((MainActivity) getActivity()).alert(getActivity(), DIALOG_WARNING, response.body().getMessage(), null, "OK", false);
                    }
                }

                @Override
                public void onFailure(Call<TransactionsApiResponse> call, Throwable t) {
                    progressBar.setVisibility(View.GONE);
                    ((MainActivity) getActivity()).alert(getActivity(), DIALOG_ERROR, t.getMessage(), null, "OK", false);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * Get all rfid tag details for bothra loading advise
     * */
    private void getRfidTagDetailBothraLA() {
        progressBar.setVisibility(View.VISIBLE);
        try {
            Call<TransactionsApiResponse> call = RetrofitController.getInstances(requireContext()).getLoadingAdviseApi().getRfidTagDetailBothraLA("Bearer " + loginUserToken, "12", "11", edtRfidTagId.getText().toString());
            call.enqueue(new Callback<TransactionsApiResponse>() {
                @Override
                public void onResponse(Call<TransactionsApiResponse> call, Response<TransactionsApiResponse> response) {
                    if (!response.isSuccessful()) {
                        progressBar.setVisibility(View.GONE);
                        ((MainActivity) getActivity()).alert(getActivity(), DIALOG_ERROR, response.errorBody().toString(), null, "OK", false);
                        return;
                    }
                    if (response.body().getStatus().equalsIgnoreCase("FOUND")) {
                        vibrate();
                        progressBar.setVisibility(View.GONE);
                        TransactionsDto transactionsDto = response.body().getTransactionsDto();
                        try {
                            String lepNo = transactionsDto.getRfidLepIssueModel().getLepNumber();
                            String lepNoId = String.valueOf(transactionsDto.getRfidLepIssueModel().getId());
                            String rfidTag = transactionsDto.getRfidLepIssueModel().getRfidMaster().getRfidNumber();
                            String driverName = transactionsDto.getRfidLepIssueModel().getDriverMaster().getDriverName();
                            String driverMobileNo = transactionsDto.getRfidLepIssueModel().getDriverMaster().getDriverMobileNo();
                            String driverLicenseNo = transactionsDto.getRfidLepIssueModel().getDriverMaster().getDriverLicenseNo();
                            String truckNo = transactionsDto.getRfidLepIssueModel().getDailyTransportReportModule().getTruckNumber();
                            String sapGrNo = String.valueOf(transactionsDto.getRfidLepIssueModel().getDailyTransportReportModule().getSapGrNumber());
                            String vesselName = transactionsDto.getRfidLepIssueModel().getDailyTransportReportModule().getVesselName();
                            String truckCapacity = String.valueOf(transactionsDto.getRfidLepIssueModel().getDailyTransportReportModule().getTruckCapacity());
                            String commodity = transactionsDto.getRfidLepIssueModel().getDailyTransportReportModule().getCommodity();
                            String destinationLocation = transactionsDto.getFunctionalLocationDestinationMaster().getStrLocationCode();
                            String destinationLocationDesc = transactionsDto.getFunctionalLocationDestinationMaster().getStrLocationDesc();

                            String isgetInLoadingTime;
                            String getInLoadingTime = null;
                            String pinnacleSupervisor = null;
                            String bothraSupervisor = null;

                            if (transactionsDto.getInLoadingTime() != null) {
                                isgetInLoadingTime = "true";
                                String entryTime = transactionsDto.getInLoadingTime();
                                LocalDateTime aLDT = LocalDateTime.parse(entryTime);
                                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
                                getInLoadingTime = aLDT.format(formatter);
                                pinnacleSupervisor = transactionsDto.getStrPinnacleLoadingSupervisor();
                                bothraSupervisor = transactionsDto.getStrBothraLoadingSupervisor();
                            } else {
                                isgetInLoadingTime = "false";
                            }
                            if (loginUserRole.equalsIgnoreCase(ROLES_LAO)) {
                                saveLADetails(rfidTag, lepNo, lepNoId, driverName, driverMobileNo, driverLicenseNo, truckNo, sapGrNo, vesselName, truckCapacity, commodity, destinationLocation, destinationLocationDesc, isgetInLoadingTime, getInLoadingTime, pinnacleSupervisor, bothraSupervisor, null);
                                ((MainActivity) requireActivity()).loadFragment(new LoadingAdviseFragment(), 1);
                            }
                        } catch (Exception e) {
                            e.getMessage();
                        }
                    } else {
                        progressBar.setVisibility(View.GONE);
                        Log.i(TAG, "onResponse: " + response.raw());
                        ((MainActivity) getActivity()).alert(getActivity(), "warning", response.body().getMessage(), null, "OK", false);
                    }
                }

                @Override
                public void onFailure(Call<TransactionsApiResponse> call, Throwable t) {
                    progressBar.setVisibility(View.GONE);
                    ((MainActivity) getActivity()).alert(getActivity(), "error", t.getMessage(), null, "OK", false);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * if warehouse is found then this is use for comparison and open screen base on storage location get match with below warehouse list (if matched then Open bothra Loading Advise screen else open Coromandel Loading advise screen)
     * */
    private boolean getWareHouseStorage() {
        progressBar.setVisibility(View.VISIBLE);
        Call<RmgNumberApiResponse> call = RetrofitController.getInstances(requireContext()).getLoadingAdviseApi().
                getAllWareHouse("Bearer " + loginUserToken, "bothra");
        call.enqueue(new Callback<RmgNumberApiResponse>() {
            @Override
            public void onResponse(Call<RmgNumberApiResponse> call, Response<RmgNumberApiResponse> response) {
                if (!response.isSuccessful()) {
                    progressBar.setVisibility(View.GONE);
                    ifWareHouseIsEmpty();
                    return;
                }
                if (response.isSuccessful()) {
                    Log.i("getWareHouseStorage", "onResponse: raw : " + response.raw());
                    progressBar.setVisibility(View.GONE);
                    arrDestinationLocation = new ArrayList<>();
                    List<StorageLocationDto> functionalLocationMasterDtoList = response.body().getStorageLocationDtos();
                    try {
                        SharedPreferences sp = requireActivity().getSharedPreferences("bothraStrLocation", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp.edit();
                        if (functionalLocationMasterDtoList == null || functionalLocationMasterDtoList.isEmpty()) {
                            arrDestinationLocation.add("dummyListDataIsAddedForCompareNotRequiredAndIsNotUseFulAnyMore");
                            arrDestinationLocation.add("dummyListDataIsAddedForCompareNotRequiredAndIsNotUseFulAnyMore2");
                            editor.putString(String.valueOf(0), "dummyListDataIsAddedForCompareNotRequiredAndIsNotUseFulAnyMore").apply();
                            editor.putString(String.valueOf(1), "dummyListDataIsAddedForCompareNotRequiredAndIsNotUseFulAnyMore2").apply();
                        } else {
                            for (int i = 0; i < functionalLocationMasterDtoList.size(); i++) {
                                String s = functionalLocationMasterDtoList.get(i).getStrLocationCode();
                                editor.putString(String.valueOf(i), s).apply();
                                arrDestinationLocation.add(s);
                            }
                        }
                        editor.putString("size", String.valueOf(arrDestinationLocation.size())).apply();

                    } catch (Exception e) {
                        Log.i(TAG, "onResponse: " + e.getMessage());
                        e.getMessage();
                    }
                }
            }

            @Override
            public void onFailure(Call<RmgNumberApiResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                ifWareHouseIsEmpty();
            }
        });
        return true;
    }

    /*
     * if warehouse is empty then below method is called and add dummy record for comparison (Open Coromandel Loading Advise screen)
     * */
    private void ifWareHouseIsEmpty() {
        arrDestinationLocation = new ArrayList<>();
        SharedPreferences sp = requireActivity().getSharedPreferences("bothraStrLocation", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        arrDestinationLocation.add("dummyListDataIsAddedForCompareNotRequiredAndIsNotUseFulAnyMore");
        arrDestinationLocation.add("dummyListDataIsAddedForCompareNotRequiredAndIsNotUseFulAnyMore2");
        editor.putString(String.valueOf(0), "dummyListDataIsAddedForCompareNotRequiredAndIsNotUseFulAnyMore").apply();
        editor.putString(String.valueOf(1), "dummyListDataIsAddedForCompareNotRequiredAndIsNotUseFulAnyMore2").apply();
        editor.putString("size", String.valueOf(arrDestinationLocation.size())).apply();
    }


    public boolean isRFIDHandleEnable() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        return sharedPreferences.getBoolean("enable_rfid_handle", true);
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

    @Override
    public void handleConnectionStatus(String name, Boolean status) {
        SettingsFragment s = new SettingsFragment();
        if (!status) {
            s.updateSwitchPreferenceValue(false);
            String text = "Error : Rfid Handle is not connected";
            errorHandle.setText(text);
            error_layout.setVisibility(View.VISIBLE);
            if (name != null) {
                ((MainActivity) requireActivity()).alert(requireContext(), DIALOG_ERROR, name, "Try reattaching the handle", "OK", false);
            } else {
                ((MainActivity) requireActivity()).alert(requireContext(), DIALOG_ERROR, "RFID handle not found", "Try reattaching the handle", "OK", false);
            }
        } else {
            error_layout.setVisibility(View.GONE);
            s.updateSwitchPreferenceValue(true);
        }
    }

    public String getScreenDetails() {
        SharedPreferences sp = requireActivity().getSharedPreferences("adminScreen", MODE_PRIVATE);
        return sp.getString("screen", null);
    }

}