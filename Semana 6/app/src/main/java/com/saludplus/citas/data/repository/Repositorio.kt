package com.saludplus.citas.data.repository

import com.saludplus.citas.data.model.Especialidad
import com.saludplus.citas.data.model.Medico
import com.saludplus.citas.data.model.Usuario
import com.saludplus.citas.data.model.Cita

object Repositorio {

    // ---------- USUARIOS ----------
    val usuarios = mutableListOf<Usuario>()
    var usuarioActual: Usuario? = null

    fun registrarUsuario(usuario: Usuario): Boolean {
        val existe = usuarios.any { it.correo.equals(usuario.correo, ignoreCase = true) }
        if (existe) return false
        usuarios.add(usuario)
        return true
    }

    fun iniciarSesion(correo: String, contrasena: String): Boolean {
        val usuario = usuarios.find {
            it.correo.equals(correo, ignoreCase = true) && it.contrasena == contrasena
        }
        usuarioActual = usuario
        return usuario != null
    }

    fun cerrarSesion() {
        usuarioActual = null
    }

    // ---------- ESPECIALIDADES ----------
    val especialidades = listOf(
        Especialidad(1, "Medicina General", "Atención integral"),
        Especialidad(2, "Pediatría", "Niños y adolescentes"),
        Especialidad(3, "Ginecología", "Salud de la mujer"),
        Especialidad(4, "Cardiología", "Corazón y vasos sanguíneos"),
        Especialidad(5, "Dermatología", "Piel, cabello y uñas"),
        Especialidad(8, "Traumatología", "Huesos y articulaciones"),
        Especialidad(7, "Oftalmología", "Salud visual"),
        Especialidad(6, "Odontología", "Salud dental y bucal")
    )

    // Filtra por nombre, sin importar mayúsculas
    fun buscarEspecialidades(texto: String): List<Especialidad> {
        return especialidades.filter { it.nombre.contains(texto.trim(), ignoreCase = true) }
    }

    // Las primeras 5 para el LazyRow de Inicio
    fun especialidadesDestacadas(): List<Especialidad> {
        return especialidades.take(5)
    }

    fun obtenerEspecialidad(id: Int): Especialidad? {
        return especialidades.find { it.id == id }
    }

    // ---------- MÉDICOS ----------
    val medicos = listOf(
        Medico(1, "Dra. Ana Torres", 1, 4.8, 12),
        Medico(2, "Dr. Luis Ramírez", 1, 4.5, 8),
        Medico(3, "Dr. Carlos Mendoza", 4, 4.9, 15),
        Medico(4, "Dra. Lucía Vargas", 4, 4.6, 10),
        Medico(5, "Dra. Sofía Paredes", 2, 4.7, 9),
        Medico(6, "Dr. Jorge Salazar", 2, 4.4, 6),
        Medico(7, "Dra. Elena Rojas", 5, 4.8, 11),
        Medico(8, "Dr. Miguel Castro", 6, 4.6, 7),
        Medico(9, "Dra. Patricia Núñez", 7, 4.7, 13),
        Medico(10, "Dra. Valeria Cruz", 3, 4.9, 14),
        Medico(11, "Dr. Andrés Flores", 8, 4.5, 10)
    )

    fun obtenerMedico(id: Int): Medico? {
        return medicos.find { it.id == id }
    }

    // Médicos de una especialidad, del mejor calificado al menos calificado
    fun medicosPorEspecialidad(especialidadId: Int): List<Medico> {
        return medicos
            .filter { it.especialidadId == especialidadId }
            .sortedByDescending { it.calificacion }
    }

    // Igual que la anterior, pero filtrando además por nombre
    fun buscarMedicos(especialidadId: Int, texto: String): List<Medico> {
        return medicosPorEspecialidad(especialidadId)
            .filter { it.nombre.contains(texto.trim(), ignoreCase = true) }
    }

    // ---------- CITAS ----------
    val citas = mutableListOf<Cita>()
    private var siguienteIdCita = 1

    // Horarios que ofrece cada médico todos los días
    val horariosBase = listOf(
        "08:00", "09:00", "10:00", "11:00",
        "14:00", "15:00", "16:00", "17:00"
    )

    // Horarios libres de un médico en una fecha (fecha en formato "2026-10-06")
    fun horariosDisponibles(medicoId: Int, fecha: String): List<String> {
        val ocupados = citas
            .filter { it.medicoId == medicoId && it.fecha == fecha }
            .map { it.hora }
        return horariosBase.filter { it !in ocupados }
    }

    // Crea la cita para el usuario en sesión. Devuelve false si no hay sesión
    // o si ese médico ya tiene una cita en esa fecha y hora.
    fun agendarCita(medicoId: Int, especialidadId: Int, fecha: String, hora: String): Boolean {
        val usuario = usuarioActual ?: return false
        val ocupado = citas.any {
            it.medicoId == medicoId && it.fecha == fecha && it.hora == hora
        }
        if (ocupado) return false
        citas.add(
            Cita(siguienteIdCita++, usuario.correo, medicoId, especialidadId, fecha, hora)
        )
        return true
    }

    fun obtenerCita(id: Int): Cita? {
        return citas.find { it.id == id }
    }

    // Citas del usuario en sesión, de la más próxima a la más lejana
    fun citasDelUsuario(): List<Cita> {
        val usuario = usuarioActual ?: return emptyList()
        return citas
            .filter { it.correoUsuario.equals(usuario.correo, ignoreCase = true) }
            .sortedWith(compareBy({ it.fecha }, { it.hora }))
    }

    // Elimina la cita y libera su horario. Devuelve true si existía.
    fun cancelarCita(id: Int): Boolean {
        return citas.removeIf { it.id == id }
    }
}