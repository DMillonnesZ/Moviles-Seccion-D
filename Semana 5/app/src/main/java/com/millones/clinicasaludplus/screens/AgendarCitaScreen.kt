package com.millones.clinicasaludplus.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.millones.clinicasaludplus.data.listaMedicos
import com.millones.clinicasaludplus.ui.theme.PurpleBorder
import com.millones.clinicasaludplus.ui.theme.PurpleLightBg
import com.millones.clinicasaludplus.ui.theme.PurplePrimary

data class DateItem(
    val dayName: String,
    val dayNumber: String,
    val displayValue: String
)

@Composable
fun AgendarCitaScreen(
    nombre: String,
    especialidad: String,
    onBackClick: () -> Unit = {},
    onConfirmarClick: (String, String) -> Unit
) {
    val medico = listaMedicos.find { it.nombre.equals(nombre, ignoreCase = true) } ?: listaMedicos.first()

    val fechas = listOf(
        DateItem("Jue", "26", "Jueves 26"),
        DateItem("Vie", "27", "Viernes 27"),
        DateItem("Sáb", "28", "Sábado 28"),
        DateItem("Lun", "30", "Lunes 30")
    )

    val horariosManana = listOf("9:00", "10:30", "11:30")
    val horariosTarde = listOf("3:00", "4:30", "5:30")

    var selectedDateItem by remember { mutableStateOf(fechas[1]) }
    var selectedTimeItem by remember { mutableStateOf("10:30") }
    var motivoConsulta by remember { mutableStateOf("") }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
    ) {
        // Top Bar
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 4.dp)
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
                text = "Agendar cita",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color(0xFF222222)
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // Doctor Summary Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = PurpleLightBg),
                border = BorderStroke(1.dp, PurpleBorder),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = medico.nombre,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color(0xFF222222)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = medico.especialidad,
                        fontSize = 13.sp,
                        color = Color(0xFF666666)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = PurplePrimary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = medico.sedeLocation,
                            fontSize = 12.sp,
                            color = Color(0xFF555555)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Month Label & Date selection
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = null,
                    tint = PurplePrimary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Selecciona fecha - Septiembre 2026",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF333333)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(fechas) { dateItem ->
                    val isSelected = dateItem == selectedDateItem
                    Box(
                        modifier = Modifier
                            .width(72.dp)
                            .height(72.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                if (isSelected) PurplePrimary else PurpleLightBg
                            )
                            .clickable { selectedDateItem = dateItem },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = dateItem.dayName,
                                fontSize = 12.sp,
                                color = if (isSelected) Color.White.copy(alpha = 0.85f) else Color(0xFF777777)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = dateItem.dayNumber,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else Color(0xFF222222)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Time selection: Turno Mañana
            Text(
                text = "Turno Mañana",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF666666),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(horariosManana) { timeStr ->
                    val isSelected = timeStr == selectedTimeItem
                    Box(
                        modifier = Modifier
                            .width(80.dp)
                            .height(44.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                if (isSelected) PurplePrimary else PurpleLightBg
                            )
                            .clickable { selectedTimeItem = timeStr },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$timeStr am",
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) Color.White else Color(0xFF333333)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Time selection: Turno Tarde
            Text(
                text = "Turno Tarde",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF666666),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(horariosTarde) { timeStr ->
                    val isSelected = timeStr == selectedTimeItem
                    Box(
                        modifier = Modifier
                            .width(80.dp)
                            .height(44.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                if (isSelected) PurplePrimary else PurpleLightBg
                            )
                            .clickable { selectedTimeItem = timeStr },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$timeStr pm",
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) Color.White else Color(0xFF333333)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Reason for visit (Optional notes)
            Text(
                text = "Motivo de la consulta (Opcional)",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF555555),
                modifier = Modifier.padding(bottom = 6.dp)
            )

            OutlinedTextField(
                value = motivoConsulta,
                onValueChange = { motivoConsulta = it },
                placeholder = {
                    Text(
                        text = "Ej. Chequeo general, molestias recurrentes...",
                        fontSize = 13.sp,
                        color = Color(0xFF999999)
                    )
                },
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PurplePrimary,
                    unfocusedBorderColor = PurpleBorder,
                    focusedContainerColor = PurpleLightBg,
                    unfocusedContainerColor = PurpleLightBg
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))
        }

        // Bottom Confirmation Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Button(
                onClick = {
                    val formattedTime = if (selectedTimeItem.contains("am", ignoreCase = true) || selectedTimeItem.contains("pm", ignoreCase = true)) {
                        selectedTimeItem
                    } else {
                        if (horariosTarde.contains(selectedTimeItem)) "$selectedTimeItem pm" else "$selectedTimeItem am"
                    }
                    onConfirmarClick(
                        selectedDateItem.displayValue,
                        formattedTime
                    )
                },
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text(
                    text = "Confirmar cita",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}
