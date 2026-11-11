package com.tecsup.pokemon.domain.model

// Una cadena evolutiva completa: sus etapas y los métodos que usa
data class CadenaEvolutiva(
    val id: Int,
    val etapas: List<EtapaEvolucion>,
    val metodos: List<MetodoEvolucion>   // ej: [PIEDRA, AMISTAD] para Eevee
)