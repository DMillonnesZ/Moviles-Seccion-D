package com.tecsup.pokemon.ui.registro

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.pokemon.data.auth.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Estado del formulario de registro
data class RegistroUiState(
    val nombre: String = "",
    val correo: String = "",
    val clave: String = "",
    val confirmar: String = "",
    val fuerzaClave: Int = 0,          // 0 vacía, 1 débil, 2 media, 3 fuerte
    val cargando: Boolean = false,
    val error: String? = null,
    val cuentaCreada: Boolean = false  // true = mostrar la celebración
)

class RegistroViewModel(
    private val authRepo: AuthRepository
) : ViewModel() {

    private val _estado = MutableStateFlow(RegistroUiState())
    val estado: StateFlow<RegistroUiState> = _estado.asStateFlow()

    private val patronCorreo = Regex("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")

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

        // Validaciones locales antes de llamar a Firebase
        val error = when {
            actual.nombre.isBlank() || actual.correo.isBlank() ||
                    actual.clave.isBlank() || actual.confirmar.isBlank() -> "Completa todos los campos"
            !patronCorreo.matches(actual.correo.trim()) -> "El correo no es válido"
            actual.clave.length < 6 -> "Mínimo 6 caracteres en la clave"
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
                    _estado.update {
                        it.copy(cargando = false, error = e.message ?: "No se pudo crear la cuenta")
                    }
                }
        }
    }

    // Suma 1 punto por cada criterio cumplido y lo convierte a nivel 1, 2 o 3
    private fun calcularFuerza(clave: String): Int {
        if (clave.isEmpty()) return 0
        var puntos = 0
        if (clave.length >= 6) puntos++
        if (clave.length >= 10) puntos++
        if (clave.any { it.isUpperCase() } && clave.any { it.isLowerCase() }) puntos++
        if (clave.any { it.isDigit() }) puntos++
        if (clave.any { !it.isLetterOrDigit() }) puntos++
        return when {
            puntos <= 1 -> 1     // débil
            puntos <= 3 -> 2     // media
            else -> 3            // fuerte
        }
    }
}