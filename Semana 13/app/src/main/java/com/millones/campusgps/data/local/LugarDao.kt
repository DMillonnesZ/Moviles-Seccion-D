package com.millones.campusgps.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
abstract class LugarDao {

    // Flow: la lista se actualiza sola cada vez que llega un cambio de Firestore
    @Query("SELECT * FROM lugares ORDER BY creado_en DESC")
    abstract fun observarTodos(): Flow<List<LugarEntity>>

    @Query("SELECT * FROM lugares WHERE id = :id")
    abstract suspend fun obtener(id: String): LugarEntity?

    @Upsert
    abstract suspend fun guardarTodos(lista: List<LugarEntity>)

    @Query("DELETE FROM lugares WHERE id NOT IN (:ids)")
    abstract suspend fun borrarExcepto(ids: List<String>)

    @Query("DELETE FROM lugares")
    abstract suspend fun borrarTodo()

    // La caché queda igual a lo que hay en Firestore (los borrados también desaparecen)
    @Transaction
    open suspend fun reemplazarTodo(lista: List<LugarEntity>) {
        if (lista.isEmpty()) {
            borrarTodo()
        } else {
            borrarExcepto(lista.map { it.id })
            guardarTodos(lista)
        }
    }
}
