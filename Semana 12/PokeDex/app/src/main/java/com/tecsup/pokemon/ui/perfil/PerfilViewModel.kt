package com.tecsup.pokemon.ui.perfil

import android.content.ContentResolver
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.pokemon.data.auth.AuthRepository
import com.tecsup.pokemon.domain.model.PerfilUsuario
import com.tecsup.pokemon.util.ImagenPerfil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// Largo máximo del nombre de entrenador
const val LARGO_MAXIMO_NOMBRE = 24

// Formulario para cambiar la contraseña
data class ClaveUiState(
    val actual: String = "",
    val nueva: String = "",
    val confirmar: String = "",
    val fuerza: Int = 0,            // 0 vacía, 1 débil, 2 media, 3 fuerte
    val cargando: Boolean = false,
    val error: String? = null,
    val cambiada: Boolean = false   // true = cerrar la ventana
)

class PerfilViewModel(
    private val authRepo: AuthRepository
) : ViewModel() {

    // Perfil en vivo: Firestore avisa cada cambio
    val perfil: StateFlow<PerfilUsuario?> = authRepo.perfil()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    private val _guardandoFoto = MutableStateFlow(false)
    val guardandoFoto: StateFlow<Boolean> = _guardandoFoto.asStateFlow()

    private val _guardandoNombre = MutableStateFlow(false)
    val guardandoNombre: StateFlow<Boolean> = _guardandoNombre.asStateFlow()

    // Aviso corto (Toast) después de guardar
    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje.asStateFlow()

    private val _clave = MutableStateFlow(ClaveUiState())
    val clave: StateFlow<ClaveUiState> = _clave.asStateFlow()

    // ----- Foto -----

    fun cambiarFoto(resolver: ContentResolver, uri: Uri) {
        if (_guardandoFoto.value) return
        viewModelScope.launch {
            _guardandoFoto.value = true
            // Leer y comprimir la imagen fuera del hilo principal
            val foto = withContext(Dispatchers.IO) {
                runCatching { ImagenPerfil.aBase64(ImagenPerfil.leer(resolver, uri)) }.getOrNull()
            }
            if (foto == null) {
                _mensaje.value = "No se pudo leer la imagen"
            } else {
                authRepo.actualizarFoto(foto)
                    .onSuccess { _mensaje.value = "¡Foto actualizada!" }
                    .onFailure { e -> _mensaje.value = e.message ?: "No se pudo guardar la foto" }
            }
            _guardandoFoto.value = false
        }
    }

    fun quitarFoto() {
        if (_guardandoFoto.value) return
        viewModelScope.launch {
            _guardandoFoto.value = true
            authRepo.actualizarFoto(null)
                .onSuccess { _mensaje.value = "Foto eliminada" }
                .onFailure { e -> _mensaje.value = e.message ?: "No se pudo quitar la foto" }
            _guardandoFoto.value = false
        }
    }

    // ----- Nombre -----

    fun guardarNombre(nombre: String, alGuardar: () -> Unit) {
        val limpio = nombre.trim()
        if (limpio.isBlank()) {
            _mensaje.value = "Escribe un nombre"
            return
        }
        if (limpio.length > LARGO_MAXIMO_NOMBRE) {
            _mensaje.value = "Máximo $LARGO_MAXIMO_NOMBRE caracteres"
            return
        }
        // Sin cambios: solo cerrar
        if (limpio == perfil.value?.nombre) {
            alGuardar()
            return
        }
        viewModelScope.launch {
            _guardandoNombre.value = true
            authRepo.actualizarNombre(limpio)
                .onSuccess {
                    _mensaje.value = "Nombre actualizado"
                    alGuardar()
                }
                .onFailure { e -> _mensaje.value = e.message ?: "No se pudo guardar el nombre" }
            _guardandoNombre.value = false
        }
    }

    // ----- Contraseña -----

    fun onActualChange(valor: String) {
        _clave.update { it.copy(actual = valor, error = null) }
    }

    fun onNuevaChange(valor: String) {
        _clave.update { it.copy(nueva = valor, fuerza = calcularFuerza(valor), error = null) }
    }

    fun onConfirmarChange(valor: String) {
        _clave.update { it.copy(confirmar = valor, error = null) }
    }

    fun reiniciarClave() {
        _clave.value = ClaveUiState()
    }

    fun cambiarClave() {
        val actual = _clave.value
        if (actual.cargando) return

        // Validaciones locales antes de llamar a Firebase
        val error = when {
            actual.actual.isBlank() || actual.nueva.isBlank() || actual.confirmar.isBlank() -> "Completa todos los campos"
            actual.nueva.length < 6 -> "Mínimo 6 caracteres en la clave nueva"
            actual.nueva == actual.actual -> "La clave nueva debe ser distinta a la actual"
            actual.nueva != actual.confirmar -> "Las contraseñas no coinciden"
            else -> null
        }
        if (error != null) {
            _clave.update { it.copy(error = error) }
            return
        }

        viewModelScope.launch {
            _clave.update { it.copy(cargando = true, error = null) }
            authRepo.cambiarClave(actual.actual, actual.nueva)
                .onSuccess {
                    _clave.update { it.copy(cargando = false, cambiada = true) }
                    _mensaje.value = "¡Contraseña actualizada!"
                }
                .onFailure { e ->
                    _clave.update {
                        it.copy(cargando = false, error = e.message ?: "No se pudo cambiar la contraseña")
                    }
                }
        }
    }

    fun mensajeMostrado() {
        _mensaje.value = null
    }

    // Mismo medidor que el Registro: 1 punto por criterio -> nivel 1, 2 o 3
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