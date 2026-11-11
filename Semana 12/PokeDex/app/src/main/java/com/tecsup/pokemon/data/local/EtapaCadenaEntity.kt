package com.tecsup.pokemon.data.local

import androidx.room.Entity

// Una etapa de una cadena evolutiva (una fila por Pokémon de la cadena)
@Entity(
    tableName = "etapas_cadena",
    primaryKeys = ["cadenaId", "especieId"]
)
data class EtapaCadenaEntity(
    val cadenaId: Int,
    val especieId: Int,
    val nombre: String,
    val condicion: String,   // "Nv. 16", "Piedra Trueno"... vacío en la etapa básica
    val metodo: String,      // NIVEL, PIEDRA... vacío en la etapa básica
    val fase: Int,           // 0 = básico, 1 = primera evolución, 2 = segunda
    val orden: Int           // posición dentro de la cadena
)