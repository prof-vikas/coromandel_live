package com.sipl.rfidtagscanner.fragments;

import static android.content.Context.MODE_PRIVATE;
import static com.sipl.rfidtagscanner.utils.Config.EMPTY_BOTHRA_SUPERVISOR;
import static com.sipl.rfidtagscanner.utils.Config.EMPTY_DESTINATION_LOCATION;
import static com.sipl.rfidtagscanner.utils.Config.EMPTY_LEP_NUMBER_LIST;
import static com.sipl.rfidtagscanner.utils.Config.EMPTY_PINNACLE_SUPERVISOR;
import static com.sipl.rfidtagscanner.utils.Config.PLANT_BOTHRA;
import static com.sipl.rfidtagscanner.utils.Config.isRMGTableRequired;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.sipl.rfidtagscanner.MainActivity;
import com.sipl.rfidtagscanner.R;
import com.sipl.rfidtagscanner.RetrofitController;
import com.sipl.rfidtagscanner.adapter.RmgDataAdapter;
import com.sipl.rfidtagscanner.adapter.TripsDataAdapter;
import com.sipl.rfidtagscanner.dto.dtos.BothraLoadingSupervisorDto;
import com.sipl.rfidtagscanner.dto.dtos.PinnacleLoadingSupervisorDto;
import com.sipl.rfidtagscanner.dto.dtos.RfidLepIssueDto;
import com.sipl.rfidtagscanner.dto.dtos.StorageLocationDto;
import com.sipl.rfidtagscanner.dto.dtos.TransactionsDto;
import com.sipl.rfidtagscanner.dto.dtos.UserMasterDto;
import com.sipl.rfidtagscanner.dto.request.LoadingAdviseRequestDto;
import com.sipl.rfidtagscanner.dto.request.UpdateBothraLoadingAdviseDto;
import com.sipl.rfidtagscanner.dto.response.BothraSupervisorApiResponse;
import com.sipl.rfidtagscanner.dto.response.DestinationLocationResponseApi;
import com.sipl.rfidtagscanner.dto.response.LoadingAdvisePostApiResponse;
import com.sipl.rfidtagscanner.dto.response.PinnacleSupervisorApiResponse;
import com.sipl.rfidtagscanner.dto.response.RfidLepApiResponse;
import com.sipl.rfidtagscanner.dto.response.TransactionsApiResponse;
import com.sipl.rfidtagscanner.entites.AuditEntity;
import com.sipl.rfidtagscanner.entites.BothraWHDto;
import com.sipl.rfidtagscanner.entites.LoadingAdviseLepDto;
import com.sipl.rfidtagscanner.utils.Helper;
import com.sipl.rfidtagscanner.utils.RecyclerviewHardcodedData;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoadingAdviseFragment extends Fragment {
    private static final String TAG = "TracingError";

    LoadingAdviseLepDto loadingAdviseLepDto;
    List<LoadingAdviseLepDto> loadingAdviseLepDtoList = new ArrayList<>();


    ArrayList<String> arrPinnacleSupervisor;
    ArrayList<String> arrAutoCompleteLepNo;
    ArrayList<String> arrBothraSupervisor;


    private TextClock tvClock;
    private ProgressBar progressBar;
    private LinearLayout mainRecyclerViewLayout;
    private Button btnSubmit, btnCancel;
    private AutoCompleteTextView autoCompleteLepNumber, autoCompletePinnacleSupervisor, autoCompleteBothraSupervisor;
    private RecyclerView recyclerViewRmgNo, recyclerViewTrip;
    private Spinner spinnerDestinationLocation;
    private TextView tvAutoCompleteLepNo, tvDestinationLocation, tvPinnacleSupervisor, tvBothraSupervisor;
    private EditText edtSapGrNo, edtTruckNumber, edtDriverName, edtDriverMobileNo, edtDriverLicenseNo, edtVesselName, edtCommodity, edtTruckCapacity, edtLoadingSupervisor, edtSourceLocation;
    private Helper helper = new Helper();

    //    userDetails
    private String loginUserName;
    private String token;
    private String loginUserStorageLocation;
    private String loginUserStorageLocationDesc;
    private String loginUserPlantCode;
    private int loginUserId;

    //    ArrayAdapter for spinner
    private ArrayAdapter<String> destinationLocationAdapter;
    private ArrayAdapter<String> pinnacleSupervisorAdapter;
    private ArrayAdapter<String> bothraSupervisorAdapter;
    private ArrayAdapter<String> arrayAdapterForLepNumber;

    private Integer selectedBothraSupervisorId;
    private String selectedBothraSupervisor;
    private Integer selectedPinnacleSupervisorId;
    private String selectedPinnacleSupervisor;
    private String selectedDestinationLocation;
    private Integer selectedLepNumberId;
    private String SelectedLepNo;

    private LinearLayout layoutBothraSupervisor, layoutPinnacleSupervisor;

    public LoadingAdviseFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_loading_adivse, container, false);
        autoCompleteLepNumber = view.findViewById(R.id.la_auto_complete_lep_number);
        autoCompletePinnacleSupervisor = view.findViewById(R.id.ll_auto_pinnacle_supervisor);
        autoCompleteBothraSupervisor = view.findViewById(R.id.ll_auto_bothra_supervisor);

        spinnerDestinationLocation = view.findViewById(R.id.ll_spinner_destination_location);

        btnSubmit = view.findViewById(R.id.btn_loading_advise_submit);
        btnCancel = view.findViewById(R.id.btn_loading_advise_reset);

        edtSourceLocation = view.findViewById(R.id.la_edt_source_location);
        edtSapGrNo = view.findViewById(R.id.edt_la_sap_rg_no);
        edtTruckNumber = view.findViewById(R.id.edt_la_truck_no);
        edtDriverName = view.findViewById(R.id.edt_la_driver_name);
        edtDriverMobileNo = view.findViewById(R.id.edt_la_driver_mobile_no);
        edtDriverLicenseNo = view.findViewById(R.id.edt_la_driver_license_no);
        edtVesselName = view.findViewById(R.id.edt_la_vessel_name);
        edtCommodity = view.findViewById(R.id.edt_la_commodity);
        edtTruckCapacity = view.findViewById(R.id.edt_la_quantity);
        edtLoadingSupervisor = view.findViewById(R.id.edt_la_loading_supervisor);

        progressBar = view.findViewById(R.id.la_progressBar);
        mainRecyclerViewLayout = view.findViewById(R.id.main_recycler_view_layoutout);

        tvClock = view.findViewById(R.id.la_tv_clock);
        tvAutoCompleteLepNo = view.findViewById(R.id.tv_la_lep_number);
        tvDestinationLocation = view.findViewById(R.id.tv_la_destination_location);
        tvBothraSupervisor = view.findViewById(R.id.tv_la_bothra_supervisor);
        tvPinnacleSupervisor = view.findViewById(R.id.tv_la_pinnacle_supervisor);

        layoutBothraSupervisor = view.findViewById(R.id.title_bothra_supervisor);
        layoutPinnacleSupervisor = view.findViewById(R.id.title_pinnacle_supervisor);

        this.loginUserName = ((MainActivity) getActivity()).getLoginUsername();
        this.loginUserId = ((MainActivity) getActivity()).getLoginUserId();
        this.loginUserPlantCode = ((MainActivity) getActivity()).getLoginUserPlantCode();
        this.loginUserStorageLocation = ((MainActivity) getActivity()).getLoginUserStorageCode();
        this.loginUserStorageLocationDesc = ((MainActivity) getActivity()).getLoginUserSourceLocationDesc();
        this.token = getToken();

        edtLoadingSupervisor.setText(loginUserName);
        edtSourceLocation.setText(loginUserStorageLocation + " - " + loginUserStorageLocationDesc);

        /*
         *  methods need to run on onCreate
         */
        updateUIBasedOnUser();
        makeTvTextCompulsory();
        displayClock();
        callOnCreateApi();

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateLoadingAdviseForm()) {
                    String lepNo = autoCompleteLepNumber.getText().toString();
                    if (arrAutoCompleteLepNo.contains(lepNo)) {
                        if (validateLepNoChange()) {
                            chooseMethodToCall();
                        } else {
                            ((MainActivity) getActivity()).alert(getActivity(), "error", "It seems selected Lep number is change", "Please try to select from Lep Number drop-down..!", "OK");
                            return;
                        }
                    } else {
                        ((MainActivity) getActivity()).alert(getActivity(), "error", "Selected Lep Number is invalid", "Please select Lep number from drop-down..!", "OK");
                        return;
                    }
                }
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetTextField();
            }
        });

        return view;
    }

    /*
     * method which keep calling getAll endpoint until condition is true
     * getAll endpoint is necessary for getting required data to run loading advise screen
     */
    private boolean callOnCreateApi() {
        getAllLepNumber();
        getAllDestinationLocation();
        getAllPinnacleSupervisor();
        getAllBothraSupervisor();
        return true;
    }

    private boolean validateLepNoChange() {
        String lepNo = autoCompleteLepNumber.getText().toString();
        if (SelectedLepNo.equalsIgnoreCase(lepNo)) {
            return true;
        } else return false;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle
            savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (isRMGTableRequired == true) {
            mainRecyclerViewLayout.setVisibility(View.VISIBLE);
            recyclerViewRmgNo = getActivity().findViewById(R.id.recycler_view_rmg_no);
            recyclerViewTrip = getActivity().findViewById(R.id.recycler_view_trips);
            setRecyclerView();
        }
    }

    private void setRecyclerView() {
        recyclerViewRmgNo.setHasFixedSize(true);
        recyclerViewTrip.setHasFixedSize(true);
        RecyclerviewHardcodedData recyclerviewHardcodedData = new RecyclerviewHardcodedData();

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerViewRmgNo.setLayoutManager(layoutManager);
        RecyclerView.LayoutManager tripLayoutManager = new LinearLayoutManager(getActivity());
        recyclerViewTrip.setLayoutManager(tripLayoutManager);

        RmgDataAdapter rmgDataAdapter = new RmgDataAdapter((Context)
                getActivity(), recyclerviewHardcodedData.initRmgData());
        recyclerViewRmgNo.setAdapter(rmgDataAdapter);
        TripsDataAdapter tripRmgDataAdapter = new TripsDataAdapter((Context)
                getActivity(), recyclerviewHardcodedData.initTripData());
        recyclerViewTrip.setAdapter(tripRmgDataAdapter);
    }


    private boolean validateLoadingAdviseForm() {
        if (autoCompleteLepNumber.length() == 0) {
            autoCompleteLepNumber.setError("This field is required");
            return false;
        }

        if (edtSapGrNo.length() == 0) {
            edtSapGrNo.setError("This field is required");
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

        if (edtTruckCapacity.length() == 0) {
            edtTruckCapacity.setError("This field is required");
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

        if (spinnerDestinationLocation.getSelectedItem().toString().equals("Select Destination")) {
            Toast.makeText(getActivity(), "Select Destination Location", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!loginUserPlantCode.equalsIgnoreCase(PLANT_BOTHRA)) {

            if (autoCompleteBothraSupervisor.length() == 0) {
                autoCompleteBothraSupervisor.setError("This field is required");
                return false;
            }

            if (autoCompletePinnacleSupervisor.length() == 0) {
                autoCompletePinnacleSupervisor.setError("This field is required");
                return false;
            }
        }
        removeErrorMessage();
        return true;
    }

    private void resetTextField() {
        autoCompleteLepNumber.setText(null);
        edtSapGrNo.setText(null);
        edtTruckNumber.setText(null);
        edtDriverName.setText(null);
        edtDriverMobileNo.setText(null);
        edtDriverLicenseNo.setText(null);
        edtVesselName.setText(null);
        edtCommodity.setText(null);
        edtTruckCapacity.setText(null);
        if (!loginUserPlantCode.equalsIgnoreCase(PLANT_BOTHRA)) {
            if (destinationLocationAdapter == null) {
                callOnCreateApi();
            } else {
                spinnerDestinationLocation.setSelection(destinationLocationAdapter.getCount());
                autoCompleteBothraSupervisor.setText(null);
                autoCompletePinnacleSupervisor.setText(null);
            }
        } else {
            if (destinationLocationAdapter == null) {
                callOnCreateApi();
            } else {
                spinnerDestinationLocation.setSelection(destinationLocationAdapter.getCount());
            }
        }
        if (arrayAdapterForLepNumber != null) {
            arrayAdapterForLepNumber.clear();
//            arrAutoCompleteLepNo.clear();
        }
        getAllLepNumber();
        removeErrorMessage();
    }

    private void removeErrorMessage() {
        autoCompleteLepNumber.setError(null);
        edtSapGrNo.setError(null);
        edtTruckNumber.setError(null);
        edtDriverName.setError(null);
        edtDriverMobileNo.setError(null);
        edtDriverLicenseNo.setError(null);
        edtVesselName.setError(null);
        edtCommodity.setError(null);
        edtTruckCapacity.setError(null);
        edtLoadingSupervisor.setError(null);
    }

    private void makeTvTextCompulsory() {
        helper.multiColorStringForTv(tvAutoCompleteLepNo, "LEP Number", " *");
        helper.multiColorStringForTv(tvDestinationLocation, "Destination \r\nLocation", " *");
        helper.multiColorStringForTv(tvBothraSupervisor, "Bothra \r\nSupervisor", " *");
        helper.multiColorStringForTv(tvPinnacleSupervisor, "Pinnacle \r\nSupervisor", " *");
    }

    private void displayClock() {
        try {
            tvClock.setFormat24Hour("dd-MM-yy hh:mm a");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getToken() {
        SharedPreferences sp = getActivity().getSharedPreferences("loginCredentials", MODE_PRIVATE);
        String token = sp.getString("tokenSPK", null);
        return token;
    }

    private boolean getAllLepNo() {
        progressBar.setVisibility(View.VISIBLE);
        try {
            Call<TransactionsApiResponse> call = RetrofitController.getInstance().getLoadingAdviseApi().getALlBothraLepNumber("Bearer " + token, "12","11");

            call.enqueue(new Callback<TransactionsApiResponse>() {
                @Override
                public void onResponse(Call<TransactionsApiResponse> call, Response<TransactionsApiResponse> response) {
                    if (!response.isSuccessful()) {
//                        alertBuilder(response.errorBody().toString());
                        progressBar.setVisibility(View.GONE);
                        ((MainActivity) getActivity()).alert(getActivity(), "error", response.errorBody().toString(), null, "OK");
                        return;
                    }


                    if (response.body().getStatus().equalsIgnoreCase("FOUND")) {
                        Log.i(TAG, "onResponse: " + response.body().getStatus());
                        HashMap<String, Integer> hashMapLepNumber = new HashMap<>();
                        arrAutoCompleteLepNo = new ArrayList<>();
                        progressBar.setVisibility(View.GONE);
                        Log.i(TAG, "getAllLepNumber : response.isSuccessful() : " + response.isSuccessful() + " responseCode : " + response.code() + " responseRaw : " + response.raw());
                        List<TransactionsDto> rfidLepIssueDtoList = response.body().getTransactionsDtos();
                        try {
                            if (rfidLepIssueDtoList == null || rfidLepIssueDtoList.isEmpty()) {
                                autoCompleteLepNumber.setHint("No Lep number available");
                                Toast.makeText(getActivity(), EMPTY_LEP_NUMBER_LIST, Toast.LENGTH_SHORT).show();
                                return;
                            } else {
                                autoCompleteLepNumber.setHint("Search Lep Number");
                            }
                            Log.i(TAG, "onResponse: " + response.body().getStatus());
                            for (int i = 0; i < rfidLepIssueDtoList.size(); i++) {
                                String strLepNumber = rfidLepIssueDtoList.get(i).getRfidLepIssueModel().getLepNumber();
                                int id = rfidLepIssueDtoList.get(i).getRfidLepIssueModel().getId();
                                String strDriverName = String.valueOf(rfidLepIssueDtoList.get(i).getRfidLepIssueModel().getDriverMaster().getDriverName());
                                String strDriverMobileNo = String.valueOf(rfidLepIssueDtoList.get(i).getRfidLepIssueModel().getDriverMaster().getDriverMobileNo());
                                String strDriverLicenseNo = String.valueOf(rfidLepIssueDtoList.get(i).getRfidLepIssueModel().getDriverMaster().getDriverLicenseNo());
                                String strSapGrNo = String.valueOf(rfidLepIssueDtoList.get(i).getRfidLepIssueModel().getDailyTransportReportModule().getSapGrNumber());
                                String strTruckNo = String.valueOf(rfidLepIssueDtoList.get(i).getRfidLepIssueModel().getDailyTransportReportModule().getTruckNumber());
                                String strVesselName = String.valueOf(rfidLepIssueDtoList.get(i).getRfidLepIssueModel().getDailyTransportReportModule().getVesselName());
                                Integer strTruckCapacity = rfidLepIssueDtoList.get(i).getRfidLepIssueModel().getDailyTransportReportModule().getTruckCapacity();
                                String strCommodity = String.valueOf(rfidLepIssueDtoList.get(i).getRfidLepIssueModel().getDailyTransportReportModule().getCommodity());

                                loadingAdviseLepDto = new LoadingAdviseLepDto(strLepNumber,strDriverName,strDriverMobileNo,strDriverLicenseNo,strSapGrNo,strTruckNo,strVesselName,strTruckCapacity,strCommodity);
                                loadingAdviseLepDtoList.add(loadingAdviseLepDto);

                                arrAutoCompleteLepNo.add(strLepNumber);
                                hashMapLepNumber.put(strLepNumber, id);
                            }
                            Log.i(TAG, "onResponse: loadingAdviseLepDtoList.size() : " + loadingAdviseLepDtoList.size());
                            arrayAdapterForLepNumber = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, arrAutoCompleteLepNo);
                            autoCompleteLepNumber.setAdapter(arrayAdapterForLepNumber);

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
                    }else {
                        progressBar.setVisibility(View.GONE);
                        ((MainActivity) getActivity()).alert(getActivity(),"warning", "No LEP no is available",null,"OK");
                    }
                }

                @Override
                public void onFailure(Call<TransactionsApiResponse> call, Throwable t) {
                    progressBar.setVisibility(View.GONE);
//                    alertBuilder(t.getMessage());
                    ((MainActivity) getActivity()).alert(getActivity(), "error", t.getMessage(), null, "OK");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    private boolean getAllLepNumber() {
        progressBar.setVisibility(View.VISIBLE);
        HashMap<String, Integer> hashMapLepNumber = new HashMap<>();
        arrAutoCompleteLepNo = new ArrayList<>();
        try {
            if ((loginUserPlantCode.equalsIgnoreCase(PLANT_BOTHRA))) {
                getAllLepNo();
            } else {
                Call<RfidLepApiResponse> call = RetrofitController.getInstance().getLoadingAdviseApi().getALlLepNumber("Bearer " + token);
                call.enqueue(new Callback<RfidLepApiResponse>() {
                    @Override
                    public void onResponse(Call<RfidLepApiResponse> call, Response<RfidLepApiResponse> response) {
                        if (!response.isSuccessful()) {
//                        alertBuilder(response.errorBody().toString());
                            progressBar.setVisibility(View.GONE);
                            ((MainActivity) getActivity()).alert(getActivity(), "error", response.errorBody().toString(), null, "OK");
                            return;
                        }

                        if (response.isSuccessful()) {
                            progressBar.setVisibility(View.GONE);
                            Log.i(TAG, "getAllLepNumber : response.isSuccessful() : " + response.isSuccessful() + " responseCode : " + response.code() + " responseRaw : " + response.raw());
                            List<RfidLepIssueDto> rfidLepIssueDtoList = response.body().getRfidLepIssueDtos();
                            try {
                                if (rfidLepIssueDtoList == null || rfidLepIssueDtoList.isEmpty()) {
                                    autoCompleteLepNumber.setHint("No Lep number available");
                                    Toast.makeText(getActivity(), EMPTY_LEP_NUMBER_LIST, Toast.LENGTH_SHORT).show();
                                    return;
                                } else {
                                    autoCompleteLepNumber.setHint("Search Lep Number");
                                }
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
                        progressBar.setVisibility(View.GONE);
//                    alertBuilder(t.getMessage());
                        ((MainActivity) getActivity()).alert(getActivity(), "error", t.getMessage(), null, "OK");
                        t.printStackTrace();
                    }
                });
            }
        } catch (Exception e) {
            Log.i(TAG, "getALlLepNumberWithFlag: " + e.getMessage());
        }
        return true;
    }

    private boolean getAllDestinationLocation() {
        progressBar.setVisibility(View.VISIBLE);
        Call<DestinationLocationResponseApi> call = RetrofitController.getInstance().getLoadingAdviseApi().
                getAllDestinationLocation("Bearer " + token);

        call.enqueue(new Callback<DestinationLocationResponseApi>() {
            @Override
            public void onResponse(Call<DestinationLocationResponseApi> call, Response<DestinationLocationResponseApi> response) {
                if (!response.isSuccessful()) {
                    progressBar.setVisibility(View.GONE);
//                    alertBuilder(response.errorBody().toString());
                    ((MainActivity) getActivity()).alert(getActivity(), "error", response.errorBody().toString(), null, "OK");
                    return;
                }
                Log.i(TAG, "onResponse: getAllDestinationLocation : responseCode : " + response.code());

                if (response.isSuccessful()) {
                    progressBar.setVisibility(View.GONE);
                    HashMap<String, String> hashMapDestinationLocation = new HashMap<>();
                    HashMap<String, String> hashMapDestinationLocationWithDesc = new HashMap<>();
                    ArrayList<String> arrDestinationLocation = new ArrayList<>();
                    ArrayList<String> arrDestinationLocationDis = new ArrayList<>();
                    Log.i(TAG, "onResponse: getAllDestinationLocation" + response.code() + " " + response.raw());
                    List<StorageLocationDto> functionalLocationMasterDtoList = response.body().getStorageLocationDtos();

                    try {
                        if (functionalLocationMasterDtoList == null || functionalLocationMasterDtoList.isEmpty()) {
                            Toast.makeText(getActivity(), EMPTY_DESTINATION_LOCATION, Toast.LENGTH_SHORT).show();
                            return;
                        }
                        for (int i = 0; i < functionalLocationMasterDtoList.size(); i++) {
                            String s = functionalLocationMasterDtoList.get(i).getStrLocationCode();
                            String sourceLocationDesc = functionalLocationMasterDtoList.get(i).getStrLocationDesc();
                            arrDestinationLocation.add(s);
                            hashMapDestinationLocation.put(s, sourceLocationDesc);

                            String srcDis = s + " - " + sourceLocationDesc;
                            arrDestinationLocationDis.add(srcDis);
                            hashMapDestinationLocationWithDesc.put(srcDis, s);


                        }
                        arrDestinationLocationDis.add("Select Destination");
//                        arrDestinationLocation.add("Select Destination");
                        String userSourceLocation = loginUserStorageLocation;
                        String userSourceLocationDesc = loginUserStorageLocationDesc;
                        String userSourceDesc = userSourceLocation + " - " + userSourceLocationDesc;
                        if (arrDestinationLocationDis.contains(userSourceDesc)) {
                            arrDestinationLocationDis.remove(userSourceDesc);
                        }
                        Log.i(TAG, "onResponse: array : " + arrDestinationLocationDis.size());

                        destinationLocationAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, arrDestinationLocationDis) {
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

                        spinnerDestinationLocation.setAdapter(destinationLocationAdapter);
                        spinnerDestinationLocation.setSelection(destinationLocationAdapter.getCount());

                        spinnerDestinationLocation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                String s = adapterView.getSelectedItem().toString();
                              /*  if (hashMapDestinationLocation.containsKey(selectedDestinationLocation)){
                                    Log.i(TAG, "onItemSelected: hashMapDestinationLocation.get(selectedDestinationLocation) : " + hashMapDestinationLocation.get(selectedDestinationLocation));
                                }*/
                                if (hashMapDestinationLocationWithDesc.containsKey(s)) {
                                    Log.i(TAG, "onItemSelected: hashMapDestinationLocation.get(selectedDestinationLocation) : " + hashMapDestinationLocationWithDesc.get(s));
                                    selectedDestinationLocation = hashMapDestinationLocationWithDesc.get(s);
                                    Log.i(TAG, "onItemSelected: selectedDestinationLocation : " + selectedDestinationLocation);
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
            public void onFailure(Call<DestinationLocationResponseApi> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
//                alertBuilder(t.getMessage());
                ((MainActivity) getActivity()).alert(getActivity(), "error", t.getMessage(), null, "OK");
//                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        return true;
    }

    private boolean getAllBothraSupervisor() {
        progressBar.setVisibility(View.VISIBLE);
        Call<BothraSupervisorApiResponse> call = RetrofitController.getInstance().getLoadingAdviseApi().getAllBothraSupervisor("Bearer " + token);
        HashMap<String, Integer> hashMapBothraSupervisor = new HashMap<>();
        arrBothraSupervisor = new ArrayList<>();
        call.enqueue(new Callback<BothraSupervisorApiResponse>() {
            @Override
            public void onResponse(Call<BothraSupervisorApiResponse> call, Response<BothraSupervisorApiResponse> response) {
                if (!response.isSuccessful()) {
                    progressBar.setVisibility(View.GONE);
                    ((MainActivity) getActivity()).alert(getActivity(), "error", response.errorBody().toString(), null, "OK");
//                    alertBuilder(response.errorBody().toString());
                    return;
                }
                Log.i(TAG, "onResponse: getAllBothraSupervisor : responseCode : " + response.code());

                if (response.isSuccessful()) {
                    progressBar.setVisibility(View.GONE);
                    List<BothraLoadingSupervisorDto> bothraLoadingSupervisorDtosList = response.body().getBothraLoadingSupervisorDtos();

                    try {
                        if (bothraLoadingSupervisorDtosList == null || bothraLoadingSupervisorDtosList.isEmpty()) {
                            Toast.makeText(getActivity(), EMPTY_BOTHRA_SUPERVISOR, Toast.LENGTH_SHORT).show();
                            return;
                        }
                        for (int i = 0; i < bothraLoadingSupervisorDtosList.size(); i++) {
                            String name = bothraLoadingSupervisorDtosList.get(i).getName();
                            int id = bothraLoadingSupervisorDtosList.get(i).getId();
                            hashMapBothraSupervisor.put(name, id);
                            arrBothraSupervisor.add(name);
                        }
                        bothraSupervisorAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, arrBothraSupervisor);
                        autoCompleteBothraSupervisor.setAdapter(bothraSupervisorAdapter);

                        autoCompleteBothraSupervisor.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                selectedBothraSupervisor = bothraSupervisorAdapter.getItem(i);
                                if (hashMapBothraSupervisor.containsKey(selectedBothraSupervisor)) {
                                    selectedBothraSupervisorId = hashMapBothraSupervisor.get(selectedBothraSupervisor);
                                    Log.i(TAG, "onItemSelected: selectedBothraSupervisorId " + selectedBothraSupervisorId);
                                }
                            }
                        });

                    } catch (Exception e) {
                        e.getMessage();
                    }
                }
            }

            @Override
            public void onFailure(Call<BothraSupervisorApiResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
//                alertBuilder(t.getMessage());
                ((MainActivity) getActivity()).alert(getActivity(), "error", t.getMessage(), null, "OK");
//                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        return true;
    }

    private boolean getAllPinnacleSupervisor() {
        Log.i(TAG, "getAllPinnacleSupervisor: " + token);
        Call<PinnacleSupervisorApiResponse> call = RetrofitController.getInstance().getLoadingAdviseApi().getAllPinnacleSupervisor("Bearer " + token);
        HashMap<String, Integer> hashMapPinnacleSupervisor = new HashMap<>();
        arrPinnacleSupervisor = new ArrayList<>();
        call.enqueue(new Callback<PinnacleSupervisorApiResponse>() {
            @Override
            public void onResponse(Call<PinnacleSupervisorApiResponse> call, Response<PinnacleSupervisorApiResponse> response) {
                if (!response.isSuccessful()) {
                    progressBar.setVisibility(View.GONE);
                    ((MainActivity) getActivity()).alert(getActivity(), "error", response.errorBody().toString(), null, "OK");
//                    alertBuilder(response.errorBody().toString());
                    return;
                }

                Log.i(TAG, "onResponse: Pinncale Supervisor found successfully" + response.raw());
                if (response.isSuccessful()) {
                    progressBar.setVisibility(View.GONE);
                    List<PinnacleLoadingSupervisorDto> pinnacleLoadingSupervisorDtoList = response.body().getPinnacleLoadingSupervisorDtos();
                    try {
                        if (pinnacleLoadingSupervisorDtoList == null || pinnacleLoadingSupervisorDtoList.isEmpty()) {
                            Toast.makeText(getActivity(), EMPTY_PINNACLE_SUPERVISOR, Toast.LENGTH_SHORT).show();
                            return;
                        }
                        for (int i = 0; i < pinnacleLoadingSupervisorDtoList.size(); i++) {
                            String name = pinnacleLoadingSupervisorDtoList.get(i).getName();
                            int id = pinnacleLoadingSupervisorDtoList.get(i).getId();
                            hashMapPinnacleSupervisor.put(name, id);
                            arrPinnacleSupervisor.add(name);
                        }
                        pinnacleSupervisorAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, arrPinnacleSupervisor);
                        autoCompletePinnacleSupervisor.setAdapter(pinnacleSupervisorAdapter);

                        autoCompletePinnacleSupervisor.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                selectedPinnacleSupervisor = pinnacleSupervisorAdapter.getItem(i);
                                if (hashMapPinnacleSupervisor.containsKey(selectedPinnacleSupervisor)) {
                                    selectedPinnacleSupervisorId = hashMapPinnacleSupervisor.get(selectedPinnacleSupervisor);
                                    Log.i(TAG, "onItemSelected: selectedPinnacleSupervisorId " + selectedPinnacleSupervisorId + " selectedPinnacleSupervisor : " + selectedPinnacleSupervisor);
                                }
                            }
                        });
                    } catch (Exception e) {
                        e.getMessage();
                    }
                }
            }

            @Override
            public void onFailure(Call<PinnacleSupervisorApiResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
//                alertBuilder(t.getMessage());
                ((MainActivity) getActivity()).alert(getActivity(), "error", t.getMessage(), null, "OK");
//                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        return true;
    }

    private void sendLoadingAdviseDetails(LoadingAdviseRequestDto loadingAdviseRequestDto) {
        progressBar.setVisibility(View.VISIBLE);
        Log.i(TAG, new Gson().toJson(loadingAdviseRequestDto).toString());
        Call<LoadingAdvisePostApiResponse> call = RetrofitController.getInstance().getLoadingAdviseApi().addRfidLepIssue("Bearer " + token, loadingAdviseRequestDto);
        call.enqueue(new Callback<LoadingAdvisePostApiResponse>() {
            @Override
            public void onResponse(Call<LoadingAdvisePostApiResponse> call, Response<LoadingAdvisePostApiResponse> response) {
                Log.i(TAG, "onResponse code : " + response.code());
                if (!response.isSuccessful()) {
                    progressBar.setVisibility(View.GONE);
                    ((MainActivity) getActivity()).alert(getActivity(), "error", response.errorBody().toString(), null, "OK");
//                    alertBuilder(response.errorBody().toString());
                }
                Log.i(TAG, "onResponse: add loading advise : " + response.body().getStatus());
                if (response.body().getStatus().equalsIgnoreCase("CREATED")) {
                    progressBar.setVisibility(View.GONE);
                    ((MainActivity) getActivity()).alert(getActivity(), "success", response.body().getMessage(), null, "OK");
                    resetTextField();
                } else {
                    progressBar.setVisibility(View.GONE);
                    ((MainActivity) getActivity()).alert(getActivity(), "error", response.body().getMessage(), null, "OK");
                    resetTextField();
                }
            }

            @Override
            public void onFailure(Call<LoadingAdvisePostApiResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                ((MainActivity) getActivity()).alert(getActivity(), "error", t.getMessage(), null, "OK");
            }
        });
    }

    private void UpdateBothraLoadingAdviseDetails(UpdateBothraLoadingAdviseDto
                                                          updateBothraLoadingAdviseDto) {
        progressBar.setVisibility(View.VISIBLE);
        Log.i(TAG, "updateBothraLoadingAdviseDto : Request Dto : <<------- " + new Gson().toJson(updateBothraLoadingAdviseDto).toString());
        Call<TransactionsApiResponse> call = RetrofitController.getInstance().getLoadingAdviseApi().updateBothraLoadingAdvise("Bearer " + token, updateBothraLoadingAdviseDto);
        call.enqueue(new Callback<TransactionsApiResponse>() {
            @Override
            public void onResponse(Call<TransactionsApiResponse> call, Response<TransactionsApiResponse> response) {
                if (!response.isSuccessful()) {
                    progressBar.setVisibility(View.GONE);
                    ((MainActivity) getActivity()).alert(getActivity(), "error", response.errorBody().toString(), null, "OK");
//                    alertBuilder(response.errorBody().toString());
                }

                if (response.body().getStatus().equalsIgnoreCase("FOUND")) {
                    progressBar.setVisibility(View.GONE);
                    ((MainActivity) getActivity()).alert(getActivity(), "success", response.body().getMessage(), null, "OK");
                    resetTextField();
                } else {
                    progressBar.setVisibility(View.GONE);
                    ((MainActivity) getActivity()).alert(getActivity(), "error", response.body().getMessage(), null, "OK");
//                    resetTextField();
                }
            }

            @Override
            public void onFailure(Call<TransactionsApiResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                ((MainActivity) getActivity()).alert(getActivity(), "error", t.getMessage(), null, "OK");
            }
        });
    }

    private LoadingAdviseRequestDto setData() {
        final Integer RSTAT = 1;
        final Integer FLAG = 1;
        LoadingAdviseRequestDto loadingAdviseRequestDto = null;

        AuditEntity auditEntity = new AuditEntity(loginUserName, null);
        RfidLepIssueDto rfidLepIssueModel = new RfidLepIssueDto(selectedLepNumberId);
        StorageLocationDto sourceMasterDto = new StorageLocationDto(loginUserStorageLocation);
        UserMasterDto loadingAdviseDto = new UserMasterDto(loginUserId);
        StorageLocationDto functionalLocationMasterDto = new StorageLocationDto(selectedDestinationLocation);
        BothraLoadingSupervisorDto bothraLoadingDto = new BothraLoadingSupervisorDto(selectedBothraSupervisorId);
        PinnacleLoadingSupervisorDto pinnacleLoadingDto = new PinnacleLoadingSupervisorDto(selectedPinnacleSupervisorId);
        loadingAdviseRequestDto = new LoadingAdviseRequestDto(auditEntity, bothraLoadingDto, loadingAdviseDto, pinnacleLoadingDto, sourceMasterDto, functionalLocationMasterDto, LocalDateTime.now().toString(), rfidLepIssueModel, FLAG, true, RSTAT);
        return loadingAdviseRequestDto;

    }

   /* private boolean nullCheck() {
        Log.i(TAG, "nullCheck: in null check");
        if (selectedBothraSupervisorId == null) {
            alertBuilder("Bothra Supervisor is not selected \n Please select Bothra supervisor from dropdown");
            return false;
        } else if (selectedPinnacleSupervisorId == null) {
            alertBuilder("Pinnacle Supervisor is not selected \n Please select Pinnacle Supervisor no from dropdown");
            return false;
        } else {
            return true;
        }

    }*/

    private UpdateBothraLoadingAdviseDto updateData() {
        final Integer BOTHRA_FLAG = 12;
        StorageLocationDto sourceMasterDto = new StorageLocationDto(loginUserStorageLocation);
        UserMasterDto loadingAdviseDto = new UserMasterDto(loginUserId);
        RfidLepIssueDto rfidLepIssueModel = new RfidLepIssueDto(selectedLepNumberId);
        StorageLocationDto functionalLocationMasterDto = new StorageLocationDto(selectedDestinationLocation);
        AuditEntity auditEntity = new AuditEntity(null, null, loginUserName, null);
        UpdateBothraLoadingAdviseDto updateBothraLoadingAdviseDto = new UpdateBothraLoadingAdviseDto(auditEntity, loadingAdviseDto, functionalLocationMasterDto, sourceMasterDto, rfidLepIssueModel, true, LocalDateTime.now().toString(), BOTHRA_FLAG);
        return updateBothraLoadingAdviseDto;
    }

    private void updateUIBasedOnUser() {
        if (loginUserPlantCode.equalsIgnoreCase(PLANT_BOTHRA)) {
            layoutBothraSupervisor.setVisibility(View.GONE);
            layoutPinnacleSupervisor.setVisibility(View.GONE);
        } else {
            layoutBothraSupervisor.setVisibility(View.VISIBLE);
            layoutPinnacleSupervisor.setVisibility(View.VISIBLE);
        }
    }

    private void chooseMethodToCall() {

        if ((loginUserPlantCode.equalsIgnoreCase(PLANT_BOTHRA))) {
            UpdateBothraLoadingAdviseDetails(updateData());
        } else {
            if (nullCheckMethod()) {
                sendLoadingAdviseDetails(setData());
            }
        }
    }

    private boolean nullCheckMethod() {
        String bothraSupervisor = autoCompleteBothraSupervisor.getText().toString();
        String pinnacleSupervisor = autoCompletePinnacleSupervisor.getText().toString();
        if (selectedBothraSupervisorId != null && selectedPinnacleSupervisorId != null) {
            Log.i(TAG, "chooseMethodToCall: selectedBothraSupervisorId " + selectedBothraSupervisorId + " selectedPinnacleSupervisorId" + selectedPinnacleSupervisorId);
            if (arrPinnacleSupervisor.contains(pinnacleSupervisor) && arrBothraSupervisor.contains(bothraSupervisor)) {
                if (selectedPinnacleSupervisor.equalsIgnoreCase(pinnacleSupervisor) && selectedBothraSupervisor.equalsIgnoreCase(bothraSupervisor)) {
                    return true;
                } else {
                    if (!bothraSupervisor.equalsIgnoreCase(selectedBothraSupervisor)) {
                        ((MainActivity) getActivity()).alert(getActivity(), "error", "It seems selected Bothra Supervisor is change", "Please try to select from drop-down..!", "OK");
                        return false;
                    } else if (!pinnacleSupervisor.equalsIgnoreCase(selectedPinnacleSupervisor)) {
                        ((MainActivity) getActivity()).alert(getActivity(), "error", "It seems selected Pinnacle Supervisor is change", "Please try to select from drop-down..!", "OK");
                        return false;
                    } else
                        return false;
                }
            } else {
                if (!arrPinnacleSupervisor.contains(pinnacleSupervisor)) {
                    ((MainActivity) getActivity()).alert(getActivity(), "error", "Selected Pinnacle supervisor is invalid", "Please try to select from drop-down..!", "OK");
                    return false;
                } else if (!arrBothraSupervisor.contains(bothraSupervisor)) {
                    ((MainActivity) getActivity()).alert(getActivity(), "error", "Selected Bothra supervisor is invalid", "Please try to select from drop-down..!", "OK");
                    return false;
                }
                return false;
            }
        } else {
            ((MainActivity) getActivity()).alert(getActivity(), "error", "It seems supervisor is type manually", "Please try to select from drop-down..!", "OK");
            return false;
        }

    }
}
