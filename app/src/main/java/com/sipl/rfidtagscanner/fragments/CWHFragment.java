package com.sipl.rfidtagscanner.fragments;

import static android.content.Context.MODE_PRIVATE;
import static com.sipl.rfidtagscanner.utils.ToastConstants.EMPTY_REMARKS;
import static com.sipl.rfidtagscanner.utils.ToastConstants.EMPTY_RMG_NUMBER;
import static com.sipl.rfidtagscanner.utils.ToastConstants.FAILED_CONNECTION;
import static com.sipl.rfidtagscanner.utils.ToastConstants.RESPONSE_NOT_200;
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
import com.sipl.rfidtagscanner.R;
import com.sipl.rfidtagscanner.RetrofitController;
import com.sipl.rfidtagscanner.adapter.RmgDataAdapter;
import com.sipl.rfidtagscanner.adapter.TripsDataAdapter;
import com.sipl.rfidtagscanner.dto.dtos.RemarksDto;
import com.sipl.rfidtagscanner.dto.dtos.RfidLepIssueDto;
import com.sipl.rfidtagscanner.dto.dtos.StorageLocationDto;
import com.sipl.rfidtagscanner.dto.dtos.TransactionsDto;
import com.sipl.rfidtagscanner.dto.request.UpdateRmgRequestDto;
import com.sipl.rfidtagscanner.dto.response.RemarkApiResponse;
import com.sipl.rfidtagscanner.dto.response.RmgNumberApiResponse;
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


public class CWHFragment extends Fragment {

    private final String TAG = "BothraAdvicePage";
    private final Integer FLAG = 4;
    String[] cities = {"Lep0001", "Lep0002", "Lep0003"};
    ArrayAdapter<String> updateRmgNoAdapter;
    ArrayAdapter<String> arrayAdapterForLepNumber;
    ArrayAdapter<String> remarksAdapter;
    EditText edtDriverName, edtTruckNumber, edtCommodity, edtGrossWeight, edtPreviousRmgNo;
    TextView tvLepNumber;

    private int updateDataFailCounters = 0;
    private int updateDataFailCounter = 3;
    private int getAllLepNumberCounterFail = 6;
    private int rmgNumberCounterFail = 6;
    private int getAllRemarksCounterFail = 6;

    private CustomToast customToast = new CustomToast();
    private Helper helper = new Helper();
    private ProgressBar progressBar;
    private LinearLayout mainRecyclerViewLayout;
    private RecyclerView recyclerViewRmgNo, recyclerViewTrip;
    private Spinner spinnerUpdateRmgNo, spinnerRemark;
    private AutoCompleteTextView autoCompleteLepNo;
    private TextClock tvClock;
    private Button btnSubmit, btnReset;
    private String selectedRemarks;
    private Integer selectedRemarksId;

    private String selectedRmgNo;
    private Integer selectedRmgNoId;

    private String selectedLepNumber;
    private Integer selectedLepNumberId;

    private String previousRmgNoId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_c_w_h, container, false);
        spinnerRemark = view.findViewById(R.id.cwh_spinner_remark);
        spinnerUpdateRmgNo = view.findViewById(R.id.cwh_spinner_update_rmg_no);
        autoCompleteLepNo = view.findViewById(R.id.cwh_auto_complete_lep_number);
        edtDriverName = view.findViewById(R.id.cwh_edt_driver_name);
        edtTruckNumber = view.findViewById(R.id.cwh_edt_truck_no);
        edtCommodity = view.findViewById(R.id.cwh_edt_commodity);
        edtGrossWeight = view.findViewById(R.id.cwh_edt_gross_weight);
        edtPreviousRmgNo = view.findViewById(R.id.cwh_edt_previous_rmg_no);
        tvLepNumber = view.findViewById(R.id.cwh_lep_number_tv);
        btnReset = view.findViewById(R.id.cwh_btn_reset);
        btnSubmit = view.findViewById(R.id.cwh_btn_submit);
        progressBar = view.findViewById(R.id.cwh_progressBar);
        mainRecyclerViewLayout = view.findViewById(R.id.main_recycler_view_layoutout);
        tvClock = view.findViewById(R.id.cwh_tv_clock);

        helper.multiColorStringForTv(tvLepNumber, "LEP Number", " *");

        setTvClock();
        callOnCreateApi();

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateLoadingAdviseForm()) {
                    Log.i(TAG, "onClick: in onclick endpoint");
                    updateRmgNo(setData());
//                    callOnCreateApi();
                    return;
                }

            }
        });
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetFields();
            }
        });

        return view;
    }

    private boolean validateLoadingAdviseForm() {
        if (autoCompleteLepNo.length() == 0) {
            autoCompleteLepNo.setError("This field is required");
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
        if (edtCommodity.length() == 0) {
            edtCommodity.setError("This field is required");
            edtCommodity.setHint("This field is required");
            return false;
        }
        if (edtGrossWeight.length() == 0) {
            edtGrossWeight.setError("This field is required");
            edtGrossWeight.setHint("This field is required");
            return false;
        }
        if (edtPreviousRmgNo.length() == 0) {
            edtPreviousRmgNo.setError("This field is required");
            edtPreviousRmgNo.setHint("This field is required");
            return false;
        }
        if (!spinnerUpdateRmgNo.getSelectedItem().toString().equals("Update RMG No") && spinnerRemark.getSelectedItem().toString().equals("Select Remarks")) {
            btnSubmit.setEnabled(false);
            Toast.makeText(getActivity(), "Select remarks", Toast.LENGTH_SHORT).show();
            return false;
        }
        removeErrorMessage();
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

    private void resetFields() {
        autoCompleteLepNo.setText(null);
        edtDriverName.setText(null);
        edtTruckNumber.setText(null);
        edtCommodity.setText(null);
        edtGrossWeight.setText(null);
        edtPreviousRmgNo.setText(null);

        if (updateRmgNoAdapter == null || remarksAdapter == null) {
            callOnCreateApi();
        } else {
            spinnerUpdateRmgNo.setSelection(updateRmgNoAdapter.getCount());
            spinnerRemark.setSelection(remarksAdapter.getCount());
        }

        if (arrayAdapterForLepNumber != null) {
            arrayAdapterForLepNumber.clear();
        }

        if (!getALlLepNumberWithFlag()) {
            getALlLepNumberWithFlag();
        }

        removeErrorMessage();
    }


    private void setRecyclerView() {
        recyclerViewRmgNo.setHasFixedSize(false);
        recyclerViewTrip.setHasFixedSize(false);
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

    private String getToken() {
        SharedPreferences sp = getActivity().getSharedPreferences("loginCredentials", MODE_PRIVATE);
        String userToken = sp.getString("tokenSPK", null);
        return userToken;
    }

    private boolean getALlLepNumberWithFlag() {
        try {
            Call<TransactionsApiResponse> call = RetrofitController.getInstance().getLoadingAdviseApi().getALlLepNumberWithFlag("Bearer " + getToken());
            call.enqueue(new Callback<TransactionsApiResponse>() {
                @Override
                public void onResponse(Call<TransactionsApiResponse> call, Response<TransactionsApiResponse> response) {
                    if (!response.isSuccessful()) {
                        String responseCode = String.valueOf(response.code());
                        Log.i(TAG, "getAllLepNumber: responseCode : " + responseCode);
                        return;
                    }
                    Log.i(TAG, "onResponse: getAllLepNumber : responseCode : " + response.code());
                    if (response.code() != 200) {
                        getALlLepNumberWithFlag();
                        Log.i(TAG, "onResponse: code " + response.code() + " " + response.raw());
                        return;
                    }

                    if (response.code() == 200) {
                        Log.i(TAG, "getALlLepNumberWithFlag: raw : " + response.raw());
                    }

                    List<TransactionsDto> transactionsDtoList = response.body().getTransactionsDtos();
                    HashMap<String, Integer> hashMapLepNumber = new HashMap<>();
                    HashMap<String, Integer> hashMapRmgNo = new HashMap<>();
                    ArrayList<String> arrAutoCompleteLepNo = new ArrayList<>();

                    try {
                        if (transactionsDtoList == null || transactionsDtoList.isEmpty()) {
                            autoCompleteLepNo.setHint("No Lep number available");
//                            customToast.toastMessage(getActivity(), EMPTY_LEP_NUMBER_LIST, 0);
                            return;
                        }
                        String strLepNumber = null, strTruckNo = null, strDriverName = null, grossWeight = null, strCommodity = null;
                        String strPreviousRmgNo = null;
                        for (int i = 0; i < transactionsDtoList.size(); i++) {
                            int transId = transactionsDtoList.get(i).getId();
                            strLepNumber = transactionsDtoList.get(i).getRfidLepIssueModel().getLepNumber();
                            int id = transactionsDtoList.get(i).getRfidLepIssueModel().getId();
                            strDriverName = transactionsDtoList.get(i).getRfidLepIssueModel().getDriverMaster().getDriverName();
                            strTruckNo = transactionsDtoList.get(i).getRfidLepIssueModel().getDailyTransportReportModule().getTruckNumber();
                            strCommodity = transactionsDtoList.get(i).getRfidLepIssueModel().getDailyTransportReportModule().getCommodity();
                            grossWeight = String.valueOf(transactionsDtoList.get(i).getGrossWeight());
                            strPreviousRmgNo = transactionsDtoList.get(i).getFunctionalLocationDestinationMaster().getStrLocationCode();
                            arrAutoCompleteLepNo.add(strLepNumber);
                            hashMapLepNumber.put(strLepNumber, id);
                        }

                        arrayAdapterForLepNumber = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, arrAutoCompleteLepNo);
                        autoCompleteLepNo.setAdapter(arrayAdapterForLepNumber);
                        String finalStrTruckNo = strTruckNo;
                        String finalStrDriverName = strDriverName;
                        String finalStrCommodity = strCommodity;
                        String finalGrossWeight = grossWeight;
                        String finalStrPreviousRmgNo = strPreviousRmgNo;
                        autoCompleteLepNo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                selectedLepNumber = arrayAdapterForLepNumber.getItem(i);
                                previousRmgNoId = finalStrPreviousRmgNo;
                                if (hashMapLepNumber.containsKey(selectedLepNumber)) {
                                    selectedLepNumberId = hashMapLepNumber.get(selectedLepNumber);
                                }

                               /* if (hashMapRmgNo.containsKey(finalStrPreviousRmgNo)) {
                                    Log.i(TAG, "onItemClick: finalStrPreviousRmgNo1 : " + finalStrPreviousRmgNo);
                                    previousRmgNoId = String.valueOf(hashMapRmgNo.get(finalStrPreviousRmgNo));

                                }*/

                                if (arrAutoCompleteLepNo.contains(selectedLepNumber)) {
                                    edtTruckNumber.setText(finalStrTruckNo);
                                    edtDriverName.setText(finalStrDriverName);
                                    edtCommodity.setText(finalStrCommodity);
                                    edtGrossWeight.setText(finalGrossWeight);
                                    edtPreviousRmgNo.setText(finalStrPreviousRmgNo);
                                }
                            }
                        });
                    } catch (Exception e) {
                        e.getMessage();
                        return;
                    }
                }

                @Override
                public void onFailure(Call<TransactionsApiResponse> call, Throwable t) {
                    getAllLepNumberCounterFail--;
                    if (getAllLepNumberCounterFail == 0) {
                        t.getMessage();
                        return;
                    }
                    if (getAllLepNumberCounterFail != 0) {
                        Log.i(TAG, "onFailure: " + t.getMessage());
//                        customToast.toastMessage(getActivity(), FAILED_CONNECTION + t.getMessage(), 0);
                        getALlLepNumberWithFlag();
                    }
                }
            });
        } catch (Exception e) {
            getALlLepNumberWithFlag();
            Log.i(TAG, "getALlLepNumberWithFlag: " + e.getMessage());

            e.printStackTrace();
        }
        return true;
    }



/*    private boolean getALlLepNumberWithFlag() {
        Log.i(TAG, "getAllLepNumber: ");
        try {
            Call<TransactionsApiResponse> call = RetrofitController.getInstance().getLoadingAdviseApi().getALlLepNumberWithFlag("Bearer " + getToken());
            call.enqueue(new Callback<TransactionsApiResponse>() {
                @Override
                public void onResponse(Call<TransactionsApiResponse> call, Response<TransactionsApiResponse> response) {

                    if (!response.isSuccessful()) {
                        String responseCode = String.valueOf(response.code());
                        Log.i(TAG, "getAllLepNumber: responseCode : " + responseCode);
                        return;
                    }
                    Log.i(TAG, "onResponse: _______________________________________________");
                    Log.i(TAG, "onResponse: getAllLepNumber : responseCode : " + response.code());
                    if (response.code() != 200) {
                        getALlLepNumberWithFlag();
                        return;
                    }

                    if (response.code() == 200) {
                        Log.i(TAG, "getALlLepNumberWithFlag: raw : " + response.raw());
                    }

                    List<TransactionsDto> transactionsDtoList = response.body().getTransactionsDtos();
                    HashMap<String, Integer> hashMapLepNumber = new HashMap<>();
                    ArrayList<String> arrAutoCompleteLepNo = new ArrayList<>();

                    try {
                        if (transactionsDtoList == null || transactionsDtoList.isEmpty()) {
                            autoCompleteLepNo.setHint("No Lep number available");
//                            customToast.toastMessage(getActivity(), EMPTY_LEP_NUMBER_LIST, 0);
                            return;
                        }
                        String strLepNumber = null, strTruckNo = null, strDriverName = null, grossWeight = null, strCommodity = null;
                        String strPreviousRmgNo = null;
                        Log.i(TAG, "onResponse: before for loop --------------");
                        int counter = 1;
                        for (int i = 0; i < transactionsDtoList.size(); i++) {
                            Log.i(TAG, "onResponse: in for loop : " + counter ++);
                            strLepNumber = transactionsDtoList.get(i).getRfidLepIssueModel().getLepNumber();
                            int id = transactionsDtoList.get(i).getRfidLepIssueModel().getId();
                            hashMapLepNumber.put(strLepNumber, id);
                            arrAutoCompleteLepNo.add(strLepNumber);
                            Log.i(TAG, "onResponse: arrAutocomplete size in loop : " + arrAutoCompleteLepNo.size());

                        }
                        Log.i(TAG, "onResponse: arrAutocomplete size : " + arrAutoCompleteLepNo.size());

                        arrayAdapterForLepNumber = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, arrAutoCompleteLepNo);
                        autoCompleteLepNo.setAdapter(arrayAdapterForLepNumber);
                        autoCompleteLepNo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                selectedLepNumber = arrayAdapterForLepNumber.getItem(i);
//                                previousRmgNoId = finalStrPreviousRmgNo;
                              *//*  if (hashMapLepNumber.containsKey(selectedLepNumber)) {
                                    selectedLepNumberId = hashMapLepNumber.get(selectedLepNumber);
                                }*//*

     *//* if (hashMapRmgNo.containsKey(finalStrPreviousRmgNo)) {
                                    Log.i(TAG, "onItemClick: finalStrPreviousRmgNo1 : " + finalStrPreviousRmgNo);
                                    previousRmgNoId = String.valueOf(hashMapRmgNo.get(finalStrPreviousRmgNo));

                                }*//*

     *//* if (arrAutoCompleteLepNo.contains(selectedLepNumber)) {
                                    edtTruckNumber.setText(finalStrTruckNo);
                                    Log.i(TAG, "onItemClick: " + finalStrDriverName);
                                    edtDriverName.setText(finalStrDriverName);
                                    edtCommodity.setText(finalStrCommodity);
                                    edtGrossWeight.setText(finalGrossWeight);
                                    edtPreviousRmgNo.setText(finalStrPreviousRmgNo);
                                    Log.i(TAG, "onItemClick: " + finalStrPreviousRmgNo);
                                }*//*
                            }
                        });
                    } catch (Exception e) {
                        e.getMessage();
                        return;
                    }
                }

                @Override
                public void onFailure(Call<TransactionsApiResponse> call, Throwable t) {
                    getAllLepNumberCounterFail--;
                    if (getAllLepNumberCounterFail == 0) {
                        t.getMessage();
                        return;
                    }
                    if (getAllLepNumberCounterFail != 0) {
//                        customToast.toastMessage(getActivity(), FAILED_CONNECTION + t.getMessage(), 0);
                        getALlLepNumberWithFlag();
                    }
                }
            });
        } catch (Exception e) {
            getALlLepNumberWithFlag();
            Log.i(TAG, "getALlLepNumberWithFlag: " + e.getMessage());

            e.printStackTrace();
        }
        return true;
    }*/

    private boolean getAllUpdateRmgNo() {
        Call<RmgNumberApiResponse> call = RetrofitController.getInstance().getLoadingAdviseApi().
                getAllCoromandelRmgNo("Bearer " + getToken(), getUserPlantLocation());

        call.enqueue(new Callback<RmgNumberApiResponse>() {
            @Override
            public void onResponse(Call<RmgNumberApiResponse> call, Response<RmgNumberApiResponse> response) {
                if (!response.isSuccessful()) {
                    String responseCode = String.valueOf(response.code());
                    Log.i(TAG, "getAllUpdateRmgNo <<>><<>>: responseCode : " + responseCode + response.raw());
                    return;
                }
                Log.i(TAG, "onResponse: getAllUpdateRmgNo : responseCode : " + response.code() + " " + response.raw());
                if (response.code() != 200) {
                    getAllUpdateRmgNo();
                    return;
                }

                if (response.code() == 200) {
                    Log.i(TAG, "onResponse:Rmg Number found successfully");
                }

                List<StorageLocationDto> functionalLocationMasterDtoList = response.body().getStorageLocationDtos();
                if (functionalLocationMasterDtoList == null || functionalLocationMasterDtoList.isEmpty()) {
                    Log.i(TAG, "onResponse: selected area is not having api ");
                    return;
                }
                Log.i(TAG, "onResponse: functionalLocationMasterDtoList" + functionalLocationMasterDtoList.size());
                HashMap<String, Integer> hashMapUpdateRmgNo = new HashMap<>();
                ArrayList<String> arrDestinationLocation = new ArrayList<>();

                try {
                    if (functionalLocationMasterDtoList == null || functionalLocationMasterDtoList.isEmpty()) {
                        customToast.toastMessage(getActivity(), EMPTY_RMG_NUMBER, 0);
                        return;
                    }
                    for (int i = 0; i < functionalLocationMasterDtoList.size(); i++) {
                        String s = functionalLocationMasterDtoList.get(i).getStrLocationCode();
//                        int id = functionalLocationMasterDtoList.get(i).getId();
//                        hashMapUpdateRmgNo.put(s, id);

                        arrDestinationLocation.add(s);
                    }
                    arrDestinationLocation.add("Update RMG No");


                    updateRmgNoAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, arrDestinationLocation) {
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
                    spinnerUpdateRmgNo.setAdapter(updateRmgNoAdapter);
                    spinnerUpdateRmgNo.setSelection(updateRmgNoAdapter.getCount());

                    spinnerUpdateRmgNo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            selectedRmgNo = adapterView.getSelectedItem().toString();
                            Log.i(TAG, "onItemSelected: " + selectedRmgNo);
                            if (hashMapUpdateRmgNo.containsKey(selectedRmgNo)) {
                                selectedRmgNoId = hashMapUpdateRmgNo.get(selectedRmgNo);


                                Log.i(TAG, "onItemSelected:  ----------- selectedRmgNoId ---------" + selectedRmgNoId);
                                Log.i(TAG, "onItemSelected:  ----------- selectedRmgNo -----------" + selectedRmgNo);

                            }
                            if (!selectedRmgNo.equalsIgnoreCase("Update RMG No")) {
                                spinnerRemark.setEnabled(true);
                                spinnerRemark.setClickable(true);
                                spinnerRemark.setFocusable(true);
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {
                            String s = adapterView.getSelectedItem().toString();
                            Log.i(TAG, "onNothingSelected: s : " + s);
                        }
                    });
                } catch (Exception e) {
                    e.getMessage();
                }
            }

            @Override
            public void onFailure(Call<RmgNumberApiResponse> call, Throwable t) {
                rmgNumberCounterFail--;
                if (rmgNumberCounterFail == 0) {
                    t.getMessage();
                    return;
                }
                if (rmgNumberCounterFail != 0) {
//                    customToast.toastMessage(getActivity(), FAILED_CONNECTION + t.getMessage(), 0);
                    getAllUpdateRmgNo();
                }
            }
        });

        return true;
    }

    private boolean getAllRemark() {
        Call<RemarkApiResponse> call = RetrofitController.getInstance().getLoadingAdviseApi().
                getAllCoromandelRemark("Bearer " + getToken());

        call.enqueue(new Callback<RemarkApiResponse>() {
            @Override
            public void onResponse(Call<RemarkApiResponse> call, Response<RemarkApiResponse> response) {
                if (!response.isSuccessful()) {
                    String responseCode = String.valueOf(response.code());
                    Log.i(TAG, "getAllRemark: responseCode : " + responseCode);
                    return;
                }
                Log.i(TAG, "onResponse: getAllRemark : responseCode : " + response.code());
                if (response.code() != 200) {
                    getAllRemark();
                    return;
                }

                if (response.code() == 200) {
                    Log.i(TAG, "onResponse: Remarks found successfully");
                }
                Log.i(TAG, "onResponse: getAllRemark " + response.code());
                List<RemarksDto> remarksDtoList = response.body().getRemarksDtos();
                HashMap<String, Integer> hashMapRemarks = new HashMap<>();
                ArrayList<String> arrRemarks = new ArrayList<>();
                try {
                    if (remarksDtoList == null || remarksDtoList.isEmpty()) {
                        customToast.toastMessage(getActivity(), EMPTY_REMARKS, 0);
                        return;
                    }
                    for (int i = 0; i < remarksDtoList.size(); i++) {
                        String s = remarksDtoList.get(i).getRemarks();
                        int id = remarksDtoList.get(i).getId();
                        hashMapRemarks.put(s, id);

                        arrRemarks.add(s);
                    }
                    arrRemarks.add("Select Remarks");

                    remarksAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, arrRemarks) {
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
                    spinnerRemark.setEnabled(false);
                    spinnerRemark.setClickable(false);
                    spinnerRemark.setAdapter(remarksAdapter);
                    spinnerRemark.setSelection(remarksAdapter.getCount());

                    spinnerRemark.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            selectedRemarks = adapterView.getSelectedItem().toString();
                            if (hashMapRemarks.containsKey(selectedRemarks)) {
                                selectedRemarksId = hashMapRemarks.get(selectedRemarks);
                                Log.i(TAG, "onItemSelected: Selected Remarks Id " + selectedRemarksId);
                            }
                            if (selectedRmgNo != null) {
                                if (selectedRmgNo.equalsIgnoreCase("Update RMG No")) {
                                    spinnerRemark.setEnabled(false);
                                    spinnerRemark.setClickable(false);
                                    spinnerRemark.setFocusable(false);
                                }
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

            @Override
            public void onFailure(Call<RemarkApiResponse> call, Throwable t) {
                getAllRemarksCounterFail--;
                if (getAllRemarksCounterFail == 0) {
                    t.getMessage();
                    return;
                }
                if (getAllRemarksCounterFail != 0) {
//                    customToast.toastMessage(getActivity(), FAILED_CONNECTION + t.getMessage(), 0);
                    getAllRemark();
                }
            }
        });
        return true;
    }

    private void updateRmgNo(UpdateRmgRequestDto updateRmgRequestDto) {
        Log.i(TAG, "updateRmgNo: in update endpoint");
        Log.i(TAG, new Gson().toJson(updateRmgRequestDto).toString());
        Call<TransactionsApiResponse> call = RetrofitController.getInstance().getLoadingAdviseApi().updateRmgNo("Bearer " + getToken(), updateRmgRequestDto);

        call.enqueue(new Callback<TransactionsApiResponse>() {
            @Override
            public void onResponse(Call<TransactionsApiResponse> call, Response<TransactionsApiResponse> response) {
                progressBar.setVisibility(View.VISIBLE);
//                String message = response.body().getMessage();
                if (!response.isSuccessful()) {
                    Log.i(TAG, "onResponse: " + response.code());
                    customToast.toastMessage(getActivity(), RESPONSE_NOT_200 + response.code(), 0);
                }

                Log.i(TAG, "onResponse: code" + response.code());
                if (response.code() == 200) {
                    alertBuilder(response.body().getMessage());
//                    customToast.toastMessage(getActivity(), RESPONSE_200, 0);
//                    arrayAdapterForLepNumber.remove(selectedLepNumber);
                    Log.i(TAG, "onResponse: message : " + response.body().getMessage());

                    progressBar.setVisibility(View.GONE);
                    resetFields();
                }

                if (response.code() != 200) {
                    if (updateDataFailCounter != 0) {
                        updateDataFailCounter--;
                        updateRmgNo(setData());
                        return;
                    }
                    customToast.errorToastMessage(getActivity(), RESPONSE_NOT_200, 0);
                    progressBar.setVisibility(View.GONE);
                }
            }


            @Override
            public void onFailure(Call<TransactionsApiResponse> call, Throwable t) {
                    alertBuilder(t.getMessage());
            }
        });
    }


    private UpdateRmgRequestDto setData() {
        StorageLocationDto selectedWareHouseNo = null;
        RemarksDto remarksDto = null;
        AuditEntity auditEntity = new AuditEntity(null, null, loginUsername(), LocalDateTime.now().toString());
        StorageLocationDto previousWareHouseNo = new StorageLocationDto(previousRmgNoId);
        if (!selectedRmgNo.equalsIgnoreCase("Update RMG No")) {
            selectedWareHouseNo = new StorageLocationDto(selectedRmgNo);
        }

        if (!selectedRemarks.equalsIgnoreCase("Select Remarks")) {
            remarksDto = new RemarksDto(selectedRemarksId);
        }
        RfidLepIssueDto rfidLepIssueDto = new RfidLepIssueDto(selectedLepNumberId);

        UpdateRmgRequestDto updateRmgRequestDto = new UpdateRmgRequestDto(auditEntity, previousWareHouseNo, selectedWareHouseNo, rfidLepIssueDto, remarksDto, FLAG);

        Log.i(TAG, "setData: in end line");


        return updateRmgRequestDto;
    }

    private void setTvClock() {
        try {
            tvClock.setFormat24Hour("dd-MM-yy hh:mm a");
            Log.i("login fragment ", "setting time done");
        } catch (Exception e) {
            Log.e(TAG, "onCreateView: date format error", e);
            e.printStackTrace();
        }
    }

    private boolean callOnCreateApi() {
        if (!getALlLepNumberWithFlag()) {
            getALlLepNumberWithFlag();
            return false;
        }

        if (!getAllUpdateRmgNo()) {
            getAllUpdateRmgNo();
            return false;
        }

        if (!getAllRemark()) {
            getAllRemark();
            return false;
        }
        return true;
    }

    private void removeErrorMessage() {
        autoCompleteLepNo.setError(null);

        edtTruckNumber.setError(null);
        edtTruckNumber.setHint(null);

        edtDriverName.setError(null);
        edtDriverName.setHint(null);

        edtCommodity.setError(null);
        edtCommodity.setHint(null);

        edtGrossWeight.setError(null);
        edtGrossWeight.setHint(null);

        edtPreviousRmgNo.setError(null);
        edtPreviousRmgNo.setHint(null);
    }

    private String getUserSourceLocation() {
        SharedPreferences sp = getActivity().getSharedPreferences("loginCredentials", MODE_PRIVATE);
        String userSourceLocation = sp.getString("UserSourceLocationSPK", null);
        return userSourceLocation;
    }

    private String getUserPlantLocation() {
        SharedPreferences sp = getActivity().getSharedPreferences("loginCredentials", MODE_PRIVATE);
        String userPlantLocation = sp.getString("userPlantLocationSPK", null);
        return userPlantLocation;
    }

    private String loginUsername() {
        SharedPreferences sp = getActivity().getSharedPreferences("loginCredentials", MODE_PRIVATE);
        String username = sp.getString("usernameSPK", null);
        return username;
    }

    private void alertBuilder(String alertMessage) {
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
}