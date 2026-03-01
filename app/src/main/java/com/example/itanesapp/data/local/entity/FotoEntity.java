package com.example.itanesapp.data.local.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/**
 * FotoEntity — Representa la tabla "fotos".
 * Cada punto turístico puede tener múltiples fotos.
 * Relación: muchas fotos → un punto turístico (ForeignKey)
 */
@Entity(
        tableName = "fotos",
        foreignKeys = @ForeignKey(
                entity = PuntoTuristicoEntity.class,
                parentColumns = "id",
                childColumns = "punto_id",
                // Si se elimina un punto, se eliminan sus fotos
                onDelete = ForeignKey.CASCADE
        ),
        indices = @Index(value = "punto_id")
)
public class FotoEntity {

    @PrimaryKey(autoGenerate = true)
    public int id;

    // FK que referencia a PuntoTuristicoEntity.id
    @ColumnInfo(name = "punto_id")
    public int puntoId;

    // URL de la foto (puede ser remota o local en assets)
    @ColumnInfo(name = "url")
    public String url;

    // Texto descriptivo opcional de la foto
    @ColumnInfo(name = "caption")
    public String caption;

    // Constructor completo
    public FotoEntity(int puntoId, String url, String caption) {
        this.puntoId = puntoId;
        this.url = url;
        this.caption = caption;
    }
}