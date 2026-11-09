package com.tecsup.pokemon.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.pokemon.data.auth.AuthRepository
import com.tecsup.pokemon.domain.model.Sesion
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Estado del formulario de login
data class LoginUiState(
    val correo: String = "",
    val clave: String = "",
    val cargando: Boolean = false,
    val error: String? = null,
    val sesion: Sesion? = null      // distinto de null = login exitoso
)

class LoginViewModel(
    private val authRepo: AuthRepository
) : ViewModel() {

    private val _estado = MutableStateFlow(LoginUiState())
    val estado: StateFlow<LoginUiState> = _estado.asStateFlow()

    fun onCorreoChange(valor: String) {
        _estado.update { it.copy(correo = valor, error = null) }
    }

    fun onClaveChange(valor: String) {
        _estado.update { it.copy(clave = valor, error = null) }
    }

    fun iniciarSesion() {
        val actual = _estado.value

        // Validación local: no se llama a Firebase si hay campos vacíos (PA-04)
        if (actual.correo.isBlank() || actual.clave.isBlank()) {
            _estado.update { it.copy(error = "Ingresa tu correo y contraseña") }
            return
        }

        viewModelScope.launch {
            _estado.update { it.copy(cargando = true, error = null) }
            authRepo.iniciarSesion(actual.correo.trim(), actual.clave)
                .onSuccess { sesion ->
                    _estado.update { it.copy(cargando = false, sesion = sesion) }
                }
                .onFailure { e ->
                    _estado.update {
                        it.copy(cargando = false, error = e.message ?: "No se pudo iniciar sesión")
                    }
                }
        }
    }
}