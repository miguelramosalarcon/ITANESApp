package com.example.itanesapp.data.remote;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;

/**
 * MockApiService — Interface Retrofit para
 * consumir los endpoints de MockAPI.io
 */
public interface MockApiService {

    @GET("recorridos")
    Call<List<RecorridoRemote>> getRecorridos();

    @GET("puntos")
    Call<List<PuntoRemote>> getPuntos();
}