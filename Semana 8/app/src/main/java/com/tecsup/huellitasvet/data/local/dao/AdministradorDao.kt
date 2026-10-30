package com.tecsup.huellitasvet.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.tecsup.huellitasvet.data.local.entity.Administrador
import kotlinx.coroutines.flow.Flow

@Dao
interface AdministradorDao {
    @Query("SELECT * FROM administradores WHERE correo = :correo AND clave = :clave LIMIT 1")
    suspend fun login(correo: String, clave: String): Administrador?

    @Query("SELECT * FROM administradores WHERE id = :id")
    suspend fun obtener(id: Int): Administrador?

    @Query("SELECT * FROM administradores ORDER BY apellido")
    fun observarTodos(): Flow<List<Administrador>>

    @Insert
    suspend fun insertarTodos(lista: List<Administrador>)

    @Query("SELECT COUNT(*) FROM administradores")
    suspend fun contar(): Int
}
