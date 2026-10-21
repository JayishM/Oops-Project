package com.nightmarket.app.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nightmarket.app.BuildConfig;

import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Centralized API client configuring Retrofit and network timeouts.
 * Allows switching between local emulator (10.0.2.2:8080) and Render HTTPS URL.
 */
public class ApiClient {

    private static final String DEFAULT_BASE_URL;
    private static String currentBaseUrl;
    private static ApiService apiService;
    private static Retrofit retrofit;

    static {
        String configuredUrl = BuildConfig.API_BASE_URL;
        if (configuredUrl == null || configuredUrl.trim().isEmpty()) {
            configuredUrl = "http://10.0.2.2:8080/";
        } else if (!configuredUrl.endsWith("/")) {
            configuredUrl = configuredUrl + "/";
        }
        DEFAULT_BASE_URL = configuredUrl;
        currentBaseUrl = DEFAULT_BASE_URL;
    }

    public static synchronized ApiService getApiService() {
        if (apiService == null) {
            buildClient(currentBaseUrl);
        }
        return apiService;
    }

    public static synchronized void setBaseUrl(String newUrl) {
        if (newUrl == null || newUrl.trim().isEmpty()) {
            newUrl = DEFAULT_BASE_URL;
        } else if (!newUrl.endsWith("/")) {
            newUrl = newUrl.trim() + "/";
        }
        if (!newUrl.equals(currentBaseUrl)) {
            currentBaseUrl = newUrl;
            buildClient(currentBaseUrl);
        }
    }

    public static String getBaseUrl() {
        return currentBaseUrl;
    }

    private static void buildClient(String baseUrl) {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .build();

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        apiService = retrofit.create(ApiService.class);
    }
}
