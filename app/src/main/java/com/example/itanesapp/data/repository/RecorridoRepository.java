package com.example.itanesapp.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.itanesapp.data.local.ITANESDatabase;
import com.example.itanesapp.data.local.dao.FotoDao;
import com.example.itanesapp.data.local.dao.PuntoDao;
import com.example.itanesapp.data.local.dao.RecorridoDao;
import com.example.itanesapp.data.local.entity.FotoEntity;
import com.example.itanesapp.data.local.entity.PuntoTuristicoEntity;
import com.example.itanesapp.data.local.entity.RecorridoEntity;

import java.util.List;

/**
 * RecorridoRepository — Capa de abstracción entre ViewModels y Room.
 *
 * El ViewModel nunca accede directamente a los DAOs.
 * Siempre pasa por el Repository, que decide si los datos
 * vienen de Room (local) o de la API (remoto).
 *
 * En esta fase solo usamos Room. La API remota se
 * integrará en la Fase 5.
 */
public class RecorridoRepository {

    // DAOs — acceso directo a las tablas
    private final RecorridoDao recorridoDao;
    private final PuntoDao puntoDao;
    private final FotoDao fotoDao;

    // --------------------------------------------------------
    // Constructor — obtiene la instancia de la BD
    // y extrae los DAOs necesarios
    // --------------------------------------------------------
    public RecorridoRepository(Application application) {
        ITANESDatabase db = ITANESDatabase.getInstance(application);
        recorridoDao = db.recorridoDao();
        puntoDao = db.puntoDao();
        fotoDao = db.fotoDao();
    }

    // --------------------------------------------------------
    // RECORRIDOS
    // --------------------------------------------------------

    /**
     * Retorna todos los recorridos como LiveData.
     * La UI se actualiza automáticamente al cambiar los datos.
     */
    public LiveData<List<RecorridoEntity>> getAllRecorridos() {
        return recorridoDao.getAll();
    }

    /**
     * Retorna un recorrido específico por su ID.
     */
    public LiveData<RecorridoEntity> getRecorridoById(int recorridoId) {
        return recorridoDao.getById(recorridoId);
    }

    // --------------------------------------------------------
    // PUNTOS TURÍSTICOS
    // --------------------------------------------------------

    /**
     * Retorna los 5 puntos de un recorrido ordenados por orden ASC.
     */
    public LiveData<List<PuntoTuristicoEntity>> getPuntosByRecorrido(int recorridoId) {
        return puntoDao.getByRecorrido(recorridoId);
    }

    /**
     * Retorna un punto turístico específico por su ID.
     */
    public LiveData<PuntoTuristicoEntity> getPuntoById(int puntoId) {
        return puntoDao.getById(puntoId);
    }

    /**
     * Retorna el siguiente punto en el recorrido.
     * Usado para el botón "Siguiente" en PuntoDetailFragment.
     */
    public LiveData<PuntoTuristicoEntity> getSiguientePunto(int recorridoId, int ordenActual) {
        return puntoDao.getSiguiente(recorridoId, ordenActual + 1);
    }

    /**
     * Retorna el punto anterior en el recorrido.
     * Usado para el botón "Anterior" en PuntoDetailFragment.
     */
    public LiveData<PuntoTuristicoEntity> getAnteriorPunto(int recorridoId, int ordenActual) {
        return puntoDao.getAnterior(recorridoId, ordenActual - 1);
    }

    // --------------------------------------------------------
    // FOTOS
    // --------------------------------------------------------

    /**
     * Retorna todas las fotos de un punto turístico.
     * El ViewPager2 usará esta lista para la galería.
     */
    public LiveData<List<FotoEntity>> getFotosByPunto(int puntoId) {
        return fotoDao.getByPunto(puntoId);
    }

    // --------------------------------------------------------
    // OPERACIONES DE ESCRITURA — siempre en hilo secundario
    // Room no permite escribir en el hilo principal (UI thread)
    // --------------------------------------------------------

    /**
     * Inserta un recorrido en background.
     */
    public void insertRecorrido(RecorridoEntity recorrido) {
        ITANESDatabase.databaseExecutor.execute(() ->
                recorridoDao.insert(recorrido)
        );
    }

    /**
     * Inserta un punto turístico en background.
     */
    public void insertPunto(PuntoTuristicoEntity punto) {
        ITANESDatabase.databaseExecutor.execute(() ->
                puntoDao.insert(punto)
        );
    }
}
