package com.tecsup.huellitasvet.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HistorialItem(
    fecha: String,
    servicio: String,
    diagnostico: String,
    tratamiento: String?,
    peso: Double?,
    esUltimo: Boolean = false
) {
    val primario = MaterialTheme.colorScheme.primary
    Row(Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
        // Punto + línea
        Column(Modifier.width(24.dp).fillMaxHeight(), horizontalAlignment = Alignment.CenterHorizontally) {
            Box(Modifier.padding(top = 4.dp).size(12.dp).background(primario, CircleShape))
            if (!esUltimo) Box(Modifier.width(2.dp).weight(1f).background(primario.copy(alpha = 0.3f)))
        }
        Spacer(Modifier.width(8.dp))
        Column(Modifier.padding(bottom = 16.dp)) {
            Text(fecha, style = MaterialTheme.typography.labelLarge, color = primario)
            Text(servicio, style = MaterialTheme.typography.titleMedium)
            Text("Diagnóstico: $diagnostico", style = MaterialTheme.typography.bodyMedium)
            if (!tratamiento.isNullOrBlank()) Text("Tratamiento: $tratamiento", style = MaterialTheme.typography.bodyMedium)
            if (peso != null) Text("Peso: $peso kg", style = MaterialTheme.typography.bodyMedium)
        }
    }
}