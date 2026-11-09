package com.tecsup.pokemon.data.repository

import com.tecsup.pokemon.data.mapper.aDetalle
import com.tecsup.pokemon.data.mapper.aItem
import com.tecsup.pokemon.data.mapper.aTipo
import com.tecsup.pokemon.data.mapper.idDesdeUrl
import com.tecsup.pokemon.data.remote.ApiService
import com.tecsup.pokemon.domain.model.Item
import com.tecsup.pokemon.domain.model.ItemDetalle
import com.tecsup.pokemon.domain.model.Tipo
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import retrofit2.HttpException
import java.io.IOException

// Temporal: guarda en memoria. En el H9 se reemplaza por Room.
class ItemRepositoryImpl(
    private val api: ApiService
) : ItemRepository {

    // Ids >= 10000 son formas especiales (megas, gigamax...)
    private val idMaximo = 10000

    private val _items = MutableStateFlow<List<Item>>(emptyList())
    private val _tipos = MutableStateFlow<List<Tipo>>(emptyList())
    private val _idsPorTipo = MutableStateFlow<Map<String, Set<Int>>>(emptyMap())
    private val _idsFavoritos = MutableStateFlow<Set<Int>>(emptySet())

    override val items: Flow<List<Item>> =
        combine(_items, _idsFavoritos) { lista, favoritos ->
            lista.map { it.copy(esFavorito = it.id in favoritos) }
        }

    override val favoritos: Flow<List<Item>> =
        items.map { lista -> lista.filter { it.esFavorito } }

    override val tipos: Flow<List<Tipo>> = _tipos

    override fun itemsPorTipo(tipo: String): Flow<List<Item>> =
        combine(items, _idsPorTipo) { lista, mapa ->
            val ids = mapa[tipo].orEmpty()
            lista.filter { it.id in ids }
        }

    override suspend fun refrescar(): Result<Int> = try {
        // 1. Nombres de todos los tipos
        val nombres = api.listarTipos().results.map { it.name }

        // 2. Detalle de cada tipo, en paralelo; se descartan los tipos vacíos
        val detalles = coroutineScope {
            nombres.map { nombre -> async { api.listarPorTipo(nombre) } }.awaitAll()
        }.filter { it.pokemon.isNotEmpty() }

        // 3. Pokémon únicos, sin formas especiales
        val pokemon = detalles
            .flatMap { tipo -> tipo.pokemon.map { it.pokemon.aItem() } }
            .filter { it.id < idMaximo }
            .distinctBy { it.id }
            .sortedBy { it.id }

        // 4. Qué Pokémon pertenecen a cada tipo
        val idsPorTipo = detalles.associate { tipo ->
            tipo.name to tipo.pokemon.map { idDesdeUrl(it.pokemon.url) }
                .filter { it < idMaximo }
                .toSet()
        }

        _tipos.value = detalles.map { it.aTipo() }
        _idsPorTipo.value = idsPorTipo
        _items.value = pokemon
        Result.success(pokemon.size)
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

    override suspend fun alternarFavorito(id: Int) {
        _idsFavoritos.update { if (id in it) it - id else it + id }
    }

    override suspend fun cantidadGuardada(): Int = _items.value.size

    override suspend fun cantidadFavoritos(): Int = _idsFavoritos.value.size

    override suspend fun limpiarCache() {
        _items.value = emptyList()
        _tipos.value = emptyList()
        _idsPorTipo.value = emptyMap()
    }
}