package com.example.itanesapp.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.itanesapp.data.local.entity.RecorridoEntity;

import java.util.List;

/**
 * RecorridoDao — Operaciones CRUD para la tabla "recorridos".
 * LiveData permite que la UI se actualice automáticamente
 * cuando los datos cambian en Room.
 */
@Dao
public interface RecorridoDao {

    // --------------------------------------------------------
    // INSERT — Inserta un recorrido, ignora si ya existe (mismo id)
    // --------------------------------------------------------
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(RecorridoEntity recorrido);

    // --------------------------------------------------------
    // INSERT LISTA — Inserta múltiples recorridos de una vez
    // Útil para el prepoblado inicial de la base de datos
    // --------------------------------------------------------
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAll(List<RecorridoEntity> recorridos);

    // --------------------------------------------------------
    // UPDATE — Actualiza un recorrido existente
    // --------------------------------------------------------
    @Update
    void update(RecorridoEntity recorrido);

    // --------------------------------------------------------
    // DELETE — Elimina un recorrido (en cascada elimina sus puntos)
    // --------------------------------------------------------
    @Delete
    void delete(RecorridoEntity recorrido);

    // --------------------------------------------------------
    // SELECT ALL — Retorna todos los recorridos como LiveData
    // La UI se actualiza automáticamente al cambiar los datos
    // --------------------------------------------------------
    @Query("SELECT * FROM recorridos ORDER BY id ASC")
    LiveData<List<RecorridoEntity>> getAll();

    // --------------------------------------------------------
    // SELECT BY ID — Retorna un recorrido específico por su id
    // --------------------------------------------------------
    @Query("SELECT * FROM recorridos WHERE id = :recorridoId")
    LiveData<RecorridoEntity> getById(int recorridoId);

    // --------------------------------------------------------
    // COUNT — Verifica si ya hay datos en la tabla
    // Útil para saber si necesitamos prepoblar la BD
    // --------------------------------------------------------
    @Query("SELECT COUNT(*) FROM recorridos")
    int getCount();
}