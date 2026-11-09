package com.tecsup.pokemon.data.repository

import com.tecsup.pokemon.data.local.ItemDao
import com.tecsup.pokemon.data.local.ItemTipoEntity
import com.tecsup.pokemon.data.mapper.aDetalle
import com.tecsup.pokemon.data.mapper.aEntity
import com.tecsup.pokemon.data.mapper.aModelo
import com.tecsup.pokemon.data.mapper.idDesdeUrl
import com.tecsup.pokemon.data.remote.ApiService
import com.tecsup.pokemon.domain.model.Item
import com.tecsup.pokemon.domain.model.ItemDetalle
import com.tecsup.pokemon.domain.model.Tipo
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import retrofit2.HttpException
import java.io.IOException

// Room es la fuente única de verdad: la UI lee de Room y Retrofit solo la actualiza
class ItemRepositoryImpl(
    private val api: ApiService,
    private val dao: ItemDao
) : ItemRepository {

    // Ids >= 10000 son formas especiales (megas, gigamax...)
    private val idMaximo = 10000

    override val items: Flow<List<Item>> =
        dao.todos().map { lista -> lista.map { it.aModelo() } }

    override val favoritos: Flow<List<Item>> =
        dao.favoritos().map { lista -> lista.map { it.aModelo() } }

    // Tipos con la cantidad de Pokémon de cada uno
    override val tipos: Flow<List<Tipo>> =
        dao.tiposConCantidad().map { lista -> lista.map { it.aModelo() } }

    override fun itemsPorTipo(tipo: String): Flow<List<Item>> =
        dao.porTipo(tipo).map { lista -> lista.map { it.aModelo() } }

    override suspend fun refrescar(): Result<Int> = try {
        // 1. Se guardan los favoritos actuales para no perderlos al sincronizar
        val favoritos = dao.idsFavoritos().toSet()

        // 2. Nombres de todos los tipos
        val nombres = api.listarTipos().results.map { it.name }

        // 3. Detalle de cada tipo, en paralelo; se descartan los tipos vacíos
        val detalles = coroutineScope {
            nombres.map { nombre -> async { api.listarPorTipo(nombre) } }.awaitAll()
        }.filter { it.pokemon.isNotEmpty() }

        // 4. Pokémon únicos, sin formas especiales, con su favorito reaplicado
        val items = detalles
            .flatMap { tipo -> tipo.pokemon.map { it.pokemon.aEntity() } }
            .filter { it.id < idMaximo }
            .distinctBy { it.id }
            .map { it.copy(esFavorito = it.id in favoritos) }

        // 5. Relación Pokémon - tipo
        val relaciones = detalles
            .flatMap { tipo ->
                tipo.pokemon.map { ItemTipoEntity(idDesdeUrl(it.pokemon.url), tipo.name) }
            }
            .filter { it.itemId < idMaximo }

        // 6. Se guarda todo en Room en una sola transacción
        dao.guardarTodo(
            items = items,
            tipos = detalles.map { it.aEntity() },
            relaciones = relaciones
        )
        Result.success(items.size)
    } catch (e: IOException) {          // sin internet
        Result.failure(e)
    } catch (e: HttpException) {        // 404, 500...
        Result.failure(e)
    }

    override suspend fun obtenerDetalle(id: Int): Result<ItemDetalle> = try {
        Result.success(api.obtenerPokemon(id).aDetalle())
    } catch (e: IOException) {
        Result.failure(e)
    } catch (e: HttpException) {
        Result.failure(e)
    }

    override suspend fun alternarFavorito(id: Int) = dao.alternarFavorito(id)

    override suspend fun cantidadGuardada(): Int = dao.contar()

    override suspend fun cantidadFavoritos(): Int = dao.contarFavoritos()

    override suspend fun limpiarCache() = dao.borrarTodo()
}