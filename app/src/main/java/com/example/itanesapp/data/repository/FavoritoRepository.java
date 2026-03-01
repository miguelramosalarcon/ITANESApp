package com.example.itanesapp.data.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;

import com.example.itanesapp.data.local.ITANESDatabase;
import com.example.itanesapp.data.local.dao.FavoritoDao;
import com.example.itanesapp.data.local.entity.FavoritoEntity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;


/**
 * FavoritoRepository — Maneja toda la lógica de favoritos.
 *
 * Esta tabla es estrictamente local, nunca se sincroniza
 * con ningún servidor. Los favoritos pertenecen al usuario
 * y viven solo en su dispositivo.
 */
public class FavoritoRepository {

    private final FavoritoDao favoritoDao;

    // --------------------------------------------------------
    // Constructor
    // --------------------------------------------------------
    public FavoritoRepository(Application application) {
        ITANESDatabase db = ITANESDatabase.getInstance(application);
        favoritoDao = db.favoritoDao();
    }

    // --------------------------------------------------------
    // LECTURA
    // --------------------------------------------------------

    /**
     * Retorna todos los favoritos guardados por el usuario.
     * Ordenados por fecha más reciente primero.
     */
    public LiveData<List<FavoritoEntity>> getAllFavoritos() {
        return favoritoDao.getAll();
    }

    /**
     * Verifica si un punto está en favoritos.
     * Retorna LiveData<Integer>: 1 = es favorito, 0 = no lo es.
     * El botón ♡ observa este valor para cambiar su estado visual.
     */
    public LiveData<Integer> isFavorito(int puntoId) {
        return favoritoDao.isFavorito(puntoId);
    }

    // --------------------------------------------------------
    // ESCRITURA — siempre en hilo secundario
    // --------------------------------------------------------

    /**
     * Agrega un punto a favoritos con la fecha actual.
     */
    public void agregarFavorito(int puntoId) {
        ITANESDatabase.databaseExecutor.execute(() -> {
            // Fecha actual en formato ISO 8601
            String fecha = LocalDateTime.now()
                    .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            favoritoDao.insert(new FavoritoEntity(puntoId, fecha));
        });
    }

    /**
     * Elimina un punto de favoritos por su ID.
     */
    public void eliminarFavorito(int puntoId) {
        ITANESDatabase.databaseExecutor.execute(() ->
                favoritoDao.deleteByPuntoId(puntoId)
        );
    }

    /**
     * Toggle — si es favorito lo elimina, si no lo es lo agrega.
     * Conveniente para el botón ♡ en PuntoDetailFragment.
     */
    public void toggleFavorito(int puntoId, boolean esFavorito) {
        if (esFavorito) {
            eliminarFavorito(puntoId);
        } else {
            agregarFavorito(puntoId);
        }
    }
}