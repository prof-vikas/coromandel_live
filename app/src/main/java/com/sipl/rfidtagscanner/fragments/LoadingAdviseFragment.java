package com.sipl.rfidtagscanner.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextClock;
import android.widget.TextView;

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

import java.time.LocalDateTime;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoadingAdviseFragment extends Fragment {
    private static final String TAG = "TracingError";
    private final Concatenator concatenator = new Concatenator();
    ArrayList<String> arrBothraStrLocation = new ArrayList<>();

    private String strisgetInLoadingTime;
    private TextClock tvClock, exitClock;
    private LinearLayout lltvClockLayout;
    private ProgressBar progressBar;
    private EditText edtDestinationLocation, edtBothraSupervisor, edtPinnacleSupervisor;
    private TextView tvDestinationLocation, tvPinnacleSupervisor, tvBothraSupervisor;
    private EditText edtRfidTagNo, edtBerthNumber, edtLepNo, edtBatchNumber, edtTruckNumber, edtDriverName, edtDriverMobileNo, edtDriverLicenseNo, edtVesselName, edtCommodity, edtLoadingSupervisor, edtSourceLocation, edtTareWeight;

    //    userDetails
    private String loginUserName;
    private String token;
    private String loginUserStorageLocation;
    private String loginUserStorageLocationDesc;
    private int loginUserId;

    private String selectedDestinationCode;
    private Integer selectedLepNumberId;
    private LinearLayout constaintEntryTimeLayout, textclockLayoutexit, llTareWeight;
    private EditText edtConstEntryTime;
    private LinearLayout layoutBothraSupervisor, layoutPinnacleSupervisor, edtBerthNumberLayout;

    public LoadingAdviseFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_loading_adivse, container, false);

        edtPinnacleSupervisor = view.findViewById(R.id.ll_edt_pinnacle_supervisor);
        edtBothraSupervisor = view.findViewById(R.id.ll_edt_bothra_supervisor);
        edtDestinationLocation = view.findViewById(R.id.ll_edt_destination_location);

        textclockLayoutexit = view.findViewById(R.id.title_exit_date_time);
        exitClock = view.findViewById(R.id.la_exit_tv_clock);

        lltvClockLayout = view.findViewById(R.id.title_date_time);
        llTareWeight = view.findViewById(R.id.title_la_tare_weight);
        constaintEntryTimeLayout = view.findViewById(R.id.title_ll_entry_time);
        edtConstEntryTime = view.findViewById(R.id.ll_edt_entryTime);

        Button btnSubmit = view.findViewById(R.id.btn_loading_advise_submit);
        Button btnCancel = view.findViewById(R.id.btn_loading_advise_reset);

        edtSourceLocation = view.findViewById(R.id.la_edt_source_location);
        edtRfidTagNo = view.findViewById(R.id.edt_la_rfid_tag_no);
        edtLepNo = view.findViewById(R.id.la_edt_lep_number);
        edtTruckNumber = view.findViewById(R.id.edt_la_truck_no);
        edtDriverName = view.findViewById(R.id.edt_la_driver_name);
        edtDriverMobileNo = view.findViewById(R.id.edt_la_driver_mobile_no);
        edtDriverLicenseNo = view.findViewById(R.id.edt_la_driver_license_no);
        edtVesselName = view.findViewById(R.id.edt_la_vessel_name);
        edtCommodity = view.findViewById(R.id.edt_la_commodity);
        edtBerthNumber = view.findViewById(R.id.edt_la_berth_number);
        edtLoadingSupervisor = view.findViewById(R.id.edt_la_loading_supervisor);
        edtBerthNumberLayout = view.findViewById(R.id.title_berth_number);
        edtBatchNumber = view.findViewById(R.id.edt_la_batch_number);
        edtTareWeight = view.findViewById(R.id.edt_la_tare_weight);

        progressBar = view.findViewById(R.id.la_progressBar);

        tvClock = view.findViewById(R.id.la_tv_clock);
        tvDestinationLocation = view.findViewById(R.id.tv_la_destination_location);
        tvBothraSupervisor = view.findViewById(R.id.tv_la_bothra_supervisor);
        tvPinnacleSupervisor = view.findViewById(R.id.tv_la_pinnacle_supervisor);

        layoutBothraSupervisor = view.findViewById(R.id.title_bothra_supervisor);
        layoutPinnacleSupervisor = view.findViewById(R.id.title_pinnacle_supervisor);

        this.loginUserName = ((MainActivity) requireActivity()).getUserName();
        String userId = ((MainActivity) requireActivity()).getUserId();
        if (userId != null){
            this.loginUserId = Integer.parseInt(userId);
        }
//        this.loginUserStorageLocation = ((MainActivity) requireActivity()).getUserSourceLocationCode();
//        this.loginUserStorageLocationDesc = ((MainActivity) requireActivity()).getUserSourceLocationDesc();
        this.token = ((MainActivity) requireActivity()).getToken();

        edtLoadingSupervisor.setText(loginUserName);



        getBundleData();

        /*
         *  methods need to run on onCreate
         */
        updateUIBasedOnUser();
        makeTvTextCompulsory();
        displayClock();
        getLoadingAdviseDetails();

        btnSubmit.setOnClickListener(view12 -> {
            if (validateLoadingAdviseForm()) {
                chooseMethodToCall();
            }
        });
        btnCancel.setOnClickListener(view1 -> resetTextField());

        return view;
    }

    private void getBundleData() {
        SharedPreferences sp = requireActivity().getSharedPreferences("bothraStrLocation", MODE_PRIVATE);
        String s = sp.getString("size", null);
        Log.i(TAG, "getBundleData: S : " + s);
        if (s != null) {
            int m = Integer.parseInt(s);
            for (int i = 0; i < m; i++) {
                String n = sp.getString(String.valueOf(i), null);
                arrBothraStrLocation.add(n);
            }
        }
    }

    private boolean validateLoadingAdviseForm() {
        if (edtRfidTagNo.length() == 0) {
            edtRfidTagNo.setError("This field is required");
            return false;
        }
        if (edtLepNo.length() == 0) {
            edtLepNo.setError("This field is required");
            return false;
        }
        if (edtBatchNumber.length() == 0) {
            edtBatchNumber.setError("This field is required");
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
        if (edtDriverMobileNo.length() == 0) {
            edtDriverMobileNo.setError("This field is required");
            return false;
        }
        if (edtDriverLicenseNo.length() == 0) {
            edtDriverLicenseNo.setError("This field is required");
            return false;
        }
        if (edtVesselName.length() == 0) {
            edtVesselName.setError("This field is required");
            return false;
        }
        if (edtCommodity.length() == 0) {
            edtCommodity.setError("This field is required");
            return false;
        }

        if (edtLoadingSupervisor.length() == 0) {
            edtLoadingSupervisor.setError("This field is required");
            return false;
        }

        if (edtSourceLocation.length() == 0) {
            edtSourceLocation.setError("This field is required");
            return false;
        }

        if (edtDestinationLocation.length() == 0) {
            edtDestinationLocation.setError("This field is required");
            return false;
        }

        if (!arrBothraStrLocation.contains(loginUserStorageLocation)) {

            if (!strisgetInLoadingTime.equalsIgnoreCase("true")) {
                if (edtPinnacleSupervisor.length() == 0) {
                    edtPinnacleSupervisor.setError("This field is required");
                    return false;
                }

                if (edtBothraSupervisor.length() == 0) {
                    edtBothraSupervisor.setError("This field is required");
                    return false;
                }

                if (edtBerthNumber.length() == 0) {
                    edtBerthNumber.setError("This field is required");
                    return false;
                }
            }

        }
        return true;
    }

    private void resetTextField() {
        ((MainActivity) requireActivity()).loadFragment(new ScanFragment(), 1);
    }

    private void makeTvTextCompulsory() {
        concatenator.multiStringConcatenate(tvBothraSupervisor, "Bothra \r\nSupervisor", " *");
        concatenator.multiStringConcatenate(tvPinnacleSupervisor, "Pinnacle \r\nSupervisor", " *");
    }

    private void displayClock() {
        try {
            tvClock.setFormat24Hour("dd-MM-yyyy hh:mm:ss");
            exitClock.setFormat24Hour("dd-MM-yyyy hh:mm:ss");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendLoadingAdviseDetails(LoadingAdviseRequestDto loadingAdviseRequestDto) {
        progressBar.setVisibility(View.VISIBLE);
        Log.i(TAG, new Gson().toJson(loadingAdviseRequestDto).toString());
        Call<LoadingAdvisePostApiResponse> call = RetrofitController.getInstances(requireActivity()).getLoadingAdviseApi().addRfidLepIssue("Bearer " + token, loadingAdviseRequestDto);
        call.enqueue(new Callback<LoadingAdvisePostApiResponse>() {
            @Override
            public void onResponse(Call<LoadingAdvisePostApiResponse> call, Response<LoadingAdvisePostApiResponse> response) {
                Log.i(TAG, "onResponse code : " + response.code());
                if (!response.isSuccessful()) {
                    progressBar.setVisibility(View.GONE);
                    ((MainActivity) getActivity()).alert(getActivity(), "error", response.errorBody().toString(), null, "OK", false);
                }
                Log.i(TAG, "onResponse: add loading advise : " + response.body().getStatus());
                if (response.body().getStatus().equalsIgnoreCase("CREATED")) {
                    progressBar.setVisibility(View.GONE);
                    ((MainActivity) getActivity()).alert(getActivity(), "success", response.body().getMessage(), null, "OK", true);
                } else {
                    progressBar.setVisibility(View.GONE);
                    ((MainActivity) getActivity()).alert(getActivity(), "error", response.body().getMessage(), null, "OK", false);
                }
            }

            @Override
            public void onFailure(Call<LoadingAdvisePostApiResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                ((MainActivity) getActivity()).alert(getActivity(), "error", t.getMessage(), null, "OK", false);
            }
        });
    }


    private void UpdateCoromadelLoadingAdviseDetails(LoadingAdviseRequestDto
                                                             loadingAdviseRequestDto) {
        progressBar.setVisibility(View.VISIBLE);
        Log.i(TAG, "UpdateCoromadelLoadingAdviseDetails : Request Dto : <<------- " + new Gson().toJson(loadingAdviseRequestDto));
        Call<TransactionsApiResponse> call = RetrofitController.getInstances(requireActivity()).getLoadingAdviseApi().updateCoromandelLoadingAdvise("Bearer " + token, loadingAdviseRequestDto);
        call.enqueue(new Callback<TransactionsApiResponse>() {
            @Override
            public void onResponse(Call<TransactionsApiResponse> call, Response<TransactionsApiResponse> response) {
                if (!response.isSuccessful()) {
                    progressBar.setVisibility(View.GONE);
                    ((MainActivity) requireActivity()).alert(requireActivity(), "error", response.errorBody().toString(), null, "OK", false);
                }
                Log.i(TAG, "onResponse: UpdateCoromadelLoadingAdviseDetails : " + response.raw());

                if (response.isSuccessful()){
                   if (response.body().getStatus() != null){
                       if (response.body().getStatus().equalsIgnoreCase("FOUND")) {
                           progressBar.setVisibility(View.GONE);
                           Log.i(TAG, "onResponse: response : " + response.body().getStatus());
                           ((MainActivity) requireActivity()).alert(requireActivity(), "success", response.body().getMessage(), null, "OK", true);
                       } else if (response.body().getStatus().equalsIgnoreCase("NOT_FOUND")) {
                           sendLoadingAdviseDetails(setData());
                       } else {
                           progressBar.setVisibility(View.GONE);
                           ((MainActivity) requireActivity()).alert(requireActivity(), "error", response.body().getMessage(), null, "OK", false);
                       }
                   }
                }
            }

            @Override
            public void onFailure(Call<TransactionsApiResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                ((MainActivity) requireActivity()).alert(requireActivity(), "error", t.getMessage(), null, "OK", false);
            }
        });
    }


    private void UpdateBothraLoadingAdviseDetails(UpdateBothraLoadingAdviseDto
                                                          updateBothraLoadingAdviseDto) {
        progressBar.setVisibility(View.VISIBLE);
        Log.i(TAG, "updateBothraLoadingAdviseDto : Request Dto : <<------- " + new Gson().toJson(updateBothraLoadingAdviseDto));
        Call<TransactionsApiResponse> call = RetrofitController.getInstances(requireActivity()).getLoadingAdviseApi().updateBothraLoadingAdvise("Bearer " + token, updateBothraLoadingAdviseDto);
        call.enqueue(new Callback<TransactionsApiResponse>() {
            @Override
            public void onResponse(Call<TransactionsApiResponse> call, Response<TransactionsApiResponse> response) {
                if (!response.isSuccessful()) {
                    progressBar.setVisibility(View.GONE);
                    ((MainActivity) requireActivity()).alert(requireActivity(), "error", response.errorBody().toString(), null, "OK", false);
                }

                if (response.body().getStatus().equalsIgnoreCase("FOUND")) {
                    progressBar.setVisibility(View.GONE);
                    ((MainActivity) requireActivity()).alert(requireActivity(), "success", response.body().getMessage(), null, "OK", true);
                    resetTextField();
                } else {
                    progressBar.setVisibility(View.GONE);
                    ((MainActivity) requireActivity()).alert(requireActivity(), "error", response.body().getMessage(), null, "OK", false);
                }
            }

            @Override
            public void onFailure(Call<TransactionsApiResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                ((MainActivity) requireActivity()).alert(requireActivity(), "error", t.getMessage(), null, "OK", false);
            }
        });
    }

    private LoadingAdviseRequestDto setData() {
        final Integer RSTAT = 1;
        final Integer FLAG = 1;
        AuditEntity auditEntity = new AuditEntity(loginUserName, null);
        RfidLepIssueDto rfidLepIssueModel = new RfidLepIssueDto(selectedLepNumberId);
        StorageLocationDto sourceMasterDto = new StorageLocationDto("0058");
        UserMasterDto loadingAdviseDto = new UserMasterDto(loginUserId);
        StorageLocationDto functionalLocationMasterDto = new StorageLocationDto(selectedDestinationCode);
        String bothraSupervisor = edtBothraSupervisor.getText().toString();
        String pinnacleSupervisor = edtPinnacleSupervisor.getText().toString();
        return new LoadingAdviseRequestDto(auditEntity, bothraSupervisor, pinnacleSupervisor, loadingAdviseDto, sourceMasterDto, functionalLocationMasterDto, rfidLepIssueModel, FLAG, true, RSTAT, String.valueOf(LocalDateTime.now()), null);
    }

    private UpdateBothraLoadingAdviseDto updateData() {
        final Integer BOTHRA_FLAG = 12;
        StorageLocationDto sourceMasterDto = new StorageLocationDto(loginUserStorageLocation);
        UserMasterDto loadingAdviseDto = new UserMasterDto(loginUserId);
        RfidLepIssueDto rfidLepIssueModel = new RfidLepIssueDto(selectedLepNumberId);
        StorageLocationDto functionalLocationMasterDto = new StorageLocationDto(selectedDestinationCode);
        AuditEntity auditEntity = new AuditEntity(null, null, loginUserName, null);
        if (strisgetInLoadingTime.equalsIgnoreCase("true")) {
            return new UpdateBothraLoadingAdviseDto(auditEntity, loadingAdviseDto, functionalLocationMasterDto, sourceMasterDto, rfidLepIssueModel, true, BOTHRA_FLAG, null, String.valueOf(LocalDateTime.now()));
        } else {
            return new UpdateBothraLoadingAdviseDto(auditEntity, loadingAdviseDto, functionalLocationMasterDto, sourceMasterDto, rfidLepIssueModel, true, BOTHRA_FLAG, String.valueOf(LocalDateTime.now()), null);
        }
    }

    private void updateUIBasedOnUser() {
        if (!arrBothraStrLocation.contains(loginUserStorageLocation)) {
            layoutBothraSupervisor.setVisibility(View.VISIBLE);
            layoutPinnacleSupervisor.setVisibility(View.VISIBLE);
            edtBerthNumberLayout.setVisibility(View.VISIBLE);
        } else {
            layoutBothraSupervisor.setVisibility(View.GONE);
            layoutPinnacleSupervisor.setVisibility(View.GONE);
            llTareWeight.setVisibility(View.VISIBLE);
            edtBerthNumberLayout.setVisibility(View.GONE);
        }
    }

    private void chooseMethodToCall() {
        if (!arrBothraStrLocation.contains(loginUserStorageLocation)) {
            UpdateCoromadelLoadingAdviseDetails(setData());
        } else {
            UpdateBothraLoadingAdviseDetails(updateData());
        }
    }

    private void getLoadingAdviseDetails() {
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
        String sapGrNo = sp.getString("sapGrNoSPK", null);
        String vesselName = sp.getString("vesselNameSPK", null);
        String truckCapacity = sp.getString("truckCapacitySPK", null);
        String commodity = sp.getString("commoditySPK", null);
        String strDestinationCode = sp.getString("strDestinationCodeSPK", null);
        String strDestinationDesc = sp.getString("strDestinationDescSPK", null);
        String strPinnacleSupervisor = sp.getString("pinnacleSupervisorSPK", null);
        String strBothraSupervisor = sp.getString("bothraSupervisorSPK", null);
        String strBerthNumber = sp.getString("BerthNumberSPK", null);
        String strBatchNumber = sp.getString("batchNumberSPK", null);
        String grSrcLoc = sp.getString("grSrcLocSPK", null);
        String grSrcLocDesc = sp.getString("grSrcLocDescSPK", null);
        String bTareWeight = sp.getString("bTareWeightSPK", null);
        this.strisgetInLoadingTime = sp.getString("isgetInLoadingTimeSPK", "false");
        String getInTime = null;

        if (strisgetInLoadingTime.equalsIgnoreCase("true")) {
            getInTime = sp.getString("getInloadingTimeSPK", null);
            constaintEntryTimeLayout.setVisibility(View.VISIBLE);
            lltvClockLayout.setVisibility(View.GONE);
            edtConstEntryTime.setText(getInTime);
            edtConstEntryTime.setEnabled(false);
            edtConstEntryTime.setBackgroundResource(R.drawable.rectangle_edt_read_only_field);
            edtPinnacleSupervisor.setText(strPinnacleSupervisor);
            edtPinnacleSupervisor.setEnabled(false);
            edtPinnacleSupervisor.setBackgroundResource(R.drawable.rectangle_edt_read_only_field);
            edtBothraSupervisor.setText(strBothraSupervisor);
            edtBothraSupervisor.setEnabled(false);
            edtBothraSupervisor.setBackgroundResource(R.drawable.rectangle_edt_read_only_field);
            textclockLayoutexit.setVisibility(View.VISIBLE);


        }
        String destinationLocation = strDestinationCode + " - " + strDestinationDesc;
        this.selectedDestinationCode = strDestinationCode;
        saveLoginAdviseData(rfidTagId, lepNo, driverName, driverMobileNo, driverLicenseNo, truckNo, sapGrNo, vesselName, truckCapacity, commodity, destinationLocation, strBerthNumber, strBatchNumber, grSrcLoc, grSrcLocDesc, bTareWeight);
    }

    private void saveLoginAdviseData(String rfidTag, String lepNo, String driverName, String driverMobileNo, String driverLicenseNo, String truckNo, String sapGrNo, String vesselName, String truckCapacity, String commodity, String destinationLocation, String berthNumber, String batchNumber, String grSrcLoc, String grSrcLocDesc, String bTareWeight) {
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
            edtSourceLocation.setText(grSrcLoc.toUpperCase() + " - " + grSrcLocDesc.toUpperCase());
        }else{
            edtSourceLocation.setText(null);
        }
        edtTareWeight.setText(bTareWeight);
//        edtSourceLocation.setText(loginUserStorageLocation + " - " + loginUserStorageLocationDesc);
        if (!arrBothraStrLocation.contains(loginUserStorageLocation)) {
            edtBerthNumber.setText(berthNumber.toUpperCase());
        }

    }
}
