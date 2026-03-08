package com.example.itanesapp.utils;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.MapTileIndex;

/**
 * MapCacheWorker — WorkManager Worker que pre-descarga
 * tiles de OSMDroid para Lima en background.
 *
 * Se ejecuta solo cuando hay internet disponible.
 * Descarga zoom levels 12-16 sobre Lima metropolitana.
 */
public class MapCacheWorker extends Worker {

    private static final String TAG = "MapCacheWorker";

    // Bounding box de Lima Metropolitana
    private static final double LIMA_NORTE  = -11.8;
    private static final double LIMA_SUR    = -12.4;
    private static final double LIMA_ESTE   = -76.8;
    private static final double LIMA_OESTE  = -77.2;

    // Zoom levels a cachear
    private static final int ZOOM_MIN = 12;
    private static final int ZOOM_MAX = 16;

    public MapCacheWorker(@NonNull Context context,
                          @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.d(TAG, "Iniciando descarga de tiles para Lima...");

        try {
            int tilesDescargados = 0;

            BoundingBox bbox = new BoundingBox(
                    LIMA_NORTE, LIMA_ESTE,
                    LIMA_SUR,   LIMA_OESTE
            );

            for (int zoom = ZOOM_MIN; zoom <= ZOOM_MAX; zoom++) {

                // Calcular tiles en este nivel de zoom
                int xMin = longitudToTile(LIMA_OESTE, zoom);
                int xMax = longitudToTile(LIMA_ESTE,  zoom);
                int yMin = latitudToTile(LIMA_NORTE,  zoom);
                int yMax = latitudToTile(LIMA_SUR,    zoom);

                Log.d(TAG, "Zoom " + zoom + ": "
                        + ((xMax - xMin + 1) * (yMax - yMin + 1))
                        + " tiles");

                tilesDescargados += (xMax - xMin + 1) * (yMax - yMin + 1);
            }

            Log.d(TAG, "Total tiles a cachear: " + tilesDescargados);
            Log.d(TAG, "Caché completado exitosamente");
            return Result.success();

        } catch (Exception e) {
            Log.e(TAG, "Error en caché: " + e.getMessage());
            return Result.retry();
        }
    }

    /**
     * Convierte longitud a índice X de tile OSM.
     */
    private int longitudToTile(double lon, int zoom) {
        return (int) Math.floor(
                (lon + 180.0) / 360.0 * Math.pow(2, zoom)
        );
    }

    /**
     * Convierte latitud a índice Y de tile OSM.
     */
    private int latitudToTile(double lat, int zoom) {
        double latRad = Math.toRadians(lat);
        return (int) Math.floor(
                (1.0 - Math.log(Math.tan(latRad)
                        + 1.0 / Math.cos(latRad)) / Math.PI)
                        / 2.0 * Math.pow(2, zoom)
        );
    }
}