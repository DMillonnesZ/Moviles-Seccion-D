package com.millones.campusgps.ui.componentes

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt

private val TEXTOS_CALIFICACION = listOf("Malo", "Regular", "Bueno", "Muy bueno", "Excelente")

// Cinco estrellas de solo lectura (valor de 0 a 5; se redondea)
@Composable
fun Estrellas(
    valor: Float,
    modifier: Modifier = Modifier,
    tamano: Dp = 14.dp
) {
    val llenas = valor.roundToInt().coerceIn(0, 5)
    Row(modifier = modifier) {
        repeat(5) { i ->
            Icon(
                imageVector = if (i < llenas) Icons.Rounded.Star else Icons.Rounded.StarBorder,
                contentDescription = null,
                tint = if (i < llenas) VidrioMapa.Estrella else Color.White.copy(alpha = 0.35f),
                modifier = Modifier.size(tamano)
            )
        }
    }
}

// Cinco estrellas grandes para calificar; la elegida rebota y abajo dice qué significa
@Composable
fun SelectorEstrellas(
    valor: Int,
    onCambiar: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            repeat(5) { i ->
                val numero = i + 1
                val rebote = remember { Animatable(1f) }
                LaunchedEffect(valor) {
                    if (valor == numero) {
                        rebote.animateTo(1.3f, tween(110))
                        rebote.animateTo(1f, spring(dampingRatio = 0.35f))
                    }
                }
                Icon(
                    imageVector = if (numero <= valor) Icons.Rounded.Star else Icons.Rounded.StarBorder,
                    contentDescription = "$numero estrellas",
                    tint = if (numero <= valor) VidrioMapa.Estrella else Color.White.copy(alpha = 0.4f),
                    modifier = Modifier
                        .size(42.dp)
                        .graphicsLayer {
                            scaleX = rebote.value
                            scaleY = rebote.value
                        }
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { onCambiar(numero) }
                )
            }
        }
        Spacer(Modifier.height(4.dp))
        Text(
            text = if (valor in 1..5) TEXTOS_CALIFICACION[valor - 1] else "Toca una estrella",
            color = if (valor in 1..5) VidrioMapa.Estrella else Color.White.copy(alpha = 0.6f),
            fontFamily = FuenteCampus,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )
    }
}
