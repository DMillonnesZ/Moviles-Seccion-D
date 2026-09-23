package com.millones.clinicasaludplus.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.millones.clinicasaludplus.ui.theme.GreenBadgeBg
import com.millones.clinicasaludplus.ui.theme.GreenBadgeText

@Composable
fun ConfirmacionScreen(
    nombre: String,
    especialidad: String,
    fecha: String,
    horario: String,
    onFinalizarClick: () -> Unit
) {
    val doctorName = nombre.ifEmpty { "Dra. Ana Torres" }
    val displayFecha = if (fecha.contains("27")) "Viernes 27" else fecha.ifEmpty { "Viernes 27" }
    val displayHorario = horario.ifEmpty { "10:30 am" }
    val fechaHoraFormatted = "$displayFecha, $displayHorario"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(1f))

        // Mint Green Circle with Checkmark
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(GreenBadgeBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Confirmado",
                tint = GreenBadgeText,
                modifier = Modifier.size(44.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Success Title
        Text(
            text = "¡Cita agendada!",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF222222)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Doctor Name
        Text(
            text = doctorName,
            fontSize = 15.sp,
            color = Color(0xFF666666)
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Date and Time
        Text(
            text = fechaHoraFormatted,
            fontSize = 14.sp,
            color = Color(0xFF777777)
        )

        Spacer(modifier = Modifier.weight(1f))

        // "Ver mis citas" Button
        Button(
            onClick = onFinalizarClick,
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF0EDF5)),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Text(
                text = "Ver mis citas",
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF444444)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}
