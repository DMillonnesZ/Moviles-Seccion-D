package com.tecsup.huellitasvet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.huellitasvet.data.local.entity.Mascota
import com.tecsup.huellitasvet.data.local.entity.Servicio
import com.tecsup.huellitasvet.data.local.relation.CitaConDetalle
import com.tecsup.huellitasvet.data.repository.VetRepository
import com.tecsup.huellitasvet.model.EstadoCita
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

@OptIn(ExperimentalCoroutinesApi::class)
class InicioViewModel(private val repo: VetRepository) : ViewModel() {

    private val duenoId = MutableStateFlow<Int?>(null)

    /** Recibe el dueño actual */
    fun cargar(id: Int) { duenoId.value = id }

    val mascotas: StateFlow<List<Mascota>> = duenoId
        .filterNotNull()
        .flatMapLatest { repo.observarMascotasDeDueno(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /** Citas PENDIENTE o CONFIRMADA desde ahora, ordenadas por fecha y hora */
    private val proximas: Flow<List<CitaConDetalle>> = duenoId
        .filterNotNull()
        .flatMapLatest { repo.observarCitasDeDueno(it) }
        .map { lista ->
            val ahora = LocalDateTime.now()
            lista.filter {
                (it.cita.estado == EstadoCita.PENDIENTE.name || it.cita.estado == EstadoCita.CONFIRMADA.name) &&
                        !it.fechaHora().isBefore(ahora)
            }.sortedBy { it.fechaHora() }
        }

    /** La primera de la lista */
    val proximaCita: StateFlow<CitaConDetalle?> = proximas
        .map { it.firstOrNull() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    /** Recordatorio: la siguiente cita después de la próxima */
    val recordatorio: StateFlow<String?> = proximas
        .map { lista ->
            lista.getOrNull(1)?.let { c ->
                val dias = ChronoUnit.DAYS.between(LocalDate.now(), LocalDate.parse(c.cita.fecha))
                val cuando = when (dias) {
                    0L -> "hoy"
                    1L -> "mañana"
                    else -> "en $dias días"
                }
                "${c.mascota.mascota.nombre}: ${c.servicio.nombre.lowercase()} $cuando"
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    /** Accesos rápidos de servicios (solo activos) */
    val servicios: StateFlow<List<Servicio>> = repo.observarServiciosActivos()
        .map { it.take(3) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}