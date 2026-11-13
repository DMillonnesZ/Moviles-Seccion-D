package com.tecsup.pokemon.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Block
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val RojoSuspension = Color(0xFFE53935)
private val RosaSuspension = Color(0xFFFFEBEE)
private val GrisSuspension = Color(0xFF7A6A5A)

// Aviso de cuenta suspendida: al intentar entrar o si la suspenden con la sesión abierta
@Composable
fun DialogoCuentaSuspendida(
    motivo: String?,
    onAceptar: () -> Unit
) {
    VentanaMadera(
        titulo = "Cuenta suspendida",
        icono = Icons.Rounded.Block,
        colorIcono = RojoSuspension,
        onCerrar = onAceptar
    ) {
        Text(
            text = "Un administrador suspendió tu cuenta, por eso no puedes ingresar por ahora.",
            color = CafeMadera,
            fontFamily = FuenteMadera,
            fontSize = 15.sp,
            textAlign = TextAlign.Center
        )

        if (!motivo.isNullOrBlank()) {
            Spacer(Modifier.height(12.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(RosaSuspension)
                    .border(1.dp, RojoSuspension.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
                    .padding(12.dp)
            ) {
                Text(
                    text = "Motivo",
                    color = RojoSuspension,
                    fontFamily = FuenteMadera,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
                Text(
                    text = motivo,
                    color = CafeMadera,
                    fontFamily = FuenteMadera,
                    fontSize = 15.sp
                )
            }
        }

        Spacer(Modifier.height(10.dp))
        Text(
            text = "Si crees que es un error, comunícate con el administrador.",
            color = GrisSuspension,
            fontFamily = FuenteMadera,
            fontSize = 13.sp,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(16.dp))
        BotonPildora(
            texto = "Entendido",
            fondo = FondoPildora.Dorado,
            colorTexto = CafeMadera,
            onClick = onAceptar,
            modifier = Modifier.fillMaxWidth()
        )
    }
}