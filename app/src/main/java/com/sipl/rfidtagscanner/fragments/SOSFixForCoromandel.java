package com.sipl.rfidtagscanner.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.sipl.rfidtagscanner.MainActivity;
import com.sipl.rfidtagscanner.R;
import com.sipl.rfidtagscanner.RetrofitController;
import com.sipl.rfidtagscanner.dto.response.DailyTransportReportModuleApiResponse;
import com.sipl.rfidtagscanner.dto.response.TransactionsApiResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SOSFixForCoromandel extends Fragment {

    private EditText edtInput;
    private Button btnSubmit;
    private String token;
    private ProgressBar progressBar;

    public SOSFixForCoromandel() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_sos_fix_for_coromandel, container, false);
        edtInput = view.findViewById(R.id.edt_sos_input);
        btnSubmit = view.findViewById(R.id.btn_sos_submit);
        progressBar = view.findViewById(R.id.sos_progressBar);
        this.token = ((MainActivity) requireActivity()).getLoginToken();

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateSOS();
            }
        });

        return view;
    }

    private void updateSOS() {
        showProgress();
        String lepNO = edtInput.getText().toString().trim();
        Log.i("TAG", "updateSOS: lepNO : " + lepNO);
        Call<DailyTransportReportModuleApiResponse> call = RetrofitController.getInstances(requireActivity()).getLoadingAdviseApi().updateSOS("Bearer " + token, lepNO);
        call.enqueue(new Callback<DailyTransportReportModuleApiResponse>() {
            @Override
            public void onResponse(Call<DailyTransportReportModuleApiResponse> call, Response<DailyTransportReportModuleApiResponse> response) {
                Log.i("TAG", "onResponse: response.raw () : " + response.raw());
                if (!response.isSuccessful()){
                    hideProgress();
                    Log.e("TAG", "onResponse: " + response.errorBody().toString() );
                    ((MainActivity)requireActivity()).alert(requireActivity(),"ERROR",response.errorBody().toString(),null,"OK",false);
                    return;
                }

                if (response.isSuccessful()){
                    hideProgress();
                    if (response.body() != null && response.body().getStatus() != null && response.body().getStatus().equalsIgnoreCase("OK")){
                        if (response.body().getMessage() != null){
                            ((MainActivity)requireActivity()).alert(requireContext(),"SUCCESS",response.body().getMessage(),null,"OK",false);
                        }
                    }else {
                        ((MainActivity)requireActivity()).alert(requireContext(),"ERROR",response.body().getMessage(),null,"OK",false);
                    }
                }
            }

            @Override
            public void onFailure(Call<DailyTransportReportModuleApiResponse> call, Throwable t) {
                t.getMessage();
                hideProgress();
                Log.e("TAG", "onFailure: Failure in updateSOS" +  t.getMessage().toString());
                ((MainActivity)requireActivity()).alert(requireContext(),"ERROR",t.getMessage(),null,"OK",false);

            }
        });

    }

    private void showProgress(){
        progressBar.setVisibility(View.VISIBLE);
        btnSubmit.setEnabled(false);
    }

    private void hideProgress(){
        progressBar.setVisibility(View.GONE);
        btnSubmit.setEnabled(true);
    }
}