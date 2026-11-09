package com.tecsup.pokemon.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

// Luz amarilla difuminada que crece y se atenúa en bucle
@Composable
fun HaloPulsante(
    modifier: Modifier = Modifier,
    color: Color = Color(0xFFFFE27A)
) {
    val transicion = rememberInfiniteTransition(label = "halo")
    val escala by transicion.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(tween(1400, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "escalaHalo"
    )

    Canvas(modifier) {
        val radio = size.minDimension / 2f * escala
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(color.copy(alpha = 0.9f), color.copy(alpha = 0.35f), Color.Transparent),
                center = center,
                radius = radio
            ),
            radius = radio,
            center = center
        )
    }
}

// Rayos de luz que giran lentamente
@Composable
fun RayosGiratorios(
    modifier: Modifier = Modifier,
    color: Color = Color(0xFFFFD54F),
    cantidad: Int = 12
) {
    val transicion = rememberInfiniteTransition(label = "rayos")
    val giro by transicion.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(16000, easing = LinearEasing)),
        label = "giroRayos"
    )

    Canvas(modifier) {
        val radio = size.minDimension / 2f
        rotate(giro) {
            repeat(cantidad) { i ->
                val angulo = 2 * PI * i / cantidad
                val inicio = Offset(
                    center.x + (radio * 0.45f * cos(angulo)).toFloat(),
                    center.y + (radio * 0.45f * sin(angulo)).toFloat()
                )
                val fin = Offset(
                    center.x + (radio * 0.95f * cos(angulo)).toFloat(),
                    center.y + (radio * 0.95f * sin(angulo)).toFloat()
                )
                // Rayos alternados: uno grueso y otro delgado
                drawLine(
                    color = color.copy(alpha = if (i % 2 == 0) 0.7f else 0.4f),
                    start = inicio,
                    end = fin,
                    strokeWidth = if (i % 2 == 0) 10.dp.toPx() else 6.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }
        }
    }
}

// Destellos de 4 puntas que titilan; posiciones en fracciones (0 a 1) del área
@Composable
fun Destellos(
    posiciones: List<Offset>,
    modifier: Modifier = Modifier,
    color: Color = Color(0xFFFFD54F)
) {
    val transicion = rememberInfiniteTransition(label = "destellos")
    val fase by transicion.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1800, easing = LinearEasing)),
        label = "faseDestellos"
    )

    Canvas(modifier) {
        posiciones.forEachIndexed { i, p ->
            // Cada destello va a destiempo de los demás
            val brillo = ((sin((fase + i * 0.27f) * 2 * PI)).toFloat() + 1f) / 2f
            val tamano = 7.dp.toPx() + 8.dp.toPx() * brillo
            dibujarDestello(
                centro = Offset(p.x * size.width, p.y * size.height),
                tamano = tamano,
                color = color.copy(alpha = 0.3f + 0.7f * brillo)
            )
        }
    }
}

private fun DrawScope.dibujarDestello(centro: Offset, tamano: Float, color: Color) {
    val forma = Path().apply {
        moveTo(centro.x, centro.y - tamano)
        quadraticTo(centro.x, centro.y, centro.x + tamano, centro.y)
        quadraticTo(centro.x, centro.y, centro.x, centro.y + tamano)
        quadraticTo(centro.x, centro.y, centro.x - tamano, centro.y)
        quadraticTo(centro.x, centro.y, centro.x, centro.y - tamano)
        close()
    }
    drawPath(forma, color)
}

// Datos de cada papelito de confeti
private data class Papelito(
    val x: Float,          // posición horizontal (0 a 1)
    val velocidad: Int,    // 1 o 2: vueltas completas por ciclo (así el bucle no salta)
    val fase: Float,
    val ancho: Float,
    val alto: Float,
    val color: Color,
    val vueltas: Int,      // giros completos por caída
    val vaiven: Float      // cuánto se mueve de lado a lado (dp)
)

// Confeti de colores que cae girando sin parar
@Composable
fun Confeti(
    modifier: Modifier = Modifier,
    cantidad: Int = 45
) {
    val colores = listOf(
        Color(0xFFE53935), Color(0xFF1E88E5), Color(0xFFFDD835),
        Color(0xFF43A047), Color(0xFFFB8C00), Color(0xFF8E24AA)
    )
    val papelitos = remember {
        List(cantidad) {
            Papelito(
                x = Random.nextFloat(),
                velocidad = if (Random.nextBoolean()) 1 else 2,
                fase = Random.nextFloat(),
                ancho = 6f + Random.nextFloat() * 5f,
                alto = 10f + Random.nextFloat() * 8f,
                color = colores.random(),
                vueltas = 1 + Random.nextInt(3),
                vaiven = 8f + Random.nextFloat() * 16f
            )
        }
    }

    val transicion = rememberInfiniteTransition(label = "confeti")
    val tiempo by transicion.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(7000, easing = LinearEasing)),
        label = "tiempoConfeti"
    )

    Canvas(modifier) {
        papelitos.forEach { p ->
            val avance = (tiempo * p.velocidad + p.fase) % 1f
            val y = avance * (size.height + 80f) - 40f
            val x = p.x * size.width + sin((avance * 4f + p.fase) * 2 * PI).toFloat() * p.vaiven.dp.toPx()
            val ancho = p.ancho.dp.toPx()
            val alto = p.alto.dp.toPx()

            rotate(degrees = avance * 360f * p.vueltas, pivot = Offset(x, y)) {
                drawRoundRect(
                    color = p.color,
                    topLeft = Offset(x - ancho / 2, y - alto / 2),
                    size = Size(ancho, alto),
                    cornerRadius = CornerRadius(2.dp.toPx())
                )
            }
        }
    }
}

// Texto en arco cuyas letras aparecen una por una con rebote
@Composable
fun TextoCurvo(
    texto: String,
    estilo: TextStyle,
    arcoGrados: Float = 16f,
    retrasoMs: Int = 0,
    pasoMs: Int = 55
) {
    val letras = texto.toList()
    val total = letras.size
    val animaciones = remember(texto) { List(total) { Animatable(0f) } }
    val radio = with(LocalDensity.current) { 220.dp.toPx() }

    LaunchedEffect(texto) {
        delay(retrasoMs.toLong())
        animaciones.forEachIndexed { i, animacion ->
            launch {
                delay((i * pasoMs).toLong())
                animacion.animateTo(
                    targetValue = 1f,
                    animationSpec = spring(dampingRatio = 0.45f, stiffness = Spring.StiffnessMediumLow)
                )
            }
        }
    }

    Row(verticalAlignment = Alignment.Bottom) {
        letras.forEachIndexed { i, letra ->
            // Ángulo de cada letra en el arco y cuánto baja para formar la curva
            val angulo = if (total > 1) -arcoGrados / 2f + arcoGrados * i / (total - 1) else 0f
            val curva = radio * (1f - cos(Math.toRadians(angulo.toDouble())).toFloat())

            Text(
                text = letra.toString(),
                style = estilo,
                modifier = Modifier.graphicsLayer {
                    val v = animaciones[i].value
                    rotationZ = angulo
                    translationY = curva + (1f - v) * -40f   // cae desde arriba
                    scaleX = v
                    scaleY = v
                    alpha = v.coerceIn(0f, 1f)
                }
            )
        }
    }
}