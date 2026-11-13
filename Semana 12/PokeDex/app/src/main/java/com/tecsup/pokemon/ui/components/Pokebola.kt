package com.tecsup.pokemon.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate

// Tonos nocturnos: rojo que oscurece hacia vino y blanco que oscurece hacia gris azulado
private val RojoClaro = Color(0xFFD23A36)
private val RojoOscuro = Color(0xFF6E1212)
private val BlancoClaro = Color(0xFFE2E4EE)
private val BlancoOscuro = Color(0xFF6F7390)
private val NegroPokebola = Color(0xFF141414)
private val LuzBoton = Color(0xFFFFE27A)

// Pokébola dibujada con código, con sombreado para escenas nocturnas.
// apertura: 0 cerrada, 1 tapa abierta. brillo: 0 apagado, 1 botón encendido.
@Composable
fun Pokebola(
    apertura: Float,
    brillo: Float,
    modifier: Modifier = Modifier
) {
    Canvas(modifier) {
        val r = size.minDimension / 2f * 0.92f
        val c = center
        val grosor = r * 0.09f
        val esquina = Offset(c.x - r, c.y - r)
        val tamano = Size(r * 2, r * 2)

        // Sombra en los bordes (da volumen)
        val sombraBorde = Brush.radialGradient(
            colors = listOf(Color.Transparent, Color.Transparent, Color.Black.copy(alpha = 0.35f)),
            center = c,
            radius = r
        )

        // ----- Mitad inferior: blanco que se oscurece hacia abajo -----
        drawArc(
            brush = Brush.verticalGradient(
                colors = listOf(BlancoClaro, BlancoOscuro),
                startY = c.y,
                endY = c.y + r
            ),
            startAngle = 0f, sweepAngle = 180f, useCenter = true,
            topLeft = esquina, size = tamano
        )
        drawArc(sombraBorde, 0f, 180f, true, esquina, tamano)
        drawArc(NegroPokebola, 0f, 180f, false, esquina, tamano, style = Stroke(grosor))

        // ----- Tapa roja que gira desde la bisagra izquierda -----
        rotate(degrees = -40f * apertura, pivot = Offset(c.x - r, c.y)) {
            drawArc(
                brush = Brush.radialGradient(
                    colors = listOf(RojoClaro, RojoOscuro),
                    center = Offset(c.x - r * 0.35f, c.y - r * 0.5f),
                    radius = r * 1.4f
                ),
                startAngle = 180f, sweepAngle = 180f, useCenter = true,
                topLeft = esquina, size = tamano
            )
            drawArc(sombraBorde, 180f, 180f, true, esquina, tamano)
            // Reflejo suave de luz de luna
            drawOval(
                color = Color.White.copy(alpha = 0.32f),
                topLeft = Offset(c.x - r * 0.6f, c.y - r * 0.78f),
                size = Size(r * 0.40f, r * 0.20f)
            )
            drawArc(NegroPokebola, 180f, 180f, false, esquina, tamano, style = Stroke(grosor))
            drawLine(NegroPokebola, Offset(c.x - r, c.y), Offset(c.x + r, c.y), grosor * 1.4f)
        }

        // ----- Franja negra y botón central -----
        drawLine(NegroPokebola, Offset(c.x - r, c.y), Offset(c.x + r, c.y), grosor * 1.4f)
        drawCircle(NegroPokebola, r * 0.30f, c)
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(BlancoClaro, BlancoOscuro),
                center = Offset(c.x - r * 0.06f, c.y - r * 0.06f),
                radius = r * 0.24f
            ),
            radius = r * 0.21f,
            center = c
        )

        // Brillo del botón al hacer "clic"
        if (brillo > 0f) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(LuzBoton.copy(alpha = brillo), Color.Transparent),
                    center = c,
                    radius = r * 0.8f
                ),
                radius = r * 0.8f,
                center = c
            )
            drawCircle(LuzBoton.copy(alpha = brillo), r * 0.21f, c)
        }
    }
}