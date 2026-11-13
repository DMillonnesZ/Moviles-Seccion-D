package com.tecsup.pokemon.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tecsup.pokemon.R

private val CafeOscuroPag = Color(0xFF4A2C14)
private val CremaPag = Color(0xFFFFF3DC)

// Paginador de madera: flechas a los lados y puntos al centro (máximo 5 visibles);
// el punto de la página actual muestra su número
@Composable
fun Paginador(
    paginaActual: Int,          // empieza en 0
    totalPaginas: Int,
    onCambiarPagina: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val visibles = 5
    val inicio = (paginaActual - visibles / 2).coerceIn(0, maxOf(0, totalPaginas - visibles))
    val fin = minOf(totalPaginas, inicio + visibles)

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .width(260.dp)
            .height(50.dp)
    ) {
        Image(
            painter = painterResource(R.drawable.tabla_enlace),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.matchParentSize()
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp)
        ) {
            Flecha(
                icono = Icons.AutoMirrored.Rounded.KeyboardArrowLeft,
                habilitada = paginaActual > 0,
                onClick = { onCambiarPagina(paginaActual - 1) }
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                for (pagina in inicio until fin) {
                    Punto(
                        numero = pagina + 1,
                        activo = pagina == paginaActual,
                        onClick = { onCambiarPagina(pagina) }
                    )
                }
            }
            Flecha(
                icono = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                habilitada = paginaActual < totalPaginas - 1,
                onClick = { onCambiarPagina(paginaActual + 1) }
            )
        }
    }
}

@Composable
private fun Flecha(
    icono: ImageVector,
    habilitada: Boolean,
    onClick: () -> Unit
) {
    Icon(
        imageVector = icono,
        contentDescription = null,
        tint = CafeOscuroPag.copy(alpha = if (habilitada) 1f else 0.3f),
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .clickable(enabled = habilitada, onClick = onClick)
    )
}

// Punto: el activo crece, se oscurece y muestra el número de la página
@Composable
private fun Punto(
    numero: Int,
    activo: Boolean,
    onClick: () -> Unit
) {
    val tamano by animateDpAsState(
        targetValue = if (activo) 26.dp else 11.dp,
        animationSpec = spring(dampingRatio = 0.55f),
        label = "tamanoPunto"
    )
    val color by animateColorAsState(
        targetValue = if (activo) CafeOscuroPag else CafeMadera.copy(alpha = 0.45f),
        label = "colorPunto"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(tamano)
            .clip(CircleShape)
            .background(color)
            .then(if (activo) Modifier.border(2.dp, CremaPag, CircleShape) else Modifier)
            .clickable(onClick = onClick)
    ) {
        if (activo) {
            Text(
                text = "$numero",
                color = CremaPag,
                fontFamily = FuenteMadera,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
        }
    }
}