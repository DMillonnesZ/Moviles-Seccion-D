package com.tecsup.pokemon.data.local

import androidx.paging.PagingSource
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

    // Pokémon de un tipo, por páginas, con sus tipos, búsqueda, filtro de favoritos y orden.
    // excluidos = Pokémon descontinuados por el admin
    // tiposExcluidos = tipos descontinuados: sus Pokémon tampoco se muestran
    @Query(
        """
        SELECT items.*,
            (SELECT GROUP_CONCAT(t2.tipo) FROM item_tipo t2 WHERE t2.itemId = items.id) AS tipos
        FROM items
        INNER JOIN item_tipo ON items.id = item_tipo.itemId
        WHERE item_tipo.tipo = :tipo
          AND items.id NOT IN (:excluidos)
          AND items.id NOT IN (SELECT t3.itemId FROM item_tipo t3 WHERE t3.tipo IN (:tiposExcluidos))
          AND (:texto = '' OR items.nombre LIKE '%' || :texto || '%' OR CAST(items.id AS TEXT) = :texto)
          AND (:soloFavoritos = 0 OR items.esFavorito = 1)
        ORDER BY
          CASE WHEN :orden = 'NUM_ASC' THEN items.id END ASC,
          CASE WHEN :orden = 'NUM_DESC' THEN items.id END DESC,
          CASE WHEN :orden = 'NOMBRE_ASC' THEN items.nombre END ASC,
          CASE WHEN :orden = 'NOMBRE_DESC' THEN items.nombre END DESC
        """
    )
    fun paginarPorTipo(
        tipo: String,
        texto: String,
        soloFavoritos: Boolean,
        orden: String,
        excluidos: List<Int>,
        tiposExcluidos: List<String>
    ): PagingSource<Int, ItemConTipos>

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

    // Tipos con la cantidad de Pokémon de cada uno
    @Query(
        """
        SELECT tipos.*, COUNT(item_tipo.itemId) AS cantidad
        FROM tipos
        LEFT JOIN item_tipo ON tipos.nombre = item_tipo.tipo
        GROUP BY tipos.nombre
        ORDER BY tipos.nombreEs
        """
    )
    fun tiposConCantidad(): Flow<List<TipoConCantidad>>

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