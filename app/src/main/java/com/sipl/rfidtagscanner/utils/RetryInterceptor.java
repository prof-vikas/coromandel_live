package com.sipl.rfidtagscanner.utils;

import android.util.Log;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class RetryInterceptor implements Interceptor {

    private static final String TAG = "RetryInterceptorError";

    private final int maxRetries;
    private final int retryDelayMillis;

    public RetryInterceptor(int maxRetries, int retryDelayMillis) {
        this.maxRetries = maxRetries;
        this.retryDelayMillis = retryDelayMillis;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response response;
        IOException ioException = null;

        // retry the request for a maximum of maxRetries times
        for (int retryCount = 0; retryCount < maxRetries; retryCount++) {
            Log.i(TAG, "intercept: " + retryCount);
            try {
                response = chain.proceed(request);
                if (response.isSuccessful()) {
                    return response;
                }
            } catch (IOException e) {
                ioException = e;
            }

            // delay before retrying the request
            try {
                Thread.sleep(retryDelayMillis);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        // if all retries fail, throw the last IOException
        if (ioException != null) {
            throw ioException;
        } else {
            throw new IOException("Unknown error occurred");
        }
    }
}


