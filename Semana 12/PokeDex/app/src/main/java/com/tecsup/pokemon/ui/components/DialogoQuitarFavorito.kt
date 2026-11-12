package com.tecsup.pokemon.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.HeartBroken
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage

private val CremaDialogo = Color(0xFFFFF4DF)
private val DoradoClaroDialogo = Color(0xFFFFE7A0)
private val DoradoDialogo = Color(0xFFF5C451)
private val DoradoBordeDialogo = Color(0xFFD9A43A)
private val RojoQuitar = Color(0xFFE53935)
private val RojoOscuro = Color(0xFFB71C1C)

// Ventana para confirmar que se quiere quitar un Pokémon de favoritos.
// Entra rebotando, muestra el Pokémon en un círculo de su color y un corazón roto que late.
@Composable
fun DialogoQuitarFavorito(
    nombre: String,
    imagenUrl: String,
    color: Color,
    onConfirmar: () -> Unit,
    onCancelar: () -> Unit
) {
    val entrada = remember { Animatable(0.7f) }
    LaunchedEffect(Unit) { entrada.animateTo(1f, spring(dampingRatio = 0.5f)) }

    val transicion = rememberInfiniteTransition(label = "corazonRoto")
    val latido by transicion.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(tween(600), RepeatMode.Reverse),
        label = "latidoRoto"
    )

    val nombreBonito = nombre.replaceFirstChar { it.uppercase() }
    val forma = RoundedCornerShape(26.dp)

    Dialog(
        onDismissRequest = onCancelar,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onCancelar
                )
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(horizontal = 32.dp)
                    .graphicsLayer {
                        scaleX = entrada.value
                        scaleY = entrada.value
                        alpha = ((entrada.value - 0.7f) / 0.3f).coerceIn(0f, 1f)
                    }
                    .shadow(12.dp, forma)
                    .clip(forma)
                    .background(Brush.verticalGradient(listOf(Color.White, CremaDialogo)))
                    .border(3.dp, Brush.verticalGradient(listOf(DoradoClaroDialogo, DoradoBordeDialogo)), forma)
                    // Evita que tocar dentro cierre la ventana
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = {}
                    )
                    .padding(22.dp)
            ) {
                // Pokémon en su círculo con el corazón roto encima
                Box(modifier = Modifier.size(120.dp)) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxSize()
                            .shadow(6.dp, CircleShape, ambientColor = color, spotColor = color)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    listOf(lerp(color, Color.White, 0.7f), lerp(color, Color.White, 0.2f))
                                )
                            )
                            .border(3.dp, Color.White, CircleShape)
                    ) {
                        AsyncImage(
                            model = imagenUrl,
                            contentDescription = nombreBonito,
                            modifier = Modifier.fillMaxSize(0.82f)
                        )
                    }
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(40.dp)
                            .graphicsLayer {
                                scaleX = latido
                                scaleY = latido
                            }
                            .shadow(3.dp, CircleShape)
                            .clip(CircleShape)
                            .background(Color.White)
                            .border(2.dp, RojoQuitar.copy(alpha = 0.4f), CircleShape)
                    ) {
                        Icon(Icons.Rounded.HeartBroken, contentDescription = null, tint = RojoQuitar, modifier = Modifier.size(24.dp))
                    }
                }

                Spacer(Modifier.height(14.dp))
                Text(
                    text = "¿Quitar de favoritos?",
                    color = CafeMadera,
                    fontFamily = FuenteMadera,
                    fontWeight = FontWeight.Bold,
                    fontSize = 21.sp
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = buildAnnotatedString {
                        append("¿Estás seguro de que quieres quitar a ")
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = lerp(color, Color.Black, 0.25f))) {
                            append(nombreBonito)
                        }
                        append(" de tus favoritos? ¡Es un Pokémon maravilloso!")
                    },
                    color = CafeMadera.copy(alpha = 0.85f),
                    fontFamily = FuenteMadera,
                    fontSize = 15.sp,
                    lineHeight = 21.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(20.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    BotonDialogo(
                        texto = "Cancelar",
                        fondo = Brush.verticalGradient(listOf(DoradoClaroDialogo, DoradoDialogo)),
                        borde = CafeMadera.copy(alpha = 0.5f),
                        colorTexto = CafeMadera,
                        onClick = onCancelar,
                        modifier = Modifier.weight(1f)
                    )
                    BotonDialogo(
                        texto = "Quitar",
                        fondo = Brush.verticalGradient(listOf(Color(0xFFFF6B66), RojoQuitar)),
                        borde = RojoOscuro,
                        colorTexto = Color.White,
                        onClick = onConfirmar,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

// Botón en forma de píldora para el diálogo
@Composable
private fun BotonDialogo(
    texto: String,
    fondo: Brush,
    borde: Color,
    colorTexto: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val forma = RoundedCornerShape(50)
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .height(46.dp)
            .shadow(3.dp, forma)
            .clip(forma)
            .background(fondo)
            .border(1.5.dp, borde, forma)
            .clickable(onClick = onClick)
    ) {
        Text(
            text = texto,
            color = colorTexto,
            fontFamily = FuenteMadera,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
    }
}