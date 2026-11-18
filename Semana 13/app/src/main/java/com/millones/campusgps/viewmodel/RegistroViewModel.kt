package com.millones.campusgps.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.millones.campusgps.data.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RegistroUiState(
    val nombre: String = "",
    val correo: String = "",
    val clave: String = "",
    val confirmar: String = "",
    val fuerzaClave: Int = 0,          // 0 vacía, 1 débil, 2 media, 3 fuerte
    val cargando: Boolean = false,
    val error: String? = null,
    val cuentaCreada: Boolean = false  // true = mostrar la celebración
) {
    // Checks verdes de cada campo (las mismas reglas que valida registrar())
    val nombreValido: Boolean get() = nombre.trim().length >= 2
    val correoValido: Boolean get() = PATRON_CORREO.matches(correo.trim())
    val claveValida: Boolean get() = clave.length >= 6
    val clavesCoinciden: Boolean get() = confirmar.isNotEmpty() && confirmar == clave
}

class RegistroViewModel(private val authRepo: AuthRepository) : ViewModel() {

    private val _estado = MutableStateFlow(RegistroUiState())
    val estado: StateFlow<RegistroUiState> = _estado.asStateFlow()

    fun onNombreChange(valor: String) {
        _estado.update { it.copy(nombre = valor, error = null) }
    }

    fun onCorreoChange(valor: String) {
        _estado.update { it.copy(correo = valor, error = null) }
    }

    fun onClaveChange(valor: String) {
        _estado.update { it.copy(clave = valor, fuerzaClave = calcularFuerza(valor), error = null) }
    }

    fun onConfirmarChange(valor: String) {
        _estado.update { it.copy(confirmar = valor, error = null) }
    }

    fun limpiarError() {
        _estado.update { it.copy(error = null) }
    }

    fun registrar() {
        val actual = _estado.value
        val error = when {
            actual.nombre.isBlank() || actual.correo.isBlank() ||
                    actual.clave.isBlank() || actual.confirmar.isBlank() -> "Completa todos los campos"
            !PATRON_CORREO.matches(actual.correo.trim()) -> "El correo no es válido"
            actual.clave.length < 6 -> "Mínimo 6 caracteres en la contraseña"
            actual.clave != actual.confirmar -> "Las contraseñas no coinciden"
            else -> null
        }
        if (error != null) {
            _estado.update { it.copy(error = error) }
            return
        }

        viewModelScope.launch {
            _estado.update { it.copy(cargando = true, error = null) }
            authRepo.registrar(actual.nombre.trim(), actual.correo.trim(), actual.clave)
                .onSuccess {
                    // Firebase deja la sesión abierta al crear la cuenta:
                    // se cierra para que el usuario entre por el login
                    authRepo.cerrarSesion()
                    _estado.update { it.copy(cargando = false, cuentaCreada = true) }
                }
                .onFailure { e ->
                    _estado.update { it.copy(cargando = false, error = e.message ?: "No se pudo crear la cuenta") }
                }
        }
    }

    // 1 punto por criterio cumplido, convertido a nivel 1, 2 o 3
    private fun calcularFuerza(clave: String): Int {
        if (clave.isEmpty()) return 0
        var puntos = 0
        if (clave.length >= 6) puntos++
        if (clave.length >= 10) puntos++
        if (clave.any { it.isUpperCase() } && clave.any { it.isLowerCase() }) puntos++
        if (clave.any { it.isDigit() }) puntos++
        if (clave.any { !it.isLetterOrDigit() }) puntos++
        return when {
            puntos <= 1 -> 1
            puntos <= 3 -> 2
            else -> 3
        }
    }
}
