package com.example.itanesapp.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.itanesapp.data.local.entity.FavoritoEntity;

import java.util.List;

/**
 * FavoritoDao — Operaciones CRUD para la tabla "favoritos".
 * Esta tabla es estrictamente local, nunca se sincroniza con el servidor.
 */
@Dao
public interface FavoritoDao {

    // --------------------------------------------------------
    // INSERT — Agrega un punto a favoritos
    // REPLACE: si el punto ya es favorito, lo reemplaza
    // (evita duplicados gracias al índice unique en punto_id)
    // --------------------------------------------------------
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(FavoritoEntity favorito);

    // --------------------------------------------------------
    // DELETE — Elimina un punto de favoritos
    // --------------------------------------------------------
    @Delete
    void delete(FavoritoEntity favorito);

    // --------------------------------------------------------
    // DELETE BY PUNTO ID — Elimina favorito por id del punto
    // Más conveniente que buscar el objeto completo primero
    // --------------------------------------------------------
    @Query("DELETE FROM favoritos WHERE punto_id = :puntoId")
    void deleteByPuntoId(int puntoId);

    // --------------------------------------------------------
    // SELECT ALL — Retorna todos los favoritos como LiveData
    // Ordenados por fecha más reciente primero
    // --------------------------------------------------------
    @Query("SELECT * FROM favoritos ORDER BY fecha_guardado DESC")
    LiveData<List<FavoritoEntity>> getAll();

    // --------------------------------------------------------
    // IS FAVORITO — Verifica si un punto está en favoritos
    // Retorna 1 si existe, 0 si no — útil para el botón ♡
    // --------------------------------------------------------
    @Query("SELECT COUNT(*) FROM favoritos WHERE punto_id = :puntoId")
    LiveData<Integer> isFavorito(int puntoId);
}