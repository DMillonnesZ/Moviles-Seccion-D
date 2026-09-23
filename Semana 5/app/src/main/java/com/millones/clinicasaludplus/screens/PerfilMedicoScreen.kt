package com.millones.clinicasaludplus.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.millones.clinicasaludplus.data.listaMedicos
import com.millones.clinicasaludplus.ui.theme.PurpleIconBg
import com.millones.clinicasaludplus.ui.theme.PurplePrimary
import com.millones.clinicasaludplus.ui.theme.StarYellow

@Composable
fun PerfilMedicoScreen(
    nombre: String,
    especialidad: String,
    calificacion: String,
    onBackClick: () -> Unit = {},
    onAgendarClick: () -> Unit
) {
    val medico = listaMedicos.find { it.nombre.equals(nombre, ignoreCase = true) }
    val expText = medico?.experiencia ?: "12 años exp."
    val resenasText = medico?.resenas ?: "128 reseñas"
    val bioText = medico?.bio ?: "Especialista en arritmias e hipertensión, formación en la Clínica Mayo."
    val especialidadDisplay = medico?.especialidad ?: especialidad
    val calificacionDisplay = if (calificacion.isNotEmpty()) calificacion else (medico?.calificacion?.toString() ?: "4.9")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        // Top Bar
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver",
                    tint = Color(0xFF222222)
                )
            }
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "Perfil del médico",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color(0xFF222222)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Center Content (moved higher up)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Large circular avatar
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape)
                    .background(PurpleIconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = PurplePrimary,
                    modifier = Modifier.size(50.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = nombre.ifEmpty { "Dra. Ana Torres" },
                fontWeight = FontWeight.Bold,
                fontSize = 19.sp,
                color = Color(0xFF222222)
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = "$especialidadDisplay · $expText",
                fontSize = 13.sp,
                color = Color(0xFF777777)
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Rating line - shifted slightly right as in reference image
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = 24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = StarYellow,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "$calificacionDisplay ($resenasText)",
                    fontSize = 13.sp,
                    color = Color(0xFF555555)
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Description - direct text without Card wrapper
        Text(
            text = bioText,
            fontSize = 14.sp,
            color = Color(0xFF444444),
            lineHeight = 20.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
        )

        Spacer(modifier = Modifier.weight(1f))

        // Action Button
        Button(
            onClick = onAgendarClick,
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Text(
                text = "Agendar cita",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(12.dp))
    }
}
