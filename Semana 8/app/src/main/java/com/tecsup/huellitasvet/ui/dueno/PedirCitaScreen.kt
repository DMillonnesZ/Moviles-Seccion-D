package com.tecsup.huellitasvet.ui.dueno

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tecsup.huellitasvet.data.local.entity.Mascota
import com.tecsup.huellitasvet.data.local.entity.Servicio
import com.tecsup.huellitasvet.model.Especie
import com.tecsup.huellitasvet.ui.components.*
import com.tecsup.huellitasvet.ui.theme.AvisoFondo
import com.tecsup.huellitasvet.ui.theme.AvisoIcono
import com.tecsup.huellitasvet.ui.theme.AvisoTexto
import com.tecsup.huellitasvet.ui.theme.BordeSuave
import com.tecsup.huellitasvet.viewmodel.PedirCitaViewModel

@Composable
fun PedirCitaScreen(
    vm: PedirCitaViewModel,
    onConfirmada: () -> Unit,
    onAtras: () -> Unit
) {
    val cs = MaterialTheme.colorScheme
    val mascotas by vm.mascotas.collectAsState()
    val servicios by vm.servicios.collectAsState()
    val mascota by vm.mascota.collectAsState()
    val servicio by vm.servicio.collectAsState()
    val fecha by vm.fecha.collectAsState()
    val hora by vm.hora.collectAsState()
    val deshabilitadas by vm.horasDeshabilitadas.collectAsState()
    val puedeConfirmar by vm.puedeConfirmar.collectAsState()
    val aceptarCitas by vm.aceptarCitas.collectAsState()
    val mensaje by vm.mensaje.collectAsState()

    Scaffold(
        containerColor = cs.background,
        topBar = { AppTopBar("Pedir cita", onAtras = onAtras) }
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            // Clínica cerrada
            if (!aceptarCitas) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                        .background(AvisoFondo, RoundedCornerShape(14.dp))
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Notifications, null, tint = AvisoIcono)
                    Spacer(Modifier.width(10.dp))
                    Text("La clínica no está recibiendo citas", color = AvisoTexto, fontWeight = FontWeight.SemiBold)
                }
            }

            // ===== ¿Quién viene? =====
            Titulo("¿Quién viene?")
            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                items(mascotas, key = { it.id }) { m ->
                    ChipMascota(m, seleccionado = mascota?.id == m.id) { vm.elegirMascota(m.id) }
                }
            }

            // ===== Servicio =====
            Titulo("Servicio")
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                servicios.forEach { s ->
                    OpcionServicio(s, seleccionado = servicio?.id == s.id) { vm.elegirServicio(s) }
                }
            }

            // ===== Fecha =====
            Titulo("Fecha")
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(vm.fechas) { f ->
                    FechaChip(f, seleccionado = f == fecha) { vm.elegirFecha(f) }
                }
            }

            // ===== Hora =====
            Row(verticalAlignment = Alignment.CenterVertically) {
                Titulo("Hora", Modifier.weight(1f))
                Text("Ocupado = gris", style = MaterialTheme.typography.labelSmall, color = cs.onSurfaceVariant)
            }
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                vm.horarios.chunked(4).forEach { fila ->
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        fila.forEach { h ->
                            HoraChip(
                                hora = h,
                                seleccionado = h == hora,
                                ocupado = h in deshabilitadas,
                                onClick = { vm.elegirHora(h) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        repeat(4 - fila.size) { Spacer(Modifier.weight(1f)) }
                    }
                }
            }

            // ===== Resumen =====
            if (mascota != null && servicio != null) {
                Spacer(Modifier.height(18.dp))
                Column(
                    Modifier
                        .fillMaxWidth()
                        .background(cs.primaryContainer.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                        .padding(14.dp)
                ) {
                    Text(
                        "${mascota!!.nombre} · ${servicio!!.nombre}",
                        color = cs.primary, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        "${fechaCorta(fecha.toString())} · ${hora ?: "--:--"} · S/ %.2f".format(servicio!!.precio),
                        style = MaterialTheme.typography.bodySmall, color = cs.onSurfaceVariant
                    )
                }
            }

            if (mensaje != null) {
                Spacer(Modifier.height(10.dp))
                Text(mensaje!!, color = cs.error, style = MaterialTheme.typography.bodyMedium)
            }

            Spacer(Modifier.height(16.dp))
            VetButton(
                texto = "Confirmar cita",
                onClick = { vm.confirmar(onConfirmada) },
                habilitado = puedeConfirmar
            )
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun Titulo(texto: String, modifier: Modifier = Modifier) {
    Text(
        texto,
        fontWeight = FontWeight.Bold,
        style = MaterialTheme.typography.bodyLarge,
        modifier = modifier.padding(top = 18.dp, bottom = 10.dp)
    )
}

@Composable
private fun ChipMascota(m: Mascota, seleccionado: Boolean, onClick: () -> Unit) {
    val cs = MaterialTheme.colorScheme
    Row(
        Modifier
            .background(if (seleccionado) cs.primaryContainer else cs.surface, RoundedCornerShape(50))
            .border(if (seleccionado) 1.5.dp else 1.dp, if (seleccionado) cs.primary else BordeSuave, RoundedCornerShape(50))
            .clickable(onClick = onClick)
            .padding(start = 8.dp, end = 20.dp, top = 6.dp, bottom = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        MascotaAvatar(Especie.valueOf(m.especie), tamano = 34.dp, conFondo = false)
        Spacer(Modifier.width(8.dp))
        Text(
            m.nombre,
            color = if (seleccionado) cs.primary else cs.onSurfaceVariant,
            fontWeight = if (seleccionado) FontWeight.Bold else FontWeight.Medium
        )
    }
}

@Composable
private fun OpcionServicio(s: Servicio, seleccionado: Boolean, onClick: () -> Unit) {
    val cs = MaterialTheme.colorScheme
    Row(
        Modifier
            .fillMaxWidth()
            .background(if (seleccionado) cs.primaryContainer else cs.surface, RoundedCornerShape(14.dp))
            .border(if (seleccionado) 1.5.dp else 1.dp, if (seleccionado) cs.primary else BordeSuave, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            iconoServicio(s.icono), null,
            tint = if (seleccionado) cs.primary else cs.onSurfaceVariant,
            modifier = Modifier.size(20.dp)
        )
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(s.nombre, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
            Text("${s.duracionMin} min", style = MaterialTheme.typography.labelSmall, color = cs.onSurfaceVariant)
        }
        Text(
            "S/ ${if (s.precio % 1.0 == 0.0) s.precio.toInt() else s.precio}",
            color = cs.primary, fontWeight = FontWeight.Bold
        )
    }
}