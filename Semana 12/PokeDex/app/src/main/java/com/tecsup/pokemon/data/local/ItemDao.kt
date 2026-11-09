package com.tecsup.pokemon.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemDao {

    // ----- Pokémon -----

    @Query("SELECT * FROM items ORDER BY id")
    fun todos(): Flow<List<ItemEntity>>

    @Query("SELECT * FROM items WHERE esFavorito = 1 ORDER BY id")
    fun favoritos(): Flow<List<ItemEntity>>

    @Query(
        """
        SELECT items.* FROM items
        INNER JOIN item_tipo ON items.id = item_tipo.itemId
        WHERE item_tipo.tipo = :tipo
        ORDER BY items.id
        """
    )
    fun porTipo(tipo: String): Flow<List<ItemEntity>>

    @Query("SELECT id FROM items WHERE esFavorito = 1")
    suspend fun idsFavoritos(): List<Int>

    @Query("SELECT COUNT(*) FROM items")
    suspend fun contar(): Int

    @Query("SELECT COUNT(*) FROM items WHERE esFavorito = 1")
    suspend fun contarFavoritos(): Int

    @Query("UPDATE items SET esFavorito = NOT esFavorito WHERE id = :id")
    suspend fun alternarFavorito(id: Int)

    // ----- Tipos -----

    @Query("SELECT * FROM tipos ORDER BY nombreEs")
    fun tipos(): Flow<List<TipoEntity>>

    // ----- Guardar -----

    @Upsert
    suspend fun guardarItems(items: List<ItemEntity>)

    @Upsert
    suspend fun guardarTipos(tipos: List<TipoEntity>)

    @Upsert
    suspend fun guardarRelaciones(relaciones: List<ItemTipoEntity>)

    // Guarda todo junto: o se guarda todo o nada
    @Transaction
    suspend fun guardarTodo(
        items: List<ItemEntity>,
        tipos: List<TipoEntity>,
        relaciones: List<ItemTipoEntity>
    ) {
        guardarItems(items)
        guardarTipos(tipos)
        guardarRelaciones(relaciones)
    }

    // ----- Limpiar caché -----

    @Query("DELETE FROM items")
    suspend fun borrarItems()

    @Query("DELETE FROM tipos")
    suspend fun borrarTipos()

    @Query("DELETE FROM item_tipo")
    suspend fun borrarRelaciones()

    @Transaction
    suspend fun borrarTodo() {
        borrarRelaciones()
        borrarTipos()
        borrarItems()
    }
}