package com.millones.campusgps.ui.componentes

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

// ---------------------------------------------------------------------------
// Fondo vivo: dos rutas que salen de las esquinas de abajo, rodean la tarjeta
// por los bordes y llegan al pin del logo, sin pasar por encima de las letras.
// ---------------------------------------------------------------------------

// Lo que el logo le cuenta al fondo: dónde está el pin, qué zona no se debe tapar
// y cuántas veces llegó la ruta (para que el pin salte)
class EstadoRuta {
    var destino by mutableStateOf<Offset?>(null)
    var zonaLibre by mutableStateOf<Rect?>(null)
    var llegadas by mutableIntStateOf(0)
}

val LocalEstadoRuta = staticCompositionLocalOf<EstadoRuta?> { null }

// El pin del logo marca aquí su posición (el centro de la cabeza del pin)
@Composable
fun Modifier.marcarDestino(): Modifier {
    val estado = LocalEstadoRuta.current ?: return this
    return this.onGloballyPositioned { coordenadas ->
        val limites = coordenadas.boundsInRoot()
        estado.destino = Offset(limites.center.x, limites.top + limites.height * 0.43f)
    }
}

// El bloque del logo y el nombre marca la zona por donde la ruta no debe pasar
@Composable
fun Modifier.marcarZonaLibre(): Modifier {
    val estado = LocalEstadoRuta.current ?: return this
    return this.onGloballyPositioned { coordenadas ->
        estado.zonaLibre = coordenadas.boundsInRoot()
    }
}

// Pone la ruta viva detrás del contenido y comparte su estado con el logo.
// duracionMs = lo que tarda la ruta en llegar al pin.
@Composable
fun ConRutaViva(
    duracionMs: Int = 3800,
    contenido: @Composable () -> Unit
) {
    val estado = remember { EstadoRuta() }
    CompositionLocalProvider(LocalEstadoRuta provides estado) {
        Box(modifier = Modifier.fillMaxSize()) {
            RutaViva(estado = estado, duracionMs = duracionMs, modifier = Modifier.fillMaxSize())
            contenido()
        }
    }
}

@Composable
private fun RutaViva(
    estado: EstadoRuta,
    duracionMs: Int,
    modifier: Modifier = Modifier
) {
    val avance = remember { Animatable(0f) }
    val opacidad = remember { Animatable(0f) }

    // Ciclo: aparece, se dibuja hasta el pin, avisa la llegada, espera y se desvanece
    LaunchedEffect(Unit) {
        while (true) {
            avance.snapTo(0f)
            opacidad.animateTo(1f, tween(300))
            avance.animateTo(1f, tween(duracionMs, easing = FastOutSlowInEasing))
            estado.llegadas++
            delay(1000)
            opacidad.animateTo(0f, tween(600))
            delay(400)
        }
    }

    val transicion = rememberInfiniteTransition(label = "paradas")
    val pulso by transicion.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1800, easing = LinearEasing)),
        label = "pulsoParadas"
    )

    Canvas(modifier) {
        // Hasta que el logo diga dónde está, no se dibuja nada
        val destino = estado.destino ?: return@Canvas
        val zona = estado.zonaLibre ?: Rect(destino, destino)

        val borde = 12.dp.toPx()            // distancia de la ruta al borde de la pantalla
        val llegada = 30.dp.toPx()          // la ruta se detiene antes del pin
        val arribaZona = (zona.top - 28.dp.toPx()).coerceAtLeast(borde)
        val w = size.width
        val h = size.height

        // Izquierda: sube por el borde y entra al pin de lado
        val rutaIzquierda = Path().apply {
            moveTo(borde, h + 20f)
            lineTo(borde, destino.y + 80.dp.toPx())
            quadraticTo(borde, destino.y, (destino.x - llegada).coerceAtLeast(borde + 1f), destino.y)
        }
        // Derecha: sube por el borde, pasa por encima del título y baja al pin
        val rutaDerecha = Path().apply {
            moveTo(w - borde, h + 20f)
            lineTo(w - borde, arribaZona + 60.dp.toPx())
            quadraticTo(w - borde, arribaZona, w - borde - 60.dp.toPx(), arribaZona)
            lineTo(destino.x + 40.dp.toPx(), arribaZona)
            quadraticTo(destino.x, arribaZona, destino.x, destino.y - llegada)
        }

        listOf(rutaIzquierda, rutaDerecha).forEach { ruta ->
            dibujarRuta(ruta, avance.value, opacidad.value, pulso)
        }
    }
}

private fun DrawScope.dibujarRuta(ruta: Path, avance: Float, opacidad: Float, pulso: Float) {
    val medida = PathMeasure().apply { setPath(ruta, false) }
    val largo = medida.length
    val punteado = PathEffect.dashPathEffect(floatArrayOf(7.dp.toPx(), 7.dp.toPx()))

    // Ruta completa muy tenue (el camino que falta recorrer)
    drawPath(
        path = ruta,
        color = Color.White.copy(alpha = 0.06f),
        style = Stroke(width = 1.5.dp.toPx(), pathEffect = punteado)
    )

    // Tramo recorrido: un halo ancho y suave debajo, la línea punteada encima
    val tramo = Path()
    medida.getSegment(0f, largo * avance, tramo, true)
    drawPath(
        path = tramo,
        color = CampusColores.VerdeMenta.copy(alpha = 0.10f * opacidad),
        style = Stroke(width = 9.dp.toPx(), cap = StrokeCap.Round)
    )
    drawPath(
        path = tramo,
        color = CampusColores.VerdeMenta.copy(alpha = 0.6f * opacidad),
        style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round, pathEffect = punteado)
    )

    // Paradas: laten y se encienden en menta cuando la ruta pasa por ellas
    listOf(0.30f, 0.62f).forEachIndexed { i, fraccion ->
        val parada = medida.getPosition(largo * fraccion)
        val encendida = avance >= fraccion
        val t = (pulso + i * 0.4f) % 1f
        drawCircle(
            color = (if (encendida) CampusColores.VerdeMenta else CampusColores.AzulCielo)
                .copy(alpha = (1f - t) * 0.3f),
            radius = 4.dp.toPx() + 12.dp.toPx() * t,
            center = parada
        )
        drawCircle(
            color = if (encendida) CampusColores.VerdeMenta.copy(alpha = opacidad.coerceAtLeast(0.4f))
            else Color.White.copy(alpha = 0.4f),
            radius = 3.5.dp.toPx(),
            center = parada
        )
    }

    // Luz en la punta mientras avanza
    if (avance > 0.001f && avance < 0.999f) {
        val cabeza = medida.getPosition(largo * avance)
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(CampusColores.VerdeMenta.copy(alpha = 0.55f * opacidad), Color.Transparent),
                center = cabeza,
                radius = 16.dp.toPx()
            ),
            radius = 16.dp.toPx(),
            center = cabeza
        )
        drawCircle(
            color = Color.White.copy(alpha = opacidad),
            radius = 3.dp.toPx(),
            center = cabeza
        )
    }
}
