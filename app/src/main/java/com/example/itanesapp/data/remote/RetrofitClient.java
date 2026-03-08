package com.example.itanesapp.data.remote;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * RetrofitClient — Singleton con dos instancias:
 * 1. MockAPI  — datos de recorridos y puntos
 * 2. ORS      — rutas en auto
 */
public class RetrofitClient {

    // ⚠️ Reemplaza con tu URL base de MockAPI
    private static final String MOCK_API_URL =
            "https://69addd85b50a169ec8806e4d.mockapi.io/api/v1/";

    private static final String ORS_URL =
            "https://api.openrouteservice.org/";

    private static Retrofit mockInstance;
    private static Retrofit orsInstance;

    // --- MockAPI ---
    public static Retrofit getMockInstance() {
        if (mockInstance == null) {
            mockInstance = new Retrofit.Builder()
                    .baseUrl(MOCK_API_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return mockInstance;
    }

    public static MockApiService getMockService() {
        return getMockInstance().create(MockApiService.class);
    }

    // --- OpenRouteService ---
    public static Retrofit getOrsInstance() {
        if (orsInstance == null) {
            orsInstance = new Retrofit.Builder()
                    .baseUrl(ORS_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return orsInstance;
    }

    public static RouteApiService getRouteService() {
        return getOrsInstance().create(RouteApiService.class);
    }
}