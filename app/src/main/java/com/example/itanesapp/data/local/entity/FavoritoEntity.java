package com.example.itanesapp.data.local.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/**
 * FavoritoEntity — Representa la tabla "favoritos".
 * Guarda los puntos turísticos marcados como favoritos por el usuario.
 * Solo existe localmente, nunca se sincroniza con el servidor.
 */
@Entity(
        tableName = "favoritos",
        foreignKeys = @ForeignKey(
                entity = PuntoTuristicoEntity.class,
                parentColumns = "id",
                childColumns = "punto_id",
                onDelete = ForeignKey.CASCADE
        ),
        // Evita duplicados: un punto no puede estar dos veces en favoritos
        indices = @Index(value = "punto_id", unique = true)
)
public class FavoritoEntity {

    @PrimaryKey(autoGenerate = true)
    public int id;

    // FK al punto turístico guardado como favorito
    @ColumnInfo(name = "punto_id")
    public int puntoId;

    // Fecha en formato ISO 8601. Ej: "2025-02-25T10:30:00"
    @ColumnInfo(name = "fecha_guardado")
    public String fechaGuardado;

    // Constructor completo
    public FavoritoEntity(int puntoId, String fechaGuardado) {
        this.puntoId = puntoId;
        this.fechaGuardado = fechaGuardado;
    }
}