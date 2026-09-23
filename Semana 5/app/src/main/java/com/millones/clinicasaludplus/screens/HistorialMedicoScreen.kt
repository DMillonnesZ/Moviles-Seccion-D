package com.millones.clinicasaludplus.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.millones.clinicasaludplus.ui.theme.PurpleContainer
import com.millones.clinicasaludplus.ui.theme.PurpleLightBg
import com.millones.clinicasaludplus.ui.theme.PurplePrimary

data class HistorialRegistro(
    val doctor: String,
    val especialidad: String,
    val fecha: String,
    val diagnostico: String,
    val tratamiento: String
)

val listaHistorial = listOf(
    HistorialRegistro(
        doctor = "Dr. Luis Vega",
        especialidad = "Pediatría General",
        fecha = "15 Sep 2023",
        diagnostico = "Chequeo médico anual de rutina.",
        tratamiento = "Multivitamínicos por 30 días."
    ),
    HistorialRegistro(
        doctor = "Dra. Rosa Díaz",
        especialidad = "Dermatología",
        fecha = "02 Jun 2023",
        diagnostico = "Dermatitis por contacto leve en antebrazo.",
        tratamiento = "Crema hidratante y corticoide tópico 5 días."
    ),
    HistorialRegistro(
        doctor = "Dra. Ana Torres",
        especialidad = "Cardiología",
        fecha = "10 Ene 2023",
        diagnostico = "Evaluación cardiovascular preventiva. ECG normal.",
        tratamiento = "Mantener actividad física regular."
    )
)

@Composable
fun HistorialMedicoScreen(
    onMenuClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = onMenuClick) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Abrir menú",
                    tint = Color(0xFF222222)
                )
            }
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "Historial médico",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF222222)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(listaHistorial) { item ->
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = PurpleLightBg),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(IntrinsicSize.Min)
                    ) {
                        Box(
                            modifier = Modifier
                                .width(6.dp)
                                .fillMaxHeight()
                                .background(
                                    color = PurplePrimary,
                                    shape = RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp)
                                )
                        )
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = item.doctor,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = Color(0xFF222222)
                                )
                                Text(
                                    text = item.fecha,
                                    fontSize = 12.sp,
                                    color = Color(0xFF777777)
                                )
                            }

                            Text(
                                text = item.especialidad,
                                fontSize = 13.sp,
                                color = Color(0xFF666666)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Diagnóstico: ${item.diagnostico}",
                                fontSize = 13.sp,
                                color = Color(0xFF333333)
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(PurpleContainer)
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "Tratamiento: ${item.tratamiento}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = PurplePrimary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
