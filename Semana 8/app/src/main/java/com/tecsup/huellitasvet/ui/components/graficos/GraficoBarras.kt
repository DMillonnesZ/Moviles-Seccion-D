package com.tecsup.huellitasvet.ui.components.graficos

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun GraficoBarras(datos: List<DatoGrafico>, modifier: Modifier = Modifier, alto: Dp = 160.dp) {
    val maximo = (datos.maxOfOrNull { it.valor } ?: 0f).coerceAtLeast(1f)

    Column(modifier.fillMaxWidth()) {
        Canvas(Modifier.fillMaxWidth().height(alto)) {
            if (datos.isEmpty()) return@Canvas
            val anchoCelda = size.width / datos.size
            val anchoBarra = anchoCelda * 0.55f
            datos.forEachIndexed { i, d ->
                val h = d.valor / maximo * size.height
                drawRoundRect(
                    color = d.color,
                    topLeft = Offset(i * anchoCelda + (anchoCelda - anchoBarra) / 2, size.height - h),
                    size = Size(anchoBarra, h),
                    cornerRadius = CornerRadius(8f, 8f)
                )
            }
        }
        Spacer(Modifier.height(6.dp))
        // Etiquetas debajo de cada barra
        Row(Modifier.fillMaxWidth()) {
            datos.forEach { d ->
                Column(Modifier.weight(1f)) {
                    Text(
                        "${d.valor.toInt()}",
                        style = MaterialTheme.typography.labelLarge,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        d.etiqueta,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}