package com.millones.campusgps.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.millones.campusgps.data.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginUiState(
    val correo: String = "",
    val clave: String = "",
    val cargando: Boolean = false,
    val cargandoGoogle: Boolean = false,
    val error: String? = null,
    val exito: Boolean = false,  // true = la navegación lleva al mapa
    val nombre: String? = null   // para el saludo de la bienvenida
) {
    // Check verde en el campo de correo
    val correoValido: Boolean get() = PATRON_CORREO.matches(correo.trim())
}

class LoginViewModel(private val authRepo: AuthRepository) : ViewModel() {

    private val _estado = MutableStateFlow(LoginUiState())
    val estado: StateFlow<LoginUiState> = _estado.asStateFlow()

    fun onCorreoChange(valor: String) {
        _estado.update { it.copy(correo = valor, error = null) }
    }

    fun onClaveChange(valor: String) {
        _estado.update { it.copy(clave = valor, error = null) }
    }

    // La pantalla lo llama cuando el aviso termina de mostrarse
    fun limpiarError() {
        _estado.update { it.copy(error = null) }
    }

    // Error al pedir la cuenta de Google (sin cuentas, sin configurar...)
    fun errorGoogle(mensaje: String) {
        _estado.update { it.copy(cargandoGoogle = false, error = mensaje) }
    }

    // La pantalla obtiene el token con Credential Manager (necesita la Activity);
    // el ViewModel solo lo pasa al repositorio, sin tocar Firebase ni Context
    fun entrarConGoogle(idToken: String) {
        viewModelScope.launch {
            _estado.update { it.copy(cargandoGoogle = true, error = null) }
            authRepo.iniciarSesionConGoogle(idToken)
                .onSuccess {
                    _estado.update {
                        it.copy(cargandoGoogle = false, exito = true, nombre = authRepo.nombreActivo.value)
                    }
                }
                .onFailure { e ->
                    _estado.update { it.copy(cargandoGoogle = false, error = e.message ?: "No se pudo entrar con Google") }
                }
        }
    }

    fun iniciarSesion() {
        val actual = _estado.value
        val error = when {
            actual.correo.isBlank() || actual.clave.isBlank() -> "Completa correo y contraseña"
            !PATRON_CORREO.matches(actual.correo.trim()) -> "El correo no es válido"
            else -> null
        }
        if (error != null) {
            _estado.update { it.copy(error = error) }
            return
        }

        viewModelScope.launch {
            _estado.update { it.copy(cargando = true, error = null) }
            authRepo.iniciarSesion(actual.correo.trim(), actual.clave)
                .onSuccess {
                    _estado.update {
                        it.copy(cargando = false, exito = true, nombre = authRepo.nombreActivo.value)
                    }
                }
                .onFailure { e ->
                    _estado.update { it.copy(cargando = false, error = e.message ?: "No se pudo iniciar sesión") }
                }
        }
    }
}

// Compartido por los formularios de acceso
internal val PATRON_CORREO = Regex("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")
