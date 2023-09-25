package com.sipl.rfidtagscanner;

import android.content.Context;
import android.util.Log;

import com.sipl.rfidtagscanner.api.ApiController;
import com.sipl.rfidtagscanner.utils.ApiConstants;
import com.sipl.rfidtagscanner.utils.RetryInterceptor;

import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Collections;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitController {
    private static final String TAG = "RetrofitController";
    private static RetrofitController instance = null;
    private ApiController apiController;

    public RetrofitController(Context context) {
        try {
            // Create a TrustManager that accepts all certificates
            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(X509Certificate[] chain, String authType) {
                        }

                        @Override
                        public void checkServerTrusted(X509Certificate[] chain, String authType) {
                        }

                        @Override
                        public X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[0];
                        }
                    }
            };

            // Create an SSLContext with the TrustManager
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts, new SecureRandom());

            // Create a customized OkHttpClient
            OkHttpClient.Builder builder = new OkHttpClient.Builder()
                    .sslSocketFactory(sslContext.getSocketFactory(), (X509TrustManager) trustAllCerts[0])
                    .protocols(Collections.singletonList(Protocol.HTTP_1_1))
                    .addInterceptor(new RetryInterceptor(1, 1000))
                    .hostnameVerifier((hostname, session) -> true);

            // Create a Retrofit instance with the customized OkHttpClient
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(ApiConstants.BASE_URL)
                    .client(builder.build())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            this.apiController = retrofit.create(ApiController.class);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "RetrofitController: in exception" + e.getMessage());
        }

    }

    public static synchronized RetrofitController getInstances(Context context) {
        if (instance == null) {
            instance = new RetrofitController(context);
        }
        return instance;
    }

    public ApiController getLoadingAdviseApi() {
        return apiController;
    }
}
