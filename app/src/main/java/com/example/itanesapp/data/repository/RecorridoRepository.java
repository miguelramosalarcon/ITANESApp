package com.example.itanesapp.data.repository;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.example.itanesapp.data.local.ITANESDatabase;
import com.example.itanesapp.data.local.dao.FotoDao;
import com.example.itanesapp.data.local.dao.PuntoDao;
import com.example.itanesapp.data.local.dao.RecorridoDao;
import com.example.itanesapp.data.local.entity.FotoEntity;
import com.example.itanesapp.data.local.entity.PuntoTuristicoEntity;
import com.example.itanesapp.data.local.entity.RecorridoEntity;
import com.example.itanesapp.data.remote.RemoteDataSource;

import java.util.List;

/**
 * RecorridoRepository — Fuente única de verdad.
 *
 * Estrategia Offline-First:
 * 1. Retorna LiveData de Room inmediatamente
 * 2. Dispara sincronización con MockAPI en background
 * 3. Room actualiza → LiveData notifica la UI solo
 */
public class RecorridoRepository {

    private static final String TAG = "RecorridoRepository";

    private final RecorridoDao recorridoDao;
    private final PuntoDao puntoDao;
    private final FotoDao fotoDao;
    private final RemoteDataSource remoteDataSource;

    public RecorridoRepository(Application application) {
        ITANESDatabase db = ITANESDatabase.getInstance(application);
        recorridoDao    = db.recorridoDao();
        puntoDao        = db.puntoDao();
        fotoDao         = db.fotoDao();
        remoteDataSource = new RemoteDataSource(
                recorridoDao, puntoDao, fotoDao);
    }

    // -------------------------------------------------------
    // Recorridos
    // -------------------------------------------------------

    public LiveData<List<RecorridoEntity>> getAllRecorridos() {
        // 1. Retorna datos locales inmediatamente
        // 2. Sincroniza en background

        sincronizar();
        return recorridoDao.getAll();
    }

    public LiveData<RecorridoEntity> getRecorridoById(int id) {
        return recorridoDao.getById(id);
    }

    // -------------------------------------------------------
    // Puntos
    // -------------------------------------------------------

    public LiveData<List<PuntoTuristicoEntity>> getPuntosByRecorrido(
            int recorridoId) {
        return puntoDao.getByRecorrido(recorridoId);
    }

    public LiveData<PuntoTuristicoEntity> getPuntoById(int id) {
        return puntoDao.getById(id);
    }

    public LiveData<PuntoTuristicoEntity> getSiguientePunto(
            int recorridoId, int ordenActual) {
        return puntoDao.getSiguiente(recorridoId, ordenActual);
    }

    public LiveData<PuntoTuristicoEntity> getAnteriorPunto(
            int recorridoId, int ordenActual) {
        return puntoDao.getAnterior(recorridoId, ordenActual);
    }

    // -------------------------------------------------------
    // Fotos
    // -------------------------------------------------------

    public LiveData<List<FotoEntity>> getFotosByPunto(int puntoId) {
        return fotoDao.getByPunto(puntoId);
    }

    // -------------------------------------------------------
    // Sincronización
    // -------------------------------------------------------

    /**
     * Dispara sincronización con MockAPI.
     * Solo cuando hay internet — si falla,
     * Room ya tiene datos del prepoblado.
     */
    public void sincronizar() {
        Log.d(TAG, "Iniciando sincronización con MockAPI...");
        remoteDataSource.sincronizarRecorridos();
        remoteDataSource.sincronizarPuntos();
    }
}