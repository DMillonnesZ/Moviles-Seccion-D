package com.millones.campusgps.data

import com.millones.campusgps.data.local.LugarDao
import com.millones.campusgps.data.local.LugarEntity
import com.millones.campusgps.data.local.aModelo
import com.millones.campusgps.model.Lugar
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Lugares en Room y sus fotos como archivos internos (Room solo guarda la ruta).
// Todo vive en este celular: no usa Firebase Storage (pide el plan Blaze de pago).
class LugaresRepositoryRoom(
    private val dao: LugarDao,
    private val fotos: AlmacenFotos
) : LugaresRepository {

    override val lugares: Flow<List<Lugar>> =
        dao.observarTodos().map { lista -> lista.map { it.aModelo() } }

    override suspend fun guardar(
        nombre: String,
        descripcion: String,
        categoria: String,
        latitud: Double,
        longitud: Double,
        fotoUri: String?
    ): Result<Unit> = runCatching {
        val ruta = fotoUri?.let { fotos.guardar(it, "lugar") }
        dao.insertar(
            LugarEntity(
                nombre = nombre,
                descripcion = descripcion,
                categoria = categoria,
                latitud = latitud,
                longitud = longitud,
                foto = ruta
            )
        )
    }.map { }   // el id que devuelve Room no se usa

    override suspend fun alternarFavorito(id: Int) {
        dao.alternarFavorito(id)
    }

    override suspend fun cambiarFoto(id: Int, fotoUri: String): Result<Unit> = runCatching {
        val anterior = dao.obtener(id)?.foto
        val nueva = fotos.guardar(fotoUri, "lugar_$id")
        dao.actualizarFoto(id, nueva)
        fotos.borrar(anterior)
    }

    override suspend fun eliminar(id: Int) {
        val foto = dao.obtener(id)?.foto
        dao.eliminar(id)
        fotos.borrar(foto)
    }
}
