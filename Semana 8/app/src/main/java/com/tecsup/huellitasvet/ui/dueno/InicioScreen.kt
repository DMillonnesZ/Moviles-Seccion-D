package com.tecsup.huellitasvet.ui.dueno

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tecsup.huellitasvet.data.local.entity.Mascota
import com.tecsup.huellitasvet.data.local.entity.Servicio
import com.tecsup.huellitasvet.data.local.relation.CitaConDetalle
import com.tecsup.huellitasvet.model.Especie
import com.tecsup.huellitasvet.model.EstadoCita
import com.tecsup.huellitasvet.ui.components.MascotaAvatar
import com.tecsup.huellitasvet.ui.components.VetButton
import com.tecsup.huellitasvet.ui.components.iconoServicio
import com.tecsup.huellitasvet.ui.theme.AvisoFondo
import com.tecsup.huellitasvet.ui.theme.AvisoIcono
import com.tecsup.huellitasvet.ui.theme.AvisoTexto
import com.tecsup.huellitasvet.ui.theme.BordeSuave
import com.tecsup.huellitasvet.ui.theme.color
import com.tecsup.huellitasvet.viewmodel.InicioViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun InicioScreen(
    vm: InicioViewModel,
    nombreDueno: String,
    onVerMascota: (Int) -> Unit,
    onAgregarMascota: () -> Unit,
    onVerTodas: () -> Unit,
    onPedirCita: () -> Unit
) {
    val mascotas by vm.mascotas.collectAsState()
    val proxima by vm.proximaCita.collectAsState()
    val recordatorio by vm.recordatorio.collectAsState()
    val servicios by vm.servicios.collectAsState()
    val cs = MaterialTheme.colorScheme

    Column(
        Modifier
            .fillMaxSize()
            .background(cs.background)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        // ===== Saludo =====
        Row(verticalAlignment = Alignment.Top) {
            Column(Modifier.weight(1f)) {
                Text("Hola, ${nombreDueno.substringBefore(" ")}", fontSize = 26.sp, fontWeight = FontWeight.Bold)
                Text("¿Cómo están tus mascotas hoy?", style = MaterialTheme.typography.bodyMedium, color = cs.onSurfaceVariant)
            }
            Icon(Icons.Outlined.Notifications, "Notificaciones", modifier = Modifier.padding(top = 6.dp))
        }

        Spacer(Modifier.height(18.dp))

        // ===== Próxima cita =====
        TarjetaProximaCita(proxima)

        Spacer(Modifier.height(22.dp))

        // ===== Mis mascotas =====
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Mis mascotas", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            Text(
                "Ver todas",
                color = cs.primary,
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.clickable(onClick = onVerTodas)
            )
        }
        Spacer(Modifier.height(10.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            items(mascotas, key = { it.id }) { m ->
                TarjetaMascotaMini(m) { onVerMascota(m.id) }
            }
            item { TarjetaAgregar(onAgregarMascota) }
        }

        Spacer(Modifier.height(22.dp))

        // ===== Servicios =====
        Text("Servicios", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            servicios.forEach { s ->
                TarjetaServicio(s, Modifier.weight(1f), onPedirCita)
            }
        }

        // ===== Recordatorio =====
        if (recordatorio != null) {
            Spacer(Modifier.height(18.dp))
            Row(
                Modifier
                    .fillMaxWidth()
                    .background(AvisoFondo, RoundedCornerShape(16.dp))
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Filled.Notifications, null, tint = AvisoIcono)
                Spacer(Modifier.width(12.dp))
                Column {
                    Text("Recordatorio", color = AvisoTexto, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                    Text(recordatorio!!, color = AvisoTexto, style = MaterialTheme.typography.bodySmall)
                }
            }
        }

        Spacer(Modifier.height(22.dp))

        VetButton(texto = "Pedir una cita", onClick = onPedirCita)
        Spacer(Modifier.height(8.dp))
    }
}

@Composable
private fun TarjetaProximaCita(c: CitaConDetalle?) {
    val cs = MaterialTheme.colorScheme
    Row(
        Modifier
            .fillMaxWidth()
            .background(cs.primary, RoundedCornerShape(20.dp))
            .padding(18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text("Próxima cita", color = cs.onPrimary.copy(alpha = 0.85f), style = MaterialTheme.typography.bodySmall)
            Spacer(Modifier.height(4.dp))

            if (c == null) {
                Text("Aún no tienes citas", color = cs.onPrimary, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text("Agenda una para tu mascota", color = cs.onPrimary.copy(alpha = 0.85f), style = MaterialTheme.typography.bodySmall)
            } else {
                val estado = EstadoCita.valueOf(c.cita.estado)
                Text(
                    "${c.mascota.mascota.nombre} · ${c.servicio.nombre}",
                    color = cs.onPrimary, fontWeight = FontWeight.Bold, fontSize = 18.sp
                )
                Spacer(Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.Schedule, null, tint = cs.onPrimary, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(
                        "${fechaCorta(c.cita.fecha)} · ${c.cita.hora}",
                        color = cs.onPrimary, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyMedium
                    )
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    estado.etiqueta,
                    color = estado.color(),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .background(cs.onPrimary, RoundedCornerShape(50))
                        .padding(horizontal = 14.dp, vertical = 3.dp)
                )
            }
        }
        if (c != null) {
            Box(
                Modifier.size(56.dp).background(cs.onPrimary, RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                MascotaAvatar(Especie.valueOf(c.mascota.mascota.especie), tamano = 46.dp, conFondo = false)
            }
        }
    }
}

@Composable
private fun TarjetaMascotaMini(m: Mascota, onClick: () -> Unit) {
    val cs = MaterialTheme.colorScheme
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cs.surface),
        border = BorderStroke(1.dp, BordeSuave),
        modifier = Modifier.width(104.dp).height(108.dp)
    ) {
        Column(
            Modifier.fillMaxSize().padding(vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                Modifier.size(52.dp).background(cs.primaryContainer, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                MascotaAvatar(Especie.valueOf(m.especie), tamano = 46.dp, conFondo = false)
            }
            Spacer(Modifier.height(6.dp))
            Text(m.nombre, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
            Text("Ver ficha", style = MaterialTheme.typography.labelSmall, color = cs.onSurfaceVariant)
        }
    }
}

@Composable
private fun TarjetaAgregar(onClick: () -> Unit) {
    val cs = MaterialTheme.colorScheme
    Column(
        Modifier
            .width(104.dp)
            .height(108.dp)
            .border(2.dp, cs.primary, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(Icons.Default.Add, null, tint = cs.primary, modifier = Modifier.size(28.dp))
        Spacer(Modifier.height(6.dp))
        Text("Agregar", color = cs.primary, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun TarjetaServicio(s: Servicio, modifier: Modifier, onClick: () -> Unit) {
    val cs = MaterialTheme.colorScheme
    Column(
        modifier
            .height(76.dp)
            .background(cs.primaryContainer.copy(alpha = 0.45f), RoundedCornerShape(16.dp))
            .border(1.dp, cs.primaryContainer, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(iconoServicio(s.icono), null, tint = cs.primary)
        Spacer(Modifier.height(6.dp))
        Text(
            s.nombre.substringBefore(" "),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}

/** "Mié 21 oct" */
internal fun fechaCorta(fecha: String): String =
    LocalDate.parse(fecha)
        .format(DateTimeFormatter.ofPattern("EEE d MMM", Locale("es")))
        .replace(".", "")
        .replaceFirstChar { it.uppercase() }