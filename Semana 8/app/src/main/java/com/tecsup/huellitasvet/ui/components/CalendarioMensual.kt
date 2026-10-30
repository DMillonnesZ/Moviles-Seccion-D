package com.tecsup.huellitasvet.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tecsup.huellitasvet.ui.theme.BordeSuave
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun CalendarioMensual(
    mes: YearMonth,
    fechaSeleccionada: LocalDate,
    diasConCitas: Set<String>,
    onElegir: (LocalDate) -> Unit,
    onAnterior: () -> Unit,
    onSiguiente: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cs = MaterialTheme.colorScheme
    val tituloMes = mes
        .format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.forLanguageTag("es")))
        .replaceFirstChar { it.uppercase() }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cs.surface),
        border = BorderStroke(1.dp, BordeSuave),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp)) {
            // Cabecera: Flechas y Mes
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onAnterior) {
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Mes anterior")
                }
                Text(
                    text = tituloMes,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )
                IconButton(onClick = onSiguiente) {
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Mes siguiente")
                }
            }

            Spacer(Modifier.height(8.dp))

            // Iniciales de días L M M J V S D
            Row(Modifier.fillMaxWidth()) {
                listOf("L", "M", "M", "J", "V", "S", "D").forEach { d ->
                    Text(
                        text = d,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.SemiBold,
                        style = MaterialTheme.typography.labelSmall,
                        color = cs.onSurfaceVariant
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            // Cuadrícula de días
            val primerDia = mes.atDay(1)
            val offset = primerDia.dayOfWeek.value - 1 // MONDAY = 1 -> offset 0
            val diasEnMes = mes.lengthOfMonth()
            val totalCeldas = offset + diasEnMes
            val filas = (0 until totalCeldas).chunked(7)

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                filas.forEach { fila ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        for (col in 0 until 7) {
                            val index = fila.getOrNull(col)
                            if (index == null || index < offset) {
                                Spacer(Modifier.weight(1f).height(42.dp))
                            } else {
                                val diaMes = index - offset + 1
                                val fecha = mes.atDay(diaMes)
                                val esHoy = fecha == LocalDate.now()
                                val esSeleccionado = fecha == fechaSeleccionada
                                val tieneCita = fecha.toString() in diasConCitas

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(42.dp)
                                        .clip(CircleShape)
                                        .clickable { onElegir(fecha) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(28.dp)
                                                .background(
                                                    if (esSeleccionado) cs.primary else cs.surface.copy(alpha = 0f),
                                                    CircleShape
                                                )
                                                .border(
                                                    width = if (esHoy && !esSeleccionado) 1.dp else 0.dp,
                                                    color = if (esHoy && !esSeleccionado) cs.primary else cs.surface.copy(alpha = 0f),
                                                    shape = CircleShape
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "$diaMes",
                                                fontSize = 13.sp,
                                                fontWeight = if (esSeleccionado || esHoy) FontWeight.Bold else FontWeight.Normal,
                                                color = when {
                                                    esSeleccionado -> cs.onPrimary
                                                    esHoy -> cs.primary
                                                    else -> cs.onSurface
                                                }
                                            )
                                        }

                                        if (tieneCita) {
                                            Spacer(Modifier.height(2.dp))
                                            Box(
                                                modifier = Modifier
                                                    .size(4.dp)
                                                    .background(
                                                        if (esSeleccionado) cs.primary else cs.primary,
                                                        CircleShape
                                                    )
                                            )
                                        } else {
                                            Spacer(Modifier.height(6.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
