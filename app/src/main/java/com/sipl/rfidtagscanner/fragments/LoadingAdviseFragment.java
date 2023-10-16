package com.sipl.rfidtagscanner.fragments;

import static android.content.Context.MODE_PRIVATE;
import static com.sipl.rfidtagscanner.utils.Config.BTN_OK;
import static com.sipl.rfidtagscanner.utils.Config.DIALOG_ERROR;
import static com.sipl.rfidtagscanner.utils.Config.DIALOG_SUCCESS;
import static com.sipl.rfidtagscanner.utils.Config.NULL_VALUE_RESPONSE;
import static com.sipl.rfidtagscanner.utils.Config.RESPONSE_CREATED;
import static com.sipl.rfidtagscanner.utils.Config.RESPONSE_FOUND;
import static com.sipl.rfidtagscanner.utils.Config.RESPONSE_NOT_FOUND;
import static com.sipl.rfidtagscanner.utils.Config.RESPONSE_OK;
import static com.sipl.rfidtagscanner.utils.Config.ROLES_B_LAO;

import android.content.SharedPreferences;
import android.os.Bundle;
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
import android.widget.TextClock;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.sipl.rfidtagscanner.MainActivity;
import com.sipl.rfidtagscanner.R;
import com.sipl.rfidtagscanner.RetrofitController;
import com.sipl.rfidtagscanner.dto.dtos.RfidLepIssueDto;
import com.sipl.rfidtagscanner.dto.dtos.StorageLocationDto;
import com.sipl.rfidtagscanner.dto.dtos.UserMasterDto;
import com.sipl.rfidtagscanner.dto.request.LoadingAdviseRequestDto;
import com.sipl.rfidtagscanner.dto.request.UpdateBothraLoadingAdviseDto;
import com.sipl.rfidtagscanner.dto.response.LoadingAdvisePostApiResponse;
import com.sipl.rfidtagscanner.dto.response.TransactionsApiResponse;
import com.sipl.rfidtagscanner.entites.AuditEntity;
import com.sipl.rfidtagscanner.utils.Concatenator;
import com.sipl.rfidtagscanner.utils.CustomErrorMessage;

import java.time.LocalDateTime;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoadingAdviseFragment extends Fragment {
    private static final String TAG = "TracingError";
    private Button btnSubmit;
    private TextClock clockInLoadingTime, clockOutLoadingTime;
    private TextView txtPinnacleSupervisor, txtBothraSupervisor;
    private EditText edtRfidTagNo, edtBerthNumber, edtLepNo, edtBatchNumber, edtTruckNumber, edtDriverName, edtDriverMobileNo, edtDriverLicenseNo, edtVesselName, edtCommodity, edtSourceLocation, edtTareWeight, edtLoadingInTime, edtDestinationLocation, edtBothraSupervisor, edtPinnacleSupervisor;
    private String loginUserName, loadingInTime, token, loginUserRoleId, selectedDestinationCode, grSourceLocation;
    private LinearLayout llBothraSupervisor, llPinnacleSupervisor, llBerthNumber, llStaticLoadingInTime, llLoadingInClock, llLoadingOutClock, llTareWeight;
    private Integer loginUserId, selectedLepNumberId;
    private ProgressBar progressBar;
    private FrameLayout rootLayout;
    private View colorOverlay;

    public LoadingAdviseFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_loading_adivse, container, false);
        txtBothraSupervisor = view.findViewById(R.id.la_txt_bothra_supervisor);
        txtPinnacleSupervisor = view.findViewById(R.id.la_txt_pinnacle_supervisor);
        edtPinnacleSupervisor = view.findViewById(R.id.la_edt_pinnacle_supervisor);
        edtBothraSupervisor = view.findViewById(R.id.la_edt_bothra_supervisor);
        edtDestinationLocation = view.findViewById(R.id.la_edt_destination_location);
        edtLoadingInTime = view.findViewById(R.id.la_edt_loading_in_time);
        edtSourceLocation = view.findViewById(R.id.la_edt_source_location);
        edtRfidTagNo = view.findViewById(R.id.la_edt_rfid_tag_no);
        edtLepNo = view.findViewById(R.id.la_edt_lep_number);
        edtTruckNumber = view.findViewById(R.id.la_edt_truck_no);
        edtDriverName = view.findViewById(R.id.la_edt_driver_name);
        edtDriverMobileNo = view.findViewById(R.id.la_edt_driver_mobile_no);
        edtDriverLicenseNo = view.findViewById(R.id.la_edt_driver_license_no);
        edtVesselName = view.findViewById(R.id.la_edt_vessel_name);
        edtCommodity = view.findViewById(R.id.la_edt_commodity);
        edtBerthNumber = view.findViewById(R.id.la_edt_berth_number);
        edtBatchNumber = view.findViewById(R.id.la_edt_batch_number);
        edtTareWeight = view.findViewById(R.id.la_edt_tare_weight);
        EditText edtLoadingSupervisor = view.findViewById(R.id.la_edt_loading_supervisor);

        llLoadingOutClock = view.findViewById(R.id.la_ll_loading_out_clock);
        llLoadingInClock = view.findViewById(R.id.la_ll_loading_in_clock);
        llTareWeight = view.findViewById(R.id.la_ll_tare_weight);
        llBothraSupervisor = view.findViewById(R.id.la_ll_bothra_supervisor);
        llPinnacleSupervisor = view.findViewById(R.id.la_ll_pinnacle_supervisor);
        llStaticLoadingInTime = view.findViewById(R.id.la_ll_static_loading_in_time);
        llBerthNumber = view.findViewById(R.id.la_ll_berth_number);
        progressBar = view.findViewById(R.id.la_progressBar);
        rootLayout = view.findViewById(R.id.la_root_layout);
        colorOverlay = view.findViewById(R.id.la_view);

        clockInLoadingTime = view.findViewById(R.id.la_clock_in_loading_time);
        clockOutLoadingTime = view.findViewById(R.id.la_clock_out_loading_time);

        btnSubmit = view.findViewById(R.id.la_btn_submit);
        Button btnCancel = view.findViewById(R.id.la_btn_cancel);

        String userId = ((MainActivity) requireActivity()).getUserId();
        if (userId != null) {
            this.loginUserId = Integer.parseInt(userId);
        }

        this.loginUserRoleId = (((MainActivity) requireActivity()).getRoleId());
        this.token = ((MainActivity) requireActivity()).getToken();
        this.loginUserName = ((MainActivity) requireActivity()).getUserName();
        edtLoadingSupervisor.setText(loginUserName);

        /*
         *  methods need to run on onCreate
         */
        updateUIBasedOnUser();
        makeTvTextCompulsory();
        displayClock();
        getLaRfidTagDetails();

        btnSubmit.setOnClickListener(view12 -> {
            if (validateInputDetail()) {
                chooseMethodToCall();
            }
        });
        btnCancel.setOnClickListener(view1 -> ((MainActivity) requireActivity()).loadFragment(new ScanFragment(), 1));

        return view;
    }

    private void updateUIBasedOnUser() {
        if (!loginUserRoleId.equalsIgnoreCase(ROLES_B_LAO)) {
            llBothraSupervisor.setVisibility(View.VISIBLE);
            llPinnacleSupervisor.setVisibility(View.VISIBLE);
            llBerthNumber.setVisibility(View.VISIBLE);
        } else {
            llBothraSupervisor.setVisibility(View.GONE);
            llPinnacleSupervisor.setVisibility(View.GONE);
            llTareWeight.setVisibility(View.VISIBLE);
            llBerthNumber.setVisibility(View.GONE);
        }
    }

    private void makeTvTextCompulsory() {
        Concatenator.multiStringConcatenate(txtBothraSupervisor, "Bothra \r\nSupervisor", " *");
        Concatenator.multiStringConcatenate(txtPinnacleSupervisor, "Pinnacle \r\nSupervisor", " *");
    }

    private void displayClock() {
        try {
            clockInLoadingTime.setFormat24Hour("dd-MM-yyyy hh:mm:ss");
            clockOutLoadingTime.setFormat24Hour("dd-MM-yyyy hh:mm:ss");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getLaRfidTagDetails() {
        SharedPreferences sp = requireActivity().getSharedPreferences("loadingAdviceDetails", MODE_PRIVATE);
        String lepNoId = sp.getString("lepNoIdSPK", null);
        if (lepNoId != null) {
            this.selectedLepNumberId = Integer.valueOf(lepNoId);
        }
        String rfidTagId = sp.getString("rfidTagSPK", null);
        String lepNo = sp.getString("lepNoSPK", null);
        String driverName = sp.getString("driverNameSPK", null);
        String driverMobileNo = sp.getString("driverMobileNoSPK", null);
        String driverLicenseNo = sp.getString("driverLicenseNoSPK", null);
        String truckNo = sp.getString("truckNoSPK", null);
        String vesselName = sp.getString("vesselNameSPK", null);
        String commodity = sp.getString("commoditySPK", null);
        String strDestinationCode = sp.getString("strDestinationCodeSPK", null);
        String strDestinationDesc = sp.getString("strDestinationDescSPK", null);
        String strPinnacleSupervisor = sp.getString("pinnacleSupervisorSPK", null);
        String strBothraSupervisor = sp.getString("bothraSupervisorSPK", null);
        String strBerthNumber = sp.getString("BerthNumberSPK", null);
        String strBatchNumber = sp.getString("batchNumberSPK", null);
        String grSrcLoc = sp.getString("grSrcLocSPK", null);
        this.grSourceLocation = grSrcLoc;
        String grSrcLocDesc = sp.getString("grSrcLocDescSPK", null);
        String bTareWeight = sp.getString("bTareWeightSPK", null);
        String strLoadingInTime = sp.getString("getInloadingTimeSPK", null);
        this.loadingInTime = strLoadingInTime;

        if (strLoadingInTime != null) {
            llStaticLoadingInTime.setVisibility(View.VISIBLE);
            llLoadingInClock.setVisibility(View.GONE);
            edtLoadingInTime.setText(strLoadingInTime);
            edtLoadingInTime.setEnabled(false);
            edtLoadingInTime.setBackgroundResource(R.drawable.rectangle_edt_read_only_field);
            edtPinnacleSupervisor.setText(strPinnacleSupervisor);
            edtPinnacleSupervisor.setEnabled(false);
            edtPinnacleSupervisor.setBackgroundResource(R.drawable.rectangle_edt_read_only_field);
            edtBothraSupervisor.setText(strBothraSupervisor);
            edtBothraSupervisor.setEnabled(false);
            edtBothraSupervisor.setBackgroundResource(R.drawable.rectangle_edt_read_only_field);
            llLoadingOutClock.setVisibility(View.VISIBLE);
        }
        String destinationLocation = strDestinationCode + " - " + strDestinationDesc;
        this.selectedDestinationCode = strDestinationCode;
        displayDataToUi(rfidTagId, lepNo, driverName, driverMobileNo, driverLicenseNo, truckNo, vesselName, commodity, destinationLocation, strBerthNumber, strBatchNumber, grSrcLoc, grSrcLocDesc, bTareWeight);
    }

    private boolean validateInputDetail() {
        if (!loginUserRoleId.equalsIgnoreCase(ROLES_B_LAO)) {
            if (loadingInTime == null) {
                if (edtPinnacleSupervisor.length() == 0) {
                    edtPinnacleSupervisor.setError("This field is required");
                    return false;
                }

                if (edtBothraSupervisor.length() == 0) {
                    edtBothraSupervisor.setError("This field is required");
                    return false;
                }
            }
        }
        return true;
    }

    private void chooseMethodToCall() {
        if (!loginUserRoleId.equalsIgnoreCase(ROLES_B_LAO)) {
            updateCilLoadingOut(initializeCilLARequestDto());
        } else {
            updateBothraLoadingAdvise(initializeBothraLARequestDto());
        }
    }

    private LoadingAdviseRequestDto initializeCilLARequestDto() {
        final Integer RSTAT = 1;
        final Integer FLAG = 1;
        AuditEntity auditEntity = new AuditEntity(loginUserName, null);
        RfidLepIssueDto rfidLepIssueModel = new RfidLepIssueDto(selectedLepNumberId);
        StorageLocationDto sourceMasterDto = new StorageLocationDto(grSourceLocation);
        UserMasterDto loadingAdviseDto = new UserMasterDto(loginUserId);
        StorageLocationDto functionalLocationMasterDto = new StorageLocationDto(selectedDestinationCode);
        String bothraSupervisor = edtBothraSupervisor.getText().toString();
        String pinnacleSupervisor = edtPinnacleSupervisor.getText().toString();
        return new LoadingAdviseRequestDto(auditEntity, bothraSupervisor, pinnacleSupervisor, loadingAdviseDto, sourceMasterDto, functionalLocationMasterDto, rfidLepIssueModel, FLAG, true, RSTAT, String.valueOf(LocalDateTime.now()), null);
    }

    private UpdateBothraLoadingAdviseDto initializeBothraLARequestDto() {
        final Integer BOTHRA_FLAG = 12;
        StorageLocationDto sourceMasterDto = new StorageLocationDto(grSourceLocation);
        UserMasterDto loadingAdviseDto = new UserMasterDto(loginUserId);
        RfidLepIssueDto rfidLepIssueModel = new RfidLepIssueDto(selectedLepNumberId);
        StorageLocationDto functionalLocationMasterDto = new StorageLocationDto(selectedDestinationCode);
        AuditEntity auditEntity = new AuditEntity(null, null, loginUserName, null);
        if (loadingInTime != null) {
            return new UpdateBothraLoadingAdviseDto(auditEntity, loadingAdviseDto, functionalLocationMasterDto, sourceMasterDto, rfidLepIssueModel, true, BOTHRA_FLAG, null, String.valueOf(LocalDateTime.now()));
        } else {
            return new UpdateBothraLoadingAdviseDto(auditEntity, loadingAdviseDto, functionalLocationMasterDto, sourceMasterDto, rfidLepIssueModel, true, BOTHRA_FLAG, String.valueOf(LocalDateTime.now()), null);
        }
    }

    private void updateCilLoadingOut(LoadingAdviseRequestDto loadingAdviseRequestDto) {
        showProgress();
        Log.i(TAG, "updateCilLoadingOut json : " + new Gson().toJson(loadingAdviseRequestDto));
        Call<TransactionsApiResponse> call = RetrofitController.getInstances(requireActivity()).getLoadingAdviseApi().updateCilLoadingOut("Bearer " + token, loadingAdviseRequestDto);
        call.enqueue(new Callback<TransactionsApiResponse>() {
            @Override
            public void onResponse(Call<TransactionsApiResponse> call, Response<TransactionsApiResponse> response) {
                hideProgress();
                Log.d(TAG, "onResponse: updateCilLoadingOut : Raw : " + response.raw());
                if (!response.isSuccessful()) {
                    ((MainActivity) requireActivity()).alert(requireActivity(), DIALOG_ERROR, response.errorBody() != null ? response.errorBody().toString() : "Error occurs while updating loading out details", null, BTN_OK, false);
                }

                if (response.body() != null && response.body().getStatus() != null && response.body().getMessage() != null) {
                    if (response.body().getStatus().equalsIgnoreCase(RESPONSE_FOUND)) {
                        ((MainActivity) requireActivity()).alert(requireActivity(), DIALOG_SUCCESS, response.body().getMessage(), null, BTN_OK, true);
                    } else if (response.body().getStatus().equalsIgnoreCase(RESPONSE_NOT_FOUND)) {
                        addCilLoadingIn(initializeCilLARequestDto());
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

    private void addCilLoadingIn(LoadingAdviseRequestDto loadingAdviseRequestDto) {
        showProgress();
        Log.i(TAG, "addCilLoadingIn json : " + new Gson().toJson(loadingAdviseRequestDto));
        Call<LoadingAdvisePostApiResponse> call = RetrofitController.getInstances(requireActivity()).getLoadingAdviseApi().addCilLoadingIn("Bearer " + token, loadingAdviseRequestDto);
        call.enqueue(new Callback<LoadingAdvisePostApiResponse>() {
            @Override
            public void onResponse(Call<LoadingAdvisePostApiResponse> call, Response<LoadingAdvisePostApiResponse> response) {
                hideProgress();
                Log.d(TAG, "onResponse: addCilLoadingIn : Raw : " + response.raw());
                if (!response.isSuccessful()) {
                    ((MainActivity) requireActivity()).alert(requireContext(), DIALOG_ERROR, response.errorBody() != null ? response.errorBody().toString() : "Error occurs while adding loading advise details", null, BTN_OK, false);
                }
                if (response.body() != null && response.body().getStatus() != null && response.body().getMessage() != null) {
                    if (response.body().getStatus().equalsIgnoreCase(RESPONSE_CREATED)) {
                        ((MainActivity) requireActivity()).alert(requireContext(), DIALOG_SUCCESS, response.body().getMessage(), null, BTN_OK, true);
                    } else {
                        ((MainActivity) requireActivity()).alert(requireContext(), DIALOG_ERROR, response.body().getMessage(), null, BTN_OK, false);
                    }
                } else {
                    ((MainActivity) requireActivity()).alert(requireActivity(), DIALOG_ERROR, NULL_VALUE_RESPONSE, null, BTN_OK, false);
                }
            }

            @Override
            public void onFailure(Call<LoadingAdvisePostApiResponse> call, Throwable t) {
                hideProgress();
                ((MainActivity) requireActivity()).alert(requireContext(), DIALOG_ERROR, CustomErrorMessage.setErrorMessage(t.getMessage()), null, BTN_OK, false);
            }
        });
    }

    private void updateBothraLoadingAdvise(UpdateBothraLoadingAdviseDto updateBothraLoadingAdviseDto) {
        showProgress();
        Log.i(TAG, "updateBothraLoadingAdvise json : " + new Gson().toJson(updateBothraLoadingAdviseDto));
        Call<TransactionsApiResponse> call = RetrofitController.getInstances(requireActivity()).getLoadingAdviseApi().updateBothraLoadingAdvise("Bearer " + token, updateBothraLoadingAdviseDto);
        call.enqueue(new Callback<TransactionsApiResponse>() {
            @Override
            public void onResponse(Call<TransactionsApiResponse> call, Response<TransactionsApiResponse> response) {
                hideProgress();
                if (!response.isSuccessful()) {
                    ((MainActivity) requireActivity()).alert(requireActivity(), DIALOG_ERROR, response.errorBody() != null ? response.errorBody().toString() : "Error occurs while updating transaction", null, BTN_OK, false);
                }
                if (response.body() != null && response.body().getStatus() != null && response.body().getMessage() != null) {
                    if (response.body().getStatus().equalsIgnoreCase(RESPONSE_OK)) {
                        ((MainActivity) requireActivity()).alert(requireActivity(), DIALOG_SUCCESS, response.body().getMessage(), null, BTN_OK, true);
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

    private void displayDataToUi(String rfidTag, String lepNo, String driverName, String driverMobileNo, String driverLicenseNo, String truckNo, String vesselName, String commodity, String destinationLocation, String berthNumber, String batchNumber, String grSrcLoc, String grSrcLocDesc, String bTareWeight) {
        edtRfidTagNo.setText(rfidTag.toUpperCase());
        edtLepNo.setText(lepNo.toUpperCase());
        edtBatchNumber.setText(batchNumber.toUpperCase());
        edtTruckNumber.setText(truckNo.toUpperCase());
        edtDriverName.setText(driverName.toUpperCase());
        edtDriverMobileNo.setText(driverMobileNo.toUpperCase());
        edtDriverLicenseNo.setText(driverLicenseNo.toUpperCase());
        edtVesselName.setText(vesselName.toUpperCase());
        edtCommodity.setText(commodity.toUpperCase());
        edtDestinationLocation.setText(destinationLocation.toUpperCase());
        if (grSrcLoc != null && grSrcLocDesc != null) {
            String grSourceLocation = grSrcLoc + " - " + grSrcLocDesc.toUpperCase();
            edtSourceLocation.setText(grSourceLocation);
        } else {
            edtSourceLocation.setText(null);
        }
        edtTareWeight.setText(bTareWeight);
        if (!loginUserRoleId.equalsIgnoreCase(ROLES_B_LAO)) {
            edtBerthNumber.setText(berthNumber);
        }
    }

    private void showProgress() {
        btnSubmit.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);
        colorOverlay.setVisibility(View.VISIBLE);
        requireActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        rootLayout.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.dusk_scanner));
    }

    private void hideProgress() {
        btnSubmit.setEnabled(true);
        progressBar.setVisibility(View.GONE);
        colorOverlay.setVisibility(View.GONE);
        requireActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        rootLayout.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.normal_scanner));
    }
}
