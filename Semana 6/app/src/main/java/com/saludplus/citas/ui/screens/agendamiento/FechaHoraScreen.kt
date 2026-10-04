package com.saludplus.citas.ui.screens.agendamiento

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.saludplus.citas.data.repository.Repositorio
import com.saludplus.citas.ui.components.AvatarMedico
import com.saludplus.citas.ui.components.BarraSuperior
import com.saludplus.citas.ui.components.BotonAzul
import com.saludplus.citas.ui.theme.AzulOscuro
import com.saludplus.citas.ui.theme.AzulPrimario
import com.saludplus.citas.ui.theme.GrisTexto

// Día mostrado en el selector. La fecha se guarda en formato ISO (año-mes-día).
private data class DiaCalendario(
    val fecha: String,
    val diaSemana: String,
    val numero: Int
)

// Fase 1: lista fija de días hábiles. En la Fase 2 se genera con LocalDate.
private val diasDeLaSemana = listOf(
    DiaCalendario("2026-10-05", "Lun", 5),
    DiaCalendario("2026-10-06", "Mar", 6),
    DiaCalendario("2026-10-07", "Mié", 7),
    DiaCalendario("2026-10-08", "Jue", 8),
    DiaCalendario("2026-10-09", "Vie", 9)
)

@Composable
fun FechaHoraScreen(
    medicoId: Int,
    onAtras: () -> Unit,
    onContinuar: (fecha: String, hora: String) -> Unit
) {
    val medico = Repositorio.obtenerMedico(medicoId)
    val especialidad = medico?.let { Repositorio.obtenerEspecialidad(it.especialidadId) }

    var fechaSeleccionada by remember { mutableStateOf<String?>(null) }
    var horaSeleccionada by remember { mutableStateOf<String?>(null) }

    // Horas libres de este médico en el día elegido (las reservadas no aparecen)
    val horarios = fechaSeleccionada
        ?.let { Repositorio.horariosDisponibles(medicoId, it) }
        ?: emptyList()

    val puedeContinuar = fechaSeleccionada != null && horaSeleccionada != null

    Scaffold(
        containerColor = Color.White,
        topBar = { BarraSuperior(titulo = "Seleccionar fecha y hora", onAtras = onAtras) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
        ) {
            // Card del médico
            if (medico != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(Color(0xFFEFF4FC))
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AvatarMedico(nombre = medico.nombre, foto = medico.foto)
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Text(
                            text = medico.nombre,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = AzulOscuro
                        )
                        Text(
                            text = especialidad?.nombre ?: "",
                            fontSize = 14.sp,
                            color = GrisTexto
                        )
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // Mes y año con flechas (se activan en la Fase 2)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { /* Fase 2: semana anterior */ }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "Semana anterior",
                        tint = GrisTexto
                    )
                }
                Text(
                    text = "Octubre 2026",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = AzulOscuro
                )
                IconButton(onClick = { /* Fase 2: semana siguiente */ }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "Semana siguiente",
                        tint = GrisTexto
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            // Selector de día
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                diasDeLaSemana.forEach { dia ->
                    DiaChip(
                        dia = dia,
                        seleccionado = dia.fecha == fechaSeleccionada,
                        onClick = {
                            fechaSeleccionada = dia.fecha
                            horaSeleccionada = null // al cambiar de día se reinicia la hora
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            // Cuadrícula de horarios disponibles
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                when {
                    fechaSeleccionada == null -> MensajeCentrado("Selecciona un día para ver los horarios")
                    horarios.isEmpty() -> MensajeCentrado("No hay horarios disponibles para este día")
                    else -> LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(horarios, key = { it }) { hora ->
                            HoraChip(
                                hora = hora,
                                seleccionada = hora == horaSeleccionada,
                                onClick = { horaSeleccionada = hora }
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            BotonAzul(
                texto = "Continuar",
                habilitado = puedeContinuar,
                onClick = {
                    val fecha = fechaSeleccionada
                    val hora = horaSeleccionada
                    if (fecha != null && hora != null) onContinuar(fecha, hora)
                }
            )

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun MensajeCentrado(texto: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Text(
            text = texto,
            fontSize = 14.sp,
            color = GrisTexto,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}

@Composable
private fun DiaChip(
    dia: DiaCalendario,
    seleccionado: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val fondo = if (seleccionado) AzulPrimario else Color(0xFFF1F5FB)
    val textoDia = if (seleccionado) Color.White else GrisTexto
    val textoNumero = if (seleccionado) Color.White else AzulOscuro

    Column(
        modifier = modifier
            .height(72.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(fondo)
            .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = dia.diaSemana, fontSize = 12.sp, color = textoDia)
        Spacer(Modifier.height(4.dp))
        Text(
            text = dia.numero.toString(),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = textoNumero
        )
    }
}

@Composable
private fun HoraChip(
    hora: String,
    seleccionada: Boolean,
    onClick: () -> Unit
) {
    val fondo = if (seleccionada) AzulPrimario else Color(0xFFF1F5FB)
    val colorTexto = if (seleccionada) Color.White else AzulOscuro

    Box(
        modifier = Modifier
            .height(52.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(fondo)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = hora,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = colorTexto
        )
    }
}