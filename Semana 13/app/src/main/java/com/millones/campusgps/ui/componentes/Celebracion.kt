package com.millones.campusgps.ui.componentes

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random

// ---------------------------------------------------------------------------
// Transición de éxito al iniciar sesión y al crear la cuenta
// ---------------------------------------------------------------------------

// Tamaño del check y proporción del círculo verde dentro de él (igual que en CheckAnimado)
private val TAMANO_CHECK = 150.dp
private const val PROPORCION_CIRCULO = 0.66f

// Verde más claro que CampusColores.Exito, solo para el check y el círculo que crece
private val VerdeCelebracion = Color(0xFF34D399)

private data class Papelito(
    val x: Float,
    val velocidad: Int,
    val fase: Float,
    val ancho: Float,
    val alto: Float,
    val color: Color,
    val vueltas: Int,
    val vaiven: Float
)

// Confeti de colores que cae girando sin parar (el de la PokeDex)
@Composable
fun Confeti(
    modifier: Modifier = Modifier,
    cantidad: Int = 45
) {
    val colores = listOf(
        CampusColores.AzulCielo, CampusColores.VerdeMenta, CampusColores.Ambar,
        CampusColores.Violeta, CampusColores.Error, Color.White
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

// Check que se dibuja solo: primero el anillo verde, luego se rellena con rebote
// y después se traza la palomita blanca; al terminar salen ondas.
@Composable
fun CheckAnimado(
    modifier: Modifier = Modifier,
    color: Color = VerdeCelebracion,
    tamano: Dp = 110.dp,
    retrasoMs: Long = 0L
) {
    val anillo = remember { Animatable(0f) }
    val relleno = remember { Animatable(0f) }
    val trazo = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        delay(retrasoMs)
        anillo.animateTo(1f, tween(450, easing = FastOutSlowInEasing))
        launch { relleno.animateTo(1f, spring(dampingRatio = 0.45f, stiffness = Spring.StiffnessMediumLow)) }
        trazo.animateTo(1f, tween(380, easing = FastOutSlowInEasing))
    }

    val transicion = rememberInfiniteTransition(label = "ondasCheck")
    val onda by transicion.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1600, easing = LinearEasing)),
        label = "ondaCheck"
    )

    Canvas(modifier = modifier.size(tamano)) {
        val r = size.minDimension / 2f
        val radio = r * PROPORCION_CIRCULO
        val grosor = radio * 0.16f

        // Ondas cuando el check ya está completo
        if (trazo.value >= 1f) {
            listOf(onda, (onda + 0.5f) % 1f).forEach { t ->
                drawCircle(
                    color = color.copy(alpha = (1f - t) * 0.4f),
                    radius = radio + (r - radio) * t
                )
            }
        }

        // Relleno que crece con rebote
        drawCircle(color = color, radius = radio * relleno.value)

        // Anillo que se traza alrededor
        drawArc(
            color = color,
            startAngle = -90f,
            sweepAngle = 360f * anillo.value,
            useCenter = false,
            topLeft = Offset(center.x - radio, center.y - radio),
            size = Size(radio * 2f, radio * 2f),
            style = Stroke(width = grosor, cap = StrokeCap.Round)
        )

        // Palomita que se dibuja de izquierda a derecha
        if (trazo.value > 0f) {
            val ruta = Path().apply {
                moveTo(center.x - radio * 0.42f, center.y + radio * 0.02f)
                lineTo(center.x - radio * 0.10f, center.y + radio * 0.34f)
                lineTo(center.x + radio * 0.45f, center.y - radio * 0.30f)
            }
            val medida = PathMeasure()
            medida.setPath(ruta, false)
            val parcial = Path()
            medida.getSegment(0f, medida.length * trazo.value, parcial, true)
            drawPath(
                path = parcial,
                color = Color.White,
                style = Stroke(width = grosor, cap = StrokeCap.Round, join = StrokeJoin.Round)
            )
        }
    }
}

// Cuenta creada: se oscurece el fondo, se dibuja el check verde (con confeti)
// y luego su círculo crece hasta tapar toda la pantalla; ahí se avisa para navegar.
// Dura unos 2 segundos en total.
@Composable
fun TransicionExito(
    conConfeti: Boolean,
    onTerminar: () -> Unit
) {
    val fondo = remember { Animatable(0f) }
    val expansion = remember { Animatable(0f) }
    val onTerminarActual by rememberUpdatedState(onTerminar)

    LaunchedEffect(Unit) {
        fondo.animateTo(1f, tween(200))
        // Lo justo para que el check termine de dibujarse
        delay(1050L)
        expansion.animateTo(1f, tween(500, easing = FastOutSlowInEasing))
        onTerminarActual()
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(CampusColores.AzulNoche.copy(alpha = 0.75f * fondo.value))
            // Bloquea los toques a la pantalla de atrás
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { }
    ) {
        if (conConfeti) {
            Confeti(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer { alpha = fondo.value }
            )
        }

        // Círculo verde que crece desde el check hasta cubrir la pantalla
        Canvas(modifier = Modifier.fillMaxSize()) {
            val e = expansion.value
            if (e > 0f) {
                val radioInicial = TAMANO_CHECK.toPx() / 2f * PROPORCION_CIRCULO
                val radioFinal = sqrt(size.width * size.width + size.height * size.height) / 2f + 8f
                drawCircle(
                    color = VerdeCelebracion,
                    radius = radioInicial + (radioFinal - radioInicial) * e
                )
            }
        }

        // El check se desvanece mientras el círculo crece
        CheckAnimado(
            tamano = TAMANO_CHECK,
            retrasoMs = 100L,
            modifier = Modifier.graphicsLayer {
                alpha = (1f - expansion.value * 2f).coerceIn(0f, 1f)
            }
        )
    }
}
