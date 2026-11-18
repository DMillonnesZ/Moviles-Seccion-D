package com.millones.campusgps.ui.componentes

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
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Alto de la barra (72) + margen inferior (12) + aire (8).
// Las pantallas lo usan para que la barra no tape su contenido.
val ALTO_BARRA_TOTAL: Dp = 92.dp

// Espacio que deben dejar abajo las pantallas (0 cuando la barra está oculta)
val LocalRellenoBarra = staticCompositionLocalOf { 0.dp }

// Una pestaña de la barra; insignia = número sobre el ícono (0 = ninguna)
data class ItemBarra(
    val etiqueta: String,
    val icono: ImageVector,
    val insignia: Int = 0
)

// Barra flotante azul noche con una pastilla y una línea menta que se deslizan a la pestaña activa.
// Igual que en la PokeDex, pero dibujada con código (no necesita la imagen de madera).
@Composable
fun BarraNavegacion(
    items: List<ItemBarra>,
    seleccionada: Int,
    onSeleccionar: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val forma = RoundedCornerShape(28.dp)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(72.dp)
            .shadow(18.dp, forma, ambientColor = CampusColores.AzulProfundo, spotColor = CampusColores.AzulProfundo)
            .clip(forma)
            .background(DegradadosCampus.Noche)
            .border(
                1.dp,
                Brush.verticalGradient(listOf(Color.White.copy(alpha = 0.25f), Color.White.copy(alpha = 0.04f))),
                forma
            )
            // Bloquea los toques para que no lleguen al mapa de atrás
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { }
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp)
        ) {
            val anchoItem: Dp = maxWidth / items.size
            val altoBarra: Dp = maxHeight

            val posicion by animateDpAsState(
                targetValue = anchoItem * seleccionada,
                animationSpec = spring(dampingRatio = 0.7f, stiffness = Spring.StiffnessMediumLow),
                label = "posicionPastilla"
            )

            // Pastilla clara detrás de la pestaña activa
            Box(
                modifier = Modifier
                    .offset(x = posicion + anchoItem * 0.08f, y = 9.dp)
                    .width(anchoItem * 0.84f)
                    .height(altoBarra - 18.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White.copy(alpha = 0.08f))
            )

            // Línea menta con resplandor bajo la pestaña activa
            Box(
                modifier = Modifier
                    .offset(x = posicion + anchoItem * 0.25f, y = altoBarra - 10.dp)
                    .width(anchoItem * 0.5f)
                    .height(8.dp)
                    .drawBehind {
                        drawOval(
                            brush = Brush.radialGradient(
                                colors = listOf(CampusColores.VerdeMenta.copy(alpha = 0.7f), Color.Transparent),
                                center = center,
                                radius = size.width / 2f
                            ),
                            topLeft = Offset(0f, -size.height),
                            size = Size(size.width, size.height * 3f)
                        )
                        drawRoundRect(
                            brush = Brush.horizontalGradient(
                                listOf(Color.Transparent, CampusColores.VerdeMenta, Color.Transparent)
                            ),
                            topLeft = Offset(0f, size.height / 2f - 1.5.dp.toPx()),
                            size = Size(size.width, 3.dp.toPx()),
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

// Pestaña: la activa sube un poco, crece y se pinta de menta
@Composable
private fun PestanaBarra(
    item: ItemBarra,
    seleccionada: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val activa by animateFloatAsState(
        targetValue = if (seleccionada) 1f else 0f,
        animationSpec = spring(dampingRatio = 0.6f),
        label = "activa"
    )
    val color by animateColorAsState(
        targetValue = if (seleccionada) CampusColores.VerdeMenta else Color.White.copy(alpha = 0.6f),
        label = "colorPestana"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = onClick
        )
    ) {
        Box {
            Icon(
                imageVector = item.icono,
                contentDescription = item.etiqueta,
                tint = color,
                modifier = Modifier
                    .size(24.dp)
                    .graphicsLayer {
                        val escala = 1f + 0.15f * activa
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
        Spacer(Modifier.height(2.dp))
        Text(
            text = item.etiqueta,
            color = color,
            fontFamily = FuenteCampus,
            fontWeight = if (seleccionada) FontWeight.Bold else FontWeight.Medium,
            fontSize = 11.sp
        )
    }
}

// Circulito rojo con un número; late cuando el número sube (para el chat más adelante)
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
                .clip(forma)
                .background(CampusColores.Error)
                .border(1.5.dp, Color.White, forma)
                .padding(horizontal = 4.dp)
        ) {
            Text(
                text = if (cantidad > 99) "99+" else "$cantidad",
                color = Color.White,
                fontFamily = FuenteCampus,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp,
                lineHeight = 10.sp
            )
        }
    }
}
