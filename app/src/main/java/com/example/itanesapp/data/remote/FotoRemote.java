package com.example.itanesapp.data.remote;

import com.google.gson.annotations.SerializedName;

/**
 * FotoRemote — Modelo para cada foto
 * dentro del array fotos de un punto.
 */
public class FotoRemote {

    @SerializedName("url")
    public String url;

    @SerializedName("caption")
    public String caption;
}