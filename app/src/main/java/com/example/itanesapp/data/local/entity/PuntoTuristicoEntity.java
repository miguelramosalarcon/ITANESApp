package com.example.itanesapp.data.local.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/**
 * PuntoTuristicoEntity — Representa la tabla "puntos_turisticos".
 * Cada punto pertenece a un recorrido y tiene coordenadas para el mapa.
 * Relación: muchos puntos → un recorrido (ForeignKey)
 */
@Entity(
        tableName = "puntos_turisticos",
        foreignKeys = @ForeignKey(
                entity = RecorridoEntity.class,
                parentColumns = "id",
                childColumns = "recorrido_id",
                // Si se elimina un recorrido, se eliminan sus puntos automáticamente
                onDelete = ForeignKey.CASCADE
        ),
        // Índice para acelerar búsquedas por recorrido_id
        indices = @Index(value = "recorrido_id")
)
public class PuntoTuristicoEntity {

    @PrimaryKey(autoGenerate = true)
    public int id;

    // FK que referencia a RecorridoEntity.id
    @ColumnInfo(name = "recorrido_id")
    public int recorridoId;

    // Nombre del lugar. Ej: "Plaza Mayor de Lima"
    @ColumnInfo(name = "nombre")
    public String nombre;

    // Descripción histórica o turística del lugar
    @ColumnInfo(name = "descripcion")
    public String descripcion;

    // Coordenadas para OSMDroid
    @ColumnInfo(name = "latitud")
    public double latitud;

    @ColumnInfo(name = "longitud")
    public double longitud;

    // Orden de visita dentro del recorrido (1 al 5)
    @ColumnInfo(name = "orden")
    public int orden;

    // URL de imagen principal del punto
    @ColumnInfo(name = "imagen_url")
    public String imagenUrl;

    // Constructor completo
    public PuntoTuristicoEntity(int recorridoId, String nombre, String descripcion,
                                double latitud, double longitud,
                                int orden, String imagenUrl) {
        this.recorridoId = recorridoId;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.latitud = latitud;
        this.longitud = longitud;
        this.orden = orden;
        this.imagenUrl = imagenUrl;
    }
}