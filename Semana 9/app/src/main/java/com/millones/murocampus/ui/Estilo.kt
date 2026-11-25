package com.millones.murocampus.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.millones.murocampus.ui.theme.Azul
import com.millones.murocampus.ui.theme.AzulBoton1
import com.millones.murocampus.ui.theme.AzulBoton2
import com.millones.murocampus.ui.theme.BordeCampo
import com.millones.murocampus.ui.theme.BordeTarjeta
import com.millones.murocampus.ui.theme.FondoInferior
import com.millones.murocampus.ui.theme.FondoMedio
import com.millones.murocampus.ui.theme.FondoSuperior
import com.millones.murocampus.ui.theme.Menta
import com.millones.murocampus.ui.theme.Superficie
import com.millones.murocampus.ui.theme.SuperficieCampo
import com.millones.murocampus.ui.theme.TextoSecundario

val FormaTarjeta = RoundedCornerShape(20.dp)

/** Fondo azul marino con degradado vertical, como en CampusGPS. */
@Composable
fun FondoDegradado(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(FondoSuperior, FondoMedio, FondoInferior)))
    ) {
        content()
    }
}

/** Superficie de "vidrio": fondo azul oscuro translúcido con borde azul. */
fun Modifier.vidrio(forma: Shape = FormaTarjeta): Modifier =
    this
        .background(Superficie.copy(alpha = 0.85f), forma)
        .border(1.dp, BordeTarjeta.copy(alpha = 0.6f), forma)

/** Botón cuadrado redondeado (menú, volver, +). `destacado` lo pinta con degradado azul. */
@Composable
fun BotonIcono(
    icono: ImageVector,
    descripcion: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    destacado: Boolean = false
) {
    val forma = RoundedCornerShape(18.dp)
    val fondo = if (destacado) {
        Modifier.background(Brush.linearGradient(listOf(AzulBoton1, AzulBoton2)), forma)
    } else {
        Modifier.background(Superficie, forma)
    }
    Box(
        modifier = modifier
            .size(52.dp)
            .then(fondo)
            .border(
                1.dp,
                if (destacado) Azul.copy(alpha = 0.7f) else BordeTarjeta.copy(alpha = 0.6f),
                forma
            )
            .clip(forma)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(icono, contentDescription = descripcion, tint = Color.White)
    }
}

/** Botón principal azul con degradado, ancho completo (Publicar evento). */
@Composable
fun BotonPrimario(
    texto: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val forma = RoundedCornerShape(20.dp)
    val fondo = if (enabled) {
        Brush.horizontalGradient(listOf(AzulBoton1, AzulBoton2))
    } else {
        Brush.horizontalGradient(listOf(Color(0xFF2A3558), Color(0xFF2A3558)))
    }
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(54.dp)
            .background(fondo, forma)
            .border(
                1.dp,
                if (enabled) Azul.copy(alpha = 0.7f) else BordeCampo.copy(alpha = 0.5f),
                forma
            )
            .clip(forma)
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            texto,
            color = if (enabled) Color.White else TextoSecundario,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleMedium
        )
    }
}

/** Botón con borde menta (Reintentar, Cargar más). */
@Composable
fun BotonSecundario(
    texto: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(
            1.dp,
            if (enabled) Menta.copy(alpha = 0.6f) else BordeCampo.copy(alpha = 0.4f)
        ),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = Menta,
            disabledContentColor = TextoSecundario.copy(alpha = 0.5f)
        )
    ) {
        Text(texto, fontWeight = FontWeight.Bold)
    }
}

/** Encabezado de pantalla: botón opcional a la izquierda, título con acento menta y botón a la derecha. */
@Composable
fun Encabezado(
    titulo: String,
    acento: String = "",
    subtitulo: String? = null,
    inicio: (@Composable () -> Unit)? = null,
    fin: (@Composable () -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        inicio?.invoke()
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = buildAnnotatedString {
                    append(titulo)
                    withStyle(SpanStyle(color = Menta)) { append(acento) }
                },
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            if (subtitulo != null) {
                Text(
                    subtitulo,
                    color = TextoSecundario,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
        fin?.invoke()
    }
}

/** Colores de los campos de texto sobre fondo oscuro. */
@Composable
fun coloresCampo() = OutlinedTextFieldDefaults.colors(
    focusedContainerColor = SuperficieCampo,
    unfocusedContainerColor = SuperficieCampo,
    disabledContainerColor = SuperficieCampo,
    focusedBorderColor = Azul,
    unfocusedBorderColor = BordeCampo,
    disabledBorderColor = BordeCampo.copy(alpha = 0.5f),
    focusedTextColor = Color.White,
    unfocusedTextColor = Color.White,
    disabledTextColor = TextoSecundario,
    focusedLabelColor = Azul,
    unfocusedLabelColor = TextoSecundario,
    disabledLabelColor = TextoSecundario,
    focusedPlaceholderColor = TextoSecundario,
    unfocusedPlaceholderColor = TextoSecundario,
    focusedLeadingIconColor = Azul,
    unfocusedLeadingIconColor = TextoSecundario,
    focusedTrailingIconColor = TextoSecundario,
    unfocusedTrailingIconColor = TextoSecundario,
    cursorColor = Azul
)
