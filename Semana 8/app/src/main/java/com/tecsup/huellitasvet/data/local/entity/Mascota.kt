package com.tecsup.huellitasvet.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "mascotas",
    foreignKeys = [
        ForeignKey(
            entity = Usuario::class,
            parentColumns = ["id"],
            childColumns = ["duenoId"]
        )
    ],
    indices = [Index("duenoId")]
)
data class Mascota(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val duenoId: Int,
    val nombre: String,
    val especie: String,     // Especie.name → PERRO | GATO | OTRO
    val raza: String,
    val anioNacimiento: Int,
    val peso: Double,
    val sexo: String         // Sexo.name → MACHO | HEMBRA
)