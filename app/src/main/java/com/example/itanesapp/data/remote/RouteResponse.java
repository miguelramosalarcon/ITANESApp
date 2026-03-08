package com.example.itanesapp.data.remote;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * RouteResponse — Modelo que mapea la respuesta JSON
 * de OpenRouteService API.
 *
 * Estructura:
 * {
 *   "features": [{
 *     "geometry": {
 *       "coordinates": [[lon,lat], [lon,lat], ...]
 *     },
 *     "properties": {
 *       "segments": [{ "distance": 1234.5, "duration": 567.8 }]
 *     }
 *   }]
 * }
 */
public class RouteResponse {

    @SerializedName("features")
    public List<Feature> features;

    public static class Feature {
        @SerializedName("geometry")
        public Geometry geometry;

        @SerializedName("properties")
        public Properties properties;
    }

    public static class Geometry {
        @SerializedName("coordinates")
        public List<List<Double>> coordinates;
    }

    public static class Properties {
        @SerializedName("segments")
        public List<Segment> segments;
    }

    public static class Segment {
        @SerializedName("distance")
        public double distance; // metros

        @SerializedName("duration")
        public double duration; // segundos
    }
}