package com.tecsup.huellitasvet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.huellitasvet.data.local.entity.Servicio
import com.tecsup.huellitasvet.data.repository.VetRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class ErroresServicio(
    val nombre: String? = null,
    val duracion: String? = null,
    val precio: String? = null
) {
    val hayErrores get() = nombre != null || duracion != null || precio != null
}

class ServiciosViewModel(private val repo: VetRepository) : ViewModel() {

    val servicios: StateFlow<List<Servicio>> = repo.observarServicios()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /** Duraciones permitidas */
    val duraciones = listOf(15, 30, 45, 60)

    private val _errores = MutableStateFlow(ErroresServicio())
    val errores: StateFlow<ErroresServicio> = _errores.asStateFlow()

    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje.asStateFlow()

    private val cache = mutableMapOf<Int, StateFlow<Servicio?>>()

    /** Para editar: un servicio por id */
    fun servicio(id: Int): StateFlow<Servicio?> = cache.getOrPut(id) {
        servicios.map { lista -> lista.find { it.id == id } }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
    }

    fun alternar(s: Servicio) {
        viewModelScope.launch { repo.cambiarActivo(s.id, !s.activo) }
    }

    /** Nuevo (id = 0) o editado */
    fun guardar(
        id: Int,
        nombre: String,
        duracionMin: Int?,
        precioTexto: String,
        descripcion: String,
        icono: String,
        activo: Boolean,
        onExito: () -> Unit
    ) {
        val precio = precioTexto.replace(",", ".").toDoubleOrNull()
        val e = ErroresServicio(
            nombre = if (nombre.isBlank()) "El nombre es obligatorio" else null,
            duracion = if (duracionMin == null || duracionMin !in duraciones) "Elige una duración" else null,
            precio = if (precio == null || precio <= 0) "Debe ser mayor que 0" else null
        )
        _errores.value = e
        if (e.hayErrores) return

        viewModelScope.launch {
            repo.guardarServicio(
                Servicio(
                    id = id,
                    nombre = nombre.trim(),
                    descripcion = descripcion.trim(),
                    duracionMin = duracionMin!!,
                    precio = precio!!,
                    activo = activo,
                    icono = icono
                )
            )
            onExito()
        }
    }

    /** Regla: un servicio con citas NO se elimina, solo se desactiva */
    fun eliminar(s: Servicio, onEliminado: () -> Unit) {
        viewModelScope.launch {
            if (repo.servicioTieneCitas(s.id)) {
                _mensaje.value = "No se puede eliminar: tiene citas. Solo puedes desactivarlo."
            } else {
                repo.eliminarServicio(s)
                onEliminado()
            }
        }
    }

    fun limpiarError(campo: String) {
        _errores.update {
            when (campo) {
                "nombre" -> it.copy(nombre = null)
                "duracion" -> it.copy(duracion = null)
                "precio" -> it.copy(precio = null)
                else -> it
            }
        }
    }
}