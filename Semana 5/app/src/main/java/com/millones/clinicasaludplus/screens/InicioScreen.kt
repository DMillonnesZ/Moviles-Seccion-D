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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
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
import com.millones.clinicasaludplus.data.Medico
import com.millones.clinicasaludplus.data.listaMedicos
import com.millones.clinicasaludplus.ui.theme.PurpleIconBg
import com.millones.clinicasaludplus.ui.theme.PurpleLightBg
import com.millones.clinicasaludplus.ui.theme.PurplePrimary
import com.millones.clinicasaludplus.ui.theme.StarYellow

@Composable
fun InicioScreen(
    modifier: Modifier = Modifier,
    onMenuClick: () -> Unit = {},
    onMedicoClick: (Medico) -> Unit
) {
    var selectedEspecialidad by remember { mutableStateOf("Cardiología") }

    val especialidades = listOf("Cardiología", "Pediatría")

    val medicosFiltrados = remember(selectedEspecialidad) {
        if (selectedEspecialidad.isEmpty()) {
            listaMedicos
        } else {
            listaMedicos.filter {
                it.especialidad.contains(selectedEspecialidad, ignoreCase = true) ||
                        (selectedEspecialidad == "Pediatría" && it.especialidad.contains("Pediatra", ignoreCase = true)) ||
                        (selectedEspecialidad == "Dermatología" && it.especialidad.contains("Dermatóloga", ignoreCase = true))
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFFFFFFF))
    ) {
        // Top Header Banner with statusBarsPadding and extra breathing room
        Surface(
            color = PurplePrimary,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 12.dp, vertical = 12.dp)
            ) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onMenuClick) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Abrir menú",
                            tint = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Clínica Salud+",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = "Hola, Juan",
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 14.sp,
                    modifier = Modifier.padding(start = 48.dp, bottom = 8.dp)
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Category Chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(especialidades) { especialidad ->
                    val isSelected = especialidad == selectedEspecialidad
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                if (isSelected) PurplePrimary else Color(0xFFEFEFF4)
                            )
                            .clickable {
                                selectedEspecialidad = if (isSelected) "" else especialidad
                            }
                            .padding(horizontal = 18.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = especialidad,
                            color = if (isSelected) Color.White else Color(0xFF555555),
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Médicos disponibles",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color(0xFF222222)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Doctors List
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(if (medicosFiltrados.isNotEmpty()) medicosFiltrados else listaMedicos) { medico ->
                    MedicoItemCard(
                        medico = medico,
                        onClick = { onMedicoClick(medico) }
                    )
                }
            }
        }
    }
}

@Composable
fun MedicoItemCard(
    medico: Medico,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = PurpleLightBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Circle avatar icon with plus
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(PurpleIconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = PurplePrimary,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
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
                    fontSize = 14.sp,
                    color = Color(0xFF777777)
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = StarYellow,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "%.1f".format(medico.calificacion),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color(0xFF333333)
                )
            }
        }
    }
}
