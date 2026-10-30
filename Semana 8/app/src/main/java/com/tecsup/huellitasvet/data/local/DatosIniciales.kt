package com.tecsup.huellitasvet.data.local

import com.tecsup.huellitasvet.data.local.entity.Servicio

object DatosIniciales {

    suspend fun sembrar(db: VetDatabase) {
        // TODO Paso 3: cambiar por usuarioDao().contar() == 0
        if (db.servicioDao().contar() > 0) return

        db.servicioDao().insertarTodos(
            listOf(
                Servicio(nombre = "Consulta general", descripcion = "Revisión completa de la mascota", duracionMin = 30, precio = 60.0, icono = "heart"),
                Servicio(nombre = "Vacunación", descripcion = "Aplicación de vacunas", duracionMin = 30, precio = 45.0, icono = "syringe"),
                Servicio(nombre = "Baño y corte", descripcion = "Baño, secado y corte de pelo", duracionMin = 60, precio = 50.0, icono = "scissors"),
                Servicio(nombre = "Desparasitación", descripcion = "Control de parásitos", duracionMin = 15, precio = 25.0, activo = false, icono = "heart")
            )
        )
        // TODO Paso 3: usuarios, mascotas y ~10 citas
    }
}