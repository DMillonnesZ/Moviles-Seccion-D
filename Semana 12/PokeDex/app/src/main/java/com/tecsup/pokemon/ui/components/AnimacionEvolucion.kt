package com.tecsup.pokemon.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.tecsup.pokemon.domain.model.EtapaEvolucion
import kotlinx.coroutines.launch
import kotlin.math.sin

// Animación de evolución como en los juegos:
// 1. Las siluetas blancas de los dos Pokémon se alternan cada vez más rápido.
// 2. Destello blanco.
// 3. Aparece el Pokémon evolucionado con rayos y destellos. Tocar para continuar.
@Composable
fun AnimacionEvolucion(
    desde: EtapaEvolucion,
    hacia: EtapaEvolucion,
    color: Color,
    onTerminar: () -> Unit
) {
    val progreso = remember { Animatable(0f) }
    val destello = remember { Animatable(0f) }
    val aparicion = remember { Animatable(0.6f) }
    var revelado by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        progreso.animateTo(1f, tween(3000, easing = LinearEasing))
        destello.animateTo(1f, tween(200))
        revelado = true
        launch { aparicion.animateTo(1f, spring(dampingRatio = 0.4f)) }
        destello.animateTo(0f, tween(700))
    }

    val nombreDesde = desde.nombre.replaceFirstChar { it.uppercase() }
    val nombreHacia = hacia.nombre.replaceFirstChar { it.uppercase() }

    Dialog(
        onDismissRequest = { if (revelado) onTerminar() },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.88f))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { if (revelado) onTerminar() }
        ) {
            // Rayos y halo del color del tipo
            RayosGiratorios(
                color = lerp(color, Color.White, 0.35f),
                modifier = Modifier
                    .size(340.dp)
                    .graphicsLayer { alpha = if (revelado) 1f else 0.45f }
            )
            HaloPulsante(
                color = lerp(color, Color.White, 0.5f),
                modifier = Modifier.size(260.dp)
            )

            if (!revelado) {
                // Siluetas blancas que se alternan cada vez más rápido
                val p = progreso.value
                val mostrarDesde = ((p * p) * 18f).toInt() % 2 == 0
                AsyncImage(
                    model = if (mostrarDesde) desde.imagenUrl else hacia.imagenUrl,
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(Color.White),
                    modifier = Modifier
                        .size(210.dp)
                        .graphicsLayer {
                            val latido = 1f + 0.05f * sin(p * 40f)
                            scaleX = latido
                            scaleY = latido
                        }
                )
            } else {
                // Pokémon evolucionado con destellos
                Destellos(
                    posiciones = listOf(
                        Offset(0.15f, 0.2f), Offset(0.85f, 0.25f), Offset(0.2f, 0.8f),
                        Offset(0.8f, 0.78f), Offset(0.5f, 0.08f)
                    ),
                    modifier = Modifier.size(300.dp)
                )
                AsyncImage(
                    model = hacia.imagenUrl,
                    contentDescription = nombreHacia,
                    modifier = Modifier
                        .size(220.dp)
                        .graphicsLayer {
                            scaleX = aparicion.value
                            scaleY = aparicion.value
                        }
                )
            }

            // Mensaje abajo
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 120.dp, start = 24.dp, end = 24.dp)
            ) {
                Text(
                    text = if (!revelado) "¿Qué? ¡$nombreDesde está evolucionando!"
                    else "¡$nombreDesde evolucionó a $nombreHacia!",
                    color = Color.White,
                    fontFamily = FuenteMadera,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center
                )
                if (revelado) {
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = "Toca para continuar",
                        color = Color.White.copy(alpha = 0.7f),
                        fontFamily = FuenteMadera,
                        fontSize = 13.sp
                    )
                }
            }

            // Destello blanco que cubre todo
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White.copy(alpha = destello.value))
            )
        }
    }
}