package com.tecsup.pokemon.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

private val CremaVentana = Color(0xFFFFF4DF)
private val DoradoClaroVentana = Color(0xFFFFE7A0)
private val DoradoBordeVentana = Color(0xFFD9A43A)

// Fondos de los botones píldora: dorado (cancelar), verde (confirmar), rojo (peligro)
object FondoPildora {
    val Dorado = Brush.verticalGradient(listOf(Color(0xFFFFE7A0), Color(0xFFF5C451)))
    val Verde = Brush.verticalGradient(listOf(Color(0xFF7BD857), Color(0xFF35A82C)))
    val Rojo = Brush.verticalGradient(listOf(Color(0xFFFF7A6E), Color(0xFFE53935)))
}

// Ventana de madera: entra rebotando, con ícono en círculo (o una cabecera propia) y título.
// La usan Perfil, Admin y el aviso de cuenta suspendida.
@Composable
fun VentanaMadera(
    titulo: String,
    icono: ImageVector,
    onCerrar: () -> Unit,
    colorIcono: Color = DoradoBordeVentana,
    cabecera: (@Composable () -> Unit)? = null,
    contenido: @Composable ColumnScope.() -> Unit
) {
    val escala = remember { Animatable(0.8f) }
    LaunchedEffect(Unit) {
        escala.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
    }
    val forma = RoundedCornerShape(24.dp)

    Dialog(
        onDismissRequest = onCerrar,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(horizontal = 24.dp, vertical = 32.dp)
                .fillMaxWidth()
                .graphicsLayer {
                    scaleX = escala.value
                    scaleY = escala.value
                }
                .shadow(10.dp, forma)
                .clip(forma)
                .background(Brush.verticalGradient(listOf(Color.White, CremaVentana)))
                .border(2.dp, Brush.verticalGradient(listOf(DoradoClaroVentana, DoradoBordeVentana)), forma)
                // Si el contenido es largo (pantallas pequeñas), se puede deslizar
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            if (cabecera != null) {
                cabecera()
            } else {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(64.dp)
                        .shadow(4.dp, CircleShape, ambientColor = colorIcono, spotColor = colorIcono)
                        .clip(CircleShape)
                        .background(Brush.radialGradient(listOf(lerp(colorIcono, Color.White, 0.45f), colorIcono)))
                        .border(3.dp, Color.White, CircleShape)
                ) {
                    Icon(icono, contentDescription = null, tint = Color.White, modifier = Modifier.size(32.dp))
                }
            }
            Spacer(Modifier.height(10.dp))
            Text(
                text = titulo,
                color = CafeMadera,
                fontFamily = FuenteMadera,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(14.dp))
            contenido()
        }
    }
}

// Botón en forma de píldora (con carga opcional)
@Composable
fun BotonPildora(
    texto: String,
    fondo: Brush,
    colorTexto: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    borde: Color = Color.White.copy(alpha = 0.6f),
    habilitado: Boolean = true,
    cargando: Boolean = false
) {
    val forma = RoundedCornerShape(50)
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .height(46.dp)
            .alpha(if (!habilitado && !cargando) 0.6f else 1f)
            .shadow(3.dp, forma)
            .clip(forma)
            .background(fondo)
            .border(1.5.dp, borde, forma)
            .clickable(enabled = habilitado && !cargando, onClick = onClick)
    ) {
        if (cargando) {
            CircularProgressIndicator(color = colorTexto, strokeWidth = 2.dp, modifier = Modifier.size(20.dp))
        } else {
            Text(
                text = texto,
                color = colorTexto,
                fontFamily = FuenteMadera,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
        }
    }
}

// Par de botones: Cancelar (dorado) y confirmar (verde por defecto)
@Composable
fun BotonesVentana(
    textoConfirmar: String,
    onConfirmar: () -> Unit,
    onCancelar: () -> Unit,
    cargando: Boolean = false,
    fondoConfirmar: Brush = FondoPildora.Verde,
    textoCancelar: String = "Cancelar"
) {
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        BotonPildora(
            texto = textoCancelar,
            fondo = FondoPildora.Dorado,
            colorTexto = CafeMadera,
            onClick = onCancelar,
            habilitado = !cargando,
            modifier = Modifier.weight(1f)
        )
        BotonPildora(
            texto = textoConfirmar,
            fondo = fondoConfirmar,
            colorTexto = Color.White,
            onClick = onConfirmar,
            cargando = cargando,
            modifier = Modifier.weight(1f)
        )
    }
}