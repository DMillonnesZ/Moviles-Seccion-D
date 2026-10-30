package com.tecsup.huellitasvet.ui.components.graficos

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun GraficoDona(
    datos: List<DatoGrafico>,
    modifier: Modifier = Modifier,
    tamano: Dp = 110.dp,
    etiquetaCentro: String = "citas"
) {
    val cs = MaterialTheme.colorScheme
    val total = datos.sumOf { it.valor.toDouble() }.toFloat()

    Row(modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        // Dona
        Box(contentAlignment = Alignment.Center) {
            Canvas(Modifier.size(tamano)) {
                val grosor = size.width * 0.2f
                val trazo = Stroke(width = grosor)
                val origen = Offset(grosor / 2, grosor / 2)
                val area = Size(size.width - grosor, size.height - grosor)

                if (total == 0f) {
                    drawArc(cs.surfaceVariant, 0f, 360f, false, origen, area, style = trazo)
                } else {
                    var inicio = -90f
                    datos.forEach { d ->
                        val barrido = d.valor / total * 360f
                        drawArc(d.color, inicio, barrido, false, origen, area, style = trazo)
                        inicio += barrido
                    }
                }
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("${total.toInt()}", fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Text(etiquetaCentro, style = MaterialTheme.typography.labelSmall, color = cs.onSurfaceVariant)
            }
        }

        Spacer(Modifier.width(24.dp))

        // Leyenda
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            datos.forEach { d ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(12.dp).background(d.color, RoundedCornerShape(3.dp)))
                    Spacer(Modifier.width(10.dp))
                    Text(d.etiqueta, style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f))
                    Text("${d.valor.toInt()}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}