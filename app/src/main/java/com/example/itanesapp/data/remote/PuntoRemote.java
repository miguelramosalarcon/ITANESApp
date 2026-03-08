package com.example.itanesapp.data.remote;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * PuntoRemote — Modelo que mapea el JSON
 * de MockAPI para puntos turísticos.
 *
 * Incluye el array de fotos embebido
 * (no hay recurso separado en MockAPI).
 */
public class PuntoRemote {

    @SerializedName("id")
    public String id;

    @SerializedName("recorridoId")
    public String recorridoId;

    @SerializedName("nombre")
    public String nombre;

    @SerializedName("descripcion")
    public String descripcion;

    @SerializedName("latitud")
    public double latitud;

    @SerializedName("longitud")
    public double longitud;

    @SerializedName("orden")
    public int orden;

    @SerializedName("imagenUrl")
    public String imagenUrl;

    @SerializedName("fotos")
    public List<FotoRemote> fotos;
}