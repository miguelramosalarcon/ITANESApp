package com.example.itanesapp.data.remote;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * RouteRequestBody — Cuerpo del POST a ORS.
 *
 * Envía las coordenadas como:
 * { "coordinates": [[lon1,lat1], [lon2,lat2], ...] }
 *
 * IMPORTANTE: ORS usa [longitud, latitud] (GeoJSON)
 * NO [latitud, longitud] como OSMDroid.
 */
public class RouteRequestBody {

    @SerializedName("coordinates")
    public List<List<Double>> coordinates;

    public RouteRequestBody(List<List<Double>> coordinates) {
        this.coordinates = coordinates;
    }
}