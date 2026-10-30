package com.tecsup.huellitasvet.data.local.dao

import androidx.room.*
import com.tecsup.huellitasvet.data.local.entity.Mascota
import com.tecsup.huellitasvet.data.local.relation.MascotaConDueno
import kotlinx.coroutines.flow.Flow

@Dao
interface MascotaDao {
    // ===== Escritura =====
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(m: Mascota): Long

    @Insert
    suspend fun insertarTodos(lista: List<Mascota>)

    @Update
    suspend fun actualizar(m: Mascota)

    @Delete
    suspend fun eliminar(m: Mascota)

    @Query("UPDATE mascotas SET peso = :peso WHERE id = :id")
    suspend fun actualizarPeso(id: Int, peso: Double)

    // ===== Lectura =====
    @Query("SELECT * FROM mascotas WHERE duenoId = :duenoId ORDER BY nombre")
    fun observarPorDueno(duenoId: Int): Flow<List<Mascota>>

    @Query("SELECT * FROM mascotas WHERE id = :id")
    fun observarUna(id: Int): Flow<Mascota?>

    @Transaction
    @Query("SELECT * FROM mascotas ORDER BY nombre")
    fun observarTodas(): Flow<List<MascotaConDueno>>

    @Query("SELECT COUNT(*) FROM citas WHERE mascotaId = :mascotaId")
    suspend fun contarCitas(mascotaId: Int): Int
}