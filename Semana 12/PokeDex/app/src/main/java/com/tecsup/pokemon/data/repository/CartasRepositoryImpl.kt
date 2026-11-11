package com.tecsup.pokemon.data.repository

import com.tecsup.pokemon.data.remote.TcgApiService
import com.tecsup.pokemon.domain.model.Carta
import retrofit2.HttpException
import java.io.IOException

class CartasRepositoryImpl(
    private val api: TcgApiService
) : CartasRepository {

    // Máximo de cartas que se muestran en el carrusel
    private val maximo = 20

    override suspend fun cartasDe(nombrePokemon: String): Result<List<Carta>> = try {
        // "mr-mime" -> ["mr", "mime"]
        val palabras = nombrePokemon.lowercase().split("-").filter { it.isNotBlank() }
        // Se busca por la palabra más larga ("mime") y luego se filtra con exactitud
        val busqueda = palabras.maxByOrNull { it.length } ?: nombrePokemon

        val cartas = api.buscarCartas(busqueda)
            .filter { it.image != null && esDelPokemon(it.name, palabras) }
            .takeLast(maximo)
            .reversed()   // las de sets más nuevos primero
            .map { dto ->
                Carta(
                    id = dto.id,
                    nombre = dto.name,
                    imagenPequena = "${dto.image}/low.webp",
                    imagenGrande = "${dto.image}/high.webp"
                )
            }
        Result.success(cartas)
    } catch (e: HttpException) {
        // TCGdex responde 404 cuando no hay cartas: no es un error, es una lista vacía
        if (e.code() == 404) Result.success(emptyList()) else Result.failure(e)
    } catch (e: IOException) {
        Result.failure(e)
    }

    // La carta es de este Pokémon si sus palabras aparecen seguidas en el nombre de la carta.
    // "Mew ex" sí es de mew; "Mewtwo" no. "Mr. Mime" sí es de mr-mime.
    private fun esDelPokemon(nombreCarta: String, palabras: List<String>): Boolean {
        val palabrasCarta = nombreCarta.lowercase()
            .split(Regex("[^a-z0-9]+"))
            .filter { it.isNotBlank() }
        return palabrasCarta.windowed(palabras.size).any { it == palabras }
    }
}