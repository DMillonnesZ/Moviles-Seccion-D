package com.tecsup.huellitasvet.ui.components.graficos

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.tecsup.huellitasvet.model.PuntoPeso
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun GraficoLineas(
    puntos: List<PuntoPeso>,
    modifier: Modifier = Modifier,
    alto: Dp = 160.dp
) {
    val cs = MaterialTheme.colorScheme
    if (puntos.size < 2) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(alto),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Aún no hay suficientes registros de peso",
                style = MaterialTheme.typography.bodyMedium,
                color = cs.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    } else {
        val medidor = rememberTextMeasurer()
        val colorLinea = cs.primary
        val estiloPeso = MaterialTheme.typography.labelSmall.copy(
            fontWeight = FontWeight.Bold,
            color = cs.onSurface
        )
        val estiloFecha = MaterialTheme.typography.labelSmall.copy(
            color = cs.onSurfaceVariant
        )

        Canvas(
            modifier = modifier
                .fillMaxWidth()
                .height(alto)
        ) {
            val n = puntos.size
            val w = size.width
            val h = size.height

            val paddingTop = 28.dp.toPx()
            val paddingBottom = 28.dp.toPx()
            val paddingX = 32.dp.toPx()
            val altoUtil = h - paddingTop - paddingBottom

            val minPeso = puntos.minOf { it.peso }
            val maxPeso = puntos.maxOf { it.peso }
            val rango = maxPeso - minPeso
            val margen = if (rango == 0.0) 1.0 else rango * 0.2
            val minEje = minPeso - margen
            val maxEje = maxPeso + margen
            val rangoEje = maxEje - minEje

            val xList = FloatArray(n)
            val yList = FloatArray(n)

            for (i in 0 until n) {
                xList[i] = paddingX + i * (w - 2 * paddingX) / (n - 1)
                val norm = ((puntos[i].peso - minEje) / rangoEje).toFloat()
                yList[i] = paddingTop + altoUtil * (1f - norm)
            }

            val path = Path().apply {
                for (i in 0 until n) {
                    if (i == 0) moveTo(xList[i], yList[i])
                    else lineTo(xList[i], yList[i])
                }
            }

            drawPath(
                path = path,
                color = colorLinea,
                style = Stroke(
                    width = 3.dp.toPx(),
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )

            for (i in 0 until n) {
                val x = xList[i]
                val y = yList[i]
                val p = puntos[i]

                drawCircle(
                    color = colorLinea,
                    radius = 5.dp.toPx(),
                    center = Offset(x, y)
                )
                drawCircle(
                    color = cs.surface,
                    radius = 2.5.dp.toPx(),
                    center = Offset(x, y)
                )

                val pesoStr = if (p.peso % 1.0 == 0.0) "${p.peso.toInt()} kg" else "${p.peso} kg"
                val textoPeso = medidor.measure(pesoStr, estiloPeso)
                drawText(
                    textoPeso,
                    topLeft = Offset(
                        x - textoPeso.size.width / 2f,
                        y - textoPeso.size.height - 4.dp.toPx()
                    )
                )

                val fechaStr = formatearFecha(p.fecha)
                val textoFecha = medidor.measure(fechaStr, estiloFecha)
                drawText(
                    textoFecha,
                    topLeft = Offset(
                        x - textoFecha.size.width / 2f,
                        h - textoFecha.size.height
                    )
                )
            }
        }
    }
}

private fun formatearFecha(fecha: String): String {
    return try {
        val parsed = LocalDate.parse(fecha)
        val formatter = DateTimeFormatter.ofPattern("d MMM", Locale.forLanguageTag("es"))
        parsed.format(formatter).replace(".", "").lowercase()
    } catch (_: Exception) {
        fecha
    }
}
