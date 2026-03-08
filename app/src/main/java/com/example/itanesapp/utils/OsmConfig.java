package com.example.itanesapp.utils;

import android.content.Context;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.views.MapView;

/**
 * OsmConfig — Configuración global de OSMDroid.
 *
 * Centraliza la inicialización del mapa:
 * - User agent (obligatorio para OSMDroid)
 * - Caché de tiles en disco
 * - Configuración por defecto del MapView
 */
public class OsmConfig {

    // Zoom mínimo y máximo permitidos
    public static final double ZOOM_DEFAULT  = 15.0;
    public static final double ZOOM_MIN      = 5.0;
    public static final double ZOOM_MAX      = 19.0;

    // Centro por defecto: Lima, Perú
    public static final double LIMA_LAT = -12.0464;
    public static final double LIMA_LON = -77.0428;

    /**
     * Inicializar OSMDroid — llamar una sola vez en Application.onCreate()
     */
    public static void init(Context context) {
        Configuration.getInstance().load(
                context,
                context.getSharedPreferences("osm_prefs", Context.MODE_PRIVATE)
        );

        // User agent obligatorio — identifica tu app ante OpenStreetMap
        Configuration.getInstance()
                .setUserAgentValue(context.getPackageName());
    }

    /**
     * Configurar un MapView con los parámetros estándar de la app.
     */
    public static void configurarMapa(MapView mapa) {
        mapa.setTileSource(TileSourceFactory.MAPNIK);
        mapa.setMultiTouchControls(true);
        mapa.setBuiltInZoomControls(false);
        mapa.getController().setZoom(ZOOM_DEFAULT);
        mapa.setMinZoomLevel(ZOOM_MIN);
        mapa.setMaxZoomLevel(ZOOM_MAX);
    }
}