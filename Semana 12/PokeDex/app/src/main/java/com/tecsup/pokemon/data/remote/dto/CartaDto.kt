package com.tecsup.pokemon.data.remote.dto

// Respuesta de GET {idioma}/cards?name=... en TCGdex (versión resumida de cada carta)
data class CartaDto(
    val id: String,          // ej: "swsh3-136"
    val localId: String?,    // número dentro del set
    val name: String,        // ej: "Charizard V"
    val image: String?       // URL base sin extensión (puede venir vacía)
)