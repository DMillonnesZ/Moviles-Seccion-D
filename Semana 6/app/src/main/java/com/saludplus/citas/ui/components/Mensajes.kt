package com.saludplus.citas.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.WavingHand
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.saludplus.citas.ui.theme.ColoresSemanticos
import com.saludplus.citas.ui.theme.Duraciones
import com.saludplus.citas.ui.theme.Espaciado
import com.saludplus.citas.ui.theme.Formas
import kotlinx.coroutines.delay

enum class TipoMensaje {
    EXITO,
    INFORMACION,
    ERROR,
    BIENVENIDA
}

data class MensajeData(
    val texto: String,
    val tipo: TipoMensaje = TipoMensaje.INFORMACION,
    val duracionMs: Long = 3500L,
    val id: Long = System.currentTimeMillis()
)

object MensajeController {
    var mensajeActual by mutableStateOf<MensajeData?>(null)
        private set

    fun mostrar(texto: String, tipo: TipoMensaje = TipoMensaje.INFORMACION, duracionMs: Long = 3500L) {
        mensajeActual = MensajeData(texto, tipo, duracionMs, System.currentTimeMillis())
    }

    fun ocultar() {
        mensajeActual = null
    }
}

@Composable
fun MensajeHost(
    modifier: Modifier = Modifier,
    mensaje: MensajeData? = MensajeController.mensajeActual,
    onDismiss: () -> Unit = { MensajeController.ocultar() }
) {
    LaunchedEffect(mensaje?.id) {
        if (mensaje != null) {
            delay(mensaje.duracionMs)
            onDismiss()
        }
    }

    AnimatedVisibility(
        visible = mensaje != null,
        enter = slideInVertically(
            initialOffsetY = { -it },
            animationSpec = tween(durationMillis = Duraciones.media)
        ) + fadeIn(animationSpec = tween(durationMillis = Duraciones.media)),
        exit = slideOutVertically(
            targetOffsetY = { -it },
            animationSpec = tween(durationMillis = Duraciones.media)
        ) + fadeOut(animationSpec = tween(durationMillis = Duraciones.media)),
        modifier = modifier
    ) {
        mensaje?.let { data ->
            val (fondoColor, textoColor, icono, iconoColor) = when (data.tipo) {
                TipoMensaje.EXITO -> Quadruple(
                    ColoresSemanticos.ExitoFondo,
                    ColoresSemanticos.Exito,
                    Icons.Default.CheckCircle,
                    ColoresSemanticos.Exito
                )
                TipoMensaje.INFORMACION -> Quadruple(
                    ColoresSemanticos.InfoFondo,
                    ColoresSemanticos.Info,
                    Icons.Default.Info,
                    ColoresSemanticos.Info
                )
                TipoMensaje.ERROR -> Quadruple(
                    ColoresSemanticos.ErrorFondo,
                    ColoresSemanticos.Error,
                    Icons.Default.Error,
                    ColoresSemanticos.Error
                )
                TipoMensaje.BIENVENIDA -> Quadruple(
                    ColoresSemanticos.InfoFondo,
                    ColoresSemanticos.Info,
                    Icons.Default.WavingHand,
                    ColoresSemanticos.Info
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Espaciado.lg, vertical = Espaciado.sm)
                    .clip(Formas.mediana)
                    .background(fondoColor)
                    .clickable { onDismiss() }
                    .padding(horizontal = Espaciado.lg, vertical = Espaciado.md)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Espaciado.sm)
                ) {
                    Icon(
                        imageVector = icono,
                        contentDescription = null,
                        tint = iconoColor,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = data.texto,
                        style = MaterialTheme.typography.bodyMedium,
                        color = textoColor,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cerrar mensaje",
                            tint = textoColor.copy(alpha = 0.7f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

private data class Quadruple<A, B, C, D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D
)
