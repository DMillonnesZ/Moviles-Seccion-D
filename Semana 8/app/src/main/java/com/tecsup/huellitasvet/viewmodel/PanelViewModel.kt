package com.tecsup.huellitasvet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.huellitasvet.data.repository.VetRepository
import com.tecsup.huellitasvet.model.EstadoCita
import com.tecsup.huellitasvet.ui.components.graficos.DatoGrafico
import com.tecsup.huellitasvet.ui.theme.AzulClaro
import com.tecsup.huellitasvet.ui.theme.VetPrimario
import com.tecsup.huellitasvet.ui.theme.color
import kotlinx.coroutines.flow.*
import java.time.LocalDate

class PanelViewModel(private val repo: VetRepository) : ViewModel() {

    val hoy: LocalDate = LocalDate.now()
    private val manana = hoy.plusDays(1).toString()
    private val inicioMes = hoy.withDayOfMonth(1).toString()      // día 1 del mes

    private fun <T> Flow<T>.estado(inicial: T) =
        stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), inicial)

    // ===== Contadores =====
    val citasHoy: StateFlow<Int> = repo.observarCitasDeFecha(hoy.toString()).estado(0)
    val pendientes: StateFlow<Int> = repo.observarCantidadPorEstado(EstadoCita.PENDIENTE).estado(0)
    val atendidas: StateFlow<Int> = repo.observarCantidadPorEstado(EstadoCita.ATENDIDA).estado(0)

    /** Suma del precio de las ATENDIDAS desde el día 1 */
    val ingresosMes: StateFlow<Double> = repo.observarIngresosDesde(inicioMes).estado(0.0)

    // ===== Gráficos (Room → DatoGrafico) =====
    val porEstado: StateFlow<List<DatoGrafico>> = combine(
        EstadoCita.entries.map { e -> repo.observarCantidadPorEstado(e).map { e to it } }
    ) { pares ->
        pares.map { (e, n) -> DatoGrafico(e.etiqueta, n.toFloat(), e.color()) }
    }.estado(emptyList())

    val porServicio: StateFlow<List<DatoGrafico>> = repo.observarCitasPorServicio()
        .map { lista ->
            val max = lista.maxOfOrNull { it.cantidad }
            lista.map {
                DatoGrafico(
                    etiqueta = it.nombre.substringBefore(" "),
                    valor = it.cantidad.toFloat(),
                    color = if (it.cantidad == max) VetPrimario else AzulClaro
                )
            }
        }.estado(emptyList())

    /** Alerta: citas PENDIENTES para hoy y mañana */
    val porConfirmar: StateFlow<Int> = combine(
        repo.observarCitasPorFecha(hoy.toString()),
        repo.observarCitasPorFecha(manana)
    ) { a, b ->
        (a + b).count { it.cita.estado == EstadoCita.PENDIENTE.name }
    }.estado(0)
}