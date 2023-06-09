package com.sipl.rfidtagscanner;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import com.sipl.rfidtagscanner.api.LoadingAdviseApi;
import com.sipl.rfidtagscanner.utils.ApiConstants;
import com.sipl.rfidtagscanner.utils.RetryInterceptor;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Collections;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitController {
    private static RetrofitController instance = null;
    private final LoadingAdviseApi loadingAdviseApi;
    private static final String TAG = "RetrofitController";

    private OkHttpClient getOkHttpClient(Context context) {
        try {
            Log.i(TAG, "getOkHttpClient: try");
              InputStream inputStream = context.getResources().openRawResource(R.raw.coromandelbiznew_crt);

            // Create a Certificate object from the input stream
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            Certificate certificate = certificateFactory.generateCertificate(inputStream);

            // Create a KeyStore and add the certificate
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null, null);
            keyStore.setCertificateEntry("alias", certificate);

            // Create a TrustManagerFactory and initialize it with the KeyStore
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);

            // Create an SSLContext and configure it with the TrustManager
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustManagerFactory.getTrustManagers(), null);

            Log.i(TAG, "getOkHttpClient: before building client");
            return new OkHttpClient.Builder()
                    .sslSocketFactory(sslContext.getSocketFactory(), (X509TrustManager) trustManagerFactory.getTrustManagers()[0])
                    .protocols(Collections.singletonList(Protocol.HTTP_1_1))
                    .addInterceptor(new RetryInterceptor(1, 1000))
                    .build();

          
        } catch (Exception e) {
            Log.i(TAG, "getOkHttpClient: " + e);
            e.printStackTrace();
            return new OkHttpClient();
        }
    }

    private RetrofitController(Context context) {
        OkHttpClient client = getOkHttpClient(context);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiConstants.BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        loadingAdviseApi = retrofit.create(LoadingAdviseApi.class);
    }

    public static synchronized RetrofitController getInstances(Context context) {
        if (instance == null) {
            instance = new RetrofitController(context);
        }
        return instance;
    }


    public LoadingAdviseApi getLoadingAdviseApi() {
        return loadingAdviseApi;
    }
}
