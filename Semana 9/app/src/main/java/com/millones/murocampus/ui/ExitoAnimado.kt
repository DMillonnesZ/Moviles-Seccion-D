package com.millones.murocampus.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.millones.murocampus.ui.theme.Azul
import com.millones.murocampus.ui.theme.BordeTarjeta
import com.millones.murocampus.ui.theme.Menta
import com.millones.murocampus.ui.theme.TarjetaGris
import kotlinx.coroutines.delay
import kotlin.math.sin
import kotlin.random.Random

private val VerdeExito = Color(0xFF22C58B)

private val coloresConfeti = listOf(
    Azul, Color(0xFFF5A623), Menta,
    Color(0xFFD0475B), Color(0xFF8E5BD0), Color(0xFF2B7BD0)
)

private class Particula(
    val x: Float,          // posición horizontal (0 a 1)
    val velocidad: Float,  // qué tan rápido cae
    val retardo: Float,    // cuándo empieza a caer (0 a 1)
    val tamano: Float,     // en dp
    val giro: Float,       // grados que gira mientras cae
    val vaiven: Float,     // amplitud del balanceo, en dp
    val frecuencia: Float, // rapidez del balanceo
    val color: Color
)

/**
 * Check que se dibuja solo + confeti. Se cierra solo o al tocar la pantalla.
 */
@Composable
fun ExitoAnimado(
    mensaje: String,
    onTerminar: () -> Unit,
    confeti: Boolean = true
) {
    val trazo = remember { Animatable(0f) }
    val caida = remember { Animatable(0f) }
    val particulas = remember {
        List(80) {
            Particula(
                x = Random.nextFloat(),
                velocidad = 0.6f + Random.nextFloat() * 0.6f,
                retardo = Random.nextFloat() * 0.3f,
                tamano = 6f + Random.nextFloat() * 8f,
                giro = 360f + Random.nextFloat() * 720f,
                vaiven = 10f + Random.nextFloat() * 30f,
                frecuencia = 6f + Random.nextFloat() * 10f,
                color = coloresConfeti.random()
            )
        }
    }

    LaunchedEffect(Unit) { trazo.animateTo(1f, tween(900, easing = FastOutSlowInEasing)) }
    LaunchedEffect(Unit) { if (confeti) caida.animateTo(1f, tween(2600, easing = LinearEasing)) }
    LaunchedEffect(Unit) {
        delay(2600)
        onTerminar()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xB3000714))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onTerminar() },
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .padding(32.dp)
                .background(TarjetaGris, RoundedCornerShape(24.dp))
                .border(1.dp, BordeTarjeta, RoundedCornerShape(24.dp))
                .padding(horizontal = 32.dp, vertical = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Canvas(modifier = Modifier.size(96.dp)) {
                val t = trazo.value
                val escala = (t / 0.35f).coerceIn(0f, 1f)          // primero crece el círculo
                val avance = ((t - 0.35f) / 0.45f).coerceIn(0f, 1f) // luego se dibuja el check

                drawCircle(color = VerdeExito, radius = (size.minDimension / 2f) * escala)

                if (avance > 0f) {
                    val w = size.width
                    val h = size.height
                    val camino = Path().apply {
                        moveTo(w * 0.28f, h * 0.52f)
                        lineTo(w * 0.44f, h * 0.67f)
                        lineTo(w * 0.73f, h * 0.36f)
                    }
                    val medida = PathMeasure()
                    medida.setPath(camino, false)
                    val parcial = Path()
                    medida.getSegment(0f, medida.length * avance, parcial, true)
                    drawPath(
                        path = parcial,
                        color = Color.White,
                        style = Stroke(
                            width = 9.dp.toPx(),
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round
                        )
                    )
                }
            }
            Text(
                mensaje,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )
        }

        if (confeti) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val t = caida.value
                particulas.forEach { p ->
                    val local = ((t - p.retardo) / (1f - p.retardo)).coerceIn(0f, 1f)
                    if (local > 0f && local < 1f) {
                        val x = p.x * size.width + sin(local * p.frecuencia) * p.vaiven.dp.toPx()
                        val y = -20.dp.toPx() + local * p.velocidad * (size.height + 40.dp.toPx())
                        val alfa = (1f - ((local - 0.8f) / 0.2f)).coerceIn(0f, 1f)
                        val lado = p.tamano.dp.toPx()
                        rotate(degrees = local * p.giro, pivot = Offset(x, y)) {
                            drawRect(
                                color = p.color.copy(alpha = alfa),
                                topLeft = Offset(x - lado / 2f, y - lado / 2f),
                                size = Size(lado, lado * 0.6f)
                            )
                        }
                    }
                }
            }
        }
    }
}
