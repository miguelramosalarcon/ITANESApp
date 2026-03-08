package com.example.itanesapp;

import android.app.Application;

import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.example.itanesapp.utils.MapCacheWorker;
import com.example.itanesapp.utils.OsmConfig;
import com.example.itanesapp.utils.SyncWorker;

import java.util.concurrent.TimeUnit;

/**
 * ITANESApp — Application principal.
 *
 * Inicializa:
 * 1. OSMDroid — mapas
 * 2. SyncWorker — sincronización de datos cada 24h
 * 3. MapCacheWorker — caché de tiles cada 24h
 */
public class ITANESApp extends Application {

    private static final String WORK_SYNC    = "sync_work";
    private static final String WORK_MAP     = "map_cache_work";

    @Override
    public void onCreate() {
        super.onCreate();

        // 1. Inicializar OSMDroid
        OsmConfig.init(this);

        // 2. Programar workers
        programarSincronizacion();
        programarCacheMapa();
    }

    /**
     * Sincroniza datos de MockAPI cada 24h.
     * Requiere cualquier tipo de conexión a internet.
     */
    private void programarSincronizacion() {
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        PeriodicWorkRequest syncRequest =
                new PeriodicWorkRequest.Builder(
                        SyncWorker.class,
                        24, TimeUnit.HOURS)
                        .setConstraints(constraints)
                        .build();

        WorkManager.getInstance(this)
                .enqueueUniquePeriodicWork(
                        WORK_SYNC,
                        ExistingPeriodicWorkPolicy.KEEP,
                        syncRequest
                );
    }

    /**
     * Descarga tiles del mapa cada 24h.
     * Solo con WiFi para no consumir datos móviles.
     */
    private void programarCacheMapa() {
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.UNMETERED)
                .setRequiresBatteryNotLow(true)
                .build();

        PeriodicWorkRequest cacheRequest =
                new PeriodicWorkRequest.Builder(
                        MapCacheWorker.class,
                        24, TimeUnit.HOURS)
                        .setConstraints(constraints)
                        .build();

        WorkManager.getInstance(this)
                .enqueueUniquePeriodicWork(
                        WORK_MAP,
                        ExistingPeriodicWorkPolicy.KEEP,
                        cacheRequest
                );
    }
}