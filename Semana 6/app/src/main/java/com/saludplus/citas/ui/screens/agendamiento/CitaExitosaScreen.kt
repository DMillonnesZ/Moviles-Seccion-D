package com.saludplus.citas.ui.screens.agendamiento

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.saludplus.citas.data.repository.Repositorio
import com.saludplus.citas.ui.components.BotonAzul
import com.saludplus.citas.ui.components.EnlaceTexto
import com.saludplus.citas.ui.components.FilaDetalle
import com.saludplus.citas.ui.components.TarjetaSuave
import com.saludplus.citas.ui.theme.AzulOscuro
import com.saludplus.citas.ui.theme.GrisTexto

@Composable
fun CitaExitosaScreen(
    onVerMisCitas: () -> Unit,
    onIrInicio: () -> Unit
) {
    // El botón Atrás del celular lleva a Inicio, no de vuelta al flujo de agendamiento
    BackHandler { onIrInicio() }

    // La cita recién creada es la última del usuario en la lista
    val correo = Repositorio.usuarioActual?.correo ?: ""
    val cita = Repositorio.citas.lastOrNull { it.correoUsuario.equals(correo, ignoreCase = true) }
    val medico = cita?.let { Repositorio.obtenerMedico(it.medicoId) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .systemBarsPadding()
            .padding(horizontal = 24.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(32.dp))

        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(Color(0xFFDDF7E8)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = Color(0xFF1E9E5A),
                modifier = Modifier.size(80.dp)
            )
        }

        Spacer(Modifier.height(24.dp))

        Text(
            text = "¡Cita agendada!",
            fontSize = 26.sp,
            fontWeight = FontWeight.ExtraBold,
            color = AzulOscuro
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = "Tu cita fue registrada correctamente",
            fontSize = 15.sp,
            color = GrisTexto,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(24.dp))

        if (cita != null) {
            TarjetaSuave(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    FilaDetalle(Icons.Default.Person, "Médico", medico?.nombre ?: "")
                    FilaDetalle(Icons.Default.DateRange, "Fecha", cita.fecha)
                    FilaDetalle(Icons.Default.AccessTime, "Hora", cita.hora)
                }
            }
        }

        Spacer(Modifier.weight(1f))

        BotonAzul(texto = "Ver mis citas", onClick = onVerMisCitas)
        Spacer(Modifier.height(4.dp))
        EnlaceTexto(texto = "Volver al inicio", onClick = onIrInicio)
    }
}