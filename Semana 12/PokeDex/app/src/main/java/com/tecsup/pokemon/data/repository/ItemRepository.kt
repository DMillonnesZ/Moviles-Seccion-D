package com.tecsup.pokemon.data.repository

import androidx.paging.PagingData
import com.tecsup.pokemon.domain.model.Item
import com.tecsup.pokemon.domain.model.ItemDetalle
import com.tecsup.pokemon.domain.model.OrdenPokemon
import com.tecsup.pokemon.domain.model.Tipo
import kotlinx.coroutines.flow.Flow

interface ItemRepository {
    val items: Flow<List<Item>>
    val favoritos: Flow<List<Item>>
    suspend fun refrescar(): Result<Int>
    suspend fun obtenerDetalle(id: Int): Result<ItemDetalle>
    suspend fun alternarFavorito(id: Int)
    suspend fun cantidadGuardada(): Int
    suspend fun cantidadFavoritos(): Int
    suspend fun limpiarCache()

    // ----- Favoritos por cuenta -----

    // Marca en Room los favoritos de la cuenta logueada.
    // adoptarLocales = si la cuenta aún no tiene lista, se suben los del teléfono (sesión ya abierta)
    suspend fun cargarFavoritosCuenta(adoptarLocales: Boolean)

    // Desmarca los favoritos del teléfono (al cerrar sesión)
    suspend fun limpiarFavoritosLocales()

    val tipos: Flow<List<Tipo>>
    fun itemsPorTipo(tipo: String): Flow<List<Item>>

    // Pokémon de un tipo por páginas, con búsqueda, filtro de favoritos y orden.
    // excluidos = ids descontinuados; tiposExcluidos = tipos descontinuados (sus Pokémon no aparecen)
    fun paginarPorTipo(
        tipo: String,
        texto: String,
        soloFavoritos: Boolean,
        orden: OrdenPokemon,
        excluidos: Set<Int> = emptySet(),
        tiposExcluidos: Set<String> = emptySet()
    ): Flow<PagingData<Item>>
}