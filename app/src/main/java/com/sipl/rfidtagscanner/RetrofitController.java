package com.sipl.rfidtagscanner;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

import com.sipl.rfidtagscanner.api.LoadingAdviseApi;
import com.sipl.rfidtagscanner.fragments.LoadingAdviseFragment;
import com.sipl.rfidtagscanner.utils.ApiConstants;
import com.sipl.rfidtagscanner.utils.RetryInterceptor;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitController {
    private static RetrofitController instance = null;
    private LoadingAdviseApi loadingAdviseApi;

    OkHttpClient client = new OkHttpClient.Builder()
            .addInterceptor(new RetryInterceptor(3, 1000))
            .build();


    private RetrofitController() {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiConstants.BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        loadingAdviseApi = retrofit.create(LoadingAdviseApi.class);
    }

    public static synchronized RetrofitController getInstance() {
        if (instance == null) {
            instance = new RetrofitController();
        }
        return instance;
    }



    public LoadingAdviseApi getLoadingAdviseApi() {
        return loadingAdviseApi;
    }
}
