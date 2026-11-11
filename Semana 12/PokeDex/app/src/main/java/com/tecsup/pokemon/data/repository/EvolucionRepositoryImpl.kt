package com.tecsup.pokemon.data.repository

import com.tecsup.pokemon.data.local.EvolucionDao
import com.tecsup.pokemon.data.mapper.aCadenas
import com.tecsup.pokemon.data.mapper.aEtapasCadena
import com.tecsup.pokemon.data.mapper.idDesdeUrl
import com.tecsup.pokemon.data.remote.ApiService
import com.tecsup.pokemon.domain.model.CadenaEvolutiva
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import retrofit2.HttpException
import java.io.IOException
import java.util.concurrent.atomic.AtomicInteger

// Igual que los tipos: Room es la fuente de verdad y la API solo la llena
class EvolucionRepositoryImpl(
    private val api: ApiService,
    private val dao: EvolucionDao
) : EvolucionRepository {

    // Máximo de descargas a la vez (para no saturar la PokeAPI ni el celular)
    private val descargasALaVez = 8

    override val cadenas: Flow<List<CadenaEvolutiva>> =
        dao.etapas().map { filas -> filas.aCadenas() }

    override val tiposPorPokemon: Flow<Map<Int, List<String>>> =
        dao.relacionesTipo().map { lista -> lista.groupBy({ it.itemId }, { it.tipo }) }

    override suspend fun cantidadGuardada(): Int = dao.contarCadenas()

    override suspend fun descargar(onProgreso: (Int, Int) -> Unit): Result<Int> = try {
        // 1. URLs de todas las cadenas
        val urls = api.listarCadenas().results.map { it.url }
        val total = urls.size
        val hechas = AtomicInteger(0)
        val semaforo = Semaphore(descargasALaVez)
        onProgreso(0, total)

        // 2. Cada cadena en paralelo (de 8 en 8); si una falla, se salta
        val filas = coroutineScope {
            urls.map { url ->
                async {
                    semaforo.withPermit {
                        val cadena = runCatching { api.obtenerCadenaEvolutiva(url) }.getOrNull()
                        onProgreso(hechas.incrementAndGet(), total)
                        cadena?.chain?.aEtapasCadena(idDesdeUrl(url))
                    }
                }
            }.awaitAll()
        }.filterNotNull()

        // 3. Si no se pudo bajar ninguna, es un problema de conexión
        if (filas.isEmpty()) throw IOException("No se pudo descargar ninguna cadena")

        // 4. Se reemplaza todo en Room en una sola transacción
        dao.reemplazarTodo(filas.flatten())
        Result.success(filas.size)
    } catch (e: IOException) {
        Result.failure(e)
    } catch (e: HttpException) {
        Result.failure(e)
    }
}