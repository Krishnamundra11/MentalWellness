package com.krishna.navbar.utils;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static final String BASE_URL = "https://mentalwellness-backend-m8dk.onrender.com/";
    private static Retrofit retrofit = null;
    
    // Default timeouts
    private static final int CONNECT_TIMEOUT = 15;
    private static final int READ_TIMEOUT = 30;
    private static final int WRITE_TIMEOUT = 15;
    private static final int MAX_RETRIES = 3;

    public static Retrofit getClient() {
        if (retrofit == null) {
            // Create OkHttpClient with custom configurations
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                    .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
                    .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
                    .addInterceptor(new RetryInterceptor(MAX_RETRIES))
                    .build();
            
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static ApiService getApiService() {
        return getClient().create(ApiService.class);
    }
    
    /**
     * Custom interceptor to handle retries for failed API calls
     */
    private static class RetryInterceptor implements Interceptor {
        private int maxRetries;
        private int retryCount = 0;

        public RetryInterceptor(int maxRetries) {
            this.maxRetries = maxRetries;
        }

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            Response response = null;
            IOException exception = null;
            
            while (retryCount < maxRetries) {
                try {
                    response = chain.proceed(request);
                    if (response.isSuccessful()) {
                        return response;
                    }
                    
                    // If response is not successful, close it and retry
                    if (response != null) {
                        response.close();
                    }
                } catch (IOException e) {
                    exception = e;
                    android.util.Log.e("RetrofitClient", "Request failed: " + e.getMessage());
                }
                
                retryCount++;
                android.util.Log.w("RetrofitClient", "Retrying request (" + retryCount + "/" + maxRetries + ")");
                
                try {
                    // Exponential backoff delay before retry
                    Thread.sleep(1000 * retryCount);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new IOException("Retry interrupted", e);
                }
            }
            
            // If we reach here, all retries have failed
            if (response != null) {
                return response;
            } else if (exception != null) {
                throw exception;
            } else {
                throw new IOException("Request failed after " + maxRetries + " retries");
            }
        }
    }
} 