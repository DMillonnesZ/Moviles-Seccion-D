package com.tecsup.huellitasvet.ui.veterinario

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.tecsup.huellitasvet.data.local.relation.CitaConDetalle
import com.tecsup.huellitasvet.model.Especie
import com.tecsup.huellitasvet.model.EstadoCita
import com.tecsup.huellitasvet.ui.components.AppTopBar
import com.tecsup.huellitasvet.ui.components.CalendarioMensual
import com.tecsup.huellitasvet.ui.components.EstadoCitaChip
import com.tecsup.huellitasvet.ui.components.MascotaAvatar
import com.tecsup.huellitasvet.ui.theme.BordeSuave
import com.tecsup.huellitasvet.viewmodel.AgendaViewModel
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun AgendaScreen(
    vm: AgendaViewModel,
    onAtender: (Int) -> Unit,
    onAtras: (() -> Unit)? = null
) {
    val cs = MaterialTheme.colorScheme
    val fecha by vm.fechaSeleccionada.collectAsState()
    val citas by vm.citas.collectAsState()
    val mostrarCalendario by vm.mostrarCalendario.collectAsState()
    val mes by vm.mes.collectAsState()
    val diasConCitas by vm.diasConCitas.collectAsState()

    Scaffold(
        containerColor = cs.background,
        contentWindowInsets = WindowInsets(0),
        topBar = {
            AppTopBar(
                titulo = "Agenda",
                onAtras = onAtras,
                acciones = {
                    IconButton(onClick = { vm.alternarCalendario() }) {
                        Icon(
                            imageVector = Icons.Outlined.CalendarMonth,
                            contentDescription = "Calendario",
                            tint = cs.primary
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(Modifier.padding(padding).fillMaxSize()) {
            if (mostrarCalendario) {
                CalendarioMensual(
                    mes = mes,
                    fechaSeleccionada = fecha,
                    diasConCitas = diasConCitas,
                    onElegir = { vm.elegirFecha(it) },
                    onAnterior = { vm.mesAnterior() },
                    onSiguiente = { vm.mesSiguiente() },
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                )
            } else {
                // Chips de días
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(vm.fechas) { f ->
                        ChipDia(f, seleccionado = f == fecha) { vm.elegirFecha(f) }
                    }
                }
            }

            if (citas.isEmpty()) {
                Text(
                    "No hay citas para este día",
                    color = cs.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(40.dp)
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 24.dp)
                ) {
                    items(citas, key = { it.cita.id }) { c ->
                        FilaAgenda(
                            c = c,
                            onConfirmar = { vm.confirmar(c) },
                            onAtender = { onAtender(c.cita.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ChipDia(f: LocalDate, seleccionado: Boolean, onClick: () -> Unit) {
    val cs = MaterialTheme.colorScheme
    val texto = if (f == LocalDate.now()) "Hoy ${f.dayOfMonth}"
    else f.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale("es"))
        .replace(".", "")
        .replaceFirstChar { it.uppercase() } + " ${f.dayOfMonth}"

    Text(
        texto,
        color = if (seleccionado) cs.onPrimary else cs.onSurface,
        fontWeight = FontWeight.SemiBold,
        style = MaterialTheme.typography.labelLarge,
        modifier = Modifier
            .background(if (seleccionado) cs.primary else cs.surface, RoundedCornerShape(50))
            .border(1.dp, if (seleccionado) cs.primary else BordeSuave, RoundedCornerShape(50))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

/** Hora a la izquierda + línea + tarjeta */
@Composable
private fun FilaAgenda(c: CitaConDetalle, onConfirmar: () -> Unit, onAtender: () -> Unit) {
    val cs = MaterialTheme.colorScheme
    val estado = EstadoCita.valueOf(c.cita.estado)
    val m = c.mascota.mascota
    val dueno = c.mascota.dueno.nombre.split(" ").let { p ->
        if (p.size > 1) "${p[0]} ${p[1].first()}." else p[0]
    }

    Row(Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
        // Hora
        Text(
            c.cita.hora,
            color = cs.primary,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.width(52.dp).padding(top = 14.dp)
        )
        // Línea
        Box(Modifier.width(2.dp).fillMaxHeight().background(cs.primaryContainer))
        Spacer(Modifier.width(12.dp))

        // Tarjeta
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = cs.surface),
            border = BorderStroke(1.dp, BordeSuave),
            modifier = Modifier.weight(1f).padding(bottom = 12.dp)
        ) {
            Row(Modifier.padding(12.dp)) {
                Box(
                    Modifier.size(44.dp).background(cs.primaryContainer, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    MascotaAvatar(Especie.valueOf(m.especie), tamano = 38.dp, conFondo = false)
                }
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    Text("${m.nombre} · ${c.servicio.nombre}", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                    Text("Dueño: $dueno", style = MaterialTheme.typography.labelSmall, color = cs.onSurfaceVariant)
                    Spacer(Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.Bottom) {
                        EstadoCitaChip(estado)
                        Spacer(Modifier.weight(1f))
                        when (estado) {
                            EstadoCita.PENDIENTE -> BotonAccion("Confirmar", onConfirmar)
                            EstadoCita.CONFIRMADA -> BotonAccion("Atender", onAtender)
                            else -> {}
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BotonAccion(texto: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(50),
        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 4.dp),
        modifier = Modifier.height(34.dp)
    ) { Text(texto, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold) }
}
