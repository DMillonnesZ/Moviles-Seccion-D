package com.tecsup.huellitasvet.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tecsup.huellitasvet.ui.theme.BordeSuave
import com.tecsup.huellitasvet.ui.theme.EstadoCancelada

/** Línea de tiempo del historial clínico: fecha, servicio, diagnóstico y peso */
@Composable
fun HistorialItem(
    fecha: String,          // ya formateada, ej. "12 oct"
    servicio: String,
    diagnostico: String,
    peso: Double?,
    esUltimo: Boolean = false
) {
    val cs = MaterialTheme.colorScheme
    Row(Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
        // Punto con check + línea
        Column(
            Modifier.width(28.dp).fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                Modifier.padding(top = 4.dp).size(22.dp).background(cs.primary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Check, null, tint = cs.onPrimary, modifier = Modifier.size(14.dp))
            }
            if (!esUltimo) {
                Box(Modifier.width(2.dp).weight(1f).background(cs.primary.copy(alpha = 0.35f)))
            }
        }

        Spacer(Modifier.width(12.dp))

        // Tarjeta
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = cs.surface),
            border = BorderStroke(1.dp, BordeSuave),
            modifier = Modifier.weight(1f).padding(bottom = 14.dp)
        ) {
            Column(Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(servicio, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
                    Text(fecha, color = cs.primary, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                }
                Spacer(Modifier.height(4.dp))
                Text(diagnostico, style = MaterialTheme.typography.bodySmall, color = cs.onSurfaceVariant)
                if (peso != null) {
                    Spacer(Modifier.height(10.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Favorite, null, tint = EstadoCancelada, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Peso: $peso kg", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}