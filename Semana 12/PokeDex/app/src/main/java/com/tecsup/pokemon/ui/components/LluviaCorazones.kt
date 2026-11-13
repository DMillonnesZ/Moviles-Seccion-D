package com.tecsup.pokemon.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.sin
import kotlin.random.Random

private val ColoresCorazones = listOf(
    Color(0xFFE53935), Color(0xFFFF4F81), Color(0xFFFF8A80), Color(0xFFFF1744), Color(0xFFF06292)
)

// Datos de un corazón volador
private data class CorazonVolador(
    val id: Long,
    val inicioX: Float,     // posición inicial (dp)
    val inicioY: Float,
    val altura: Float,      // cuánto sube (dp)
    val desvio: Float,      // cuánto se va de lado (dp)
    val duracion: Int,      // ms
    val retraso: Long,      // ms antes de empezar
    val tamano: Float,      // dp
    val giro: Float,        // grados
    val color: Color
)

// Lluvia de corazones en toda la pantalla, como el "me encanta" de TikTok.
// Cada vez que 'disparo' aumenta, salen corazones y uno grande en el centro.
// No bloquea los toques de la pantalla de atrás.
@Composable
fun LluviaCorazones(
    disparo: Int,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val ancho = maxWidth.value
        val alto = maxHeight.value
        val corazones = remember { mutableStateListOf<CorazonVolador>() }
        val grande = remember { Animatable(0f) }

        LaunchedEffect(disparo) {
            if (disparo > 0) {
                // Corazón grande en el centro
                launch {
                    grande.snapTo(0f)
                    grande.animateTo(1f, tween(900))
                }
                // Lluvia de corazones desde la parte baja de la pantalla
                repeat(18) { i ->
                    corazones += CorazonVolador(
                        id = disparo * 1000L + i,
                        inicioX = Random.nextFloat() * (ancho - 40f),
                        inicioY = alto * (0.7f + Random.nextFloat() * 0.25f),
                        altura = alto * (0.45f + Random.nextFloat() * 0.45f),
                        desvio = Random.nextFloat() * 120f - 60f,
                        duracion = 1400 + Random.nextInt(1000),
                        retraso = Random.nextLong(0, 450),
                        tamano = 18f + Random.nextFloat() * 22f,
                        giro = Random.nextFloat() * 70f - 35f,
                        color = ColoresCorazones.random()
                    )
                }
            }
        }

        // Corazones que suben
        corazones.forEach { corazon ->
            key(corazon.id) {
                CorazonAnimado(corazon = corazon, onFin = { corazones.remove(corazon) })
            }
        }

        // Corazón grande: aparece con rebote y se desvanece
        if (grande.value in 0.001f..0.999f) {
            val t = grande.value
            Icon(
                imageVector = Icons.Rounded.Favorite,
                contentDescription = null,
                tint = Color(0xFFFF4F81),
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(120.dp)
                    .graphicsLayer {
                        val escala = when {
                            t < 0.25f -> t / 0.25f * 1.2f
                            t < 0.4f -> 1.2f - (t - 0.25f) / 0.15f * 0.2f
                            else -> 1f + (t - 0.4f) * 0.3f
                        }
                        scaleX = escala
                        scaleY = escala
                        alpha = if (t < 0.6f) 0.95f else 0.95f * (1f - (t - 0.6f) / 0.4f)
                    }
            )
        }
    }
}

@Composable
private fun CorazonAnimado(corazon: CorazonVolador, onFin: () -> Unit) {
    val progreso = remember { Animatable(0f) }

    LaunchedEffect(corazon.id) {
        delay(corazon.retraso)
        progreso.animateTo(1f, tween(corazon.duracion, easing = FastOutSlowInEasing))
        onFin()
    }

    Icon(
        imageVector = Icons.Rounded.Favorite,
        contentDescription = null,
        tint = corazon.color,
        modifier = Modifier
            .offset(x = corazon.inicioX.dp, y = corazon.inicioY.dp)
            .size(corazon.tamano.dp)
            .graphicsLayer {
                val t = progreso.value
                // Sube en zigzag hacia un lado
                translationX = (corazon.desvio * t + sin(t * PI * 3).toFloat() * 14f).dp.toPx()
                translationY = -(corazon.altura * t).dp.toPx()
                // Aparece creciendo y luego se achica un poco
                val escala = if (t < 0.15f) t / 0.15f * 1.2f else 1.2f - (t - 0.15f) * 0.4f
                scaleX = escala
                scaleY = escala
                rotationZ = corazon.giro * t
                // Invisible antes de empezar; se desvanece al final
                alpha = if (t == 0f) 0f else 1f - t * t
            }
    )
}