package com.example.itanesapp.data.remote;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * RetrofitClient — Singleton para instancia de Retrofit.
 * Apunta a la API de OpenRouteService.
 */
public class RetrofitClient {

    private static final String BASE_URL =
            "https://api.openrouteservice.org/";

    private static Retrofit instance;

    public static Retrofit getInstance() {
        if (instance == null) {
            instance = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return instance;
    }

    public static RouteApiService getRouteService() {
        return getInstance().create(RouteApiService.class);
    }
}