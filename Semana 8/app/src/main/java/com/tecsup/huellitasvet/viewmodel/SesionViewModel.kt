package com.tecsup.huellitasvet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.huellitasvet.data.local.entity.Usuario
import com.tecsup.huellitasvet.data.prefs.PreferenciasStore
import com.tecsup.huellitasvet.data.repository.VetRepository
import com.tecsup.huellitasvet.model.Rol
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SesionViewModel(
    private val repo: VetRepository,
    private val prefs: PreferenciasStore
) : ViewModel() {

    private val _usuarioActual = MutableStateFlow<Usuario?>(null)
    val usuarioActual: StateFlow<Usuario?> = _usuarioActual.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    val modoOscuro: StateFlow<Boolean> = prefs.modoOscuro
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    // Rol con el que se pinta el tema (null = sin sesión)
    val rolActivo: StateFlow<Rol?> = prefs.perfilActivo
        .map { if (it.isBlank()) null else Rol.valueOf(it) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    /** Splash: si hay sesión guardada, la recupera y devuelve su rol */
    suspend fun restaurarSesion(): Rol? {
        val perfil = prefs.perfilActivo.first()
        if (perfil.isBlank()) return null
        val usuario = repo.obtenerUsuario(prefs.usuarioId.first()) ?: return null
        _usuarioActual.value = usuario
        return Rol.valueOf(perfil)
    }

    fun login(correo: String, clave: String, rolSeleccionado: Rol, onExito: (Rol) -> Unit) {
        // Regla 1: campos vacíos
        if (correo.isBlank() || clave.isBlank()) {
            _error.value = "Completa correo y contraseña"
            return
        }
        viewModelScope.launch {
            val usuario = repo.login(correo, clave)
            when {
                // Regla 2: credenciales incorrectas
                usuario == null -> _error.value = "Correo o contraseña incorrectos"

                // Regla 3: el rol no coincide con la pestaña
                usuario.rol != rolSeleccionado.name ->
                    _error.value = "Esta cuenta no pertenece al perfil ${rolSeleccionado.etiqueta}"

                // Éxito
                else -> {
                    _error.value = null
                    _usuarioActual.value = usuario
                    prefs.guardarSesion(rolSeleccionado, usuario.id)
                    onExito(rolSeleccionado)
                }
            }
        }
    }

    fun limpiarError() { _error.value = null }

    fun cambiarModoOscuro(b: Boolean) {
        viewModelScope.launch { prefs.guardarModoOscuro(b) }
    }

    fun cerrarSesion(onListo: () -> Unit) {
        viewModelScope.launch {
            prefs.cerrarSesion()
            _usuarioActual.value = null
            onListo()
        }
    }
}