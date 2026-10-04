package com.saludplus.citas.data.repository

import com.saludplus.citas.data.model.Usuario

object Repositorio {

    val usuarios = mutableListOf<Usuario>()
    var usuarioActual: Usuario? = null

    // Devuelve false si el correo ya está registrado
    fun registrarUsuario(usuario: Usuario): Boolean {
        val existe = usuarios.any { it.correo.equals(usuario.correo, ignoreCase = true) }
        if (existe) return false
        usuarios.add(usuario)
        return true
    }

    // Busca el usuario por correo y contraseña; guarda la sesión si lo encuentra
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
}