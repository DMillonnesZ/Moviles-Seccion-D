package com.millones.campusgps.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface LugarDao {

    // Flow: la lista se actualiza sola cuando se guarda, edita o borra un lugar
    @Query("SELECT * FROM lugares ORDER BY creado_en DESC")
    fun observarTodos(): Flow<List<LugarEntity>>

    @Query("SELECT * FROM lugares WHERE id = :id")
    suspend fun obtener(id: Int): LugarEntity?

    @Insert
    suspend fun insertar(lugar: LugarEntity): Long

    @Query("UPDATE lugares SET favorito = NOT favorito WHERE id = :id")
    suspend fun alternarFavorito(id: Int)

    @Query("UPDATE lugares SET foto = :ruta WHERE id = :id")
    suspend fun actualizarFoto(id: Int, ruta: String)

    @Query("DELETE FROM lugares WHERE id = :id")
    suspend fun eliminar(id: Int)
}
