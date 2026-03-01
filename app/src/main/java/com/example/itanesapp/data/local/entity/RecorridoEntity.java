package com.example.itanesapp.data.local.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * RecorridoEntity — Representa la tabla "recorridos" en SQLite.
 * Cada registro es un recorrido turístico con 5 puntos de visita.
 */
@Entity(tableName = "recorridos")
public class RecorridoEntity {

    // ID autogenerado por Room al insertar
    @PrimaryKey(autoGenerate = true)
    public int id;

    // Nombre del recorrido. Ej: "Recorrido Norte de Lima"
    @ColumnInfo(name = "nombre")
    public String nombre;

    // Descripción general del recorrido
    @ColumnInfo(name = "descripcion")
    public String descripcion;

    // URL de la imagen de portada del recorrido
    @ColumnInfo(name = "imagen_url")
    public String imagenUrl;

    // Duración estimada en horas. Ej: 3.5
    @ColumnInfo(name = "duracion_horas")
    public double duracionHoras;

    // --------------------------------------------------------
    // Constructor completo (Room no lo requiere pero es útil
    // para crear objetos manualmente en el prepoblado)
    // --------------------------------------------------------
    public RecorridoEntity(String nombre, String descripcion,
                           String imagenUrl, double duracionHoras) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.imagenUrl = imagenUrl;
        this.duracionHoras = duracionHoras;
    }
}