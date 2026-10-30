package com.tecsup.huellitasvet.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tecsup.huellitasvet.model.Especie
import com.tecsup.huellitasvet.model.EstadoCita

@Composable
fun CitaCard(
    mascota: String,
    especie: Especie,
    servicio: String,
    fecha: String,
    hora: String,
    estado: EstadoCita,
    precio: Double,
    modifier: Modifier = Modifier,
    subtitulo: String? = null,              // ej. "Dueño: Ana"
    onClick: (() -> Unit)? = null,
    acciones: @Composable RowScope.() -> Unit = {}
) {
    val contenido: @Composable () -> Unit = {
        Column(Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                MascotaAvatar(especie, tamano = 48.dp)
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(mascota, style = MaterialTheme.typography.titleMedium)
                    Text(servicio, style = MaterialTheme.typography.bodyMedium)
                    if (subtitulo != null) Text(subtitulo, style = MaterialTheme.typography.bodyMedium)
                }
                EstadoCitaChip(estado)
            }
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("📅 $fecha   🕒 $hora", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
                Text("S/ %.2f".format(precio), style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End, content = acciones)
        }
    }
    val forma = RoundedCornerShape(16.dp)
    if (onClick != null) Card(onClick = onClick, shape = forma, modifier = modifier.fillMaxWidth()) { contenido() }
    else Card(shape = forma, modifier = modifier.fillMaxWidth()) { contenido() }
}