package com.millones.campusgps.ui.componentes

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Tarjeta base de los estados (vacío, error): blanca con borde celeste
@Composable
fun TarjetaEstado(
    modifier: Modifier = Modifier,
    contenido: @Composable ColumnScope.() -> Unit
) {
    val forma = RoundedCornerShape(28.dp)
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxWidth()
            .shadow(14.dp, forma, ambientColor = CampusColores.AzulProfundo, spotColor = CampusColores.AzulProfundo)
            .clip(forma)
            .background(DegradadosCampus.Tarjeta)
            .border(1.5.dp, DegradadosCampus.BordeTarjeta, forma)
            .padding(horizontal = 24.dp, vertical = 28.dp),
        content = contenido
    )
}

// Pin que salta con ondas que se expanden (reemplaza al Pikachu animado de la PokeDex).
// Está dibujado con código: no necesita imágenes.
@Composable
fun PinPulsante(
    modifier: Modifier = Modifier,
    color: Color = CampusColores.AzulCielo,
    tamano: Dp = 110.dp
) {
    val transicion = rememberInfiniteTransition(label = "pin")
    val onda by transicion.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1600, easing = LinearEasing)),
        label = "onda"
    )
    val salto by transicion.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(700, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "salto"
    )

    Box(contentAlignment = Alignment.Center, modifier = modifier.size(tamano)) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val radioMax = size.minDimension / 2f
            // Dos ondas desfasadas
            listOf(onda, (onda + 0.5f) % 1f).forEach { t ->
                drawCircle(
                    color = color.copy(alpha = (1f - t) * 0.35f),
                    radius = radioMax * (0.3f + 0.7f * t)
                )
            }
            // Sombra del pin: se achica cuando el pin sube
            val anchoSombra = radioMax * (0.46f - 0.12f * salto)
            drawOval(
                color = CampusColores.AzulProfundo.copy(alpha = 0.18f),
                topLeft = Offset(center.x - anchoSombra / 2f, center.y + radioMax * 0.32f),
                size = Size(anchoSombra, radioMax * 0.12f)
            )
        }
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .graphicsLayer { translationY = -salto * 8.dp.toPx() }
                .size(tamano * 0.46f)
                .shadow(6.dp, CircleShape)
                .clip(CircleShape)
                .background(DegradadosCampus.Primario)
                .border(3.dp, Color.White, CircleShape)
        ) {
            Icon(
                imageVector = Icons.Rounded.LocationOn,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(tamano * 0.26f)
            )
        }
    }
}

// Cargando: pin animado + mensaje en píldora blanca
@Composable
fun Cargando(
    mensaje: String = "Cargando...",
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PinPulsante()
        Spacer(Modifier.height(12.dp))
        Text(
            text = mensaje,
            color = CampusColores.TextoOscuro,
            fontFamily = FuenteCampus,
            fontSize = 15.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .shadow(4.dp, RoundedCornerShape(50))
                .clip(RoundedCornerShape(50))
                .background(Color.White)
                .border(1.dp, CampusColores.Borde, RoundedCornerShape(50))
                .padding(horizontal = 18.dp, vertical = 6.dp)
        )
    }
}

// Vacío: ícono en círculo menta, título y mensaje
@Composable
fun VacioView(
    titulo: String,
    mensaje: String,
    icono: ImageVector,
    modifier: Modifier = Modifier
) {
    TarjetaEstado(modifier = modifier) {
        IconoCirculo(icono = icono, color = CampusColores.Exito, tamano = 72.dp)
        Spacer(Modifier.height(14.dp))
        Text(
            text = titulo,
            color = CampusColores.TextoOscuro,
            fontFamily = FuenteCampus,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 20.sp,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = mensaje,
            color = CampusColores.TextoSecundario,
            fontFamily = FuenteCampus,
            fontSize = 15.sp,
            textAlign = TextAlign.Center
        )
    }
}

// Error: ícono en círculo rojo, título, mensaje y botón opcional
@Composable
fun EstadoError(
    titulo: String,
    mensaje: String,
    icono: ImageVector,
    modifier: Modifier = Modifier,
    textoAccion: String? = null,
    onAccion: () -> Unit = {}
) {
    TarjetaEstado(modifier = modifier) {
        IconoCirculo(icono = icono, color = CampusColores.Error, tamano = 72.dp)
        Spacer(Modifier.height(14.dp))
        Text(
            text = titulo,
            color = CampusColores.TextoOscuro,
            fontFamily = FuenteCampus,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 20.sp,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = mensaje,
            color = CampusColores.TextoSecundario,
            fontFamily = FuenteCampus,
            fontSize = 15.sp,
            textAlign = TextAlign.Center
        )
        if (textoAccion != null) {
            Spacer(Modifier.height(18.dp))
            BotonPildora(
                texto = textoAccion,
                fondo = FondoPildora.Primario,
                colorTexto = Color.White,
                onClick = onAccion,
                modifier = Modifier.fillMaxWidth(0.75f)
            )
        }
    }
}
