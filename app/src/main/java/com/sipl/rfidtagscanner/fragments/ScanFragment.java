package com.sipl.rfidtagscanner.fragments;

import static android.content.Context.MODE_PRIVATE;
import static android.content.Context.VIBRATOR_SERVICE;
import static com.sipl.rfidtagscanner.utils.Config.BTN_OK;
import static com.sipl.rfidtagscanner.utils.Config.DIALOG_ERROR;
import static com.sipl.rfidtagscanner.utils.Config.DIALOG_WARNING;
import static com.sipl.rfidtagscanner.utils.Config.NULL_VALUE_RESPONSE;
import static com.sipl.rfidtagscanner.utils.Config.RESPONSE_ALREADY_REPORTED;
import static com.sipl.rfidtagscanner.utils.Config.RESPONSE_FORBIDDEN;
import static com.sipl.rfidtagscanner.utils.Config.RESPONSE_FOUND;
import static com.sipl.rfidtagscanner.utils.Config.RESPONSE_NOT_FOUND;
import static com.sipl.rfidtagscanner.utils.Config.ROLES_ADMIN_PLANT;
import static com.sipl.rfidtagscanner.utils.Config.ROLES_BWH;
import static com.sipl.rfidtagscanner.utils.Config.ROLES_B_LAO;
import static com.sipl.rfidtagscanner.utils.Config.ROLES_CWH;
import static com.sipl.rfidtagscanner.utils.Config.ROLES_C_LAO;

import android.app.Dialog;
import android.content.Context;
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
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sipl.rfidtagscanner.MainActivity;
import com.sipl.rfidtagscanner.R;
import com.sipl.rfidtagscanner.RetrofitController;
import com.sipl.rfidtagscanner.RfidHandler;
import com.sipl.rfidtagscanner.dto.dtos.GenericData;
import com.sipl.rfidtagscanner.dto.dtos.RfidLepIssueDto;
import com.sipl.rfidtagscanner.dto.dtos.TransactionsDto;
import com.sipl.rfidtagscanner.dto.response.RfidLepApiResponse;
import com.sipl.rfidtagscanner.dto.response.TransactionsApiResponse;
import com.sipl.rfidtagscanner.interf.HandleStatusInterface;
import com.sipl.rfidtagscanner.interf.RFIDDataModel;
import com.sipl.rfidtagscanner.interf.RfidUiDataDto;
import com.sipl.rfidtagscanner.utils.CustomErrorMessage;
import com.zebra.rfid.api3.TagData;

import java.lang.reflect.Type;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScanFragment extends Fragment implements HandleStatusInterface {

    private static final String TAG = "ConnectFragment";
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
    private Button btnVerify;
    private RfidHandler rfidHandler;
    private String loginUserRole, loginUserToken, adminSelectedNavScreen, loginUserName;
    private TextView errorHandle;
    private LinearLayout llErrorLayout;
    private Boolean isLoadingDifferenceEnable;
    private FrameLayout rootLayout;
    private View colorOverlay;

    public ScanFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scan, container, false);
        edtRfidTagId = view.findViewById(R.id.sf_edt_rfid_tag);
        errorHandle = view.findViewById(R.id.sf_error);
        llErrorLayout = view.findViewById(R.id.error_layout);
        progressBar = view.findViewById(R.id.sf_progressBar);
        rootLayout = view.findViewById(R.id.sf_root_layout);
        colorOverlay = view.findViewById(R.id.sf_view);

        this.loginUserRole = ((MainActivity) requireActivity()).getRoleId();
        this.loginUserToken = ((MainActivity) requireActivity()).getToken();
        this.adminSelectedNavScreen = getScreenDetails();
        this.loginUserName = ((MainActivity) requireActivity()).getUserName();

        btnVerify = view.findViewById(R.id.sf_btn_verify);
        checkInitialRFIDEnableStatus();
        this.isLoadingDifferenceEnable = isLoadingDifferenceEnable();

        btnVerify.setOnClickListener(view1 -> {
            if (edtRfidTagId.length() != 0) {
                vibrate();
                getRfidDetails();
            } else {
                edtRfidTagId.setError("This field is required");
            }
        });

        RFIDDataModel.getInstance().getRFIDStatus().observe(getViewLifecycleOwner(), currentRFIDObserver);
        return view;
    }

    private void checkInitialRFIDEnableStatus() {
        boolean value = isRFIDHandleEnable();
        try {
            if (value) {
                edtRfidTagId.setEnabled(false);
                rfidHandler = new RfidHandler(requireActivity());
                rfidHandler.InitSDK(this);
            } else {
                edtRfidTagId.setEnabled(true);
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
    private void getRfidDetails() {
        if (loginUserRole.equalsIgnoreCase(ROLES_C_LAO) || (loginUserRole.equalsIgnoreCase(ROLES_ADMIN_PLANT)) && (adminSelectedNavScreen.equalsIgnoreCase("cLoadingAdvise"))) {
            getCilLoadingInTagDetails();
        } else if (loginUserRole.equalsIgnoreCase(ROLES_B_LAO) || (loginUserRole.equalsIgnoreCase(ROLES_ADMIN_PLANT)) && (adminSelectedNavScreen.equalsIgnoreCase("bLoadingAdvise"))) {
            getBothraLaTagDetails();
        } else {
            getWareHouseTagDetails();
        }
    }

    private void getCilLoadingInTagDetails() {
        showProgress();
        Call<RfidLepApiResponse> call = RetrofitController.getInstances(requireContext()).getLoadingAdviseApi().getCilLaTagDetails("Bearer " + loginUserToken, edtRfidTagId.getText().toString(), loginUserName);
        call.enqueue(new Callback<RfidLepApiResponse>() {
            @Override
            public void onResponse(Call<RfidLepApiResponse> call, Response<RfidLepApiResponse> response) {
                hideProgress();
                vibrate();
                Log.d(TAG, "onResponse: getCilLaTagDetails : Raw : " + response.raw());
                if (!response.isSuccessful()) {
                    ((MainActivity) requireActivity()).alert(requireContext(), DIALOG_ERROR, response.errorBody() != null ? response.errorBody().toString() : "Error occurs while fetching CIL Loading details", null, BTN_OK, false);
                }

                if (response.body() != null && response.body().getStatus() != null && response.body().getMessage() != null) {
                    if (response.body().getStatus().equalsIgnoreCase(RESPONSE_FOUND)) {
                        if (response.body().getRfidLepIssueDto() != null) {
                            RfidLepIssueDto rfidLepIssueDto = response.body().getRfidLepIssueDto();
                            try {
                                String rfidTag = rfidLepIssueDto.getRfidMaster().getRfidNumber();
                                String lepNo = rfidLepIssueDto.getLepNumber();
                                String lepNoId = rfidLepIssueDto.getId().toString();
                                String driverName = rfidLepIssueDto.getDriverMaster().getDriverName();
                                String driverMobileNo = rfidLepIssueDto.getDriverMaster().getDriverMobileNo();
                                String driverLicenseNo = rfidLepIssueDto.getDriverMaster().getDriverLicenseNo();
                                String truckNo = rfidLepIssueDto.getDailyTransportReportModule().getVehicleMaster().getVehicleRegistrationNumber();
                                String vesselName = rfidLepIssueDto.getDailyTransportReportModule().getSapGrnDetailsEntity().getVesselName();
                                String commodity = rfidLepIssueDto.getDailyTransportReportModule().getSapGrnDetailsEntity().getDescription();
                                String batchNumber = rfidLepIssueDto.getDailyTransportReportModule().getSapGrnDetailsEntity().getBatch();
                                String destinationLocation = rfidLepIssueDto.getDestinationLocation().getStrLocationCode();
                                String berthLocation = rfidLepIssueDto.getBerthMaster().getBerthNumber();
                                String destinationLocationDesc = rfidLepIssueDto.getDestinationLocation().getStrLocationDesc();
                                String grSrcLoc = rfidLepIssueDto.getDailyTransportReportModule().getSourceLocationCode();
                                String grSrcLocDesc = rfidLepIssueDto.getDailyTransportReportModule().getSourceDescription();

                                if (rfidLepIssueDto.getRstat() == 0) {
                                    getCilLoadingOutTagDetails();
                                    return;
                                }
                                if (loginUserRole.equalsIgnoreCase(ROLES_C_LAO)) {
                                    saveLoadingInfo(rfidTag, lepNo, lepNoId, driverName, driverMobileNo, driverLicenseNo, truckNo, vesselName, commodity, destinationLocation, destinationLocationDesc, null, null, null, berthLocation, batchNumber, grSrcLoc, grSrcLocDesc, null);
                                } else {
                                    ((MainActivity) requireActivity()).alert(requireContext(), DIALOG_WARNING, "Something went wrong", null, BTN_OK, false);
                                }
                            } catch (Exception e) {
                                ((MainActivity) requireActivity()).alert(requireContext(), DIALOG_WARNING, "Exception occurs while fetching loading in details", "Exception : " + e.getMessage(), BTN_OK, false);
                            }
                        }
                    } else if (response.body().getStatus().equalsIgnoreCase(RESPONSE_NOT_FOUND)) {
                        getCilLoadingOutTagDetails();
                    } else if (response.body().getStatus().equalsIgnoreCase(RESPONSE_ALREADY_REPORTED)) {
                        ((MainActivity) requireActivity()).alert(requireActivity(), DIALOG_WARNING, response.body().getMessage(), null, BTN_OK, false);
                    } else if (response.body().getStatus().equalsIgnoreCase(RESPONSE_FORBIDDEN)) {
                        ((MainActivity) requireActivity()).alert(requireActivity(), DIALOG_WARNING, response.body().getMessage(), null, BTN_OK, false);
                    } else {
                        ((MainActivity) requireActivity()).alert(requireContext(), DIALOG_ERROR, response.body().getMessage(), null, BTN_OK, false);
                    }
                } else {
                    ((MainActivity) requireActivity()).alert(requireActivity(), DIALOG_ERROR, NULL_VALUE_RESPONSE, null, BTN_OK, false);
                }
            }

            @Override
            public void onFailure(Call<RfidLepApiResponse> call, Throwable t) {
                hideProgress();
                ((MainActivity) requireActivity()).alert(requireContext(), DIALOG_ERROR, CustomErrorMessage.setErrorMessage(t.getMessage()), null, BTN_OK, false);
            }
        });
    }

    private void getCilLoadingOutTagDetails() {
        showProgress();
        Call<TransactionsApiResponse> call = RetrofitController.getInstances(requireContext()).getLoadingAdviseApi().getCilLoadingOutTagDetails("Bearer " + loginUserToken, "1", "0", edtRfidTagId.getText().toString(), loginUserName);
        call.enqueue(new Callback<TransactionsApiResponse>() {
            @Override
            public void onResponse(Call<TransactionsApiResponse> call, Response<TransactionsApiResponse> response) {
                hideProgress();
                Log.d(TAG, "onResponse: getCilLoadingOutTagDetails : Raw : " + response.raw());
                if (!response.isSuccessful()) {
                    ((MainActivity) requireActivity()).alert(requireActivity(), DIALOG_ERROR, response.errorBody() != null ? response.errorBody().toString() : "Error occurs while fetching CIL Loading Out details", null, BTN_OK, false);
                }
                if (response.body() != null && response.body().getStatus() != null && response.body().getMessage() != null) {
                    if (response.body().getStatus().equalsIgnoreCase(RESPONSE_FOUND)) {
                        if (response.body().getTransactionsDto() != null) {
                            TransactionsDto transactionsDto = response.body().getTransactionsDto();
                            try {
                                String lepNo = transactionsDto.getRfidLepIssueModel().getLepNumber();
                                String lepNoId = String.valueOf(transactionsDto.getRfidLepIssueModel().getId());
                                String rfidTag = transactionsDto.getRfidLepIssueModel().getRfidMaster().getRfidNumber();
                                String driverName = transactionsDto.getRfidLepIssueModel().getDriverMaster().getDriverName();
                                String driverMobileNo = transactionsDto.getRfidLepIssueModel().getDriverMaster().getDriverMobileNo();
                                String driverLicenseNo = transactionsDto.getRfidLepIssueModel().getDriverMaster().getDriverLicenseNo();
                                String truckNo = transactionsDto.getRfidLepIssueModel().getDailyTransportReportModule().getVehicleMaster().getVehicleRegistrationNumber();
                                String vesselName = transactionsDto.getRfidLepIssueModel().getDailyTransportReportModule().getSapGrnDetailsEntity().getVesselName();
                                String commodity = transactionsDto.getRfidLepIssueModel().getDailyTransportReportModule().getSapGrnDetailsEntity().getDescription();
                                String batchNumber = transactionsDto.getRfidLepIssueModel().getDailyTransportReportModule().getSapGrnDetailsEntity().getBatch();
                                String destinationLocation = transactionsDto.getFunctionalLocationDestinationMaster().getStrLocationCode();
                                String destinationLocationDesc = transactionsDto.getFunctionalLocationDestinationMaster().getStrLocationDesc();
                                String berthNumber = transactionsDto.getRfidLepIssueModel().getBerthMaster().getBerthNumber();
                                String grSrcLoc = transactionsDto.getRfidLepIssueModel().getDailyTransportReportModule().getSourceLocationCode();
                                String grSrcLocDesc = transactionsDto.getRfidLepIssueModel().getDailyTransportReportModule().getSourceDescription();
                                String inLoadingTime = null;
                                String pinnacleSupervisor = null;
                                String bothraSupervisor = null;

                                if (transactionsDto.inLoadingTime() != null) {
                                    String loadingInTime = transactionsDto.inLoadingTime();
                                    inLoadingTime = getTruncatedDateTime(loadingInTime);
                                    pinnacleSupervisor = transactionsDto.getStrPinnacleLoadingSupervisor();
                                    bothraSupervisor = transactionsDto.getStrBothraLoadingSupervisor();
                                    if (isLoadingDifferenceEnable) {
                                        if (!getCalculateDate(loadingInTime, "Buffer time between loading In - loading Out is 3 min")) {
                                            return;
                                        }
                                    }
                                }

                                if (loginUserRole.equalsIgnoreCase(ROLES_C_LAO)) {
                                    saveLoadingInfo(rfidTag, lepNo, lepNoId, driverName, driverMobileNo, driverLicenseNo, truckNo, vesselName, commodity, destinationLocation, destinationLocationDesc, inLoadingTime, pinnacleSupervisor, bothraSupervisor, berthNumber, batchNumber, grSrcLoc, grSrcLocDesc, null);
                                } else {
                                    ((MainActivity) requireActivity()).alert(requireContext(), DIALOG_WARNING, "Something went wrong", null, BTN_OK, false);
                                }
                            } catch (Exception e) {
                                ((MainActivity) requireActivity()).alert(requireContext(), DIALOG_WARNING, "Exception occurs while fetching loading out details", "Exception : " + e.getMessage(), BTN_OK, false);
                            }
                        }
                    } else if (response.body().getStatus().equalsIgnoreCase(RESPONSE_ALREADY_REPORTED)) {
                        ((MainActivity) requireActivity()).alert(requireActivity(), DIALOG_WARNING, response.body().getMessage(), null, BTN_OK, false);
                    } else if (response.body().getStatus().equalsIgnoreCase(RESPONSE_FORBIDDEN)) {
                        ((MainActivity) requireActivity()).alert(requireActivity(), DIALOG_WARNING, response.body().getMessage(), null, BTN_OK, false);
                    } else {
                        ((MainActivity) requireActivity()).alert(requireActivity(), DIALOG_ERROR, response.body().getMessage(), null, BTN_OK, false);
                    }
                } else {
                    ((MainActivity) requireActivity()).alert(requireActivity(), DIALOG_ERROR, NULL_VALUE_RESPONSE, null, BTN_OK, false);
                }
            }

            @Override
            public void onFailure(Call<TransactionsApiResponse> call, Throwable t) {
                hideProgress();
                ((MainActivity) requireActivity()).alert(requireActivity(), DIALOG_ERROR, CustomErrorMessage.setErrorMessage(t.getMessage()), null, BTN_OK, false);
            }
        });
    }

    private void getBothraLaTagDetails() {
        showProgress();
        String loginUserId = ((MainActivity) requireActivity()).getUserName();
        Call<TransactionsApiResponse> call = RetrofitController.getInstances(requireContext()).getLoadingAdviseApi().getBothraLaTagDetails("Bearer " + loginUserToken, "12", "11", edtRfidTagId.getText().toString(), loginUserId);
        call.enqueue(new Callback<TransactionsApiResponse>() {
            @Override
            public void onResponse(Call<TransactionsApiResponse> call, Response<TransactionsApiResponse> response) {
                hideProgress();
                vibrate();
                Log.d(TAG, "onResponse: getBothraLaTagDetails : Raw : " + response.raw());
                if (!response.isSuccessful()) {
                    ((MainActivity) getActivity()).alert(getActivity(), DIALOG_ERROR, response.errorBody() != null ? response.errorBody().toString() : "Error occurs while fetching Bothra loading details", null, BTN_OK, false);
                }
                if (response.body() != null && response.body().getStatus() != null && response.body().getMessage() != null) {
                    if (response.body().getStatus().equalsIgnoreCase(RESPONSE_FOUND)) {
                        if (response.body().getTransactionsDto() != null) {
                            TransactionsDto transactionsDto = response.body().getTransactionsDto();
                            try {
                                String lepNo = transactionsDto.getRfidLepIssueModel().getLepNumber();
                                String lepNoId = String.valueOf(transactionsDto.getRfidLepIssueModel().getId());
                                String rfidTag = transactionsDto.getRfidLepIssueModel().getRfidMaster().getRfidNumber();
                                String driverName = transactionsDto.getRfidLepIssueModel().getDriverMaster().getDriverName();
                                String driverMobileNo = transactionsDto.getRfidLepIssueModel().getDriverMaster().getDriverMobileNo();
                                String driverLicenseNo = transactionsDto.getRfidLepIssueModel().getDriverMaster().getDriverLicenseNo();
                                String truckNo = transactionsDto.getRfidLepIssueModel().getDailyTransportReportModule().getVehicleMaster().getVehicleRegistrationNumber();
                                String vesselName = transactionsDto.getRfidLepIssueModel().getDailyTransportReportModule().getSapGrnDetailsEntity().getVesselName();
                                String commodity = transactionsDto.getRfidLepIssueModel().getDailyTransportReportModule().getSapGrnDetailsEntity().getDescription();
                                String batchNumber = transactionsDto.getRfidLepIssueModel().getDailyTransportReportModule().getSapGrnDetailsEntity().getBatch();
                                String destinationLocation = transactionsDto.getFunctionalLocationDestinationMaster().getStrLocationCode();
                                String destinationLocationDesc = transactionsDto.getFunctionalLocationDestinationMaster().getStrLocationDesc();
                                String grSrcLoc = transactionsDto.getRfidLepIssueModel().getDailyTransportReportModule().getSourceLocationCode();
                                String grSrcLocDesc = transactionsDto.getRfidLepIssueModel().getDailyTransportReportModule().getSourceDescription();
                                String bTareWeight = transactionsDto.getSourceTareWeight().toString();
                                String inLoadingTime = null;
                                String pinnacleSupervisor = null;
                                String bothraSupervisor = null;

                                if (transactionsDto.inLoadingTime() != null) {
                                    String loadingInTime = transactionsDto.inLoadingTime();
                                    inLoadingTime = getTruncatedDateTime(loadingInTime);
                                    pinnacleSupervisor = transactionsDto.getStrPinnacleLoadingSupervisor();
                                    bothraSupervisor = transactionsDto.getStrBothraLoadingSupervisor();
                                    if (isLoadingDifferenceEnable) {
                                        if (!getCalculateDate(loadingInTime, "Buffer time between Loading In - Loading Out is 3 min")) {
                                            return;
                                        }
                                    }
                                }
                                if (loginUserRole.equalsIgnoreCase(ROLES_B_LAO)) {
                                    saveLoadingInfo(rfidTag, lepNo, lepNoId, driverName, driverMobileNo, driverLicenseNo, truckNo, vesselName, commodity, destinationLocation, destinationLocationDesc, inLoadingTime, pinnacleSupervisor, bothraSupervisor, null, batchNumber, grSrcLoc, grSrcLocDesc, bTareWeight);
                                } else {
                                    ((MainActivity) requireActivity()).alert(requireContext(), DIALOG_WARNING, "Something went wrong", null, BTN_OK, false);
                                }
                            } catch (Exception e) {
                                ((MainActivity) requireActivity()).alert(requireContext(), DIALOG_WARNING, "Exception occurs while fetching loading details", "Exception : " + e.getMessage(), BTN_OK, false);
                            }
                        }
                    } else if (response.body().getStatus().equalsIgnoreCase(RESPONSE_ALREADY_REPORTED)) {
                        ((MainActivity) requireActivity()).alert(requireActivity(), DIALOG_WARNING, response.body().getMessage(), null, BTN_OK, false);
                    } else if (response.body().getStatus().equalsIgnoreCase(RESPONSE_FORBIDDEN)) {
                        ((MainActivity) requireActivity()).alert(requireActivity(), DIALOG_WARNING, response.body().getMessage(), null, BTN_OK, false);
                    } else {
                        ((MainActivity) requireActivity()).alert(requireActivity(), DIALOG_ERROR, response.body().getMessage(), null, BTN_OK, false);
                    }
                } else {
                    ((MainActivity) requireActivity()).alert(requireActivity(), DIALOG_ERROR, NULL_VALUE_RESPONSE, null, BTN_OK, false);
                }
            }

            @Override
            public void onFailure(Call<TransactionsApiResponse> call, Throwable t) {
                hideProgress();
                ((MainActivity) requireActivity()).alert(requireActivity(), DIALOG_ERROR, CustomErrorMessage.setErrorMessage(t.getMessage()), null, BTN_OK, false);
            }
        });
    }

    private void getWareHouseTagDetails() {
        if (loginUserRole.equalsIgnoreCase(ROLES_BWH) || (loginUserRole.equalsIgnoreCase(ROLES_ADMIN_PLANT) && (adminSelectedNavScreen.equalsIgnoreCase("bothra")))) {
            getBothraWhUnloadingInTagDetails();
        } else if (loginUserRole.equalsIgnoreCase(ROLES_CWH) || (loginUserRole.equalsIgnoreCase(ROLES_ADMIN_PLANT) && (adminSelectedNavScreen.equalsIgnoreCase("coromandel")))) {
            getCilWarehouseDetail();
        }
    }

    private void getBothraWhUnloadingInTagDetails() {
        showProgress();
        Call<TransactionsApiResponse> call = RetrofitController.getInstances(requireContext()).getLoadingAdviseApi().getBothraWhUnloadingInTagDetails("Bearer " + loginUserToken, "8", "7", edtRfidTagId.getText().toString(), loginUserName);
        call.enqueue(new Callback<TransactionsApiResponse>() {
            @Override
            public void onResponse(Call<TransactionsApiResponse> call, Response<TransactionsApiResponse> response) {
                hideProgress();
                vibrate();
                Log.d(TAG, "onResponse: getBothraWhUnloadingInTagDetails : Raw : " + response.raw());
                if (!response.isSuccessful()) {
                    ((MainActivity) requireActivity()).alert(requireActivity(), DIALOG_ERROR, response.errorBody() != null ? response.errorBody().toString() : "Error occurs while fetching Bothra unloading in details", null, BTN_OK, false);
                    return;
                }
                if (response.body() != null && response.body().getStatus() != null && response.body().getMessage() != null) {
                    if (response.body().getStatus().equalsIgnoreCase(RESPONSE_FOUND)) {
                        if (response.body().getTransactionsDto() != null) {
                            TransactionsDto transactionsDto = response.body().getTransactionsDto();
                            try {
                                String lepNo = transactionsDto.getRfidLepIssueModel().getLepNumber();
                                String lepNoId = String.valueOf(transactionsDto.getRfidLepIssueModel().getId());
                                String rfidTag = transactionsDto.getRfidLepIssueModel().getRfidMaster().getRfidNumber();
                                String driverName = transactionsDto.getRfidLepIssueModel().getDriverMaster().getDriverName();
                                String truckNo = transactionsDto.getRfidLepIssueModel().getDailyTransportReportModule().getVehicleMaster().getVehicleRegistrationNumber();
                                String commodity = transactionsDto.getRfidLepIssueModel().getDailyTransportReportModule().getSapGrnDetailsEntity().getDescription();
                                String batchNumber = transactionsDto.getRfidLepIssueModel().getDailyTransportReportModule().getSapGrnDetailsEntity().getBatch();
                                String wareHouseCode = transactionsDto.getWarehouse().getStrLocationCode();
                                String wareHouseDesc = transactionsDto.getWarehouse().getStrLocationDesc();
                                String strInUnloadingTime = transactionsDto.getInUnLoadingTime();
                                String previousRmgNo = null;
                                String PreviousRmgNoDesc = null;
                                String remarks = null;

                                if (strInUnloadingTime != null) {
                                    if (isLoadingDifferenceEnable) {
                                        if (!getCalculateDate(strInUnloadingTime, "Buffer time between Unloading In - Unloading Out is 3 min")) {
                                            return;
                                        }
                                    }
                                    if (transactionsDto.getPriviousWarehouse().getStrLocationCode() != null && transactionsDto.getPriviousWarehouse().getStrLocationDesc() != null) {
                                        previousRmgNo = transactionsDto.getPriviousWarehouse().getStrLocationCode();
                                        PreviousRmgNoDesc = transactionsDto.getPriviousWarehouse().getStrLocationDesc();
                                        if (transactionsDto.getRemarkMaster() != null) {
                                            remarks = transactionsDto.getRemarkMaster().getRemarks();
                                        }
                                    }
                                }

                                if (loginUserRole.equalsIgnoreCase(ROLES_BWH)) {
                                    String sourceGrossWeight;
                                    if (transactionsDto.getSourceGrossWeight() != null) {
                                        sourceGrossWeight = String.valueOf(transactionsDto.getSourceGrossWeight());
                                    } else {
                                        sourceGrossWeight = String.valueOf(transactionsDto.getGrossWeight());
                                    }
                                    saveBothraWareHouseInfo(lepNo, lepNoId, rfidTag, driverName, truckNo, commodity, null, previousRmgNo, PreviousRmgNoDesc, sourceGrossWeight, null, strInUnloadingTime, wareHouseCode, wareHouseDesc, remarks, batchNumber, "authorized");
                                } else {
                                    ((MainActivity) requireActivity()).alert(requireContext(), DIALOG_WARNING, "Something went wrong", null, BTN_OK, false);
                                }
                            } catch (Exception e) {
                                ((MainActivity) requireActivity()).alert(requireContext(), DIALOG_WARNING, "Exception occurs while fetching Bothra unloading in details", "Exception : " + e.getMessage(), BTN_OK, false);
                            }
                        }
                    } else if (response.body().getStatus().equalsIgnoreCase(RESPONSE_ALREADY_REPORTED)) {
                        ((MainActivity) requireActivity()).alert(requireActivity(), DIALOG_WARNING, response.body().getMessage(), null, BTN_OK, false);
                    } else if (response.body().getStatus().equalsIgnoreCase(RESPONSE_FORBIDDEN)) {
                        if (response.body().getTransactionsDto() != null) {
                            TransactionsDto transactionsDto = response.body().getTransactionsDto();
                            try {
                                String lepNo = transactionsDto.getRfidLepIssueModel().getLepNumber();
                                String lepNoId = String.valueOf(transactionsDto.getRfidLepIssueModel().getId());
                                String rfidTag = transactionsDto.getRfidLepIssueModel().getRfidMaster().getRfidNumber();
                                String driverName = transactionsDto.getRfidLepIssueModel().getDriverMaster().getDriverName();
                                String truckNo = transactionsDto.getRfidLepIssueModel().getDailyTransportReportModule().getVehicleMaster().getVehicleRegistrationNumber();
                                String commodity = transactionsDto.getRfidLepIssueModel().getDailyTransportReportModule().getSapGrnDetailsEntity().getDescription();
                                String batchNumber = transactionsDto.getRfidLepIssueModel().getDailyTransportReportModule().getSapGrnDetailsEntity().getBatch();
                                String wareHouseCode = transactionsDto.getWarehouse().getStrLocationCode();
                                String wareHouseDesc = transactionsDto.getWarehouse().getStrLocationDesc();
                                String strInUnloadingTime = transactionsDto.getInUnLoadingTime();
                                String previousRmgNo = null;
                                String PreviousRmgNoDesc = null;
                                String remarks = null;

                                if (strInUnloadingTime != null) {
                                    if (isLoadingDifferenceEnable) {
                                        if (!getCalculateDate(strInUnloadingTime, "Buffer time between Unloading In - Unloading Out is 3 min")) {
                                            return;
                                        }
                                    }
                                    if (transactionsDto.getPriviousWarehouse().getStrLocationCode() != null && transactionsDto.getPriviousWarehouse().getStrLocationDesc() != null) {
                                        previousRmgNo = transactionsDto.getPriviousWarehouse().getStrLocationCode();
                                        PreviousRmgNoDesc = transactionsDto.getPriviousWarehouse().getStrLocationDesc();
                                        if (transactionsDto.getRemarkMaster() != null) {
                                            remarks = transactionsDto.getRemarkMaster().getRemarks();
                                        }
                                    }
                                }

                                if (loginUserRole.equalsIgnoreCase(ROLES_BWH)) {
                                    String sourceGrossWeight;
                                    if (transactionsDto.getSourceGrossWeight() != null) {
                                        sourceGrossWeight = String.valueOf(transactionsDto.getSourceGrossWeight());
                                    } else {
                                        sourceGrossWeight = String.valueOf(transactionsDto.getGrossWeight());
                                    }
                                    List<String> location = getLoginUserAssignedLocation();
//                                    String msg = "Access Denied for User  As he does not have permission to receive cargo into location code ";
                                    warehouseAlert(requireActivity(), "Access Denied for User "+ loginUserName + ", As he does not have permission to receive cargo into LEP location code " + wareHouseCode +"\n"+ "\nYou still want to receive cargo into your assigned LEP locations " + location + " ?", 2, lepNo, lepNoId, rfidTag, driverName, truckNo, commodity, null, previousRmgNo, PreviousRmgNoDesc, sourceGrossWeight, null, strInUnloadingTime, wareHouseCode, wareHouseDesc, remarks, batchNumber, strInUnloadingTime);
//                                    warehouseAlert(requireActivity(), response.body().getMessage() + "\nYour assign warehouse location are " + location + "\n" + "\nAre you still want to continue with your assign warehouse ?", 2, lepNo, lepNoId, rfidTag, driverName, truckNo, commodity, null, previousRmgNo, PreviousRmgNoDesc, sourceGrossWeight, null, strInUnloadingTime, wareHouseCode, wareHouseDesc, remarks, batchNumber, strInUnloadingTime);
//                                    saveBothraWareHouseInfo(lepNo, lepNoId, rfidTag, driverName, truckNo, commodity, null, previousRmgNo, PreviousRmgNoDesc, sourceGrossWeight, null, strInUnloadingTime, wareHouseCode, wareHouseDesc, remarks, batchNumber);
                                } else {
                                    ((MainActivity) requireActivity()).alert(requireContext(), DIALOG_WARNING, "Something went wrong", null, BTN_OK, false);
                                }
                            } catch (Exception e) {
                                ((MainActivity) requireActivity()).alert(requireContext(), DIALOG_WARNING, "Exception occurs while fetching Bothra unloading in details", "Exception : " + e.getMessage(), BTN_OK, false);
                            }
                        }
                    } else {
                        getBothraWhUnloadingOutTagDetails();
                    }
                } else {
                    ((MainActivity) requireActivity()).alert(requireActivity(), DIALOG_ERROR, NULL_VALUE_RESPONSE, null, BTN_OK, false);
                }
            }

            @Override
            public void onFailure(Call<TransactionsApiResponse> call, Throwable t) {
                hideProgress();
                ((MainActivity) requireActivity()).alert(requireActivity(), DIALOG_ERROR, CustomErrorMessage.setErrorMessage(t.getMessage()), null, BTN_OK, false);
            }
        });
    }


    private void getBothraWhUnloadingOutTagDetails() {
        showProgress();
        Call<TransactionsApiResponse> call = RetrofitController.getInstances(requireContext()).getLoadingAdviseApi().getBothraWhUnloadingOutTagDetails("Bearer " + loginUserToken, "8", edtRfidTagId.getText().toString(), loginUserName);
        call.enqueue(new Callback<TransactionsApiResponse>() {
            @Override
            public void onResponse(Call<TransactionsApiResponse> call, Response<TransactionsApiResponse> response) {
                hideProgress();
                vibrate();
                Log.d(TAG, "onResponse: getBothraWhUnloadingOutTagDetails : Raw : " + response.raw());
                if (!response.isSuccessful()) {
                    ((MainActivity) requireActivity()).alert(requireActivity(), DIALOG_ERROR, response.errorBody() != null ? response.errorBody().toString() : "Error occurs while fetching Bothra unloading out details", null, BTN_OK, false);
                    return;
                }
                if (response.body() != null && response.body().getStatus() != null && response.body().getMessage() != null) {
                    if (response.body().getStatus().equalsIgnoreCase(RESPONSE_FOUND)) {
                        if (response.body().getTransactionsDto() != null) {
                            TransactionsDto transactionsDto = response.body().getTransactionsDto();
                            try {
                                String lepNo = transactionsDto.getRfidLepIssueModel().getLepNumber();
                                String lepNoId = String.valueOf(transactionsDto.getRfidLepIssueModel().getId());
                                String rfidTag = transactionsDto.getRfidLepIssueModel().getRfidMaster().getRfidNumber();
                                String driverName = transactionsDto.getRfidLepIssueModel().getDriverMaster().getDriverName();
                                String truckNo = transactionsDto.getRfidLepIssueModel().getDailyTransportReportModule().getVehicleMaster().getVehicleRegistrationNumber();
                                String commodity = transactionsDto.getRfidLepIssueModel().getDailyTransportReportModule().getSapGrnDetailsEntity().getDescription();
                                String batchNumber = transactionsDto.getRfidLepIssueModel().getDailyTransportReportModule().getSapGrnDetailsEntity().getBatch();
                                String wareHouseCode = transactionsDto.getWarehouse().getStrLocationCode();
                                String wareHouseDesc = transactionsDto.getWarehouse().getStrLocationDesc();
                                String strInUnloadingTime = transactionsDto.getInUnLoadingTime();
                                String previousRmgNo = null;
                                String PreviousRmgNoDesc = null;
                                String remarks = null;

                                if (strInUnloadingTime != null) {
                                    if (isLoadingDifferenceEnable) {
                                        if (!getCalculateDate(strInUnloadingTime, "Buffer time between Unloading In - Unloading Out is 3 min")) {
                                            return;
                                        }
                                    }
                                    if (transactionsDto.getPriviousWarehouse().getStrLocationCode() != null && transactionsDto.getPriviousWarehouse().getStrLocationDesc() != null) {
                                        previousRmgNo = transactionsDto.getPriviousWarehouse().getStrLocationCode();
                                        PreviousRmgNoDesc = transactionsDto.getPriviousWarehouse().getStrLocationDesc();
                                        if (transactionsDto.getRemarkMaster() != null) {
                                            remarks = transactionsDto.getRemarkMaster().getRemarks();
                                        }
                                    }
                                }

                                if (loginUserRole.equalsIgnoreCase(ROLES_BWH)) {
                                    String sourceGrossWeight;
                                    if (transactionsDto.getSourceGrossWeight() != null) {
                                        sourceGrossWeight = String.valueOf(transactionsDto.getSourceGrossWeight());
                                    } else {
                                        sourceGrossWeight = String.valueOf(transactionsDto.getGrossWeight());
                                    }
                                    saveBothraWareHouseInfo(lepNo, lepNoId, rfidTag, driverName, truckNo, commodity, null, previousRmgNo, PreviousRmgNoDesc, sourceGrossWeight, null, strInUnloadingTime, wareHouseCode, wareHouseDesc, remarks, batchNumber, "authorized");
                                } else {
                                    ((MainActivity) requireActivity()).alert(requireContext(), DIALOG_WARNING, "Something went wrong", null, BTN_OK, false);
                                }

                            } catch (Exception e) {
                                ((MainActivity) requireActivity()).alert(requireContext(), DIALOG_WARNING, "Exception occurs while fetching Bothra unloading out details", "Exception : " + e.getMessage(), BTN_OK, false);
                            }
                        }
                    } else if (response.body().getStatus().equalsIgnoreCase(RESPONSE_FORBIDDEN)) {
                        if (response.body().getTransactionsDto() != null) {
                            TransactionsDto transactionsDto = response.body().getTransactionsDto();
                            try {
                                String lepNo = transactionsDto.getRfidLepIssueModel().getLepNumber();
                                String lepNoId = String.valueOf(transactionsDto.getRfidLepIssueModel().getId());
                                String rfidTag = transactionsDto.getRfidLepIssueModel().getRfidMaster().getRfidNumber();
                                String driverName = transactionsDto.getRfidLepIssueModel().getDriverMaster().getDriverName();
                                String truckNo = transactionsDto.getRfidLepIssueModel().getDailyTransportReportModule().getVehicleMaster().getVehicleRegistrationNumber();
                                String commodity = transactionsDto.getRfidLepIssueModel().getDailyTransportReportModule().getSapGrnDetailsEntity().getDescription();
                                String batchNumber = transactionsDto.getRfidLepIssueModel().getDailyTransportReportModule().getSapGrnDetailsEntity().getBatch();
                                String wareHouseCode = transactionsDto.getWarehouse().getStrLocationCode();
                                String wareHouseDesc = transactionsDto.getWarehouse().getStrLocationDesc();
                                String strInUnloadingTime = transactionsDto.getInUnLoadingTime();
                                String previousRmgNo = null;
                                String PreviousRmgNoDesc = null;
                                String remarks = null;

                                if (strInUnloadingTime != null) {
                                    if (isLoadingDifferenceEnable) {
                                        if (!getCalculateDate(strInUnloadingTime, "Buffer time between Unloading In - Unloading Out is 3 min")) {
                                            return;
                                        }
                                    }
                                    if (transactionsDto.getPriviousWarehouse().getStrLocationCode() != null && transactionsDto.getPriviousWarehouse().getStrLocationDesc() != null) {
                                        previousRmgNo = transactionsDto.getPriviousWarehouse().getStrLocationCode();
                                        PreviousRmgNoDesc = transactionsDto.getPriviousWarehouse().getStrLocationDesc();
                                        if (transactionsDto.getRemarkMaster() != null) {
                                            remarks = transactionsDto.getRemarkMaster().getRemarks();
                                        }
                                    }
                                }

                                if (loginUserRole.equalsIgnoreCase(ROLES_BWH)) {
                                    String sourceGrossWeight;
                                    if (transactionsDto.getSourceGrossWeight() != null) {
                                        sourceGrossWeight = String.valueOf(transactionsDto.getSourceGrossWeight());
                                    } else {
                                        sourceGrossWeight = String.valueOf(transactionsDto.getGrossWeight());
                                    }
                                    saveBothraWareHouseInfo(lepNo, lepNoId, rfidTag, driverName, truckNo, commodity, null, previousRmgNo, PreviousRmgNoDesc, sourceGrossWeight, null, strInUnloadingTime, wareHouseCode, wareHouseDesc, remarks, batchNumber, "authorized");
//                                    warehouseAlert(requireActivity(), "Planned LEP location " + previousRmgNo + " - " + PreviousRmgNoDesc.toUpperCase() + " is not assign to user " + loginUserName.toUpperCase() + "\nAre you still want to continue with your assign warehouse ?", 2, lepNo, lepNoId, rfidTag, driverName, truckNo, commodity, null, previousRmgNo, PreviousRmgNoDesc, sourceGrossWeight, null, strInUnloadingTime, wareHouseCode, wareHouseDesc, remarks, batchNumber, strInUnloadingTime);
                                } else {
                                    ((MainActivity) requireActivity()).alert(requireContext(), DIALOG_WARNING, "Something went wrong", null, BTN_OK, false);
                                }

                            } catch (Exception e) {
                                ((MainActivity) requireActivity()).alert(requireContext(), DIALOG_WARNING, "Exception occurs while fetching Bothra unloading out details", "Exception : " + e.getMessage(), BTN_OK, false);
                            }
                        }
//                        ((MainActivity) requireActivity()).alert(requireActivity(), DIALOG_WARNING, response.body().getMessage(), null, BTN_OK, false);
                    } else if (response.body().getStatus().equalsIgnoreCase(RESPONSE_ALREADY_REPORTED)) {
                        ((MainActivity) requireActivity()).alert(requireActivity(), DIALOG_WARNING, response.body().getMessage(), null, BTN_OK, false);
                    } else {
                        ((MainActivity) requireActivity()).alert(requireActivity(), DIALOG_ERROR, response.body().getMessage(), null, BTN_OK, false);
                    }
                } else {
                    ((MainActivity) requireActivity()).alert(requireActivity(), DIALOG_ERROR, NULL_VALUE_RESPONSE, null, BTN_OK, false);
                }
            }

            @Override
            public void onFailure(Call<TransactionsApiResponse> call, Throwable t) {
                hideProgress();
                ((MainActivity) requireActivity()).alert(requireActivity(), DIALOG_ERROR, CustomErrorMessage.setErrorMessage(t.getMessage()), null, BTN_OK, false);
            }
        });
    }

    private void getCilWarehouseDetail() {
        showProgress();
        Call<TransactionsApiResponse> call = RetrofitController.getInstances(requireContext()).getLoadingAdviseApi().getCilWarehouseDetail("Bearer " + loginUserToken, "4", "3", edtRfidTagId.getText().toString(), loginUserName);
        call.enqueue(new Callback<TransactionsApiResponse>() {
            @Override
            public void onResponse(Call<TransactionsApiResponse> call, Response<TransactionsApiResponse> response) {
                hideProgress();
                vibrate();
                Log.d(TAG, "onResponse: getCilWarehouseDetail : Raw : " + response.raw());
                if (!response.isSuccessful()) {
                    ((MainActivity) requireActivity()).alert(requireActivity(), DIALOG_ERROR, response.errorBody() != null ? response.errorBody().toString() : "Error occurs while fetching Bothra unloading out details", null, BTN_OK, false);
                }
                if (response.body() != null && response.body().getStatus() != null && response.body().getMessage() != null) {
                    if (response.body().getStatus().equalsIgnoreCase(RESPONSE_FOUND)) {
                        vibrate();
                        TransactionsDto transactionsDto = response.body().getTransactionsDto();
                        try {
                            String lepNo = transactionsDto.getRfidLepIssueModel().getLepNumber();
                            String lepNoId = String.valueOf(transactionsDto.getRfidLepIssueModel().getId());
                            String rfidTag = transactionsDto.getRfidLepIssueModel().getRfidMaster().getRfidNumber();
                            String driverName = transactionsDto.getRfidLepIssueModel().getDriverMaster().getDriverName();
                            String truckNo = transactionsDto.getRfidLepIssueModel().getDailyTransportReportModule().getVehicleMaster().getVehicleRegistrationNumber();
                            String commodity = transactionsDto.getRfidLepIssueModel().getDailyTransportReportModule().getSapGrnDetailsEntity().getDescription();
                            String batchNumber = transactionsDto.getRfidLepIssueModel().getDailyTransportReportModule().getSapGrnDetailsEntity().getBatch();
                            String wareHouseCode = transactionsDto.getWarehouse().getStrLocationCode();
                            String wareHouseDesc = transactionsDto.getWarehouse().getStrLocationDesc();
                            String strInUnloadingTime = transactionsDto.getInUnLoadingTime();
                            String previousRmgNo = null;
                            String PreviousRmgNoDesc = null;
                            String remarks = null;

                            if (strInUnloadingTime != null) {
                                if (isLoadingDifferenceEnable) {
                                    if (!getCalculateDate(strInUnloadingTime, "Buffer time between loading In - loading Out is 3 min")) {
                                        return;
                                    }
                                }
                                if (transactionsDto.getPriviousWarehouse().getStrLocationCode() != null && transactionsDto.getPriviousWarehouse().getStrLocationDesc() != null) {
                                    previousRmgNo = transactionsDto.getPriviousWarehouse().getStrLocationCode();
                                    PreviousRmgNoDesc = transactionsDto.getPriviousWarehouse().getStrLocationDesc();
                                    if (transactionsDto.getRemarkMaster() != null) {
                                        remarks = transactionsDto.getRemarkMaster().getRemarks();
                                    }
                                }
                            }

                            if (loginUserRole.equalsIgnoreCase(ROLES_CWH)) {
                                String GrossWeight = String.valueOf(transactionsDto.getGrossWeight());
                                saveCilWareHouseInfo(lepNo, lepNoId, rfidTag, driverName, truckNo, commodity, GrossWeight, previousRmgNo, PreviousRmgNoDesc, null, null, strInUnloadingTime, wareHouseCode, wareHouseDesc, remarks, batchNumber, "authorized");
                            } else {
                                ((MainActivity) requireActivity()).alert(requireContext(), DIALOG_WARNING, "Something went wrong", null, BTN_OK, false);
                            }
                        } catch (Exception e) {
                            ((MainActivity) requireActivity()).alert(requireContext(), DIALOG_WARNING, "Exception occurs while fetching CIL unloading details", "Exception : " + e.getMessage(), BTN_OK, false);
                        }
                    } else if (response.body().getStatus().equalsIgnoreCase(RESPONSE_FORBIDDEN)) {
                        if (response.body().getTransactionsDto() != null) {
                            TransactionsDto transactionsDto = response.body().getTransactionsDto();
                            try {
                                String lepNo = transactionsDto.getRfidLepIssueModel().getLepNumber();
                                String lepNoId = String.valueOf(transactionsDto.getRfidLepIssueModel().getId());
                                String rfidTag = transactionsDto.getRfidLepIssueModel().getRfidMaster().getRfidNumber();
                                String driverName = transactionsDto.getRfidLepIssueModel().getDriverMaster().getDriverName();
                                String truckNo = transactionsDto.getRfidLepIssueModel().getDailyTransportReportModule().getVehicleMaster().getVehicleRegistrationNumber();
                                String commodity = transactionsDto.getRfidLepIssueModel().getDailyTransportReportModule().getSapGrnDetailsEntity().getDescription();
                                String batchNumber = transactionsDto.getRfidLepIssueModel().getDailyTransportReportModule().getSapGrnDetailsEntity().getBatch();
                                String wareHouseCode = transactionsDto.getWarehouse().getStrLocationCode();
                                String wareHouseDesc = transactionsDto.getWarehouse().getStrLocationDesc();
                                String strInUnloadingTime = transactionsDto.getInUnLoadingTime();
                                String previousRmgNo = null;
                                String PreviousRmgNoDesc = null;
                                String remarks = null;

                                if (strInUnloadingTime != null) {
                                    if (isLoadingDifferenceEnable) {
                                        if (!getCalculateDate(strInUnloadingTime, "Buffer time between loading In - loading Out is 3 min")) {
                                            return;
                                        }
                                    }
                                    if (transactionsDto.getPriviousWarehouse().getStrLocationCode() != null && transactionsDto.getPriviousWarehouse().getStrLocationDesc() != null) {
                                        previousRmgNo = transactionsDto.getPriviousWarehouse().getStrLocationCode();
                                        PreviousRmgNoDesc = transactionsDto.getPriviousWarehouse().getStrLocationDesc();
                                        if (transactionsDto.getRemarkMaster() != null) {
                                            remarks = transactionsDto.getRemarkMaster().getRemarks();
                                        }
                                    }
                                }

                                if (loginUserRole.equalsIgnoreCase(ROLES_CWH)) {
                                    String GrossWeight = String.valueOf(transactionsDto.getGrossWeight());
                                    if (strInUnloadingTime != null) {
                                        saveCilWareHouseInfo(lepNo, lepNoId, rfidTag, driverName, truckNo, commodity, GrossWeight, previousRmgNo, PreviousRmgNoDesc, null, null, strInUnloadingTime, wareHouseCode, wareHouseDesc, remarks, batchNumber, "authorized");
                                    } else {
                                        List<String> location = getLoginUserAssignedLocation();
//                                        warehouseAlert(requireActivity(), "Planned LEP location " + wareHouseCode + " - " + wareHouseDesc + " is not assign to user " + loginUserName.toUpperCase() + "\n" + location + "\nAre you still want to continue with your assign warehouse ?", 1, lepNo, lepNoId, rfidTag, driverName, truckNo, commodity, GrossWeight, previousRmgNo, PreviousRmgNoDesc, null, null, strInUnloadingTime, wareHouseCode, wareHouseDesc, remarks, batchNumber, strInUnloadingTime);
                                        warehouseAlert(requireActivity(), "Access Denied for User "+ loginUserName + ", As he does not have permission to receive cargo into LEP location code " + wareHouseCode + "\n"+ "\nYou still want to receive cargo into your assigned LEP locations " + location + " ?", 1, lepNo, lepNoId, rfidTag, driverName, truckNo, commodity, GrossWeight, previousRmgNo, PreviousRmgNoDesc, null, null, strInUnloadingTime, wareHouseCode, wareHouseDesc, remarks, batchNumber, strInUnloadingTime);
//                                        warehouseAlert(requireActivity(), response.body().getMessage() + "\nYour assign warehouse location are " + location + "\n" + "\nAre you still want to continue with your assign warehouse ?", 1, lepNo, lepNoId, rfidTag, driverName, truckNo, commodity, GrossWeight, previousRmgNo, PreviousRmgNoDesc, null, null, strInUnloadingTime, wareHouseCode, wareHouseDesc, remarks, batchNumber, strInUnloadingTime);
                                    }
                                } else {
                                    ((MainActivity) requireActivity()).alert(requireContext(), DIALOG_WARNING, "Something went wrong", null, BTN_OK, false);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                ((MainActivity) requireActivity()).alert(requireContext(), DIALOG_WARNING, "Exception occurs while fetching CIL unloading details", "Exception : " + e.getMessage(), BTN_OK, false);
                            }
                        }

//                        ((MainActivity) requireActivity()).alert(requireActivity(), DIALOG_WARNING, response.body().getMessage(), null, BTN_OK, false);
                    } else if (response.body().getStatus().equalsIgnoreCase(RESPONSE_ALREADY_REPORTED)) {
                        ((MainActivity) requireActivity()).alert(requireActivity(), DIALOG_WARNING, response.body().getMessage(), null, BTN_OK, false);
                    } else {
                        ((MainActivity) requireActivity()).alert(requireActivity(), DIALOG_ERROR, response.body().getMessage(), null, BTN_OK, false);
                    }
                } else {
                    ((MainActivity) requireActivity()).alert(requireActivity(), DIALOG_ERROR, NULL_VALUE_RESPONSE, null, BTN_OK, false);
                }
            }

            @Override
            public void onFailure(Call<TransactionsApiResponse> call, Throwable t) {
                hideProgress();
                ((MainActivity) requireActivity()).alert(requireActivity(), DIALOG_ERROR, CustomErrorMessage.setErrorMessage(t.getMessage()), null, BTN_OK, false);
            }
        });
    }

    private void saveLoadingInfo(String rfidTag, String lepNo, String lepNoId, String driverName, String driverMobileNo, String driverLicenseNo, String truckNo, String vesselName, String commodity, String strDestinationCode, String strDestinationDesc, String inLoadingTime, String pinnacleSupervisor, String bothraSupervisor, String BerthNumber, String batchNumber, String grSrcLoc, String grSrcLocDesc, String bTareWeight) {
        SharedPreferences sp = requireActivity().getSharedPreferences("loadingAdviceDetails", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("rfidTagSPK", rfidTag).apply();
        editor.putString("lepNoSPK", lepNo).apply();
        editor.putString("lepNoIdSPK", lepNoId).apply();
        editor.putString("driverNameSPK", driverName).apply();
        editor.putString("driverMobileNoSPK", driverMobileNo).apply();
        editor.putString("driverLicenseNoSPK", driverLicenseNo).apply();
        editor.putString("truckNoSPK", truckNo).apply();
        editor.putString("vesselNameSPK", vesselName).apply();
        editor.putString("commoditySPK", commodity).apply();
        editor.putString("strDestinationCodeSPK", strDestinationCode).apply();
        editor.putString("getInloadingTimeSPK", inLoadingTime).apply();
        editor.putString("pinnacleSupervisorSPK", pinnacleSupervisor).apply();
        editor.putString("bothraSupervisorSPK", bothraSupervisor).apply();
        editor.putString("BerthNumberSPK", BerthNumber).apply();
        editor.putString("batchNumberSPK", batchNumber).apply();
        editor.putString("strDestinationDescSPK", strDestinationDesc).apply();
        editor.putString("grSrcLocSPK", grSrcLoc).apply();
        editor.putString("grSrcLocDescSPK", grSrcLocDesc).apply();
        editor.putString("bTareWeightSPK", bTareWeight).apply();
        ((MainActivity) requireActivity()).loadFragment(new LoadingAdviseFragment(), 1);
    }

    private void saveCilWareHouseInfo(String lepNo, String lepNoId, String rfidTag, String driverName, String truckNo, String commodity, String GrossWeight, String previousRmgNo, String PreviousRmgNoDesc, String sourceGrossWeight, String vehicleInTime, String inUnloadingTime, String wareHouseCode, String wareHouseDesc, String remarks, String batchNumber, String userType) {
        SharedPreferences sp = requireActivity().getSharedPreferences("WareHouseDetails", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("rfidTagSPK", rfidTag).apply();
        editor.putString("lepNoSPK", lepNo).apply();
        editor.putString("inUnloadingTimeSPK", inUnloadingTime).apply();
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
        editor.putString("batchNumberSPK", batchNumber).apply();
        editor.putString("userTypeSPK", userType).apply();
        ((MainActivity) requireActivity()).loadFragment(new CWHFragment(), 1);
    }

    private void saveBothraWareHouseInfo(String lepNo, String lepNoId, String rfidTag, String driverName, String truckNo, String commodity, String GrossWeight, String previousRmgNo, String PreviousRmgNoDesc, String sourceGrossWeight, String vehicleInTime, String inUnloadingTime, String wareHouseCode, String wareHouseDesc, String remarks, String batchNumber, String userType) {
        SharedPreferences sp = requireActivity().getSharedPreferences("WareHouseDetails", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("rfidTagSPK", rfidTag).apply();
        editor.putString("lepNoSPK", lepNo).apply();
        editor.putString("inUnloadingTimeSPK", inUnloadingTime).apply();
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
        editor.putString("batchNumberSPK", batchNumber).apply();
        editor.putString("userTypeSPK", userType).apply();
        ((MainActivity) requireActivity()).loadFragment(new BWHFragment(), 1);
    }

    public boolean isRFIDHandleEnable() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        return sharedPreferences.getBoolean("enable_rfid_handle", true);
    }

/*    private void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
        btnVerify.setEnabled(false);
        requireActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void hideProgress() {
        progressBar.setVisibility(View.GONE);
        btnVerify.setEnabled(true);
        requireActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }*/

    private void showProgress() {
        btnVerify.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);
        colorOverlay.setVisibility(View.VISIBLE);
        requireActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        rootLayout.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.dusk_scanner));
    }

    private void hideProgress() {
        btnVerify.setEnabled(true);
        progressBar.setVisibility(View.GONE);
        colorOverlay.setVisibility(View.GONE);
        requireActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        rootLayout.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.normal_scanner));
    }

    @Override
    public void readerConnectionStatus(String name, Boolean status) {
        SettingsFragment s = new SettingsFragment();
        if (!status) {
            s.updateSwitchPreferenceValue(false);
            String text = "Error : Rfid Handle is not connected";
            errorHandle.setText(text);
            llErrorLayout.setVisibility(View.VISIBLE);
            if (name != null) {
                ((MainActivity) requireActivity()).alert(requireContext(), DIALOG_ERROR, name, "Try reattaching the handle", "OK", false);
            } else {
                ((MainActivity) requireActivity()).alert(requireContext(), DIALOG_ERROR, "RFID handle not found", "Try reattaching the handle", "OK", false);
            }
        } else {
            llErrorLayout.setVisibility(View.GONE);
            s.updateSwitchPreferenceValue(true);
        }
    }

    public String getScreenDetails() {
        SharedPreferences sp = requireActivity().getSharedPreferences("adminScreen", MODE_PRIVATE);
        return sp.getString("screen", null);
    }

    private boolean isLoadingDifferenceEnable() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        return sharedPreferences.getBoolean("enable_loading_difference", true);
    }

    private LocalDateTime getTruncatedDate(String localDateTime) {
        LocalDateTime now = LocalDateTime.parse(localDateTime);
        LocalDateTime truncatedNow = now.truncatedTo(ChronoUnit.SECONDS);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        String formattedDateTime = truncatedNow.format(formatter);
        return LocalDateTime.parse(formattedDateTime);
    }

    private String getTruncatedDateTime(String localDateTime) {
        LocalDateTime now = LocalDateTime.parse(localDateTime);
        LocalDateTime truncatedNow = now.truncatedTo(ChronoUnit.SECONDS);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        return truncatedNow.format(formatter);
    }

    private boolean getCalculateDate(String strPrimaryTime, String initialMessage) {
        try {
            LocalDateTime inTime = getTruncatedDate(strPrimaryTime);
            LocalDateTime outTime = getTruncatedDate(LocalDateTime.now().toString());
            Duration duration = Duration.between(inTime, outTime);
            long totalSeconds = duration.getSeconds();

            if (totalSeconds < 180) {
                long remainingTime = 180 - totalSeconds;
                ((MainActivity) getActivity()).alert(getActivity(), DIALOG_WARNING, initialMessage, "Please wait for " + remainingTime + " second and then try again", "OK", false);
                return false;
            } else {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            ((MainActivity) requireActivity()).alert(requireActivity(), DIALOG_WARNING, "Exception occurs .. !", e.getMessage(), BTN_OK, false);
            return false;
        }
    }

    public void warehouseAlert(Context context, String dialogMessage, int locationFlag, String lepNo, String lepNoId, String rfidTag, String driverName, String truckNo, String commodity, String GrossWeight, String previousRmgNo, String PreviousRmgNoDesc, String sourceGrossWeight, String vehicleInTime, String inUnloadingTime, String wareHouseCode, String wareHouseDesc, String remarks, String batchNumber, String strInUnloadingTime) {
        Dialog dialog = new Dialog(context);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setContentView(R.layout.dialog_questionary);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        TextView dialogMessageTxt = dialog.findViewById(R.id.dialog_message);
        TextView btnCancel = dialog.findViewById(R.id.txt_btn_cancel);
        TextView btnOk = dialog.findViewById(R.id.txt_btn_ok);
        dialogMessageTxt.setText(dialogMessage);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edtRfidTagId.setText(null);
                dialog.dismiss();
            }
        });

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                if (locationFlag == 1) {
                    saveCilWareHouseInfo(lepNo, lepNoId, rfidTag, driverName, truckNo, commodity, GrossWeight, previousRmgNo, PreviousRmgNoDesc, null, null, strInUnloadingTime, wareHouseCode, wareHouseDesc, remarks, batchNumber, "unAuthorizedUser");
//                    ((MainActivity) requireActivity()).loadFragment(new CWHFragment(), 1);
                } else if (locationFlag == 2) {
                    saveBothraWareHouseInfo(lepNo, lepNoId, rfidTag, driverName, truckNo, commodity, null, previousRmgNo, PreviousRmgNoDesc, sourceGrossWeight, null, strInUnloadingTime, wareHouseCode, wareHouseDesc, remarks, batchNumber, "unAuthorizedUser");
                } else {
                    Log.e(TAG, "onClick: Wrong flag is passed");
                }
            }
        });

        dialog.show();
    }

    private List<String> getLoginUserAssignedLocation() {
        ArrayList<String> arrayList = new ArrayList<>();
        String storageLocation = ((MainActivity) requireActivity()).destinationLocationDtoList();
        Type listType = new TypeToken<List<GenericData>>() {
        }.getType();
        List<GenericData> parsedDestinationList = new Gson().fromJson(storageLocation, listType);
        for (GenericData name : parsedDestinationList) {
            String s = name.getValue();
            arrayList.add(s);
            if (arrayList.size() == 6) {
                arrayList.add("...");
                return arrayList;

            }
        }
        return arrayList;
    }
}