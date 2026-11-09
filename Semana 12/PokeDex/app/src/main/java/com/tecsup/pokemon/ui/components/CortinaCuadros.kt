package com.tecsup.pokemon.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.sin

// Cortina de cuadros que cubre la pantalla, ejecuta la acción y luego se retira.
// accion = null: no se muestra nada.
@Composable
fun CortinaCuadros(
    accion: (() -> Unit)?,
    onTerminar: () -> Unit,
    color: Color = Color.White,
    tamanoCuadro: Dp = 44.dp,
    duracionMs: Int = 700,      // duración de cubrir y de descubrir (cada una)
    pausaMs: Long = 180         // tiempo con la pantalla totalmente cubierta
) {
    // 0 = vacío, 1 = pantalla cubierta, 2 = cuadros retirados
    val progreso = remember { Animatable(0f) }

    LaunchedEffect(accion) {
        if (accion != null) {
            progreso.snapTo(0f)
            progreso.animateTo(1f, tween(duracionMs, easing = LinearEasing))   // cubrir
            accion()                                                            // cambio de pantalla oculto
            delay(pausaMs)
            progreso.animateTo(2f, tween(duracionMs, easing = LinearEasing))   // descubrir
            onTerminar()
            progreso.snapTo(0f)
        }
    }

    if (accion == null && progreso.value == 0f) return

    Box(
        modifier = Modifier
            .fillMaxSize()
            // Bloquea los toques mientras corre la transición
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        awaitPointerEvent().changes.forEach { it.consume() }
                    }
                }
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val lado = tamanoCuadro.toPx()
            val columnas = ceil(size.width / lado).toInt()
            val filas = ceil(size.height / lado).toInt()
            val p = progreso.value

            for (fila in 0 until filas) {
                for (columna in 0 until columnas) {
                    // Orden de aparición: oleada diagonal + un poco de azar
                    val diagonal = (columna.toFloat() / columnas + fila.toFloat() / filas) / 2f
                    val orden = diagonal * 0.7f + ruido(columna, fila) * 0.3f

                    val escala = if (p <= 1f) {
                        // Cubrir: cada cuadro salta hacia el frente con rebote
                        val t = ((p - orden * 0.6f) / 0.4f).coerceIn(0f, 1f)
                        rebote(t)
                    } else {
                        // Descubrir: cada cuadro se encoge en el mismo orden
                        val t = ((p - 1f - orden * 0.6f) / 0.4f).coerceIn(0f, 1f)
                        1f - t * t
                    }

                    if (escala > 0f) {
                        // +1 px para que no queden rayitas entre cuadros
                        val ladoActual = (lado + 1f) * escala
                        drawRoundRect(
                            color = color,
                            topLeft = Offset(
                                columna * lado + (lado - ladoActual) / 2f,
                                fila * lado + (lado - ladoActual) / 2f
                            ),
                            size = Size(ladoActual, ladoActual),
                            // Esquinas redondeadas mientras crecen, rectas al cubrir
                            cornerRadius = CornerRadius(6.dp.toPx() * (1f - escala.coerceAtMost(1f)))
                        )
                    }
                }
            }
        }
    }
}

// Curva con rebote: pasa un poco de 1 y vuelve (efecto de salir hacia el frente)
private fun rebote(t: Float): Float {
    val c1 = 1.70158f
    val c3 = c1 + 1f
    val x = t - 1f
    return 1f + c3 * x * x * x + c1 * x * x
}

// Número "aleatorio" fijo para cada cuadro (siempre el mismo en cada frame)
private fun ruido(columna: Int, fila: Int): Float {
    val v = sin(columna * 12.9898f + fila * 78.233f) * 43758.5453f
    return v - floor(v)
}