package com.tecsup.pokemon.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val CremaTarjeta = Color(0xFFFFF4DF)
private val DoradoClaroTarjeta = Color(0xFFFFE7A0)
private val DoradoBordeTarjeta = Color(0xFFD9A43A)
private val RojoFila = Color(0xFFE53935)
private val GrisFila = Color(0xFF7A6A5A)

// Tarjeta crema con borde dorado y pestaña arriba (Perfil, Admin y resumen de Favoritos)
@Composable
fun TarjetaPestana(
    titulo: String,
    icono: ImageVector,
    modifier: Modifier = Modifier,
    alineacion: Alignment.Horizontal = Alignment.Start,
    contenido: @Composable ColumnScope.() -> Unit
) {
    val forma = RoundedCornerShape(20.dp)
    Box(modifier = modifier.fillMaxWidth()) {
        Column(
            horizontalAlignment = alineacion,
            modifier = Modifier
                .padding(top = 14.dp)
                .fillMaxWidth()
                .shadow(4.dp, forma)
                .clip(forma)
                .background(Brush.verticalGradient(listOf(Color.White, CremaTarjeta)))
                .border(1.5.dp, Brush.verticalGradient(listOf(DoradoClaroTarjeta, DoradoBordeTarjeta)), forma)
                .padding(start = 14.dp, end = 14.dp, top = 28.dp, bottom = 14.dp),
            content = contenido
        )
        PestanaMetodo(
            texto = titulo,
            icono = icono,
            modifier = Modifier.padding(start = 10.dp)
        )
    }
}

// Fila con ícono en círculo, título y subtítulo.
// Si se puede tocar muestra una flecha; mientras carga, un indicador.
@Composable
fun FilaOpcion(
    icono: ImageVector,
    titulo: String,
    color: Color,
    modifier: Modifier = Modifier,
    subtitulo: String? = null,
    peligro: Boolean = false,
    cargando: Boolean = false,
    habilitado: Boolean = true,
    onClick: (() -> Unit)? = null
) {
    val tocable = onClick != null
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .alpha(if (tocable && !habilitado && !cargando) 0.5f else 1f)
            .clip(RoundedCornerShape(14.dp))
            .clickable(enabled = tocable && habilitado && !cargando) { onClick?.invoke() }
            .padding(horizontal = 6.dp, vertical = 9.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.15f))
        ) {
            Icon(icono, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = titulo,
                color = if (peligro) RojoFila else CafeMadera,
                fontFamily = FuenteMadera,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            if (subtitulo != null) {
                Text(
                    text = subtitulo,
                    color = GrisFila,
                    fontFamily = FuenteMadera,
                    fontSize = 13.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        when {
            cargando -> CircularProgressIndicator(color = color, strokeWidth = 2.dp, modifier = Modifier.size(20.dp))
            tocable -> Icon(
                Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                contentDescription = null,
                tint = CafeMadera.copy(alpha = 0.5f)
            )
        }
    }
}

// Línea fina para separar filas
@Composable
fun DivisorSuave() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .height(1.dp)
            .background(CafeMadera.copy(alpha = 0.1f))
    )
}