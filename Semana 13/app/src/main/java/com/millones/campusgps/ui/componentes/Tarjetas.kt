package com.millones.campusgps.ui.componentes

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

// Fondos de los botones píldora (en la PokeDex: dorado, verde y rojo)
object FondoPildora {
    val Primario = DegradadosCampus.Primario
    val Suave = DegradadosCampus.Suave
    val Menta = DegradadosCampus.Menta
    val Peligro = DegradadosCampus.Peligro
}

// Botón en forma de píldora, con carga opcional
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
    val interaccion = remember { MutableInteractionSource() }
    val presionado by interaccion.collectIsPressedAsState()
    val escala by animateFloatAsState(
        targetValue = if (presionado) 0.95f else 1f,
        animationSpec = spring(dampingRatio = 0.5f),
        label = "escalaPildora"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .height(48.dp)
            .graphicsLayer {
                scaleX = escala
                scaleY = escala
            }
            .alpha(if (!habilitado && !cargando) 0.5f else 1f)
            .shadow(4.dp, forma)
            .clip(forma)
            .background(fondo)
            .border(1.5.dp, borde, forma)
            .clickable(
                interactionSource = interaccion,
                indication = null,
                enabled = habilitado && !cargando,
                onClick = onClick
            )
    ) {
        if (cargando) {
            CircularProgressIndicator(color = colorTexto, strokeWidth = 2.dp, modifier = Modifier.size(20.dp))
        } else {
            Text(
                text = texto,
                color = colorTexto,
                fontFamily = FuenteCampus,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
        }
    }
}

// Par de botones: Cancelar (claro) y confirmar (azul por defecto)
@Composable
fun BotonesVentana(
    textoConfirmar: String,
    onConfirmar: () -> Unit,
    onCancelar: () -> Unit,
    cargando: Boolean = false,
    fondoConfirmar: Brush = FondoPildora.Primario,
    textoCancelar: String = "Cancelar"
) {
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        BotonPildora(
            texto = textoCancelar,
            fondo = FondoPildora.Suave,
            colorTexto = CampusColores.TextoOscuro,
            borde = CampusColores.Borde,
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

// Ventana emergente: entra rebotando, con ícono en círculo y título (VentanaMadera de la PokeDex)
@Composable
fun VentanaCampus(
    titulo: String,
    icono: ImageVector,
    onCerrar: () -> Unit,
    colorIcono: Color = CampusColores.AzulPrimario,
    contenido: @Composable ColumnScope.() -> Unit
) {
    val escala = remember { Animatable(0.8f) }
    LaunchedEffect(Unit) {
        escala.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
    }
    val forma = RoundedCornerShape(28.dp)

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
                .shadow(16.dp, forma)
                .clip(forma)
                .background(DegradadosCampus.Tarjeta)
                .border(1.5.dp, DegradadosCampus.BordeTarjeta, forma)
                .verticalScroll(rememberScrollState())
                .padding(22.dp)
        ) {
            IconoCirculo(icono = icono, color = colorIcono, tamano = 64.dp)
            Spacer(Modifier.height(12.dp))
            Text(
                text = titulo,
                color = CampusColores.TextoOscuro,
                fontFamily = FuenteCampus,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 20.sp,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(14.dp))
            contenido()
        }
    }
}

// Círculo con degradado del color dado, borde blanco e ícono blanco
@Composable
fun IconoCirculo(
    icono: ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    tamano: Dp = 56.dp
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(tamano)
            .shadow(6.dp, CircleShape, ambientColor = color, spotColor = color)
            .clip(CircleShape)
            .background(Brush.radialGradient(listOf(lerp(color, Color.White, 0.4f), color)))
            .border(3.dp, Color.White, CircleShape)
    ) {
        Icon(icono, contentDescription = null, tint = Color.White, modifier = Modifier.size(tamano * 0.5f))
    }
}

// Pestaña pequeña que va encima de una tarjeta (título de sección)
@Composable
fun PestanaTitulo(
    texto: String,
    icono: ImageVector,
    modifier: Modifier = Modifier
) {
    val forma = RoundedCornerShape(50)
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .shadow(4.dp, forma)
            .clip(forma)
            .background(DegradadosCampus.Primario)
            .border(1.5.dp, Color.White, forma)
            .padding(horizontal = 14.dp, vertical = 6.dp)
    ) {
        Icon(icono, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
        Spacer(Modifier.width(6.dp))
        Text(
            text = texto,
            color = Color.White,
            fontFamily = FuenteCampus,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp
        )
    }
}

// Tarjeta blanca con borde celeste y pestaña arriba (Perfil y futuras secciones)
@Composable
fun TarjetaPestana(
    titulo: String,
    icono: ImageVector,
    modifier: Modifier = Modifier,
    contenido: @Composable ColumnScope.() -> Unit
) {
    val forma = RoundedCornerShape(22.dp)
    Box(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .padding(top = 15.dp)
                .fillMaxWidth()
                .shadow(6.dp, forma, ambientColor = CampusColores.AzulPrimario, spotColor = CampusColores.AzulPrimario)
                .clip(forma)
                .background(DegradadosCampus.Tarjeta)
                .border(1.5.dp, DegradadosCampus.BordeTarjeta, forma)
                .padding(start = 12.dp, end = 12.dp, top = 28.dp, bottom = 12.dp),
            content = contenido
        )
        PestanaTitulo(
            texto = titulo,
            icono = icono,
            modifier = Modifier.padding(start = 14.dp)
        )
    }
}

// Fila con ícono en círculo, título y subtítulo. Si se puede tocar muestra una flecha.
@Composable
fun FilaOpcion(
    icono: ImageVector,
    titulo: String,
    color: Color,
    modifier: Modifier = Modifier,
    subtitulo: String? = null,
    peligro: Boolean = false,
    cargando: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    val tocable = onClick != null
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(enabled = tocable && !cargando) { onClick?.invoke() }
            .padding(horizontal = 6.dp, vertical = 10.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.14f))
        ) {
            Icon(icono, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = titulo,
                color = if (peligro) CampusColores.Error else CampusColores.TextoOscuro,
                fontFamily = FuenteCampus,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            if (subtitulo != null) {
                Text(
                    text = subtitulo,
                    color = CampusColores.TextoSecundario,
                    fontFamily = FuenteCampus,
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
                tint = CampusColores.TextoSecundario
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
            .background(CampusColores.Borde)
    )
}

// Botón redondo flotante (BotonVolver de la PokeDex): se hunde al presionarlo
@Composable
fun BotonRedondo(
    icono: ImageVector,
    descripcion: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    fondo: Brush = DegradadosCampus.Suave,
    tinte: Color = CampusColores.AzulPrimario,
    tamano: Dp = 52.dp,
    habilitado: Boolean = true,
    cargando: Boolean = false
) {
    val interaccion = remember { MutableInteractionSource() }
    val presionado by interaccion.collectIsPressedAsState()
    val hundir by animateFloatAsState(
        targetValue = if (presionado) 0.88f else 1f,
        animationSpec = spring(dampingRatio = 0.5f),
        label = "hundirBoton"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(tamano)
            .graphicsLayer {
                scaleX = hundir
                scaleY = hundir
            }
            .alpha(if (habilitado) 1f else 0.5f)
            .shadow(8.dp, CircleShape, ambientColor = CampusColores.AzulProfundo, spotColor = CampusColores.AzulProfundo)
            .clip(CircleShape)
            .background(fondo)
            .border(1.5.dp, Color.White, CircleShape)
            .clickable(
                interactionSource = interaccion,
                indication = null,
                enabled = habilitado && !cargando,
                onClick = onClick
            )
    ) {
        // Brillo superior
        Canvas(modifier = Modifier.matchParentSize()) {
            drawOval(
                color = Color.White.copy(alpha = 0.35f),
                topLeft = Offset(size.width * 0.25f, size.height * 0.08f),
                size = Size(size.width * 0.5f, size.height * 0.2f)
            )
        }
        if (cargando) {
            CircularProgressIndicator(color = tinte, strokeWidth = 2.dp, modifier = Modifier.size(tamano * 0.42f))
        } else {
            Icon(icono, contentDescription = descripcion, tint = tinte, modifier = Modifier.size(tamano * 0.48f))
        }
    }
}
