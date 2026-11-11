package com.tecsup.pokemon.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface EvolucionDao {

    // Todas las etapas, agrupables por cadena y en su orden
    @Query("SELECT * FROM etapas_cadena ORDER BY cadenaId, orden")
    fun etapas(): Flow<List<EtapaCadenaEntity>>

    // Tipos de cada Pokémon guardado (para el color de los medallones)
    @Query("SELECT * FROM item_tipo")
    fun relacionesTipo(): Flow<List<ItemTipoEntity>>

    @Query("SELECT COUNT(DISTINCT cadenaId) FROM etapas_cadena")
    suspend fun contarCadenas(): Int

    @Upsert
    suspend fun guardar(etapas: List<EtapaCadenaEntity>)

    @Query("DELETE FROM etapas_cadena")
    suspend fun borrar()

    // Reemplaza todas las cadenas: o se guarda todo o nada
    @Transaction
    suspend fun reemplazarTodo(etapas: List<EtapaCadenaEntity>) {
        borrar()
        guardar(etapas)
    }
}