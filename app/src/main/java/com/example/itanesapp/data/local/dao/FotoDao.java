package com.example.itanesapp.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.itanesapp.data.local.entity.FotoEntity;

import java.util.List;

/**
 * FotoDao — Operaciones CRUD para la tabla "fotos".
 */
@Dao
public interface FotoDao {

    // --------------------------------------------------------
    // INSERT — Inserta una foto
    // --------------------------------------------------------
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(FotoEntity foto);

    // --------------------------------------------------------
    // INSERT LISTA — Inserta múltiples fotos de una vez
    // --------------------------------------------------------
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAll(List<FotoEntity> fotos);

    // --------------------------------------------------------
    // DELETE — Elimina una foto
    // --------------------------------------------------------
    @Delete
    void delete(FotoEntity foto);

    // --------------------------------------------------------
    // SELECT BY PUNTO — Retorna todas las fotos de un punto turístico
    // El ViewPager2 usará esta lista para mostrar la galería
    // --------------------------------------------------------
    @Query("SELECT * FROM fotos WHERE punto_id = :puntoId ORDER BY id ASC")
    LiveData<List<FotoEntity>> getByPunto(int puntoId);

    // --------------------------------------------------------
    // DELETE BY PUNTO — Elimina todas las fotos de un punto
    // Útil al sincronizar fotos nuevas desde la API
    // --------------------------------------------------------
    @Query("DELETE FROM fotos WHERE punto_id = :puntoId")
    void deleteByPunto(int puntoId);
}