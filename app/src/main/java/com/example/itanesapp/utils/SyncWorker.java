package com.example.itanesapp.utils;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.itanesapp.data.repository.RecorridoRepository;

/**
 * SyncWorker — WorkManager Worker que sincroniza
 * datos de MockAPI con Room en background.
 *
 * Se ejecuta cada 24h cuando hay internet disponible.
 * Si falla → WorkManager reintenta automáticamente.
 */
public class SyncWorker extends Worker {

    private static final String TAG = "SyncWorker";

    public SyncWorker(@NonNull Context context,
                      @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.d(TAG, "Iniciando sincronización en background...");

        try {
            Application application =
                    (Application) getApplicationContext();

            RecorridoRepository repository =
                    new RecorridoRepository(application);
            repository.sincronizar();

            Log.d(TAG, "Sincronización completada");
            return Result.success();

        } catch (Exception e) {
            Log.e(TAG, "Error en sincronización: " + e.getMessage());
            return Result.retry();
        }
    }
}