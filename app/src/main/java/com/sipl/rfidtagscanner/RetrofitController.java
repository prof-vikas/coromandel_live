package com.sipl.rfidtagscanner;

import com.sipl.rfidtagscanner.api.LoadingAdviseApi;
import com.sipl.rfidtagscanner.utils.ApiConstants;
import com.sipl.rfidtagscanner.utils.RetryInterceptor;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitController {
    private static RetrofitController instance = null;
    private final LoadingAdviseApi loadingAdviseApi;
    OkHttpClient client = new OkHttpClient.Builder()
            .addInterceptor(new RetryInterceptor(1, 1000))
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
