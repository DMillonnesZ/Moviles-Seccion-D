package com.millones.campusgps.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.millones.campusgps.data.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RecuperarUiState(
    val correo: String = "",
    val cargando: Boolean = false,
    val error: String? = null,
    val mensaje: String? = null   // aviso verde cuando el correo se envió
) {
    val correoValido: Boolean get() = PATRON_CORREO.matches(correo.trim())
}

class RecuperarViewModel(private val authRepo: AuthRepository) : ViewModel() {

    private val _estado = MutableStateFlow(RecuperarUiState())
    val estado: StateFlow<RecuperarUiState> = _estado.asStateFlow()

    fun onCorreoChange(valor: String) {
        _estado.update { it.copy(correo = valor, error = null) }
    }

    fun limpiarError() {
        _estado.update { it.copy(error = null) }
    }

    fun limpiarMensaje() {
        _estado.update { it.copy(mensaje = null) }
    }

    fun enviar() {
        val correo = _estado.value.correo.trim()
        val error = when {
            correo.isBlank() -> "Ingresa tu correo"
            !PATRON_CORREO.matches(correo) -> "El correo no es válido"
            else -> null
        }
        if (error != null) {
            _estado.update { it.copy(error = error) }
            return
        }

        viewModelScope.launch {
            _estado.update { it.copy(cargando = true, error = null, mensaje = null) }
            authRepo.recuperarClave(correo)
                .onSuccess {
                    _estado.update { it.copy(cargando = false, mensaje = "Te enviamos un enlace a $correo") }
                }
                .onFailure { e ->
                    _estado.update { it.copy(cargando = false, error = e.message ?: "No se pudo enviar el correo") }
                }
        }
    }
}
