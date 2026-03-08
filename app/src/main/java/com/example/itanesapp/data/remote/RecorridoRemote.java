package com.example.itanesapp.data.remote;

import com.google.gson.annotations.SerializedName;

/**
 * RecorridoRemote — Modelo que mapea el JSON
 * de MockAPI para recorridos.
 *
 * Separado de RecorridoEntity (Room) para mantener
 * independencia entre capa remota y local.
 */
public class RecorridoRemote {

    @SerializedName("id")
    public String id;

    @SerializedName("nombre")
    public String nombre;

    @SerializedName("descripcion")
    public String descripcion;

    @SerializedName("imagenUrl")
    public String imagenUrl;

    @SerializedName("duracionHoras")
    public double duracionHoras;
}