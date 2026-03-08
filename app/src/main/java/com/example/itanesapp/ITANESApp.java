package com.example.itanesapp;

import android.app.Application;

import com.example.itanesapp.utils.OsmConfig;

/**
 * ITANESApp — Application principal.
 *
 * Se ejecuta ANTES que cualquier Activity.
 * Ideal para inicializar librerías globales como OSMDroid.
 */
public class ITANESApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Inicializar OSMDroid una sola vez
        OsmConfig.init(this);
    }
}