package com.millones.campusgps.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.millones.campusgps.model.Lugar

// Copia local (caché) de los lugares de Firestore: la app los muestra aunque no haya internet.
// id = id del documento en Firestore.
@Entity(tableName = "lugares")
data class LugarEntity(
    @PrimaryKey val id: String,
    val nombre: String,
    val descripcion: String,
    val categoria: String,
    val latitud: Double,
    val longitud: Double,
    val favorito: Boolean = false,
    // Ruta del archivo local de la foto (null = sin foto)
    val foto: String? = null,
    @ColumnInfo(name = "autor_uid") val autorUid: String,
    @ColumnInfo(name = "autor_nombre") val autorNombre: String,
    @ColumnInfo(name = "creado_en") val creadoEn: Long
)

fun LugarEntity.aModelo(miUid: String?) = Lugar(
    id = id,
    nombre = nombre,
    descripcion = descripcion,
    categoria = categoria,
    latitud = latitud,
    longitud = longitud,
    favorito = favorito,
    foto = foto,
    autorUid = autorUid,
    autorNombre = autorNombre,
    esMio = miUid != null && autorUid == miUid
)
