package com.tecsup.pokemon.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.SubcomposeAsyncImage
import com.tecsup.pokemon.domain.model.Carta
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin

private val DoradoHalo = Color(0xFFFFE27A)
private val ColoresHolograficos = listOf(
    Color(0xFFFFD54F), Color(0xFFFF6FB5), Color(0xFF7C4DFF),
    Color(0xFF40C4FF), Color(0xFF69F0AE), Color(0xFFFFD54F)
)

// Estados del carrusel: no dependen del resto del Detalle
sealed interface EstadoCartas {
    data object Cargando : EstadoCartas
    data class Lista(val cartas: List<Carta>) : EstadoCartas
    data object Vacio : EstadoCartas
    data class Error(val mensaje: String) : EstadoCartas
}

// Carrusel tipo coverflow: la carta del centro grande y brillante, las de los lados inclinadas
@Composable
fun CarruselCartas(
    estado: EstadoCartas,
    onCartaClick: (Carta) -> Unit,
    onReintentar: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 120.dp)
    ) {
        when (estado) {
            is EstadoCartas.Cargando -> CircularProgressIndicator(color = Color.White, strokeWidth = 3.dp)

            is EstadoCartas.Vacio -> MensajeCartas("Este Pokémon no tiene cartas disponibles")

            is EstadoCartas.Error -> Column(horizontalAlignment = Alignment.CenterHorizontally) {
                MensajeCartas(estado.mensaje)
                TextButton(onClick = onReintentar) {
                    Text("Reintentar", color = Color.White, fontFamily = FuenteMadera)
                }
            }

            is EstadoCartas.Lista -> Coverflow(cartas = estado.cartas, onCartaClick = onCartaClick)
        }
    }
}

// Mensaje sobre una pastilla crema para que se lea sobre el fondo
@Composable
private fun MensajeCartas(texto: String) {
    Text(
        text = texto,
        color = CafeMadera,
        fontFamily = FuenteMadera,
        fontSize = 14.sp,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(Color(0xFFFFF4DF).copy(alpha = 0.92f))
            .padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@Composable
private fun Coverflow(cartas: List<Carta>, onCartaClick: (Carta) -> Unit) {
    val estadoLista = rememberLazyListState()
    val anchoCarta = 150.dp

    // Ángulo que gira el borde holográfico y mueve el brillo
    val transicion = rememberInfiniteTransition(label = "holografico")
    val angulo by transicion.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(3200, easing = LinearEasing)),
        label = "anguloHolografico"
    )

    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        // Relleno a los lados para que la primera y la última carta puedan quedar al centro
        val relleno = ((maxWidth - anchoCarta) / 2).coerceAtLeast(0.dp)

        LazyRow(
            state = estadoLista,
            flingBehavior = rememberSnapFlingBehavior(lazyListState = estadoLista),
            contentPadding = PaddingValues(horizontal = relleno, vertical = 22.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            itemsIndexed(cartas, key = { _, carta -> carta.id }) { indice, carta ->
                CartaBrillante(
                    carta = carta,
                    angulo = angulo,
                    // Qué tan lejos del centro está: 0 en el centro, ±1 a una carta de distancia
                    distancia = {
                        val info = estadoLista.layoutInfo
                        val item = info.visibleItemsInfo.firstOrNull { it.index == indice }
                        if (item == null) {
                            1f
                        } else {
                            val centroVista = (info.viewportStartOffset + info.viewportEndOffset) / 2f
                            val centroItem = item.offset + item.size / 2f
                            ((centroItem - centroVista) / item.size).coerceIn(-1f, 1f)
                        }
                    },
                    onClick = { onCartaClick(carta) },
                    modifier = Modifier.width(anchoCarta)
                )
            }
        }
    }
}

// Carta con borde holográfico que gira, halo dorado y brillo que la recorre (más fuerte al centro)
@Composable
private fun CartaBrillante(
    carta: Carta,
    angulo: Float,
    distancia: () -> Float,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .aspectRatio(245f / 337f)
            // Coverflow: las de los lados más chicas, inclinadas y tenues
            .graphicsLayer {
                val d = distancia()
                val a = abs(d)
                val escala = 1f - 0.2f * a
                scaleX = escala
                scaleY = escala
                rotationY = -d * 28f
                alpha = 1f - 0.4f * a
                cameraDistance = 12f * density
            }
            // Halo dorado y borde holográfico
            .drawBehind {
                val foco = 1f - abs(distancia())
                val esquina = 10.dp.toPx()

                // Halo: varios contornos cada vez más grandes y suaves
                for (i in 1..5) {
                    val extra = i * 3.dp.toPx()
                    drawRoundRect(
                        color = DoradoHalo.copy(alpha = 0.10f * foco),
                        topLeft = Offset(-extra, -extra),
                        size = Size(size.width + extra * 2, size.height + extra * 2),
                        cornerRadius = CornerRadius(esquina + extra)
                    )
                }

                // Borde de colores que gira
                val radianes = Math.toRadians(angulo.toDouble())
                val r = max(size.width, size.height)
                val inicio = Offset(
                    center.x + (cos(radianes) * r).toFloat(),
                    center.y + (sin(radianes) * r).toFloat()
                )
                val fin = Offset(
                    center.x - (cos(radianes) * r).toFloat(),
                    center.y - (sin(radianes) * r).toFloat()
                )
                val grosor = 3.dp.toPx()
                drawRoundRect(
                    brush = Brush.linearGradient(ColoresHolograficos, start = inicio, end = fin),
                    topLeft = Offset(-grosor, -grosor),
                    size = Size(size.width + grosor * 2, size.height + grosor * 2),
                    cornerRadius = CornerRadius(esquina + grosor),
                    style = Stroke(width = grosor),
                    alpha = 0.5f + 0.5f * foco
                )
            }
            .clip(RoundedCornerShape(10.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
    ) {
        SubcomposeAsyncImage(
            model = carta.imagenPequena,
            contentDescription = carta.nombre,
            modifier = Modifier
                .fillMaxSize()
                // Brillo diagonal que recorre la carta
                .drawWithContent {
                    drawContent()
                    val foco = 1f - abs(distancia())
                    val avance = (angulo / 360f) * 2f - 0.5f
                    val x = size.width * avance
                    drawRect(
                        brush = Brush.linearGradient(
                            colors = listOf(Color.Transparent, Color.White.copy(alpha = 0.30f * foco), Color.Transparent),
                            start = Offset(x, 0f),
                            end = Offset(x + size.width * 0.5f, size.height)
                        )
                    )
                },
            loading = {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.background(Color(0xFFEDE0C8))
                ) {
                    CircularProgressIndicator(color = CafeMadera, strokeWidth = 2.dp, modifier = Modifier.size(22.dp))
                }
            }
        )
    }
}

// Carta en grande: entra girando como si se diera vuelta; se cierra tocando o con atrás
@Composable
fun VisorCarta(
    carta: Carta?,
    onCerrar: () -> Unit
) {
    if (carta == null) return

    Dialog(
        onDismissRequest = onCerrar,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        val entrada = remember(carta.id) { Animatable(0f) }
        LaunchedEffect(carta.id) {
            entrada.animateTo(1f, spring(dampingRatio = 0.6f, stiffness = Spring.StiffnessLow))
        }

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.7f))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onCerrar
                )
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                SubcomposeAsyncImage(
                    model = carta.imagenGrande,
                    contentDescription = carta.nombre,
                    modifier = Modifier
                        .fillMaxWidth(0.82f)
                        .aspectRatio(600f / 825f)
                        .graphicsLayer {
                            rotationY = (1f - entrada.value) * 90f
                            val escala = 0.7f + 0.3f * entrada.value
                            scaleX = escala
                            scaleY = escala
                            alpha = entrada.value.coerceIn(0f, 1f)
                            cameraDistance = 12f * density
                        }
                        .clip(RoundedCornerShape(12.dp)),
                    loading = {
                        Box(contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = Color.White)
                        }
                    }
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    text = carta.nombre,
                    color = Color.White,
                    fontFamily = FuenteMadera,
                    fontSize = 20.sp
                )
                Text(
                    text = "Toca para cerrar",
                    color = Color.White.copy(alpha = 0.7f),
                    fontFamily = FuenteMadera,
                    fontSize = 12.sp
                )
            }
        }
    }
}