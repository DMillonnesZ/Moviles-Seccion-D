package com.tecsup.pokemon.data.repository

import com.tecsup.pokemon.domain.model.Carta

interface CartasRepository {
    // Cartas de un Pokémon según su nombre de la PokeAPI (ej: "charizard", "mr-mime")
    suspend fun cartasDe(nombrePokemon: String): Result<List<Carta>>
}