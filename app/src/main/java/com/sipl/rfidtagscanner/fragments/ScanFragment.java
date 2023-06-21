package com.sipl.rfidtagscanner.fragments;

import static android.content.Context.MODE_PRIVATE;
import static android.content.Context.VIBRATOR_SERVICE;
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
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.sipl.rfidtagscanner.dto.dtos.StorageLocationDto;
import com.sipl.rfidtagscanner.dto.dtos.TransactionsDto;
import com.sipl.rfidtagscanner.dto.response.RfidLepApiResponse;
import com.sipl.rfidtagscanner.dto.response.RmgNumberApiResponse;
import com.sipl.rfidtagscanner.dto.response.TransactionsApiResponse;
import com.sipl.rfidtagscanner.interf.MyListener;
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

public class ScanFragment extends Fragment implements MyListener {

    private static final String TAG = "ConnectFragment";
    private int viewId;
    private ArrayList<String> arrDestinationLocation;
    private ProgressBar progressBar;
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
    private RfidHandler rfidHandler;
    private String loginUserRole;
    private String loginUserToken;
    private String loginUserStorageLocation;
    private TextView errorHandle;
    private LinearLayout error_layout;
    private String admin_String = null;

    public ScanFragment(int viewId1) {
        this.viewId = viewId1;
    }

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
//        this.loginUserPlantCode = ((MainActivity) getActivity()).getLoginUserPlantCode();
        this.admin_String = getScreenDetails();
        Log.i(TAG, "onCreateView: admin_String : " + admin_String);
        this.loginUserStorageLocation = ((MainActivity) getActivity()).getLoginUserStorageCode();

        Button btnVerify = view.findViewById(R.id.sf_btn_verify);
        Boolean value = isRFIDHandleEnable();
        Log.i(TAG, "onCreateView: " + value);
        if (value) {
            Log.i(TAG, "onCreateView:  handle device");
            edtRfidTagId.setEnabled(false);
            rfidHandler = new RfidHandler(requireActivity());
            rfidHandler.InitSDK(this);
        } else {
            edtRfidTagId.setEnabled(true);
            Log.i(TAG, "onCreateView: no handle device");
        }
        getWareHouseStorage();

        btnVerify.setOnClickListener(view1 -> {
            vibrate();
            RfidDetailsLoadingAdvise();
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

    private void getRfidTagDetailCoromandelLA() {
        Log.i(TAG, "getRfidTagDetailCoromandelLA: ");
        progressBar.setVisibility(View.VISIBLE);
        try {
            Call<RfidLepApiResponse> call = RetrofitController.getInstances(requireContext()).getLoadingAdviseApi().getRfidTagDetailCoromandelLA("Bearer " + loginUserToken, edtRfidTagId.getText().toString());
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
                            String destinationLocationDesc = rfidLepIssueDto.getDestinationLocation().getStrLocationDesc();

                            Log.i(TAG, "onResponse: destinationLocation : " + destinationLocation);
                            Log.i(TAG, "onResponse: destinationLocationDesc : " + destinationLocationDesc);

                            String role = ((MainActivity) requireActivity()).getLoginUserRole();
                            if (role.equalsIgnoreCase(ROLES_LAO)) {
                                Log.i(TAG, "onResponse: before share pref");
                                saveLADataSharedPref(rfidTag, lepNo, lepNoId, driverName, driverMobileNo, driverLicenseNo, truckNo, sapGrNo, vesselName, truckCapacity, commodity, destinationLocation, destinationLocationDesc);
                                ((MainActivity) requireActivity()).loadFragment(new LoadingAdviseFragment(), 1);
                            }

                        } catch (Exception e) {
                            e.getMessage();
                            return;
                        }
                    } else {
                        progressBar.setVisibility(View.GONE);
                        Log.i(TAG, "onResponse: " + response.raw());
                        ((MainActivity) requireActivity()).alert(requireContext(), "WARNING", response.body().getMessage(), null, "OK");
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

    private void saveLADataSharedPref(String rfidTag, String lepNo, String lepNoId, String driverName, String driverMobileNo, String driverLicenseNo, String truckNo, String sapGrNo, String vesselName, String truckCapacity, String commodity, String strDestinationCode, String strDestinationDesc) {
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
        Log.i(TAG, "saveLADataSharedPref: strDestinationCode : " + strDestinationCode);
        Log.i(TAG, "saveLADataSharedPref: strDestinationDesc : " + strDestinationDesc);
        editor.putString("strDestinationDescSPK", strDestinationDesc).apply();
        editor.apply();
    }

    private void saveWHDataToSharedPref(String lepNo, String lepNoId, String rfidTag, String driverName, String truckNo, String commodity, String GrossWeight, String previousRmgNo, String PreviousRmgNoDesc, String sourceGrossWeight, String isWeighbridgeAvailable, Integer callFrom, String vehicleInTime) {
        SharedPreferences sp = requireActivity().getSharedPreferences("WareHouseDetails", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        Log.i(TAG, "saveWHDataToSharedPref: in sherfPrench data list");
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
        editor.putString("isWeighbridgeAvailableSPK", isWeighbridgeAvailable).apply();
        editor.putInt("callFromSPK", callFrom).apply();
        editor.putString("vehicleInTimeSPK", vehicleInTime).apply();
        editor.apply();
    }

    private void getWareHouseDetails() {
        Log.i(TAG, "getWareHouseDetails: (Start)");
        progressBar.setVisibility(View.VISIBLE);
        try {
            if (loginUserRole.equalsIgnoreCase(ROLES_BWH) || (loginUserRole.equalsIgnoreCase(ROLES_ADMIN_PLANT) && (admin_String.equalsIgnoreCase("bothra")))) {
                Log.i(TAG, "getWareHouseDetails: in bothra whs and roles and admin_string : " + loginUserRole + " " + admin_String);
                getBothraWareHouseDetails();
            } else if (loginUserRole.equalsIgnoreCase(ROLES_CWH) || (loginUserRole.equalsIgnoreCase(ROLES_ADMIN_PLANT) && (admin_String.equalsIgnoreCase("coromandel")))) {
                Log.i(TAG, "getWareHouseDetails: in coromandel whs  and roles and admin_string :  "+ loginUserRole + " " + admin_String);
                Call<TransactionsApiResponse> call = RetrofitController.getInstances(requireContext()).getLoadingAdviseApi().getCoromandelWHDetails("Bearer " + loginUserToken, "4", "3", edtRfidTagId.getText().toString());

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
                                String rfidTag = transactionsDto.getRfidLepIssueModel().getRfidMaster().getRfidNumber();
                                String driverName = transactionsDto.getRfidLepIssueModel().getDriverMaster().getDriverName();
                                String truckNo = transactionsDto.getRfidLepIssueModel().getDailyTransportReportModule().getTruckNumber();
                                String commodity = transactionsDto.getRfidLepIssueModel().getDailyTransportReportModule().getCommodity();
                                String previousRmgNo = transactionsDto.getFunctionalLocationDestinationMaster().getStrLocationCode();
                                String PreviousRmgNoDesc = transactionsDto.getFunctionalLocationDestinationMaster().getStrLocationDesc();
                                String destinationLocationByUIcode = transactionsDto.getWarehouse().getStrLocationCode();
                                String destinationLocationByUIdesc = transactionsDto.getWarehouse().getStrLocationDesc();


//                                Boolean isWeighBridgeAvailble = transactionsDto.getFunctionalLocationDestinationMaster().getWbAvailable();


                                if (loginUserRole.equalsIgnoreCase(ROLES_CWH)) {
                                    String GrossWeight = String.valueOf(transactionsDto.getGrossWeight());
                                    if (destinationLocationByUIcode != null) {
                                        saveWHDataToSharedPref(lepNo, lepNoId, rfidTag, driverName, truckNo, commodity, GrossWeight, destinationLocationByUIcode, destinationLocationByUIdesc, null, null, 0, null);
                                        ((MainActivity) requireActivity()).loadFragment(new CWHFragment(), 1);
                                    } else {
                                        saveWHDataToSharedPref(lepNo, lepNoId, rfidTag, driverName, truckNo, commodity, GrossWeight, previousRmgNo, PreviousRmgNoDesc, null, null, 0, null);
                                        ((MainActivity) requireActivity()).loadFragment(new CWHFragment(), 1);
                                    }
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
                            Log.i(TAG, "onResponse: " + response.raw());
                            ((MainActivity) getActivity()).alert(getActivity(), "warning", response.body().getMessage(), null, "OK");
                        }
                    }

                    @Override
                    public void onFailure(Call<TransactionsApiResponse> call, Throwable t) {
                        progressBar.setVisibility(View.GONE);
                        ((MainActivity) getActivity()).alert(getActivity(), "error", t.getMessage(), null, "OK");
                    }
                });
            } else {
                progressBar.setVisibility(View.GONE);
                Log.i(TAG, "getWareHouseDetails: method not call wh");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getBothraWareHouseDetails() {
        Log.i(TAG, "getBothraWareHouseDetails: in bothra whs");
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
                                String previousRmgNo = transactionsDto.getFunctionalLocationDestinationMaster().getStrLocationCode();
                                String PreviousRmgNoDesc = transactionsDto.getFunctionalLocationDestinationMaster().getStrLocationDesc();
                                String isWeighBridgeAvailble = String.valueOf(transactionsDto.getFunctionalLocationDestinationMaster().getWbAvailable());
                                String destinationLocationByUIcode = transactionsDto.getWarehouse().getStrLocationCode();
                                String destinationLocationByUIdesc = transactionsDto.getWarehouse().getStrLocationDesc();
                                String destinationLocationByUIWEighbridgedesc = String.valueOf(transactionsDto.getWarehouse().getWbAvailable());

                                Log.i(TAG, "onResponse: response data ge fet successfully");
                                if (loginUserRole.equalsIgnoreCase(ROLES_BWH)) {
                                    String sourceGrossWeight = String.valueOf(transactionsDto.getSourceGrossWeight());
                                    if (destinationLocationByUIcode != null) {
                                        saveWHDataToSharedPref(lepNo, lepNoId, rfidTag, driverName, truckNo, commodity, null, destinationLocationByUIcode, destinationLocationByUIdesc, sourceGrossWeight, destinationLocationByUIWEighbridgedesc, 1, null);
                                    } else {
                                        saveWHDataToSharedPref(lepNo, lepNoId, rfidTag, driverName, truckNo, commodity, null, previousRmgNo, PreviousRmgNoDesc, sourceGrossWeight, isWeighBridgeAvailble, 1, null);
                                    }
                                    Log.i(TAG, "onResponse: before load fragment method");
                                    ((MainActivity) requireActivity()).loadFragment(new BWHFragment(), 1);
                                } else {
                                    Log.i(TAG, "onResponse: in else roles other than ROLES_BWS : " + ROLES_BWH);
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
                            Log.i(TAG, "onResponse: in else bothra2 url");
                            getBothraWareHouseDetails2();
                        }
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

    private void getBothraWareHouseDetails2() {
        Log.i(TAG, "getBothraWareHouseDetails2: in bothra 2 url method");
        progressBar.setVisibility(View.VISIBLE);
        try {
            Call<TransactionsApiResponse> call = RetrofitController.getInstances(requireContext()).getLoadingAdviseApi().getBothraWHDetailsForExit("Bearer " + loginUserToken, "8", edtRfidTagId.getText().toString());
            call.enqueue(new Callback<TransactionsApiResponse>() {
                @Override
                public void onResponse(Call<TransactionsApiResponse> call, Response<TransactionsApiResponse> response) {
                    if (!response.isSuccessful()) {
                        progressBar.setVisibility(View.GONE);
                        ((MainActivity) getActivity()).alert(getActivity(), "error", response.errorBody().toString(), null, "OK");
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
                            String previousRmgNo = transactionsDto.getFunctionalLocationDestinationMaster().getStrLocationCode();
                            String PreviousRmgNoDesc = transactionsDto.getFunctionalLocationDestinationMaster().getStrLocationDesc();
                            String isWeighBridgeAvailble = String.valueOf(transactionsDto.getFunctionalLocationDestinationMaster().getWbAvailable());
                            String strWareHouseCode = transactionsDto.getWarehouse().getStrLocationCode();
                            String strWareHouseCodeDesc = transactionsDto.getWarehouse().getStrLocationDesc();
                            String strWbAvailable = String.valueOf(transactionsDto.getWarehouse().getWbAvailable());


                            String strEntryTime = transactionsDto.getVehicleInTime();
                            LocalDateTime aLDT = LocalDateTime.parse(strEntryTime);
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
                            String entryTime = aLDT.format(formatter);


                            if (loginUserRole.equalsIgnoreCase(ROLES_BWH)) {
                                String sourceGrossWeight = String.valueOf(transactionsDto.getSourceGrossWeight());
                                if (strWareHouseCode != null) {
                                    saveWHDataToSharedPref(lepNo, lepNoId, rfidTag, driverName, truckNo, commodity, null, strWareHouseCode, strWareHouseCodeDesc, sourceGrossWeight, strWbAvailable, 2, entryTime);
                                } else {
                                    saveWHDataToSharedPref(lepNo, lepNoId, rfidTag, driverName, truckNo, commodity, null, previousRmgNo, PreviousRmgNoDesc, sourceGrossWeight, isWeighBridgeAvailble, 2, entryTime);
                                }
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
                        Log.i(TAG, "onResponse: " + response.raw());
                        ((MainActivity) getActivity()).alert(getActivity(), "warning", response.body().getMessage(), null, "OK");
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
        Log.i(TAG, "RfidDetailsLoadingAdvise: btnVerify is clicked ");
        if (loginUserRole.equalsIgnoreCase(ROLES_LAO) || (loginUserRole.equalsIgnoreCase(ROLES_ADMIN_PLANT)) && (admin_String.equalsIgnoreCase("loadingAdvise"))) {
            Log.i(TAG, "RfidDetailsLoadingAdvise: admin screen plus roles : " + loginUserRole + " " + admin_String);
            if (arrDestinationLocation.contains(loginUserStorageLocation)) {
                getRfidTagDetailBothraLA();
            } else {
                getRfidTagDetailCoromandelLA();
            }
        } else {
            Log.i(TAG, "RfidDetailsLoadingAdvise: in else going for whs");
            getWareHouseDetails();
        }
    }

    private void getRfidTagDetailBothraLA() {
        Log.i(TAG, "getRfidTagDetailBothraLA: ");
        progressBar.setVisibility(View.VISIBLE);
        try {
            Call<TransactionsApiResponse> call = RetrofitController.getInstances(requireContext()).getLoadingAdviseApi().getRfidTagDetailBothraLA("Bearer " + loginUserToken, "12", "11", edtRfidTagId.getText().toString());
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
                            String wareHouseCode = transactionsDto.getWarehouse().getStrLocationCode();
                            String wareHouseCodeDesc = transactionsDto.getWarehouse().getStrLocationDesc();

                            Log.i(TAG, "onResponse: wareHouseCode : " + wareHouseCode);
                            Log.i(TAG, "onResponse: wareHouseCodeDesc : " + wareHouseCodeDesc);

                            Log.i(TAG, "onResponse: destinationLocation : " + destinationLocation);
                            Log.i(TAG, "onResponse: destinationLocationDesc : " + destinationLocationDesc);

                            if (loginUserRole.equalsIgnoreCase(ROLES_LAO)) {
                                saveLADataSharedPref(rfidTag, lepNo, lepNoId, driverName, driverMobileNo, driverLicenseNo, truckNo, sapGrNo, vesselName, truckCapacity, commodity, destinationLocation, destinationLocationDesc);
                                ((MainActivity) requireActivity()).loadFragment(new LoadingAdviseFragment(), 1);
                            }
                        } catch (Exception e) {
                            e.getMessage();
                            return;
                        }
                    } else {
                        progressBar.setVisibility(View.GONE);
                        Log.i(TAG, "onResponse: " + response.raw());
                        ((MainActivity) getActivity()).alert(getActivity(), "warning", response.body().getMessage(), null, "OK");
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

    private boolean getWareHouseStorage() {
        Log.i("getWareHouseStorage", "getAllWareHouse: ()");
        progressBar.setVisibility(View.VISIBLE);
        Call<RmgNumberApiResponse> call = RetrofitController.getInstances(requireContext()).getLoadingAdviseApi().
                getAllWareHouse("Bearer " + loginUserToken, "bothra");

        call.enqueue(new Callback<RmgNumberApiResponse>() {
            @Override
            public void onResponse(Call<RmgNumberApiResponse> call, Response<RmgNumberApiResponse> response) {
                if (!response.isSuccessful()) {
                    progressBar.setVisibility(View.GONE);
                    ((MainActivity) getActivity()).alert(getActivity(), "error", response.errorBody().toString(), null, "OK");
                    return;
                }
                Log.i("getWareHouseStorage", "onResponse: getAllWareHouse : responseCode : " + response.code());
                if (response.isSuccessful()) {
                    progressBar.setVisibility(View.GONE);
                    Log.i("getWareHouseStorage", "onResponse: getAllWareHouse " + response.code());
                    List<StorageLocationDto> functionalLocationMasterDtoList = response.body().getStorageLocationDtos();
                    arrDestinationLocation = new ArrayList<>();
                    try {
                        if (functionalLocationMasterDtoList == null || functionalLocationMasterDtoList.isEmpty()) {
                            return;
                        }
                        SharedPreferences sp = requireActivity().getSharedPreferences("bothraStrLocation", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp.edit();
                        for (int i = 0; i < functionalLocationMasterDtoList.size(); i++) {
                            String s = functionalLocationMasterDtoList.get(i).getStrLocationCode();
                            editor.putString(String.valueOf(i), s).apply();
                            Log.i("getWareHouseStorage", "onResponse: " + i + "   " + s);
                            arrDestinationLocation.add(s);
                        }
                        editor.putString("size", String.valueOf(arrDestinationLocation.size())).apply();

                    } catch (Exception e) {
                        e.getMessage();
                    }
                }
            }

            @Override
            public void onFailure(Call<RmgNumberApiResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                ((MainActivity) getActivity()).alert(getActivity(), "error", t.getMessage(), null, "OK");
            }
        });
        return true;
    }

    public boolean isRFIDHandleEnable() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        if (sharedPreferences.contains("enable_rfid_handle")) {
            Boolean value = sharedPreferences.getBoolean("enable_rfid_handle", false);
            return value;
        }
        return false;
    }

    @Override
    public void onNotConnectedTpHandle(String name, Boolean status) {
        SettingsFragment s = new SettingsFragment();
        if (!status) {

            s.updateSwitchPreferenceValue(false);
            String text = "Error : Rfid Handle is not connected";
            errorHandle.setText(text);
            error_layout.setVisibility(View.VISIBLE);
            ((MainActivity) requireActivity()).alert(requireContext(), "ERROR", name, "Try re attaching the handle", "OK");
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