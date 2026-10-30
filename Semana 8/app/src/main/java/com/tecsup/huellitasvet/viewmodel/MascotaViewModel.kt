package com.tecsup.huellitasvet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.huellitasvet.data.local.entity.Mascota
import com.tecsup.huellitasvet.data.local.relation.CitaConDetalle
import com.tecsup.huellitasvet.data.repository.VetRepository
import com.tecsup.huellitasvet.model.Especie
import com.tecsup.huellitasvet.model.PuntoPeso
import com.tecsup.huellitasvet.model.Sexo
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.Year

/** Lo que escribe el usuario en el formulario (texto crudo) */
data class FormMascota(
    val id: Int = 0,
    val duenoId: Int,
    val nombre: String,
    val especie: Especie?,
    val raza: String,
    val anio: String,
    val peso: String,
    val sexo: Sexo?
)

/** Un error por campo (null = sin error) */
data class ErroresMascota(
    val nombre: String? = null,
    val especie: String? = null,
    val anio: String? = null,
    val peso: String? = null,
    val sexo: String? = null
) {
    val hayErrores get() = listOf(nombre, especie, anio, peso, sexo).any { it != null }
}

class MascotaViewModel(private val repo: VetRepository) : ViewModel() {

    private val _errores = MutableStateFlow(ErroresMascota())
    val errores: StateFlow<ErroresMascota> = _errores.asStateFlow()

    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje.asStateFlow()

    // Cache para no recrear el StateFlow en cada recomposición
    private val mascotas = mutableMapOf<Int, StateFlow<Mascota?>>()
    private val historiales = mutableMapOf<Int, StateFlow<List<CitaConDetalle>>>()
    private val registroPesos = mutableMapOf<Int, StateFlow<List<PuntoPeso>>>()

    fun mascota(id: Int): StateFlow<Mascota?> = mascotas.getOrPut(id) {
        repo.observarMascota(id)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
    }

    /** Historial clínico: citas ATENDIDAS, la más reciente primero */
    fun historial(mascotaId: Int): StateFlow<List<CitaConDetalle>> = historiales.getOrPut(mascotaId) {
        repo.observarHistorial(mascotaId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    }

    /** Pesos registrados en citas ATENDIDAS, ordenados por fecha ASC */
    fun pesos(mascotaId: Int): StateFlow<List<PuntoPeso>> = registroPesos.getOrPut(mascotaId) {
        repo.observarPesos(mascotaId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    }

    /** Vacunas al día: tiene una vacunación atendida en el último año */
    fun vacunasAlDia(lista: List<CitaConDetalle>): Boolean {
        val haceUnAnio = LocalDate.now().minusYears(1)
        return lista.any {
            it.servicio.nombre.contains("Vacun", ignoreCase = true) &&
                    LocalDate.parse(it.cita.fecha).isAfter(haceUnAnio)
        }
    }

    fun guardar(f: FormMascota, onExito: () -> Unit) {
        val anioActual = Year.now().value
        val anio = f.anio.toIntOrNull()
        val peso = f.peso.replace(",", ".").toDoubleOrNull()

        val e = ErroresMascota(
            nombre = if (f.nombre.trim().length < 2) "Mínimo 2 caracteres" else null,
            especie = if (f.especie == null) "Elige una especie" else null,
            anio = if (anio == null || anio !in 1990..anioActual) "Entre 1990 y $anioActual" else null,
            peso = if (peso == null || peso <= 0) "Debe ser mayor que 0" else null,
            sexo = if (f.sexo == null) "Elige el sexo" else null
        )
        _errores.value = e
        if (e.hayErrores) return

        viewModelScope.launch {
            repo.guardarMascota(
                Mascota(
                    id = f.id,
                    duenoId = f.duenoId,
                    nombre = f.nombre.trim(),
                    especie = f.especie!!.name,
                    raza = f.raza.trim(),
                    anioNacimiento = anio!!,
                    peso = peso!!,
                    sexo = f.sexo!!.name
                )
            )
            onExito()
        }
    }

    /** Regla: solo se elimina si NO tiene citas */
    fun eliminar(m: Mascota, onEliminada: () -> Unit) {
        viewModelScope.launch {
            if (repo.mascotaTieneCitas(m.id)) {
                _mensaje.value = "No se puede eliminar a ${m.nombre}: tiene citas registradas"
            } else {
                repo.eliminarMascota(m)
                onEliminada()
            }
        }
    }

    fun limpiarError(campo: String) {
        _errores.update {
            when (campo) {
                "nombre" -> it.copy(nombre = null)
                "especie" -> it.copy(especie = null)
                "anio" -> it.copy(anio = null)
                "peso" -> it.copy(peso = null)
                "sexo" -> it.copy(sexo = null)
                else -> it
            }
        }
    }

    fun limpiarMensaje() { _mensaje.value = null }
}