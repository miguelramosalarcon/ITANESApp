package com.example.itanesapp.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.itanesapp.data.local.entity.PuntoTuristicoEntity;

import java.util.List;

/**
 * PuntoDao — Operaciones CRUD para la tabla "puntos_turisticos".
 */
@Dao
public interface PuntoDao {

    // --------------------------------------------------------
    // INSERT — Inserta un punto turístico
    // --------------------------------------------------------
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(PuntoTuristicoEntity punto);

    // --------------------------------------------------------
    // INSERT LISTA — Inserta múltiples puntos de una vez
    // --------------------------------------------------------
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAll(List<PuntoTuristicoEntity> puntos);

    // --------------------------------------------------------
    // UPDATE — Actualiza un punto existente
    // --------------------------------------------------------
    @Update
    void update(PuntoTuristicoEntity punto);

    // --------------------------------------------------------
    // DELETE — Elimina un punto turístico
    // --------------------------------------------------------
    @Delete
    void delete(PuntoTuristicoEntity punto);

    // --------------------------------------------------------
    // SELECT BY RECORRIDO — Retorna los 5 puntos de un recorrido
    // ordenados por su campo "orden" (1 al 5)
    // --------------------------------------------------------
    @Query("SELECT * FROM puntos_turisticos WHERE recorrido_id = :recorridoId ORDER BY orden ASC")
    LiveData<List<PuntoTuristicoEntity>> getByRecorrido(int recorridoId);

    // --------------------------------------------------------
    // SELECT BY ID — Retorna un punto específico por su id
    // --------------------------------------------------------
    @Query("SELECT * FROM puntos_turisticos WHERE id = :puntoId")
    LiveData<PuntoTuristicoEntity> getById(int puntoId);

    // --------------------------------------------------------
    // SELECT SIGUIENTE — Retorna el siguiente punto en el recorrido
    // Útil para el botón "Siguiente" en PuntoDetailFragment
    // --------------------------------------------------------
    @Query("SELECT * FROM puntos_turisticos WHERE recorrido_id = :recorridoId AND orden = :ordenSiguiente")
    LiveData<PuntoTuristicoEntity> getSiguiente(int recorridoId, int ordenSiguiente);

    // --------------------------------------------------------
    // SELECT ANTERIOR — Retorna el punto anterior en el recorrido
    // Útil para el botón "Anterior" en PuntoDetailFragment
    // --------------------------------------------------------
    @Query("SELECT * FROM puntos_turisticos WHERE recorrido_id = :recorridoId AND orden = :ordenAnterior")
    LiveData<PuntoTuristicoEntity> getAnterior(int recorridoId, int ordenAnterior);
}