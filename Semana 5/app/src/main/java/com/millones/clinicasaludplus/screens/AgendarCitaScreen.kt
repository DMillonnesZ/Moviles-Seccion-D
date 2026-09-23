package com.millones.clinicasaludplus.screens

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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
    val fechas = listOf(
        DateItem("Jue", "26", "Jueves 26"),
        DateItem("Vie", "27", "Viernes 27"),
        DateItem("Sáb", "28", "Sábado 28")
    )

    val horarios = listOf("9:00", "10:30", "3:00")

    var selectedDateItem by remember { mutableStateOf(fechas[1]) }
    var selectedTimeItem by remember { mutableStateOf("10:30") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
            .padding(16.dp)
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
                text = "Agendar cita",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color(0xFF222222)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Date selection
        Text(
            text = "Selecciona fecha",
            fontSize = 14.sp,
            color = Color(0xFF555555),
            modifier = Modifier.padding(bottom = 12.dp)
        )

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

        Spacer(modifier = Modifier.height(28.dp))

        // Time selection
        Text(
            text = "Selecciona hora",
            fontSize = 14.sp,
            color = Color(0xFF555555),
            modifier = Modifier.padding(bottom = 12.dp)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(horarios) { timeStr ->
                val isSelected = timeStr == selectedTimeItem
                Box(
                    modifier = Modifier
                        .width(80.dp)
                        .height(48.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            if (isSelected) PurplePrimary else PurpleLightBg
                        )
                        .clickable { selectedTimeItem = timeStr },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = timeStr,
                        fontSize = 14.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) Color.White else Color(0xFF333333)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Confirm button
        Button(
            onClick = {
                val formattedTime = if (selectedTimeItem.contains("am", ignoreCase = true) || selectedTimeItem.contains("pm", ignoreCase = true)) {
                    selectedTimeItem
                } else {
                    if (selectedTimeItem == "3:00") "$selectedTimeItem pm" else "$selectedTimeItem am"
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

        Spacer(modifier = Modifier.height(16.dp))
    }
}
