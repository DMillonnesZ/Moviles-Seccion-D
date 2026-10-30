package com.tecsup.huellitasvet.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.tecsup.huellitasvet.model.Especie

@Composable
fun MascotaAvatar(
    especie: Especie,
    modifier: Modifier = Modifier,
    tamano: Dp = 56.dp,
    conFondo: Boolean = true
) {
    val fondo = MaterialTheme.colorScheme.primaryContainer
    val (cara, oreja) = when (especie) {
        Especie.PERRO -> Color(0xFFD6A066) to Color(0xFF8B4513)   // café
        Especie.GATO -> Color(0xFF9CA3AF) to Color(0xFF9CA3AF)    // gris
        Especie.OTRO -> Color(0xFFF3E1C7) to Color(0xFF8B4513)    // crema
    }
    val oscuro = Color(0xFF2B2B2B)

    Canvas(modifier.size(tamano)) {
        val w = size.width
        val centro = Offset(w * 0.5f, w * 0.55f)
        val radio = w * 0.26f

        // Fondo
        if (conFondo) drawCircle(fondo)

        // 1. Orejas DETRÁS de la cabeza
        if (especie == Especie.GATO) {
            drawPath(Path().apply {
                moveTo(w * 0.27f, w * 0.48f); lineTo(w * 0.29f, w * 0.2f); lineTo(w * 0.47f, w * 0.33f); close()
            }, cara)
            drawPath(Path().apply {
                moveTo(w * 0.73f, w * 0.48f); lineTo(w * 0.71f, w * 0.2f); lineTo(w * 0.53f, w * 0.33f); close()
            }, cara)
        } else {
            orejaCaida(oreja, Offset(w * 0.27f, w * 0.48f), w, -10f)
            orejaCaida(oreja, Offset(w * 0.73f, w * 0.48f), w, 10f)
        }

        // 2. Cabeza
        drawCircle(cara, radius = radio, center = centro)

        // 3. Ojos
        drawCircle(oscuro, w * 0.028f, Offset(w * 0.42f, w * 0.52f))
        drawCircle(oscuro, w * 0.028f, Offset(w * 0.58f, w * 0.52f))

        // 4. Nariz
        drawCircle(oscuro, w * 0.03f, Offset(w * 0.5f, w * 0.6f))

        // 5. Boquita "w"
        val g = w * 0.015f
        drawLine(oscuro, Offset(w * 0.5f, w * 0.62f), Offset(w * 0.46f, w * 0.66f), g, StrokeCap.Round)
        drawLine(oscuro, Offset(w * 0.5f, w * 0.62f), Offset(w * 0.54f, w * 0.66f), g, StrokeCap.Round)
    }
}

private fun DrawScope.orejaCaida(color: Color, centro: Offset, w: Float, grados: Float) {
    val tam = Size(w * 0.15f, w * 0.32f)
    rotate(grados, pivot = centro) {
        drawOval(color, topLeft = Offset(centro.x - tam.width / 2, centro.y - tam.height / 2), size = tam)
    }
}