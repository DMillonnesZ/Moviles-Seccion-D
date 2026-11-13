package com.tecsup.pokemon.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

private val LuzLuciernaga = Color(0xFFFFE87A)
private val CuerpoLuciernaga = Color(0xFFFFF6C2)

// Datos de cada luciérnaga
private data class Luciernaga(
    val x: Float,          // posición base (0 a 1)
    val y: Float,
    val ampX: Float,       // cuánto se aleja de su base (dp)
    val ampY: Float,
    val vueltas: Int,      // ciclos por bucle (entero para que no salte)
    val fase: Float,
    val parpadeo: Int,     // veces que late su luz por bucle
    val tamano: Float      // tamaño relativo
)

// Luciérnagas que vuelan por la pantalla con luz que late y alitas que aletean
@Composable
fun Luciernagas(
    modifier: Modifier = Modifier,
    cantidad: Int = 26
) {
    val luciernagas = remember {
        List(cantidad) {
            Luciernaga(
                x = Random.nextFloat(),
                y = Random.nextFloat(),
                ampX = 20f + Random.nextFloat() * 40f,
                ampY = 15f + Random.nextFloat() * 35f,
                vueltas = 1 + Random.nextInt(2),
                fase = Random.nextFloat(),
                parpadeo = 2 + Random.nextInt(3),
                tamano = 0.7f + Random.nextFloat() * 0.6f
            )
        }
    }

    val transicion = rememberInfiniteTransition(label = "luciernagas")
    // Movimiento lento de vuelo
    val tiempo by transicion.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(14000, easing = LinearEasing)),
        label = "vuelo"
    )
    // Aleteo rápido de las alas
    val aleteo by transicion.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(180, easing = LinearEasing)),
        label = "aleteo"
    )

    Canvas(modifier) {
        luciernagas.forEach { l ->
            val angulo = 2 * PI * (tiempo * l.vueltas + l.fase)
            // Trayectoria suave tipo "ocho" alrededor de su punto base
            val x = l.x * size.width +
                    (sin(angulo) * l.ampX.dp.toPx()).toFloat() +
                    (sin(angulo * 2) * l.ampX.dp.toPx() * 0.3f).toFloat()
            val y = l.y * size.height +
                    (cos(angulo) * l.ampY.dp.toPx()).toFloat() +
                    (sin(angulo * 3) * l.ampY.dp.toPx() * 0.2f).toFloat()

            // Brillo que late
            val brillo = ((sin(2 * PI * (tiempo * l.parpadeo + l.fase * 3)) + 1) / 2).toFloat()
            val radioLuz = 11.dp.toPx() * l.tamano * (0.7f + 0.3f * brillo)

            // Halo de luz
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(LuzLuciernaga.copy(alpha = 0.25f + 0.6f * brillo), Color.Transparent),
                    center = Offset(x, y),
                    radius = radioLuz
                ),
                radius = radioLuz,
                center = Offset(x, y)
            )

            // Alitas que aletean (dos óvalos translúcidos a los lados)
            val apertura = 0.35f + 0.65f * abs(sin(2 * PI * (aleteo + l.fase))).toFloat()
            val anchoAla = 5.dp.toPx() * l.tamano * apertura
            val altoAla = 3.dp.toPx() * l.tamano
            val alturaAla = y - 2.dp.toPx() * l.tamano
            drawOval(
                color = Color.White.copy(alpha = 0.35f),
                topLeft = Offset(x - anchoAla - 1.dp.toPx(), alturaAla - altoAla / 2),
                size = Size(anchoAla, altoAla)
            )
            drawOval(
                color = Color.White.copy(alpha = 0.35f),
                topLeft = Offset(x + 1.dp.toPx(), alturaAla - altoAla / 2),
                size = Size(anchoAla, altoAla)
            )

            // Cuerpo brillante
            drawCircle(
                color = CuerpoLuciernaga.copy(alpha = 0.6f + 0.4f * brillo),
                radius = 2.2.dp.toPx() * l.tamano,
                center = Offset(x, y)
            )
        }
    }
}