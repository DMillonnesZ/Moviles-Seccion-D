package com.tecsup.pokemon.ui.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.pokemon.data.auth.AuthRepository
import com.tecsup.pokemon.data.auth.CuentaSuspendidaException
import com.tecsup.pokemon.data.repository.ItemRepository
import com.tecsup.pokemon.domain.model.Sesion
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface EstadoSesion {
    data object Verificando : EstadoSesion
    data object SinSesion : EstadoSesion
    data class Activa(val sesion: Sesion) : EstadoSesion
}

// Aviso de cuenta suspendida (al abrir la app o con la sesión abierta)
data class AvisoSuspension(val motivo: String?)

class SesionViewModel(
    private val authRepo: AuthRepository,
    private val itemRepo: ItemRepository
) : ViewModel() {

    private val _estado = MutableStateFlow<EstadoSesion>(EstadoSesion.Verificando)
    val estado: StateFlow<EstadoSesion> = _estado.asStateFlow()

    // Distinto de null = mostrar el aviso de cuenta suspendida
    private val _suspension = MutableStateFlow<AvisoSuspension?>(null)
    val suspension: StateFlow<AvisoSuspension?> = _suspension.asStateFlow()

    // Escucha la cuenta mientras hay sesión, por si un admin la suspende
    private var vigilancia: Job? = null

    init {
        // Al abrir la app se revisa si ya había sesión (R11)
        viewModelScope.launch {
            try {
                val sesion = authRepo.sesionActual()
                if (sesion == null) {
                    _estado.value = EstadoSesion.SinSesion
                    itemRepo.limpiarFavoritosLocales()
                } else {
                    _estado.value = EstadoSesion.Activa(sesion)
                    vigilarCuenta()
                    // Sesión que ya estaba abierta: si la cuenta no tiene lista, adopta la del teléfono
                    itemRepo.cargarFavoritosCuenta(adoptarLocales = true)
                }
            } catch (e: CuentaSuspendidaException) {
                // La suspendieron mientras la app estaba cerrada
                _suspension.value = AvisoSuspension(e.motivo)
                _estado.value = EstadoSesion.SinSesion
                itemRepo.limpiarFavoritosLocales()
            }
        }
    }

    fun sesionIniciada(sesion: Sesion) {
        _estado.value = EstadoSesion.Activa(sesion)
        vigilarCuenta()
        // Cada cuenta ve solo sus favoritos
        viewModelScope.launch { itemRepo.cargarFavoritosCuenta(adoptarLocales = false) }
    }

    fun cerrarSesion() {
        vigilancia?.cancel()
        vigilancia = null
        authRepo.cerrarSesion()
        _estado.value = EstadoSesion.SinSesion
        // El siguiente usuario no ve los favoritos del anterior
        viewModelScope.launch { itemRepo.limpiarFavoritosLocales() }
    }

    fun avisoSuspensionVisto() {
        _suspension.value = null
    }

    // Si el documento del usuario pasa a activo = false, se cierra la sesión con aviso
    private fun vigilarCuenta() {
        vigilancia?.cancel()
        vigilancia = viewModelScope.launch {
            authRepo.perfil().collect { perfil ->
                if (perfil != null && !perfil.activo) {
                    _suspension.value = AvisoSuspension(perfil.motivoSuspension)
                    cerrarSesion()
                }
            }
        }
    }
}