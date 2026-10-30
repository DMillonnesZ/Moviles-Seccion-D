package com.tecsup.huellitasvet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.huellitasvet.data.local.entity.Administrador
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

    private val _adminActual = MutableStateFlow<Administrador?>(null)
    val adminActual: StateFlow<Administrador?> = _adminActual.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    // ===== Preferencias (DataStore) =====
    val modoOscuro: StateFlow<Boolean> = prefs.modoOscuro
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)
    val notificaciones: StateFlow<Boolean> = prefs.notificaciones
        .stateIn(viewModelScope, SharingStarted.Eagerly, true)
    val aceptarCitas: StateFlow<Boolean> = prefs.aceptarCitas
        .stateIn(viewModelScope, SharingStarted.Eagerly, true)

    /** Rol con el que se pinta el tema (null = sin sesión) */
    val rolActivo: StateFlow<Rol?> = prefs.perfilActivo
        .map { if (it.isBlank()) null else Rol.valueOf(it) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    /** Splash: si hay sesión guardada, la recupera y devuelve su rol */
    suspend fun restaurarSesion(): Rol? {
        val perfil = prefs.perfilActivo.first()
        if (perfil.isBlank()) return null
        val id = prefs.usuarioId.first()
        return if (perfil == Rol.ADMINISTRADOR.name) {
            val admin = repo.obtenerAdministrador(id) ?: return null
            _adminActual.value = admin
            _usuarioActual.value = null
            Rol.ADMINISTRADOR
        } else {
            val usuario = repo.obtenerUsuario(id) ?: return null
            _usuarioActual.value = usuario
            _adminActual.value = null
            Rol.valueOf(perfil)
        }
    }

    fun login(correo: String, clave: String, rolSeleccionado: Rol, onExito: (Rol) -> Unit) {
        if (correo.isBlank() || clave.isBlank()) {
            _error.value = "Completa correo y contraseña"
            return
        }
        viewModelScope.launch {
            if (rolSeleccionado == Rol.ADMINISTRADOR) {
                val admin = repo.loginAdministrador(correo, clave)
                if (admin == null) {
                    _error.value = "Correo o contraseña incorrectos"
                } else {
                    _error.value = null
                    _adminActual.value = admin
                    _usuarioActual.value = null
                    prefs.guardarSesion(Rol.ADMINISTRADOR, admin.id)
                    onExito(Rol.ADMINISTRADOR)
                }
            } else {
                val usuario = repo.login(correo, clave)
                when {
                    usuario == null -> _error.value = "Correo o contraseña incorrectos"
                    usuario.rol != rolSeleccionado.name ->
                        _error.value = "Esta cuenta no pertenece al perfil ${rolSeleccionado.etiqueta}"
                    else -> {
                        _error.value = null
                        _usuarioActual.value = usuario
                        _adminActual.value = null
                        prefs.guardarSesion(rolSeleccionado, usuario.id)
                        onExito(rolSeleccionado)
                    }
                }
            }
        }
    }

    fun limpiarError() { _error.value = null }

    fun cambiarModoOscuro(b: Boolean) { viewModelScope.launch { prefs.guardarModoOscuro(b) } }
    fun cambiarNotificaciones(b: Boolean) { viewModelScope.launch { prefs.guardarNotificaciones(b) } }
    fun cambiarAceptarCitas(b: Boolean) { viewModelScope.launch { prefs.guardarAceptarCitas(b) } }

    /** Modo práctica: guarda el OTRO perfil y avisa para navegar a su grafo */
    fun cambiarPerfil(onListo: (Rol) -> Unit) {
        viewModelScope.launch {
            val actual = rolActivo.value ?: return@launch
            if (actual == Rol.ADMINISTRADOR) return@launch
            val otro = if (actual == Rol.CLIENTE) Rol.ADMIN else Rol.CLIENTE
            val usuario = repo.obtenerUsuarioPorRol(otro) ?: return@launch
            prefs.guardarSesion(otro, usuario.id)
            _usuarioActual.value = usuario
            _adminActual.value = null
            onListo(otro)
        }
    }

    fun cerrarSesion(onListo: () -> Unit) {
        viewModelScope.launch {
            prefs.cerrarSesion()
            _usuarioActual.value = null
            _adminActual.value = null
            onListo()
        }
    }
}
