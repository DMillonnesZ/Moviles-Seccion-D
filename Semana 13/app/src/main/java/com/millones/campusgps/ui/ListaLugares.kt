package com.millones.campusgps.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.millones.campusgps.model.LugarConDistancia
import com.millones.campusgps.ui.componentes.CampusColores
import com.millones.campusgps.ui.componentes.FotoLugar
import com.millones.campusgps.ui.componentes.FuenteCampus
import com.millones.campusgps.ui.componentes.VidrioMapa
import com.millones.campusgps.ui.componentes.desvanecerBordes
import com.millones.campusgps.ui.componentes.estiloCategoria
import com.millones.campusgps.util.formatearDistancia
import com.millones.campusgps.viewmodel.RADIO_CERCANOS

// Colores de los lugares (marcadores, tarjetas y leyenda)
object ColoresLugar {
    val Actual = Color(0xFFEF4444)      // rojo: estás ahí (radio de 60 m)
    val Cerca = Color(0xFF22C55E)       // verde: a menos de RADIO_CERCANOS
    val Favorito = Color(0xFFFFC94D)    // amarillo: favorito pero lejos
    val Registrado = Color(0xFF3B82F6)  // azul: registrado, no favorito y lejos
}

fun colorDeLugar(item: LugarConDistancia, esActual: Boolean): Color = when {
    esActual -> ColoresLugar.Actual
    (item.metros ?: Float.MAX_VALUE) <= RADIO_CERCANOS -> ColoresLugar.Cerca
    item.lugar.favorito -> ColoresLugar.Favorito
    else -> ColoresLugar.Registrado
}

// Lista de lugares (favoritos o todos). Cada tarjeta: foto circular que se puede cambiar,
// título, descripción, distancia y estrella. Mantener presionada una tarjeta = eliminar.
@Composable
fun ListaLugares(
    lugares: List<LugarConDistancia>,
    seleccionado: Int?,
    aquiId: Int?,
    onClick: (Int) -> Unit,
    onFavorito: (Int) -> Unit,
    onCambiarFoto: (Int) -> Unit,
    onEliminar: (Int) -> Unit,
    modifier: Modifier = Modifier,
    contenidoPadding: PaddingValues = PaddingValues(start = 12.dp, end = 12.dp, top = 4.dp, bottom = 16.dp)
) {
    LazyColumn(
        modifier = modifier.desvanecerBordes(arriba = 8.dp, abajo = 16.dp),
        contentPadding = contenidoPadding,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(lugares, key = { it.lugar.id }) { item ->
            TarjetaLugar(
                item = item,
                seleccionado = item.lugar.id == seleccionado,
                estasAqui = item.lugar.id == aquiId,
                onClick = { onClick(item.lugar.id) },
                onFavorito = { onFavorito(item.lugar.id) },
                onCambiarFoto = { onCambiarFoto(item.lugar.id) },
                onEliminar = { onEliminar(item.lugar.id) },
                // Al ordenar por distancia, guardar o eliminar, las tarjetas se deslizan
                modifier = Modifier.animateItem()
            )
        }
    }
}

// Tarjeta de vidrio de un lugar. La seleccionada se resalta con el color de su categoría.
@Composable
private fun TarjetaLugar(
    item: LugarConDistancia,
    seleccionado: Boolean,
    estasAqui: Boolean,
    onClick: () -> Unit,
    onFavorito: () -> Unit,
    onCambiarFoto: () -> Unit,
    onEliminar: () -> Unit,
    modifier: Modifier = Modifier
) {
    val lugar = item.lugar
    val estilo = estiloCategoria(lugar.categoria)
    val forma = RoundedCornerShape(18.dp)
    val haptico = LocalHapticFeedback.current
    val colorEstado = colorDeLugar(item, estasAqui)

    val escala by animateFloatAsState(
        targetValue = if (seleccionado) 1f else 0.985f,
        animationSpec = spring(dampingRatio = 0.6f),
        label = "escalaLugar"
    )
    val colorBorde by animateColorAsState(
        targetValue = if (seleccionado) estilo.color else Color.White.copy(alpha = 0.12f),
        label = "bordeLugar"
    )
    val fondo by animateColorAsState(
        targetValue = if (seleccionado) estilo.color.copy(alpha = 0.16f) else Color.White.copy(alpha = 0.05f),
        label = "fondoLugar"
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = escala
                scaleY = escala
            }
            .clip(forma)
            .background(fondo)
            .border(if (seleccionado) 1.5.dp else 1.dp, colorBorde, forma)
            .combinedClickable(
                onClick = onClick,
                onLongClick = {
                    haptico.performHapticFeedback(HapticFeedbackType.LongPress)
                    onEliminar()
                }
            )
            .padding(start = 10.dp, end = 6.dp, top = 9.dp, bottom = 9.dp)
    ) {
        FotoLugar(
            ruta = lugar.foto,
            categoria = lugar.categoria,
            tamano = 50.dp,
            colorBorde = colorEstado,
            onCambiarFoto = onCambiarFoto
        )

        Spacer(Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = lugar.nombre,
                    color = Color.White,
                    fontFamily = FuenteCampus,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )
                if (estasAqui) {
                    Spacer(Modifier.width(6.dp))
                    EtiquetaAqui()
                }
            }
            // Descripción; si no tiene, la categoría
            Text(
                text = lugar.descripcion.ifBlank { lugar.categoria },
                color = if (lugar.descripcion.isBlank()) lerp(estilo.color, Color.White, 0.35f)
                else Color.White.copy(alpha = 0.7f),
                fontFamily = FuenteCampus,
                fontSize = 13.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = lugar.categoria,
                color = lerp(estilo.color, Color.White, 0.35f),
                fontFamily = FuenteCampus,
                fontWeight = FontWeight.SemiBold,
                fontSize = 11.sp
            )
        }

        Spacer(Modifier.width(6.dp))

        // Distancia en píldora de vidrio ("~ 120 m")
        Text(
            text = item.metros?.let { "~ ${formatearDistancia(it)}" } ?: "-- m",
            color = Color.White.copy(alpha = 0.85f),
            fontFamily = FuenteCampus,
            fontWeight = FontWeight.SemiBold,
            fontSize = 12.sp,
            maxLines = 1,
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .background(Color.White.copy(alpha = 0.06f))
                .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(50))
                .padding(horizontal = 10.dp, vertical = 5.dp)
        )

        EstrellaFavorito(activa = lugar.favorito, onClick = onFavorito)
    }
}

// Estrella de favorito: se llena de amarillo y rebota al marcarla
@Composable
fun EstrellaFavorito(
    activa: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val rebote = remember { Animatable(1f) }
    var anterior by remember { mutableStateOf(activa) }
    LaunchedEffect(activa) {
        if (activa && !anterior) {
            rebote.animateTo(1.35f, tween(120))
            rebote.animateTo(1f, spring(dampingRatio = 0.35f))
        }
        anterior = activa
    }
    val color by animateColorAsState(
        targetValue = if (activa) VidrioMapa.Estrella else Color.White.copy(alpha = 0.55f),
        label = "colorEstrella"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(38.dp)
            .clip(CircleShape)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
    ) {
        Icon(
            imageVector = if (activa) Icons.Rounded.Star else Icons.Rounded.StarBorder,
            contentDescription = if (activa) "Quitar de favoritos" else "Agregar a favoritos",
            tint = color,
            modifier = Modifier
                .size(24.dp)
                .graphicsLayer {
                    scaleX = rebote.value
                    scaleY = rebote.value
                }
        )
    }
}

// Etiqueta menta: estás a menos de 50 m de este lugar
@Composable
private fun EtiquetaAqui() {
    Text(
        text = "Estás aquí",
        color = CampusColores.VerdeMenta,
        fontFamily = FuenteCampus,
        fontWeight = FontWeight.Bold,
        fontSize = 10.sp,
        maxLines = 1,
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(CampusColores.VerdeMenta.copy(alpha = 0.16f))
            .padding(horizontal = 7.dp, vertical = 2.dp)
    )
}
