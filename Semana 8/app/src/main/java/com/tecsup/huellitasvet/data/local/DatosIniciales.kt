package com.tecsup.huellitasvet.data.local

import com.tecsup.huellitasvet.data.local.entity.Administrador
import com.tecsup.huellitasvet.data.local.entity.Cita
import com.tecsup.huellitasvet.data.local.entity.Mascota
import com.tecsup.huellitasvet.data.local.entity.Servicio
import com.tecsup.huellitasvet.data.local.entity.Usuario
import com.tecsup.huellitasvet.model.Especie
import com.tecsup.huellitasvet.model.EstadoCita
import com.tecsup.huellitasvet.model.Rol
import com.tecsup.huellitasvet.model.Sexo
import java.time.LocalDate

object DatosIniciales {

    // Fecha relativa a hoy: d(0) = hoy, d(-10) = hace 10 días, d(2) = en 2 días
    private fun d(dias: Long): String = LocalDate.now().plusDays(dias).toString()

    suspend fun sembrar(db: VetDatabase) {
        if (db.usuarioDao().contar() > 0) return

        // ===== Servicios (ids 1..4) =====
        db.servicioDao().insertarTodos(
            listOf(
                Servicio(nombre = "Consulta general", descripcion = "Revisión completa de la mascota", duracionMin = 30, precio = 60.0, icono = "heart"),
                Servicio(nombre = "Vacunación", descripcion = "Aplicación de vacunas", duracionMin = 30, precio = 45.0, icono = "syringe"),
                Servicio(nombre = "Baño y corte", descripcion = "Baño, secado y corte de pelo", duracionMin = 60, precio = 50.0, icono = "scissors"),
                Servicio(nombre = "Desparasitación", descripcion = "Control de parásitos", duracionMin = 15, precio = 25.0, activo = false, icono = "heart")
            )
        )

        // ===== Usuarios (ids 1..3) — datos ficticios =====
        db.usuarioDao().insertarTodos(
            listOf(
                Usuario(nombre = "Ana Torres", correo = "ana@correo.test", clave = "1234", rol = Rol.CLIENTE.name),
                Usuario(nombre = "Luis Ramos", correo = "luis@correo.test", clave = "1234", rol = Rol.CLIENTE.name),
                Usuario(nombre = "Dra. Carla Vega", correo = "vet@huellitas.test", clave = "admin", rol = Rol.ADMIN.name)
            )
        )

        // ===== Mascotas (ids 1..4) =====
        db.mascotaDao().insertarTodos(
            listOf(
                Mascota(duenoId = 1, nombre = "Luna", especie = Especie.GATO.name, raza = "Siamesa", anioNacimiento = 2023, peso = 4.3, sexo = Sexo.HEMBRA.name),
                Mascota(duenoId = 1, nombre = "Max", especie = Especie.PERRO.name, raza = "Labrador", anioNacimiento = 2021, peso = 24.5, sexo = Sexo.MACHO.name),
                Mascota(duenoId = 2, nombre = "Rocky", especie = Especie.PERRO.name, raza = "Bulldog", anioNacimiento = 2019, peso = 30.0, sexo = Sexo.MACHO.name),
                Mascota(duenoId = 2, nombre = "Michi", especie = Especie.GATO.name, raza = "Mestizo", anioNacimiento = 2022, peso = 3.8, sexo = Sexo.HEMBRA.name)
            )
        )

        // ===== Citas (11) =====
        val A = EstadoCita.ATENDIDA.name
        val P = EstadoCita.PENDIENTE.name
        val C = EstadoCita.CONFIRMADA.name
        val X = EstadoCita.CANCELADA.name

        db.citaDao().insertarTodos(
            listOf(
                // Pasadas ATENDIDAS — Luna tiene 3 (peso 4.0 → 4.2 → 4.3)
                Cita(mascotaId = 1, servicioId = 3, fecha = d(-150), hora = "15:00", estado = A, precio = 50.0,
                    diagnostico = "Higiene general", tratamiento = "Champú hipoalergénico", pesoRegistrado = 4.0),
                Cita(mascotaId = 1, servicioId = 1, fecha = d(-60), hora = "10:00", estado = A, precio = 60.0,
                    diagnostico = "Control de rutina, sin hallazgos", tratamiento = "Ninguno", pesoRegistrado = 4.2),
                Cita(mascotaId = 1, servicioId = 2, fecha = d(-18), hora = "11:00", estado = A, precio = 45.0,
                    diagnostico = "Vacuna triple felina", tratamiento = "Reposo 24 horas", pesoRegistrado = 4.3),
                Cita(mascotaId = 2, servicioId = 1, fecha = d(-20), hora = "09:30", estado = A, precio = 60.0,
                    diagnostico = "Otitis leve en oído derecho", tratamiento = "Gotas óticas por 7 días", pesoRegistrado = 24.5),
                Cita(mascotaId = 3, servicioId = 2, fecha = d(-15), hora = "10:30", estado = A, precio = 45.0,
                    diagnostico = "Vacuna séxtuple aplicada", tratamiento = "Observación 48 horas", pesoRegistrado = 30.0),

                // Pasada CANCELADA
                Cita(mascotaId = 4, servicioId = 1, fecha = d(-5), hora = "16:00", estado = X, precio = 60.0),

                // Hoy
                Cita(mascotaId = 4, servicioId = 1, fecha = d(0), hora = "16:00", estado = P, precio = 60.0, nota = "Revisar patita"),
                Cita(mascotaId = 3, servicioId = 3, fecha = d(0), hora = "17:00", estado = C, precio = 50.0),

                // Próximos días
                Cita(mascotaId = 1, servicioId = 2, fecha = d(1), hora = "10:30", estado = C, precio = 45.0),
                Cita(mascotaId = 4, servicioId = 1, fecha = d(2), hora = "11:30", estado = C, precio = 60.0),
                Cita(mascotaId = 2, servicioId = 2, fecha = d(5), hora = "09:00", estado = P, precio = 45.0, nota = "Refuerzo de vacuna")
            )
        )

        db.administradorDao().insertarTodos(
            listOf(
                Administrador(codigo = "ADM001", nombre = "Carlos", apellido = "Mendoza", correo = "admin@huellitas.test", clave = "admin123"),
                Administrador(codigo = "ADM002", nombre = "Rosa", apellido = "Quispe", correo = "rosa.admin@huellitas.test", clave = "admin123")
            )
        )
    }
}
