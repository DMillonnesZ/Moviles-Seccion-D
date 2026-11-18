package com.millones.campusgps.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.millones.campusgps.model.Lugar

// Tabla "lugares": cada lugar que el usuario guarda desde el mapa
@Entity(tableName = "lugares")
data class LugarEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nombre: String,
    val descripcion: String,
    val categoria: String,
    val latitud: Double,
    val longitud: Double,
    val favorito: Boolean = false,
    // Ruta del archivo de la foto dentro de la app (null = sin foto)
    val foto: String? = null,
    @ColumnInfo(name = "creado_en") val creadoEn: Long = System.currentTimeMillis()
)

fun LugarEntity.aModelo() = Lugar(
    id = id,
    nombre = nombre,
    descripcion = descripcion,
    categoria = categoria,
    latitud = latitud,
    longitud = longitud,
    favorito = favorito,
    foto = foto
)
