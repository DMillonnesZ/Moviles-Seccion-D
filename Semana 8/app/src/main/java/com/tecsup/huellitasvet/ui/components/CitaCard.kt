package com.tecsup.huellitasvet.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tecsup.huellitasvet.model.EstadoCita
import com.tecsup.huellitasvet.ui.theme.BordeSuave

/**
 * Tarjeta de cita: ícono del servicio, título, detalle, estado y una acción opcional abajo a la derecha.
 */
@Composable
fun CitaCard(
    servicio: String,
    icono: String,                 // heart, syringe, scissors
    detalle: String,               // ej. "Luna · Mié 21 · 10:30"
    estado: EstadoCita,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    accion: (@Composable () -> Unit)? = null
) {
    val cs = MaterialTheme.colorScheme
    val contenido: @Composable () -> Unit = {
        Row(Modifier.padding(14.dp)) {
            Box(
                Modifier.size(44.dp).background(cs.primaryContainer, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(iconoServicio(icono), null, tint = cs.primary)
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(servicio, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.height(2.dp))
                Text(detalle, style = MaterialTheme.typography.bodySmall, color = cs.onSurfaceVariant)
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.Bottom) {
                    EstadoCitaChip(estado)
                    Spacer(Modifier.weight(1f))
                    accion?.invoke()
                }
            }
        }
    }
    val forma = RoundedCornerShape(16.dp)
    val colores = CardDefaults.cardColors(containerColor = cs.surface)
    val borde = BorderStroke(1.dp, BordeSuave)

    if (onClick != null) {
        Card(onClick = onClick, shape = forma, colors = colores, border = borde, modifier = modifier.fillMaxWidth()) { contenido() }
    } else {
        Card(shape = forma, colors = colores, border = borde, modifier = modifier.fillMaxWidth()) { contenido() }
    }
}