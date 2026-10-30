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
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun GraficoBarras(datos: List<DatoGrafico>, modifier: Modifier = Modifier, alto: Dp = 120.dp) {
    val cs = MaterialTheme.colorScheme
    val maximo = (datos.maxOfOrNull { it.valor } ?: 0f).coerceAtLeast(1f)
    val medidor = rememberTextMeasurer()
    val estiloValor = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = cs.onSurface)

    Column(modifier.fillMaxWidth()) {
        Canvas(Modifier.fillMaxWidth().height(alto)) {
            if (datos.isEmpty()) return@Canvas
            val espacioTexto = 18.dp.toPx()
            val altoUtil = size.height - espacioTexto
            val celda = size.width / datos.size
            val ancho = celda * 0.5f

            datos.forEachIndexed { i, d ->
                val h = d.valor / maximo * altoUtil
                val x = i * celda + (celda - ancho) / 2
                val y = size.height - h

                drawRoundRect(
                    color = d.color,
                    topLeft = Offset(x, y),
                    size = Size(ancho, h),
                    cornerRadius = CornerRadius(6.dp.toPx())
                )
                // Valor encima de la barra
                val texto = medidor.measure("${d.valor.toInt()}", estiloValor)
                drawText(
                    texto,
                    topLeft = Offset(
                        i * celda + (celda - texto.size.width) / 2,
                        y - texto.size.height - 2.dp.toPx()
                    )
                )
            }
        }
        Spacer(Modifier.height(6.dp))
        // Etiquetas debajo
        Row(Modifier.fillMaxWidth()) {
            datos.forEach { d ->
                Text(
                    d.etiqueta,
                    style = MaterialTheme.typography.labelSmall,
                    color = cs.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}