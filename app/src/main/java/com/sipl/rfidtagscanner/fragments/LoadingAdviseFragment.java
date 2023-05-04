package com.sipl.rfidtagscanner.fragments;

import static android.content.Context.MODE_PRIVATE;
import static com.sipl.rfidtagscanner.utils.Config.EMPTY_BOTHRA_SUPERVISOR;
import static com.sipl.rfidtagscanner.utils.Config.EMPTY_DESTINATION_LOCATION;
import static com.sipl.rfidtagscanner.utils.Config.EMPTY_PINNACLE_SUPERVISOR;
import static com.sipl.rfidtagscanner.utils.Config.PLANT_BOTHRA;

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

import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.sipl.rfidtagscanner.MainActivity;
import com.sipl.rfidtagscanner.R;
import com.sipl.rfidtagscanner.RetrofitController;
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
import com.sipl.rfidtagscanner.dto.response.TransactionsApiResponse;
import com.sipl.rfidtagscanner.entites.AuditEntity;
import com.sipl.rfidtagscanner.utils.Helper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoadingAdviseFragment extends Fragment {
    private static final String TAG = "TracingError";
    private final Helper helper = new Helper();
    ArrayList<String> arrPinnacleSupervisor;
    ArrayList<String> arrBothraSupervisor;
    ArrayList<String> arrBothraStrLocation = new ArrayList<>();
    private TextClock tvClock;
    private ProgressBar progressBar;
    private AutoCompleteTextView autoCompletePinnacleSupervisor, autoCompleteBothraSupervisor;
    private Spinner spinnerDestinationLocation;
    private TextView tvDestinationLocation, tvPinnacleSupervisor, tvBothraSupervisor;
    private EditText edtRfidTagNo, edtLepNo, edtSapGrNo, edtTruckNumber, edtDriverName, edtDriverMobileNo, edtDriverLicenseNo, edtVesselName, edtCommodity, edtTruckCapacity, edtLoadingSupervisor, edtSourceLocation;
    //    userDetails
    private String loginUserName;
    private String token;
    private String loginUserStorageLocation;
    private String loginUserStorageLocationDesc;
//    private String loginUserPlantCode;
    private int loginUserId;

    //    ArrayAdapter for spinner
    private ArrayAdapter<String> destinationLocationAdapter;
    private ArrayAdapter<String> pinnacleSupervisorAdapter;
    private ArrayAdapter<String> bothraSupervisorAdapter;

    private Integer selectedBothraSupervisorId;
    private String selectedBothraSupervisor;
    private Integer selectedPinnacleSupervisorId;
    private String selectedPinnacleSupervisor;
    private String selectedDestinationLocation;
    private Integer selectedLepNumberId;

    private LinearLayout layoutBothraSupervisor, layoutPinnacleSupervisor;

    public LoadingAdviseFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_loading_adivse, container, false);

        autoCompletePinnacleSupervisor = view.findViewById(R.id.ll_auto_pinnacle_supervisor);
        autoCompleteBothraSupervisor = view.findViewById(R.id.ll_auto_bothra_supervisor);

        spinnerDestinationLocation = view.findViewById(R.id.ll_spinner_destination_location);

        Button btnSubmit = view.findViewById(R.id.btn_loading_advise_submit);
        Button btnCancel = view.findViewById(R.id.btn_loading_advise_reset);

        edtSourceLocation = view.findViewById(R.id.la_edt_source_location);
        edtRfidTagNo = view.findViewById(R.id.edt_la_rfid_tag_no);
        edtLepNo = view.findViewById(R.id.la_edt_lep_number);
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

        tvClock = view.findViewById(R.id.la_tv_clock);
        tvDestinationLocation = view.findViewById(R.id.tv_la_destination_location);
        tvBothraSupervisor = view.findViewById(R.id.tv_la_bothra_supervisor);
        tvPinnacleSupervisor = view.findViewById(R.id.tv_la_pinnacle_supervisor);

        layoutBothraSupervisor = view.findViewById(R.id.title_bothra_supervisor);
        layoutPinnacleSupervisor = view.findViewById(R.id.title_pinnacle_supervisor);

        this.loginUserName = ((MainActivity) requireActivity()).getLoginUsername();
        this.loginUserId = ((MainActivity) requireActivity()).getLoginUserId();
//        this.loginUserPlantCode = ((MainActivity) requireActivity()).getLoginUserPlantCode();
        this.loginUserStorageLocation = ((MainActivity) requireActivity()).getLoginUserStorageCode();
        this.loginUserStorageLocationDesc = ((MainActivity) requireActivity()).getLoginUserSourceLocationDesc();
        this.token = ((MainActivity) requireActivity()).getLoginToken();

        edtLoadingSupervisor.setText(loginUserName);

        edtSourceLocation.setText(loginUserStorageLocation + " - " + loginUserStorageLocationDesc);

        getBundleData();

        /*
         *  methods need to run on onCreate
         */

        updateUIBasedOnUser();
        makeTvTextCompulsory();
        displayClock();
        getLoadingAdviseDetails();
        callOnCreateApi();

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
            int s = Integer.parseInt(sp.getString("size", null));
        for (int i = 0; i < s; i++) {
            String m = sp.getString(String.valueOf(i), null);
            arrBothraStrLocation.add(m);
        }
    }

    private void callOnCreateApi() {
        getAllDestinationLocation();
        getAllPinnacleSupervisor();
        getAllBothraSupervisor();
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
//        if (!loginUserPlantCode.equalsIgnoreCase(PLANT_BOTHRA)) {
        if (!arrBothraStrLocation.contains(loginUserStorageLocation)) {

            if (autoCompleteBothraSupervisor.length() == 0) {
                autoCompleteBothraSupervisor.setError("This field is required");
                return false;
            }

            if (autoCompletePinnacleSupervisor.length() == 0) {
                autoCompletePinnacleSupervisor.setError("This field is required");
                return false;
            }
        }
        return true;
    }

    private void resetTextField() {
        ((MainActivity) requireActivity()).loadFragment(new ScanFragment(), 1);
    }


    private void makeTvTextCompulsory() {
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

    private void getAllDestinationLocation() {
        progressBar.setVisibility(View.VISIBLE);
        Call<DestinationLocationResponseApi> call = RetrofitController.getInstance().getLoadingAdviseApi().
                getAllDestinationLocation("Bearer " + token);

        call.enqueue(new Callback<DestinationLocationResponseApi>() {
            @Override
            public void onResponse(Call<DestinationLocationResponseApi> call, Response<DestinationLocationResponseApi> response) {
                if (!response.isSuccessful()) {
                    progressBar.setVisibility(View.GONE);
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
        Log.i(TAG, "updateBothraLoadingAdviseDto : Request Dto : <<------- " + new Gson().toJson(updateBothraLoadingAdviseDto));
        Call<TransactionsApiResponse> call = RetrofitController.getInstance().getLoadingAdviseApi().updateBothraLoadingAdvise("Bearer " + token, updateBothraLoadingAdviseDto);
        call.enqueue(new Callback<TransactionsApiResponse>() {
            @Override
            public void onResponse(Call<TransactionsApiResponse> call, Response<TransactionsApiResponse> response) {
                if (!response.isSuccessful()) {
                    progressBar.setVisibility(View.GONE);
                    ((MainActivity) requireActivity()).alert(requireActivity(), "error", response.errorBody().toString(), null, "OK");
                }

                if (response.body().getStatus().equalsIgnoreCase("FOUND")) {
                    progressBar.setVisibility(View.GONE);
                    ((MainActivity) requireActivity()).alert(requireActivity(), "success", response.body().getMessage(), null, "OK");
                    resetTextField();
                } else {
                    progressBar.setVisibility(View.GONE);
                    ((MainActivity) requireActivity()).alert(requireActivity(), "error", response.body().getMessage(), null, "OK");
                }
            }

            @Override
            public void onFailure(Call<TransactionsApiResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                ((MainActivity) requireActivity()).alert(requireActivity(), "error", t.getMessage(), null, "OK");
            }
        });
    }

    private LoadingAdviseRequestDto setData() {
        final Integer RSTAT = 1;
        final Integer FLAG = 1;
        AuditEntity auditEntity = new AuditEntity(loginUserName, null);
        RfidLepIssueDto rfidLepIssueModel = new RfidLepIssueDto(selectedLepNumberId);
        StorageLocationDto sourceMasterDto = new StorageLocationDto(loginUserStorageLocation);
        UserMasterDto loadingAdviseDto = new UserMasterDto(loginUserId);
        StorageLocationDto functionalLocationMasterDto = new StorageLocationDto(selectedDestinationLocation);
        BothraLoadingSupervisorDto bothraLoadingDto = new BothraLoadingSupervisorDto(selectedBothraSupervisorId);
        PinnacleLoadingSupervisorDto pinnacleLoadingDto = new PinnacleLoadingSupervisorDto(selectedPinnacleSupervisorId);
        return new LoadingAdviseRequestDto(auditEntity, bothraLoadingDto, loadingAdviseDto, pinnacleLoadingDto, sourceMasterDto, functionalLocationMasterDto, LocalDateTime.now().toString(), rfidLepIssueModel, FLAG, true, RSTAT);

    }


    private UpdateBothraLoadingAdviseDto updateData() {
        final Integer BOTHRA_FLAG = 12;
        StorageLocationDto sourceMasterDto = new StorageLocationDto(loginUserStorageLocation);
        UserMasterDto loadingAdviseDto = new UserMasterDto(loginUserId);
        RfidLepIssueDto rfidLepIssueModel = new RfidLepIssueDto(selectedLepNumberId);
        StorageLocationDto functionalLocationMasterDto = new StorageLocationDto(selectedDestinationLocation);
        AuditEntity auditEntity = new AuditEntity(null, null, loginUserName, null);
        return new UpdateBothraLoadingAdviseDto(auditEntity, loadingAdviseDto, functionalLocationMasterDto, sourceMasterDto, rfidLepIssueModel, true, LocalDateTime.now().toString(), BOTHRA_FLAG);
    }

    private void updateUIBasedOnUser() {
//        if (loginUserPlantCode.equalsIgnoreCase(PLANT_BOTHRA)) {
        if (arrBothraStrLocation.contains(loginUserStorageLocation)) {
            layoutBothraSupervisor.setVisibility(View.GONE);
            layoutPinnacleSupervisor.setVisibility(View.GONE);
        } else {
            layoutBothraSupervisor.setVisibility(View.VISIBLE);
            layoutPinnacleSupervisor.setVisibility(View.VISIBLE);
        }
    }

    private void chooseMethodToCall() {

//        if ((loginUserPlantCode.equalsIgnoreCase(PLANT_BOTHRA))) {
      if (arrBothraStrLocation.contains(loginUserStorageLocation)) {
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
            if (arrPinnacleSupervisor.contains(pinnacleSupervisor) && arrBothraSupervisor.contains(bothraSupervisor)) {
                if (selectedPinnacleSupervisor.equalsIgnoreCase(pinnacleSupervisor) && selectedBothraSupervisor.equalsIgnoreCase(bothraSupervisor)) {
                    return true;
                } else {
                    if (!bothraSupervisor.equalsIgnoreCase(selectedBothraSupervisor)) {
                        ((MainActivity) requireActivity()).alert(requireActivity(), "error", "It seems selected Bothra Supervisor is change", "Please try to select from drop-down..!", "OK");
                        return false;
                    } else if (!pinnacleSupervisor.equalsIgnoreCase(selectedPinnacleSupervisor)) {
                        ((MainActivity) requireActivity()).alert(requireActivity(), "error", "It seems selected Pinnacle Supervisor is change", "Please try to select from drop-down..!", "OK");
                        return false;
                    } else
                        return false;
                }
            } else {
                if (!arrPinnacleSupervisor.contains(pinnacleSupervisor)) {
                    ((MainActivity) requireActivity()).alert(requireActivity(), "error", "Selected Pinnacle supervisor is invalid", "Please try to select from drop-down..!", "OK");
                    return false;
                } else if (!arrBothraSupervisor.contains(bothraSupervisor)) {
                    ((MainActivity) requireActivity()).alert(requireActivity(), "error", "Selected Bothra supervisor is invalid", "Please try to select from drop-down..!", "OK");
                    return false;
                }
                return false;
            }
        } else {
            ((MainActivity) requireActivity()).alert(requireActivity(), "error", "It seems supervisor is type manually", "Please try to select from drop-down..!", "OK");
            return false;
        }

    }

    private void getLoadingAdviseDetails() {
        SharedPreferences sp = requireActivity().getSharedPreferences("loadingAdviceDetails", MODE_PRIVATE);
        this.selectedLepNumberId = Integer.valueOf(sp.getString("lepNoIdSPK", null));
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
        saveLoginAdviseData(rfidTagId, lepNo, driverName, driverMobileNo, driverLicenseNo, truckNo, sapGrNo, vesselName, truckCapacity, commodity);
    }

    private void saveLoginAdviseData(String rfidTag, String lepNo, String driverName, String driverMobileNo, String driverLicenseNo, String truckNo, String sapGrNo, String vesselName, String truckCapacity, String commodity) {
        edtRfidTagNo.setText(rfidTag);
        edtLepNo.setText(lepNo);
        edtSapGrNo.setText(sapGrNo);
        edtTruckNumber.setText(truckNo);
        edtDriverName.setText(driverName);
        edtDriverMobileNo.setText(driverMobileNo);
        edtDriverLicenseNo.setText(driverLicenseNo);
        edtVesselName.setText(vesselName);
        edtCommodity.setText(commodity);
        edtTruckCapacity.setText(truckCapacity);
    }
}
