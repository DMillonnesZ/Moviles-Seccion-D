package com.millones.campusgps.ui.componentes

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.ErrorOutline
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.millones.campusgps.model.AvisoMapa
import kotlinx.coroutines.delay

// Insignia flotante para los avisos ("Los demás ya ven tu ubicación", "Evento publicado"...):
// píldora de vidrio oscuro con un ícono de color, baja desde arriba y se va sola.
// Verde menta = todo bien, rojo = algo falló.
@Composable
fun AvisoFlotante(
    aviso: AvisoMapa?,
    onOcultar: () -> Unit,
    modifier: Modifier = Modifier,
    duracionMs: Long = 2600
) {
    // Guarda el último aviso para que se vea mientras se va
    var ultimo by remember { mutableStateOf<AvisoMapa?>(null) }
    LaunchedEffect(aviso) {
        if (aviso != null) {
            ultimo = aviso
            delay(duracionMs)
            onOcultar()
        }
    }

    AnimatedVisibility(
        visible = aviso != null,
        enter = slideInVertically(spring(dampingRatio = 0.6f)) { -it } + fadeIn() + scaleIn(initialScale = 0.9f),
        exit = slideOutVertically(tween(250)) { -it } + fadeOut(tween(250)),
        modifier = modifier
    ) {
        val actual = ultimo ?: return@AnimatedVisibility
        val color = if (actual.exito) CampusColores.VerdeMenta else Color(0xFFFF8A8A)
        val forma = RoundedCornerShape(50)

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .shadow(14.dp, forma, ambientColor = Color.Black, spotColor = Color.Black)
                .clip(forma)
                .background(VidrioMapa.FondoSolido.copy(alpha = 0.96f))
                .border(1.dp, color.copy(alpha = 0.6f), forma)
                .padding(start = 6.dp, end = 18.dp, top = 6.dp, bottom = 6.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.2f))
            ) {
                Icon(
                    imageVector = if (actual.exito) Icons.Rounded.CheckCircle else Icons.Rounded.ErrorOutline,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(Modifier.width(10.dp))
            Text(
                text = actual.texto,
                color = Color.White,
                fontFamily = FuenteCampus,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
