package com.saludplus.citas.data.repository

import com.saludplus.citas.data.model.Especialidad
import com.saludplus.citas.data.model.Medico
import com.saludplus.citas.data.model.Usuario

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
        Especialidad(1, "Medicina General", "Consulta y control general de salud"),
        Especialidad(2, "Cardiología", "Corazón y sistema circulatorio"),
        Especialidad(3, "Pediatría", "Atención médica para niños"),
        Especialidad(4, "Dermatología", "Cuidado de la piel, cabello y uñas"),
        Especialidad(5, "Odontología", "Salud dental y bucal"),
        Especialidad(6, "Oftalmología", "Salud visual y ocular"),
        Especialidad(7, "Ginecología", "Salud de la mujer"),
        Especialidad(8, "Traumatología", "Huesos, músculos y articulaciones")
    )

    // Filtra por nombre, sin importar mayúsculas
    fun buscarEspecialidades(texto: String): List<Especialidad> {
        return especialidades.filter { it.nombre.contains(texto.trim(), ignoreCase = true) }
    }

    // Las primeras 4 para el LazyRow de Inicio
    fun especialidadesDestacadas(): List<Especialidad> {
        return especialidades.take(4)
    }

    fun obtenerEspecialidad(id: Int): Especialidad? {
        return especialidades.find { it.id == id }
    }

    // ---------- MÉDICOS ----------
    val medicos = listOf(
        Medico(1, "Dra. Ana Torres", 1, 4.8, 12),
        Medico(2, "Dr. Luis Ramírez", 1, 4.5, 8),
        Medico(3, "Dr. Carlos Mendoza", 2, 4.9, 15),
        Medico(4, "Dra. Lucía Vargas", 2, 4.6, 10),
        Medico(5, "Dra. Sofía Paredes", 3, 4.7, 9),
        Medico(6, "Dr. Jorge Salazar", 3, 4.4, 6),
        Medico(7, "Dra. Elena Rojas", 4, 4.8, 11),
        Medico(8, "Dr. Miguel Castro", 5, 4.6, 7),
        Medico(9, "Dra. Patricia Núñez", 6, 4.7, 13),
        Medico(10, "Dra. Valeria Cruz", 7, 4.9, 14),
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
}