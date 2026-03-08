package com.example.itanesapp.data.remote;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * RouteApiService — Interface Retrofit para
 * OpenRouteService Directions API.
 *
 * Endpoint: POST /v2/directions/{profile}
 * Profile: driving-car, foot-walking, cycling-regular
 */
public interface RouteApiService {

    @POST("v2/directions/{profile}")
    Call<RouteResponse> getRoute(
            @Header("Authorization") String apiKey,
            @Path("profile") String profile,
            @Body RouteRequestBody body
    );
}