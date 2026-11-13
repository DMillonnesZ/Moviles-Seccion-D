package com.tecsup.pokemon.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tecsup.pokemon.R

private val DoradoBrillo = Color(0xFFFFB300)
private val AmarilloLuz = Color(0xFFFFE066)
private val CafeOscuro = Color(0xFF3E2410)
private val RojoInsignia = Color(0xFFE53935)

// Espacio que deben dejar abajo las pantallas para que la barra no tape el contenido
val LocalRellenoBarra = staticCompositionLocalOf { 0.dp }

// Una pestaña de la barra; insignia = número que se muestra sobre el ícono (0 = ninguna)
data class ItemBarra(
    val etiqueta: String,
    val icono: ImageVector,
    val insignia: Int = 0
)

// Barra de navegación de madera con línea luminosa que se desliza a la pestaña activa
@Composable
fun BarraNavegacion(
    items: List<ItemBarra>,
    seleccionada: Int,
    onSeleccionar: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(76.dp)
            // Bloquea los toques en toda la barra para que no lleguen a la pantalla de atrás
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { }
    ) {
        Image(
            painter = painterResource(R.drawable.barra_navegacion),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )

        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 28.dp)
        ) {
            val anchoItem: Dp = maxWidth / items.size

            // La línea se desliza con rebote suave hasta la pestaña elegida
            val posicionLinea by animateDpAsState(
                targetValue = anchoItem * seleccionada,
                animationSpec = spring(dampingRatio = 0.7f, stiffness = Spring.StiffnessMediumLow),
                label = "posicionLinea"
            )

            // Línea luminosa difuminada bajo la pestaña activa
            Box(
                modifier = Modifier
                    .offset(x = posicionLinea + anchoItem * 0.15f, y = maxHeight - 14.dp)
                    .width(anchoItem * 0.7f)
                    .height(12.dp)
                    .drawBehind {
                        // Resplandor
                        drawOval(
                            brush = Brush.radialGradient(
                                colors = listOf(AmarilloLuz.copy(alpha = 0.8f), Color.Transparent),
                                center = center,
                                radius = size.width / 2f
                            ),
                            topLeft = Offset(0f, -size.height),
                            size = Size(size.width, size.height * 3f)
                        )
                        // Línea central
                        drawRoundRect(
                            brush = Brush.horizontalGradient(
                                listOf(Color.Transparent, DoradoBrillo, Color.Transparent)
                            ),
                            topLeft = Offset(0f, size.height / 2f - 2.dp.toPx()),
                            size = Size(size.width, 4.dp.toPx()),
                            cornerRadius = CornerRadius(2.dp.toPx())
                        )
                    }
            )

            Row(modifier = Modifier.fillMaxSize()) {
                items.forEachIndexed { indice, item ->
                    PestanaBarra(
                        item = item,
                        seleccionada = indice == seleccionada,
                        onClick = { onSeleccionar(indice) },
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    )
                }
            }
        }
    }
}

// Pestaña: la activa sube y queda iluminada con luz amarilla suave;
// las demás brillan al presionarlas o al pasar el cursor
@Composable
private fun PestanaBarra(
    item: ItemBarra,
    seleccionada: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interaccion = remember { MutableInteractionSource() }
    val presionada by interaccion.collectIsPressedAsState()
    val encima by interaccion.collectIsHoveredAsState()

    val activa by animateFloatAsState(
        targetValue = if (seleccionada) 1f else 0f,
        animationSpec = spring(dampingRatio = 0.6f),
        label = "activa"
    )
    val resplandor by animateFloatAsState(
        targetValue = when {
            presionada -> 0.9f
            encima -> 0.6f
            else -> 0f
        },
        label = "resplandor"
    )
    val color by animateColorAsState(
        targetValue = if (seleccionada) CafeOscuro else CafeMadera.copy(alpha = 0.75f),
        label = "colorPestana"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .hoverable(interaccion)
            .clickable(interactionSource = interaccion, indication = null, onClick = onClick)
            // Luz amarilla suave detrás del ícono y el texto
            .drawBehind {
                val intensidad = maxOf(resplandor, activa)
                if (intensidad > 0f) {
                    val ancho = size.width * 1.05f
                    val alto = size.height * 0.95f
                    drawOval(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                AmarilloLuz.copy(alpha = 0.75f * intensidad),
                                AmarilloLuz.copy(alpha = 0.3f * intensidad),
                                Color.Transparent
                            ),
                            center = center,
                            radius = ancho / 2f
                        ),
                        topLeft = Offset(center.x - ancho / 2f, center.y - alto / 2f),
                        size = Size(ancho, alto)
                    )
                }
            }
    ) {
        // Ícono con la insignia encima (si tiene)
        Box {
            Icon(
                imageVector = item.icono,
                contentDescription = item.etiqueta,
                tint = color,
                modifier = Modifier
                    .size(26.dp)
                    .graphicsLayer {
                        val escala = 1f + 0.12f * activa
                        scaleX = escala
                        scaleY = escala
                        translationY = -3.dp.toPx() * activa
                    }
            )
            Insignia(
                cantidad = item.insignia,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 12.dp, y = (-6).dp)
            )
        }
        Text(
            text = item.etiqueta,
            color = color,
            fontFamily = FuenteMadera,
            fontSize = 13.sp
        )
    }
}

// Circulito rojo con un número; late cuando el número sube
@Composable
private fun Insignia(cantidad: Int, modifier: Modifier = Modifier) {
    val escala = remember { Animatable(1f) }
    var anterior by remember { mutableIntStateOf(cantidad) }

    LaunchedEffect(cantidad) {
        if (cantidad > anterior) {
            escala.animateTo(1.45f, tween(120))
            escala.animateTo(1f, spring(dampingRatio = 0.35f))
        }
        anterior = cantidad
    }

    AnimatedVisibility(
        visible = cantidad > 0,
        enter = scaleIn(),
        exit = scaleOut(),
        modifier = modifier
    ) {
        val forma = RoundedCornerShape(50)
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .graphicsLayer {
                    scaleX = escala.value
                    scaleY = escala.value
                }
                .defaultMinSize(minWidth = 18.dp)
                .height(18.dp)
                .shadow(2.dp, forma)
                .clip(forma)
                .background(RojoInsignia)
                .border(1.5.dp, Color.White, forma)
                .padding(horizontal = 4.dp)
        ) {
            Text(
                text = if (cantidad > 99) "99+" else "$cantidad",
                color = Color.White,
                fontFamily = FuenteMadera,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp,
                lineHeight = 10.sp
            )
        }
    }
}