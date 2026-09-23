package com.millones.clinicasaludplus.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
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
import com.millones.clinicasaludplus.data.Cita
import com.millones.clinicasaludplus.data.CitasRepository
import com.millones.clinicasaludplus.data.EstadoCita
import com.millones.clinicasaludplus.ui.theme.GrayBadgeBg
import com.millones.clinicasaludplus.ui.theme.GrayBadgeText
import com.millones.clinicasaludplus.ui.theme.GreenBadgeBg
import com.millones.clinicasaludplus.ui.theme.GreenBadgeText
import com.millones.clinicasaludplus.ui.theme.PurpleBorder
import com.millones.clinicasaludplus.ui.theme.PurpleLightBg
import com.millones.clinicasaludplus.ui.theme.PurplePrimary
import com.millones.clinicasaludplus.ui.theme.RedBadgeBg
import com.millones.clinicasaludplus.ui.theme.RedBadgeText

@Composable
fun MisCitasScreen(
    onMenuClick: () -> Unit = {}
) {
    var selectedFilter by remember { mutableStateOf("Todas") }
    val filtros = listOf("Todas", "Confirmadas", "Completadas", "Canceladas")

    var citaACancelar by remember { mutableStateOf<Cita?>(null) }

    // Reactive list that updates immediately on cancellation
    val citasStateList = remember {
        mutableStateListOf<Cita>().apply {
            addAll(CitasRepository.citas)
        }
    }

    fun syncCitas() {
        citasStateList.clear()
        citasStateList.addAll(CitasRepository.citas)
    }

    val citasFiltradas = remember(selectedFilter, citasStateList.toList()) {
        when (selectedFilter) {
            "Confirmadas" -> citasStateList.filter { it.estado == EstadoCita.CONFIRMADA }
            "Completadas" -> citasStateList.filter { it.estado == EstadoCita.COMPLETADA }
            "Canceladas" -> citasStateList.filter { it.estado == EstadoCita.CANCELADA }
            else -> citasStateList
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        // Top Row / Header
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
                text = "Mis citas",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF222222)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Filter Tabs
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(filtros) { filtro ->
                val isSelected = filtro == selectedFilter
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isSelected) PurplePrimary else Color(0xFFF0EFF4))
                        .clickable { selectedFilter = filtro }
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = filtro,
                        fontSize = 13.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) Color.White else Color(0xFF555555)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (citasFiltradas.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No tienes citas en este estado",
                    fontSize = 14.sp,
                    color = Color(0xFF888888)
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(citasFiltradas, key = { it.id }) { cita ->
                    CitaCardItem(
                        cita = cita,
                        onCancelar = { citaACancelar = cita }
                    )
                }
            }
        }
    }

    // Confirmation Dialog to Cancel
    if (citaACancelar != null) {
        AlertDialog(
            onDismissRequest = { citaACancelar = null },
            title = { Text("Cancelar cita", fontWeight = FontWeight.Bold) },
            text = { Text("¿Estás seguro de que deseas cancelar la cita con ${citaACancelar?.medicoNombre}?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        citaACancelar?.let { CitasRepository.cancelarCita(it.id) }
                        citaACancelar = null
                        syncCitas() // Instantaneous list state update
                    }
                ) {
                    Text("Sí, cancelar", color = RedBadgeText, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { citaACancelar = null }) {
                    Text("Volver")
                }
            }
        )
    }
}

@Composable
fun CitaCardItem(
    cita: Cita,
    onCancelar: () -> Unit
) {
    val isConfirmed = cita.estado == EstadoCita.CONFIRMADA
    val isCancelled = cita.estado == EstadoCita.CANCELADA

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = PurpleLightBg),
        border = BorderStroke(1.dp, PurpleBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
        ) {
            // Left purple strip indicator for confirmed
            if (isConfirmed) {
                Box(
                    modifier = Modifier
                        .width(6.dp)
                        .fillMaxHeight()
                        .background(
                            color = PurplePrimary,
                            shape = RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp)
                        )
                )
            }

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
                        text = cita.medicoNombre,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF222222)
                    )

                    Text(
                        text = cita.id,
                        fontSize = 11.sp,
                        color = Color(0xFF888888)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = cita.fechaHora,
                    fontSize = 14.sp,
                    color = Color(0xFF555555)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = Color(0xFF777777),
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = cita.sedeLocation,
                        fontSize = 12.sp,
                        color = Color(0xFF777777)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Status Pill
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                when {
                                    isConfirmed -> GreenBadgeBg
                                    isCancelled -> RedBadgeBg
                                    else -> GrayBadgeBg
                                }
                            )
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = when {
                                isConfirmed -> "Confirmada"
                                isCancelled -> "Cancelada"
                                else -> "Completada"
                            },
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = when {
                                isConfirmed -> GreenBadgeText
                                isCancelled -> RedBadgeText
                                else -> GrayBadgeText
                            }
                        )
                    }

                    if (isConfirmed) {
                        Text(
                            text = "Cancelar",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = RedBadgeText,
                            modifier = Modifier
                                .clickable { onCancelar() }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}
