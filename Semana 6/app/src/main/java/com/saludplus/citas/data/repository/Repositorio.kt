package com.saludplus.citas.data.repository

import com.saludplus.citas.data.model.Especialidad
import com.saludplus.citas.data.model.Medico
import com.saludplus.citas.data.model.Usuario
import com.saludplus.citas.data.model.Cita
import com.saludplus.citas.data.model.Resultado

object Repositorio {

    // Indicador consumible para mostrar el mensaje de bienvenida dinámico una sola vez por inicio de sesión
    var mensajeBienvenidaPendiente = false

    fun consumirMensajeBienvenida(): Boolean {
        if (mensajeBienvenidaPendiente) {
            mensajeBienvenidaPendiente = false
            return true
        }
        return false
    }

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
        if (usuario != null) {
            mensajeBienvenidaPendiente = true
        }
        return usuario != null
    }

    fun cerrarSesion() {
        usuarioActual = null
        mensajeBienvenidaPendiente = false
    }

    // Preferencia de sonido de notificaciones en memoria
    var sonidoNotificacionesHabilitado: Boolean = true

    fun actualizarUsuario(nombre: String, telefono: String): Boolean {
        val actual = usuarioActual ?: return false
        val nombreLimpio = nombre.trim()
        val telefonoLimpio = telefono.trim()
        if (nombreLimpio.isBlank() || telefonoLimpio.isBlank()) return false

        val usuarioActualizado = actual.copy(
            nombre = nombreLimpio,
            telefono = telefonoLimpio
        )

        val idx = usuarios.indexOfFirst { it.correo.equals(actual.correo, ignoreCase = true) }
        if (idx != -1) {
            usuarios[idx] = usuarioActualizado
        } else {
            usuarios.add(usuarioActualizado)
        }
        usuarioActual = usuarioActualizado
        return true
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

    // Retrato de randomuser.me; genero = "men" o "women", numero = 0 a 99
    private fun retrato(genero: String, numero: Int) =
        "https://randomuser.me/api/portraits/$genero/$numero.jpg"

    // ---------- MÉDICOS ----------
    val medicos = listOf(
        Medico(1, "Dra. Ana Torres", 1, 4.8, 12, 128, "Disponible hoy", retrato("women", 44)),
        Medico(2, "Dr. Luis Ramírez", 1, 4.5, 8, 95, "Disponible mañana", retrato("men", 32)),
        Medico(3, "Dr. Carlos Mendoza", 4, 4.9, 15, 210, "Disponible hoy", retrato("men", 45)),
        Medico(4, "Dra. Lucía Vargas", 4, 4.6, 10, 87, "Disponible esta semana", retrato("women", 68)),
        Medico(5, "Dra. Sofía Paredes", 2, 4.7, 9, 142, "Disponible hoy", retrato("women", 65)),
        Medico(6, "Dr. Jorge Salazar", 2, 4.4, 6, 63, "Disponible mañana", retrato("men", 52)),
        Medico(7, "Dra. Elena Rojas", 5, 4.8, 11, 119, "Disponible hoy", retrato("women", 26)),
        Medico(8, "Dr. Miguel Castro", 6, 4.6, 7, 76, "Disponible esta semana", retrato("men", 75)),
        Medico(9, "Dra. Patricia Núñez", 7, 4.7, 13, 134, "Disponible mañana", retrato("women", 33)),
        Medico(10, "Dra. Valeria Cruz", 3, 4.9, 14, 156, "Disponible hoy", retrato("women", 90)),
        Medico(11, "Dr. Andrés Flores", 8, 4.5, 10, 101, "Disponible esta semana", retrato("men", 18)),
        Medico(12, "Dra. Claudia Rojas", 3, 4.8, 9, 112, "Disponible mañana", retrato("women", 17)),
        Medico(13, "Dra. Mariana Soto", 3, 4.6, 7, 70, "Disponible esta semana", retrato("women", 79)),
        Medico(14, "Dr. Ricardo León", 5, 4.5, 8, 64, "Disponible mañana", retrato("men", 61)),
        Medico(15, "Dr. Fernando Ibarra", 6, 4.7, 10, 88, "Disponible hoy", retrato("men", 22)),
        Medico(16, "Dr. Daniel Quispe", 7, 4.4, 6, 59, "Disponible hoy", retrato("men", 86)),
        Medico(17, "Dra. Camila Herrera", 8, 4.8, 12, 97, "Disponible mañana", retrato("women", 12))
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
        "09:00", "09:30", "10:00",
        "10:30", "11:00", "11:30",
        "12:00", "12:30", "13:00"
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

    // ---------- RESULTADOS ----------
    // Lista fija de ejemplo: es la misma para todos los usuarios
    val resultados = listOf(
        Resultado(1, "Hemograma completo", "2026-09-18", 1, "Disponible"),
        Resultado(2, "Perfil lipídico", "2026-09-25", 4, "Disponible"),
        Resultado(3, "Electrocardiograma", "2026-10-01", 4, "Disponible"),
        Resultado(4, "Radiografía de rodilla", "2026-10-02", 8, "En proceso"),
        Resultado(5, "Examen de la vista", "2026-09-10", 7, "Disponible"),
        Resultado(6, "Glucosa en ayunas", "2026-10-03", 1, "En proceso")
    )

    fun obtenerResultado(id: Int): Resultado? {
        return resultados.find { it.id == id }
    }
}
