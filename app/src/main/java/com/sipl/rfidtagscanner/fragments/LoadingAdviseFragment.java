package com.sipl.rfidtagscanner.fragments;

import static android.content.Context.MODE_PRIVATE;
import static com.sipl.rfidtagscanner.utils.ToastConstants.EMPTY_BOTHRA_SUPERVISOR;
import static com.sipl.rfidtagscanner.utils.ToastConstants.EMPTY_DESTINATION_LOCATION;
import static com.sipl.rfidtagscanner.utils.ToastConstants.EMPTY_LEP_NUMBER_LIST;
import static com.sipl.rfidtagscanner.utils.ToastConstants.EMPTY_PINNACLE_SUPERVISOR;
import static com.sipl.rfidtagscanner.utils.ToastConstants.PLANT_BOTHRA;
import static com.sipl.rfidtagscanner.utils.ToastConstants.USERNAME;
import static com.sipl.rfidtagscanner.utils.ToastConstants.USER_ID;
import static com.sipl.rfidtagscanner.utils.ToastConstants.USER_PLANT_LOCATION;
import static com.sipl.rfidtagscanner.utils.ToastConstants.isGetPlantLocationByDescEnable;
import static com.sipl.rfidtagscanner.utils.ToastConstants.isGetSourceLocationByDescEnable;
import static com.sipl.rfidtagscanner.utils.ToastConstants.isRMGTableRequired;

import android.content.Context;
import android.content.DialogInterface;
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
import androidx.appcompat.app.AlertDialog;
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
import com.sipl.rfidtagscanner.utils.CustomToast;
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

    private int destinationLocationCounterFail = 6;
    private int getAllLepNumberCounterFail = 6;
    private int getAllPinnacleCounterFail = 6;
    private int getAllBothraCounterFail = 6;

    private TextClock tvClock;
    private ProgressBar progressBar;
    private LinearLayout mainRecyclerViewLayout;
    private Button btnSubmit, btnCancel;
    private AutoCompleteTextView autoCompleteLepNumber, autoCompletePinnacleSupervisor, autoCompleteBothraSupervisor;
    private RecyclerView recyclerViewRmgNo, recyclerViewTrip;
    private Spinner spinnerDestinationLocation;
    private TextView tvAutoCompleteLepNo, tvDestinationLocation, tvPinnacleSupervisor, tvBothraSupervisor;
    private EditText edtSapGrNo, edtTruckNumber, edtDriverName, edtDriverMobileNo, edtDriverLicenseNo, edtVesselName, edtCommodity, edtTruckCapacity, edtLoadingSupervisor, edtSourceLocation;
    private CustomToast customToast = new CustomToast();
    private Helper helper = new Helper();

    //    ArrayAdapter for spinner
    private ArrayAdapter<String> destinationLocationAdapter;
    private ArrayAdapter<String> pinnacleSupervisorAdapter;
    private ArrayAdapter<String> bothraSupervisorAdapter;
    private ArrayAdapter<String> arrayAdapterForLepNumber;

    private String selectedBothraSupervisor;
    private Integer selectedBothraSupervisorId;

    private String selectedPinnacleSupervisor;
    private Integer selectedPinnacleSupervisorId;

    private String selectedDestinationLocation;
    private String selectedLepNumber;
    private Integer selectedLepNumberId;

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

        edtLoadingSupervisor.setText(((MainActivity) getActivity()).getSharedPrefsValues(USERNAME));
        edtSourceLocation.setText(getUserSourceLocation());


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
//                    sendLoadingAdviseDetails(setData());
                    chooseMethodToCall();
                    return;
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
        Log.i(TAG, "callOnCreateApi: test simultaneously : call 1");
        if (!getAllDestinationLocation()) {
            getAllDestinationLocation();
            return false;
        }
        Log.i(TAG, "callOnCreateApi: test simultaneously : call 2");
        if (!getAllPinnacleSupervisor()) {
            getAllPinnacleSupervisor();
            return false;
        }
        Log.i(TAG, "callOnCreateApi: test simultaneously : call 3");
        if (!getAllBothraSupervisor()) {
            getAllBothraSupervisor();
            return false;
        }
        Log.i(TAG, "callOnCreateApi: test simultaneously : call 4");
        if (!getAllLepNumber()) {
            getAllLepNumber();
            return false;
        }
        Log.i(TAG, "callOnCreateApi: test simultaneously : call end");
        return true;
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
        Log.i(TAG, "setRecyclerView: <<Start>>");
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

        Log.i(TAG, "setRecyclerView: <<END>>");
    }

    public void alertBuilder(String alertMessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(alertMessage)
                .setCancelable(false)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private boolean validateLoadingAdviseForm() {
        if (autoCompleteLepNumber.length() == 0) {
            autoCompleteLepNumber.setError("This field is required");
            return false;
        }

        if (edtSapGrNo.length() == 0) {
            edtSapGrNo.setError("This field is required");
            edtSapGrNo.setHint("This field is required");
            return false;
        }

        if (edtTruckNumber.length() == 0) {
            edtTruckNumber.setError("This field is required");
            edtTruckNumber.setHint("This field is required");
            return false;
        }

        if (edtDriverName.length() == 0) {
            edtDriverName.setError("This field is required");
            edtDriverName.setHint("This field is required");
            return false;
        }

        if (edtDriverMobileNo.length() == 0) {
            edtDriverMobileNo.setError("This field is required");
            edtDriverMobileNo.setHint("This field is required");
            return false;
        }

        if (edtDriverLicenseNo.length() == 0) {
            edtDriverLicenseNo.setError("This field is required");
            edtDriverLicenseNo.setHint("This field is required");
            return false;
        }

        if (edtVesselName.length() == 0) {
            edtVesselName.setError("This field is required");
            edtVesselName.setHint("This field is required");
            return false;
        }

        if (edtCommodity.length() == 0) {
            edtCommodity.setError("This field is required");
            edtCommodity.setHint("This field is required");
            return false;
        }

        if (edtTruckCapacity.length() == 0) {
            edtTruckCapacity.setError("This field is required");
            edtTruckCapacity.setHint("This field is required");
            return false;
        }

        if (edtLoadingSupervisor.length() == 0) {
            edtLoadingSupervisor.setError("This field is required");
            edtLoadingSupervisor.setHint("This field is required");
            return false;
        }

        if (edtSourceLocation.length() == 0) {
            edtSourceLocation.setError("This field is required");
            edtSourceLocation.setHint("This field is required");
            return false;
        }

        if (spinnerDestinationLocation.getSelectedItem().toString().equals("Select Destination")) {
            Toast.makeText(getActivity(), "Select Destination Location", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!((MainActivity) getActivity()).getSharedPrefsValues(USER_PLANT_LOCATION).equalsIgnoreCase(PLANT_BOTHRA)) {

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
        if (!((MainActivity) getActivity()).getSharedPrefsValues(USER_PLANT_LOCATION).equalsIgnoreCase(PLANT_BOTHRA)) {
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
        }
        if (!getAllLepNumber()) {
            getAllLepNumber();
        }
        removeErrorMessage();
    }

    private void removeErrorMessage() {
        autoCompleteLepNumber.setError(null);

        edtSapGrNo.setError(null);
        edtSapGrNo.setHint(null);

        edtTruckNumber.setError(null);
        edtTruckNumber.setHint(null);

        edtDriverName.setError(null);
        edtDriverName.setHint(null);

        edtDriverMobileNo.setError(null);
        edtDriverMobileNo.setHint(null);

        edtDriverLicenseNo.setError(null);
        edtDriverLicenseNo.setHint(null);

        edtVesselName.setError(null);
        edtVesselName.setHint(null);

        edtCommodity.setError(null);
        edtCommodity.setHint(null);

        edtTruckCapacity.setError(null);
        edtTruckCapacity.setHint(null);

        edtLoadingSupervisor.setError(null);
        edtLoadingSupervisor.setHint(null);
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
            Log.i("login fragment ", "setting time done");
        } catch (Exception e) {
            Log.e(TAG, "onCreateView: date format error", e);
            e.printStackTrace();
        }
    }

    private String getToken() {
        SharedPreferences sp = getActivity().getSharedPreferences("loginCredentials", MODE_PRIVATE);
        String token = sp.getString("tokenSPK", null);
        return token;
    }

  /*  private String loginUsername() {
        SharedPreferences sp = getActivity().getSharedPreferences("loginCredentials", MODE_PRIVATE);
        String username = sp.getString("usernameSPK", null);
        return username;
    }*/

    // userID means userId position in Database
   /* private int loginUserID() {
        SharedPreferences sp = getActivity().getSharedPreferences("loginCredentials", MODE_PRIVATE);
        int userID = Integer.parseInt(sp.getString("userIDSPK", null));
        return userID;
    }*/

    private String getUserSourceLocation() {
        SharedPreferences sp = getActivity().getSharedPreferences("loginCredentials", MODE_PRIVATE);
        String userSourceLocation = sp.getString("UserSourceLocationSPK", null);
        return userSourceLocation;
    }

    private boolean getAllLepNumber() {
        progressBar.setVisibility(View.VISIBLE);
        HashMap<String, Integer> hashMapLepNumber = new HashMap<>();
//        HashMap<String, String> hashMapCreatedTime = new HashMap<>();
//        HashMap<String, String> hashMapCreatedBy = new HashMap<>();
        ArrayList<String> arrAutoCompleteLepNo = new ArrayList<>();
        try {
            Call<RfidLepApiResponse> call = RetrofitController.getInstance().getLoadingAdviseApi().getALlLepNumber("Bearer " + getToken());
            call.enqueue(new Callback<RfidLepApiResponse>() {
                @Override
                public void onResponse(Call<RfidLepApiResponse> call, Response<RfidLepApiResponse> response) {
                    progressBar.setVisibility(View.GONE);
                    if (!response.isSuccessful()) {
                        String responseCode = String.valueOf(response.code());
                        Log.i(TAG, "getAllLepNumber: responseCode : " + responseCode + response.raw());
                        return;
                    }
                    if (response.code() != 200) {
                        getAllLepNumber();
                        return;
                    }
                    if (response.isSuccessful()) {
                        Log.i(TAG, "onResponse: " + response.body().getStatus());
                        Log.i(TAG, "onResponse: " + response.raw());
                        List<RfidLepIssueDto> rfidLepIssueDtoList = response.body().getRfidLepIssueDtos();
                        try {
                            if (rfidLepIssueDtoList == null || rfidLepIssueDtoList.isEmpty()) {
                                autoCompleteLepNumber.setHint("No Lep number available");
                                customToast.toastMessage(getActivity(), EMPTY_LEP_NUMBER_LIST, 0);
                                return;
                            }
                            String strLepNumber = null, strSapGrNo = null, strTruckNo = null, strDriverName = null, strDriverMobileNo = null, strDriverLicenseNo = null, strVesselName = null, strTruckCapacity = null, strCommodity = null;
                            for (int i = 0; i < rfidLepIssueDtoList.size(); i++) {
                                strLepNumber = rfidLepIssueDtoList.get(i).getLepNumber();
                                int id = rfidLepIssueDtoList.get(i).getId();
                                String strDN = rfidLepIssueDtoList.get(i).getDriverMaster().getDriverMobileNo().toString();
                                strDriverName = String.valueOf(rfidLepIssueDtoList.get(i).getDriverMaster().getDriverName());
                                strDriverMobileNo = String.valueOf(rfidLepIssueDtoList.get(i).getDriverMaster().getDriverMobileNo());
                                strDriverLicenseNo = String.valueOf(rfidLepIssueDtoList.get(i).getDriverMaster().getDriverLicenseNo());
                                strSapGrNo = String.valueOf(rfidLepIssueDtoList.get(i).getDailyTransportReportModule().getSapGrNumber());
                                strTruckNo = String.valueOf(rfidLepIssueDtoList.get(i).getDailyTransportReportModule().getTruckNumber());
                                strVesselName = String.valueOf(rfidLepIssueDtoList.get(i).getDailyTransportReportModule().getVesselName());
                                strTruckCapacity = String.valueOf(rfidLepIssueDtoList.get(i).getDailyTransportReportModule().getTruckCapacity());
                                strCommodity = String.valueOf(rfidLepIssueDtoList.get(i).getDailyTransportReportModule().getCommodity());
                                arrAutoCompleteLepNo.add(strLepNumber);
                                hashMapLepNumber.put(strLepNumber, id);
                            }
                            arrayAdapterForLepNumber = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, arrAutoCompleteLepNo);
                            autoCompleteLepNumber.setAdapter(arrayAdapterForLepNumber);
                            String finalStrSapGrNo = strSapGrNo;
                            String finalStrTruckNo = strTruckNo;
                            String finalStrDriverName = strDriverName;
                            String finalStrDriverMobileNo = strDriverMobileNo;
                            String finalStrDriverLicenseNo = strDriverLicenseNo;
                            String finalStrVesselName = strVesselName;
                            String finalStrTruckCapacity = strTruckCapacity;
                            String finalStrCommodity = strCommodity;
                            autoCompleteLepNumber.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                    selectedLepNumber = arrayAdapterForLepNumber.getItem(i);
                                    if (hashMapLepNumber.containsKey(selectedLepNumber)) {
                                        selectedLepNumberId = hashMapLepNumber.get(selectedLepNumber);
                                    }
                                    if (arrAutoCompleteLepNo.contains(selectedLepNumber)) {
                                        edtSapGrNo.setText(finalStrSapGrNo);
                                        edtTruckNumber.setText(finalStrTruckNo);
                                        edtDriverName.setText(finalStrDriverName);
                                        edtDriverMobileNo.setText(finalStrDriverMobileNo);
                                        edtDriverLicenseNo.setText(finalStrDriverLicenseNo);
                                        edtVesselName.setText(finalStrVesselName);
                                        edtCommodity.setText(finalStrCommodity);
                                        edtTruckCapacity.setText(finalStrTruckCapacity);
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
                    getAllLepNumberCounterFail--;
                    if (getAllLepNumberCounterFail == 0) {
                        t.getMessage();
                        progressBar.setVisibility(View.GONE);
                        return;
                    }
                    if (getAllLepNumberCounterFail != 0) {
//                        customToast.toastMessage(getActivity(), FAILED_CONNECTION + t.getMessage(), 0);
                        getAllLepNumber();
                    }
                }
            });
        } catch (Exception e) {
            Log.i(TAG, "getALlLepNumberWithFlag: " + e.getMessage());
        }
        return true;
    }

    private boolean getAllDestinationLocation() {
        Call<DestinationLocationResponseApi> call = RetrofitController.getInstance().getLoadingAdviseApi().
                getAllDestinationLocation("Bearer " + getToken());
        HashMap<String, Integer> hashMapDestinationLocation = new HashMap<>();
        ArrayList<String> arrDestinationLocation = new ArrayList<>();
        call.enqueue(new Callback<DestinationLocationResponseApi>() {
            @Override
            public void onResponse(Call<DestinationLocationResponseApi> call, Response<DestinationLocationResponseApi> response) {
                if (!response.isSuccessful()) {
                    String responseCode = String.valueOf(response.code());
                    Log.i(TAG, "getAllDestinationLocation: responseCode : " + responseCode + response.raw());
                    return;
                }
                Log.i(TAG, "onResponse: getAllDestinationLocation : responseCode : " + response.code());
                if (response.code() != 200) {
                    getAllDestinationLocation();
                    return;
                }
                if (response.isSuccessful()) {

                    Log.i(TAG, "onResponse: getAllDestinationLocation" + response.code() + " " + response.raw());
                    List<StorageLocationDto> functionalLocationMasterDtoList = response.body().getStorageLocationDtos();

                    try {
                        if (functionalLocationMasterDtoList == null || functionalLocationMasterDtoList.isEmpty()) {
                            customToast.toastMessage(getActivity(), EMPTY_DESTINATION_LOCATION, 0);
                            return;
                        }
                        for (int i = 0; i < functionalLocationMasterDtoList.size(); i++) {
                            String s = functionalLocationMasterDtoList.get(i).getStrLocationCode();

                            if (isGetSourceLocationByDescEnable == true) {
                                String StorageCodeDesc = functionalLocationMasterDtoList.get(i).getStrLocationDesc();
                                arrDestinationLocation.add(StorageCodeDesc);
                            } else if (isGetPlantLocationByDescEnable == true) {
                                String plantCode = functionalLocationMasterDtoList.get(i).getPlantMaster().getPlantCode();
                                arrDestinationLocation.add(plantCode);
                            } else {
                                arrDestinationLocation.add(s);
                            }

                        }
                        arrDestinationLocation.add("Select Destination");
                        String userSourceLocation = getUserSourceLocation();
                        if (arrDestinationLocation.contains(userSourceLocation)) {
                            arrDestinationLocation.remove(userSourceLocation);
                        }
                        Log.i(TAG, "onResponse: array : " + arrDestinationLocation.size());

                        destinationLocationAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, arrDestinationLocation) {
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
                                selectedDestinationLocation = adapterView.getSelectedItem().toString();
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
                destinationLocationCounterFail--;
                if (destinationLocationCounterFail == 0) {
                    t.getMessage();
                    return;
                }
                if (destinationLocationCounterFail != 0) {
//                    customToast.toastMessage(getActivity(), FAILED_CONNECTION + t.getMessage(), 0);
                    getAllDestinationLocation();
                }
            }
        });
        return true;
    }

    private boolean getAllBothraSupervisor() {
        Call<BothraSupervisorApiResponse> call = RetrofitController.getInstance().getLoadingAdviseApi().getAllBothraSupervisor("Bearer " + getToken());
        HashMap<String, Integer> hashMapBothraSupervisor = new HashMap<>();
        ArrayList<String> arrBothraSupervisor = new ArrayList<>();
        call.enqueue(new Callback<BothraSupervisorApiResponse>() {
            @Override
            public void onResponse(Call<BothraSupervisorApiResponse> call, Response<BothraSupervisorApiResponse> response) {
                if (!response.isSuccessful()) {
                    Log.i(TAG, "onResponse: code : " + response.code() + response.raw());
                    return;
                }
                Log.i(TAG, "onResponse: getAllBothraSupervisor : responseCode : " + response.code());
                if (response.code() != 200) {
                    getAllBothraSupervisor();
                    return;
                }

                if (response.code() == 200) {
                    Log.i(TAG, "onResponse: Bothra supervisor found successfully" + response.raw());
                }
                List<BothraLoadingSupervisorDto> bothraLoadingSupervisorDtosList = response.body().getBothraLoadingSupervisorDtos();

                try {
                    if (bothraLoadingSupervisorDtosList == null || bothraLoadingSupervisorDtosList.isEmpty()) {
                        customToast.toastMessage(getActivity(), EMPTY_BOTHRA_SUPERVISOR, 0);
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

            @Override
            public void onFailure(Call<BothraSupervisorApiResponse> call, Throwable t) {
                getAllBothraCounterFail--;
                if (getAllBothraCounterFail == 0) {
                    t.getMessage();
                    return;
                }
                if (getAllBothraCounterFail != 0) {
                    getAllBothraSupervisor();
                }
            }
        });
        return true;
    }

    private boolean getAllPinnacleSupervisor() {
        Log.i(TAG, "getAllPinnacleSupervisor: " + getToken());
        Call<PinnacleSupervisorApiResponse> call = RetrofitController.getInstance().getLoadingAdviseApi().getAllPinnacleSupervisor("Bearer " + getToken());
        HashMap<String, Integer> hashMapPinnacleSupervisor = new HashMap<>();
        ArrayList<String> arrPinnacleSupervisor = new ArrayList<>();
        call.enqueue(new Callback<PinnacleSupervisorApiResponse>() {
            @Override
            public void onResponse(Call<PinnacleSupervisorApiResponse> call, Response<PinnacleSupervisorApiResponse> response) {
                if (!response.isSuccessful()) {
                    Log.i(TAG, "getAllPinnacleSupervisor: responseCode : " + response.code());
                    return;
                }
                if (response.code() != 200) {
                    Log.i(TAG, "onResponse: getAllPinnacleSupervisor : " + response.body());
                    getAllPinnacleSupervisor();
                    return;
                }
                if (response.code() == 200) {
                    Log.i(TAG, "onResponse: Pinncale Supervisor found successfully" + response.raw());
                    List<PinnacleLoadingSupervisorDto> pinnacleLoadingSupervisorDtoList = response.body().getPinnacleLoadingSupervisorDtos();
                    try {
                        if (pinnacleLoadingSupervisorDtoList == null || pinnacleLoadingSupervisorDtoList.isEmpty()) {
                            customToast.toastMessage(getActivity(), EMPTY_PINNACLE_SUPERVISOR, 0);
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
                getAllPinnacleCounterFail--;
                if (getAllPinnacleCounterFail == 0) {
                    Log.i(TAG, "onFailure: getAllPinnacleCounterFail : " + t.getMessage());
                    return;
                }
                if (getAllPinnacleCounterFail != 0) {
                    getAllPinnacleSupervisor();
                }
            }
        });
        return true;
    }

    private void sendLoadingAdviseDetails(LoadingAdviseRequestDto loadingAdviseRequestDto) {
        progressBar.setVisibility(View.VISIBLE);
        Log.i(TAG, new Gson().toJson(loadingAdviseRequestDto).toString());
        Call<LoadingAdvisePostApiResponse> call = RetrofitController.getInstance().getLoadingAdviseApi().addRfidLepIssue("Bearer " + getToken(), loadingAdviseRequestDto);
        call.enqueue(new Callback<LoadingAdvisePostApiResponse>() {
            @Override
            public void onResponse(Call<LoadingAdvisePostApiResponse> call, Response<LoadingAdvisePostApiResponse> response) {
                progressBar.setVisibility(View.GONE);
                Log.i(TAG, "onResponse code : " + response.code());
                if (response.code() == 200) {
                    alertBuilder(response.body().getMessage());
                    progressBar.setVisibility(View.GONE);
                    resetTextField();
                }
                if (response.code() != 200) {
                    Log.i(TAG, "onResponse: " + response.code());
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<LoadingAdvisePostApiResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Log.i(TAG, "onFailure: " + t.getMessage());
                t.printStackTrace();
            }
        });
    }

    private void UpdateBothraLoadingAdviseDetails(UpdateBothraLoadingAdviseDto updateBothraLoadingAdviseDto) {
        progressBar.setVisibility(View.VISIBLE);
        Log.i(TAG, "updateBothraLoadingAdviseDto : Request Dto : <<------- "+new Gson().toJson(updateBothraLoadingAdviseDto).toString());
        Call<TransactionsApiResponse> call = RetrofitController.getInstance().getLoadingAdviseApi().updateBothraLoadingAdvise("Bearer " + getToken(), updateBothraLoadingAdviseDto);
        call.enqueue(new Callback<TransactionsApiResponse>() {
            @Override
            public void onResponse(Call<TransactionsApiResponse> call, Response<TransactionsApiResponse> response) {
                progressBar.setVisibility(View.GONE);
                Log.i(TAG, "onResponse code : " + response.code());
                if (response.code() == 200) {
                    alertBuilder(response.body().getMessage());
                    progressBar.setVisibility(View.GONE);
                    resetTextField();
                }
                if (response.code() != 200) {
                    Log.i(TAG, "onResponse: " + response.code());
                    alertBuilder(String.valueOf(response.errorBody()));
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<TransactionsApiResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Log.i(TAG, "onFailure: " + t.getMessage());
                t.printStackTrace();
            }
        });
    }

    private LoadingAdviseRequestDto setData() {
        final Integer RSTAT = 1;
        final Integer FLAG = 1;
//        final Integer BOTHRA_FLAG = 12;
        int userid = Integer.parseInt(((MainActivity) getActivity()).getSharedPrefsValues(USER_ID));
        AuditEntity auditEntity = new AuditEntity(((MainActivity) getActivity()).getSharedPrefsValues(USERNAME), LocalDateTime.now().toString());
        RfidLepIssueDto rfidLepIssueModel = new RfidLepIssueDto(selectedLepNumberId);
        StorageLocationDto sourceMasterDto = new StorageLocationDto(getUserSourceLocation());
        UserMasterDto loadingAdviseDto = new UserMasterDto(userid);
        StorageLocationDto functionalLocationMasterDto = new StorageLocationDto(selectedDestinationLocation);
        LoadingAdviseRequestDto loadingAdviseRequestDto;
//        if (!((MainActivity) getActivity()).getSharedPrefsValues(USER_PLANT_LOCATION).equalsIgnoreCase(PLANT_BOTHRA)) {
        BothraLoadingSupervisorDto bothraLoadingDto = new BothraLoadingSupervisorDto(selectedBothraSupervisorId);
        PinnacleLoadingSupervisorDto pinnacleLoadingDto = new PinnacleLoadingSupervisorDto(selectedPinnacleSupervisorId);
        loadingAdviseRequestDto = new LoadingAdviseRequestDto(auditEntity, bothraLoadingDto, loadingAdviseDto, pinnacleLoadingDto, sourceMasterDto, functionalLocationMasterDto, LocalDateTime.now().toString(), rfidLepIssueModel, FLAG, true, RSTAT);
//        } else {
//            loadingAdviseRequestDto = new LoadingAdviseRequestDto(auditEntity, loadingAdviseDto, sourceMasterDto, functionalLocationMasterDto, LocalDateTime.now().toString(), rfidLepIssueModel, BOTHRA_FLAG, true, RSTAT);
//        }
        return loadingAdviseRequestDto;
    }

    private UpdateBothraLoadingAdviseDto updateData() {
        final Integer BOTHRA_FLAG = 12;
        int userid = Integer.parseInt(((MainActivity) getActivity()).getSharedPrefsValues(USER_ID));
        StorageLocationDto sourceMasterDto = new StorageLocationDto(getUserSourceLocation());
        UserMasterDto loadingAdviseDto = new UserMasterDto(userid);
        RfidLepIssueDto rfidLepIssueModel = new RfidLepIssueDto(selectedLepNumberId);
        StorageLocationDto functionalLocationMasterDto = new StorageLocationDto(selectedDestinationLocation);
        AuditEntity auditEntity = new AuditEntity(null, null, ((MainActivity) getActivity()).getSharedPrefsValues(USERNAME), LocalDateTime.now().toString());
        UpdateBothraLoadingAdviseDto updateBothraLoadingAdviseDto = new UpdateBothraLoadingAdviseDto(auditEntity,loadingAdviseDto,functionalLocationMasterDto,sourceMasterDto,rfidLepIssueModel,true,LocalDateTime.now().toString(),BOTHRA_FLAG);
        return updateBothraLoadingAdviseDto;
    }

  /*  private String getUserPlantCode() {
        SharedPreferences sp = getActivity().getSharedPreferences("loginCredentials", MODE_PRIVATE);
        String loginUserPlantCode = sp.getString("userPlantLocationSPK", null);
        return loginUserPlantCode;
    }*/

    private void updateUIBasedOnUser() {
        String plantCode = ((MainActivity) getActivity()).getSharedPrefsValues(USER_PLANT_LOCATION);
        if (plantCode.equalsIgnoreCase(PLANT_BOTHRA)) {
            layoutBothraSupervisor.setVisibility(View.GONE);
            layoutPinnacleSupervisor.setVisibility(View.GONE);
        } else {
            layoutBothraSupervisor.setVisibility(View.VISIBLE);
            layoutPinnacleSupervisor.setVisibility(View.VISIBLE);
        }
    }

    private void chooseMethodToCall() {
        String plantCode = ((MainActivity) getActivity()).getSharedPrefsValues(USER_PLANT_LOCATION);
        if ((plantCode.equalsIgnoreCase(PLANT_BOTHRA))) {
            UpdateBothraLoadingAdviseDetails(updateData());
        } else {
            sendLoadingAdviseDetails(setData());
        }
    }


}




