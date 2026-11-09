package com.tecsup.pokemon.ui.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.pokemon.data.auth.AuthRepository
import com.tecsup.pokemon.domain.model.Sesion
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface EstadoSesion {
    data object Verificando : EstadoSesion
    data object SinSesion : EstadoSesion
    data class Activa(val sesion: Sesion) : EstadoSesion
}

class SesionViewModel(
    private val authRepo: AuthRepository
) : ViewModel() {

    private val _estado = MutableStateFlow<EstadoSesion>(EstadoSesion.Verificando)
    val estado: StateFlow<EstadoSesion> = _estado.asStateFlow()

    init {
        // Al abrir la app se revisa si ya había sesión (R11)
        viewModelScope.launch {
            val sesion = authRepo.sesionActual()
            _estado.value = if (sesion == null) EstadoSesion.SinSesion else EstadoSesion.Activa(sesion)
        }
    }

    fun sesionIniciada(sesion: Sesion) {
        _estado.value = EstadoSesion.Activa(sesion)
    }

    fun cerrarSesion() {
        authRepo.cerrarSesion()
        _estado.value = EstadoSesion.SinSesion
    }
}