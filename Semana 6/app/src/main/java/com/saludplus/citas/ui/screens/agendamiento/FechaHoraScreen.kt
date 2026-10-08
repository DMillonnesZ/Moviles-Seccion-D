package com.saludplus.citas.ui.screens.agendamiento

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
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
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.saludplus.citas.data.repository.Repositorio
import com.saludplus.citas.ui.components.AvatarMedico
import com.saludplus.citas.ui.components.BarraSuperior
import com.saludplus.citas.ui.components.BotonAzul
import com.saludplus.citas.ui.components.efectoPresion
import com.saludplus.citas.ui.theme.AzulClaro
import com.saludplus.citas.ui.theme.AzulOscuro
import com.saludplus.citas.ui.theme.AzulPrimario
import com.saludplus.citas.ui.theme.GrisTexto
import com.saludplus.citas.util.Fechas
import java.time.LocalDate

@Composable
fun FechaHoraScreen(
    medicoId: Int,
    onAtras: () -> Unit,
    onContinuar: (fecha: String, hora: String) -> Unit
) {
    val medico = Repositorio.obtenerMedico(medicoId)
    val especialidad = medico?.let { Repositorio.obtenerEspecialidad(it.especialidadId) }

    val hoy = remember { LocalDate.now() }
    var offsetSemanas by rememberSaveable { mutableLongStateOf(0L) }

    var fechaSeleccionada by rememberSaveable { mutableStateOf<String?>(null) }
    var horaSeleccionada by rememberSaveable { mutableStateOf<String?>(null) }

    // Generar la semana dinámica con LocalDate
    val semana = remember(offsetSemanas) { Fechas.semanaDeCalendario(hoy, offsetSemanas) }
    val primerDiaSemana = semana.firstOrNull() ?: hoy
    val tituloMesAnio = Fechas.mesYAnio(primerDiaSemana)

    // Horas libres de este médico en la fecha seleccionada
    val horarios = fechaSeleccionada
        ?.let { Repositorio.horariosDisponibles(medicoId, it) }
        ?: emptyList()

    val puedeContinuar = fechaSeleccionada != null && horaSeleccionada != null

    val textoBoton = if (puedeContinuar && fechaSeleccionada != null && horaSeleccionada != null) {
        val parsed = LocalDate.parse(fechaSeleccionada)
        val diaNombre = Fechas.nombreDiaLargo(parsed.dayOfWeek)
        "Continuar ($diaNombre ${parsed.dayOfMonth} de ${Fechas.nombreMesMinuscula(parsed.monthValue)} · $horaSeleccionada)"
    } else {
        "Continuar"
    }

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

            Spacer(Modifier.height(16.dp))

            // Mes y año con navegación por semanas
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        if (offsetSemanas > 0) {
                            offsetSemanas--
                            fechaSeleccionada = null
                            horaSeleccionada = null
                        }
                    },
                    enabled = offsetSemanas > 0
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "Semana anterior",
                        tint = if (offsetSemanas > 0) AzulOscuro else Color.LightGray
                    )
                }

                Text(
                    text = tituloMesAnio,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = AzulOscuro
                )

                IconButton(
                    onClick = {
                        offsetSemanas++
                        fechaSeleccionada = null
                        horaSeleccionada = null
                    }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "Semana siguiente",
                        tint = AzulOscuro
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            // Selector de día con transición al cambiar de semana
            AnimatedContent(
                targetState = offsetSemanas,
                transitionSpec = {
                    val avanzando = targetState > initialState
                    slideInHorizontally(
                        initialOffsetX = { if (avanzando) it else -it },
                        animationSpec = tween(300)
                    ) + fadeIn() togetherWith slideOutHorizontally(
                        targetOffsetX = { if (avanzando) -it else it },
                        animationSpec = tween(300)
                    ) + fadeOut()
                },
                label = "TransicionSemana"
            ) { targetOffset ->
                val semanaAnimada = remember(targetOffset) { Fechas.semanaDeCalendario(hoy, targetOffset) }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    semanaAnimada.forEach { fecha ->
                        val fechaIso = fecha.toString()
                        DiaChip(
                            fecha = fecha,
                            seleccionado = fechaIso == fechaSeleccionada,
                            onClick = {
                                fechaSeleccionada = fechaIso
                                horaSeleccionada = null
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
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
                texto = textoBoton,
                habilitado = puedeContinuar,
                onClick = {
                    val fecha = fechaSeleccionada
                    val hora = horaSeleccionada
                    if (fecha != null && hora != null) onContinuar(fecha, hora)
                },
                modifier = Modifier.efectoPresion(habilitado = puedeContinuar) {
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
    fecha: LocalDate,
    seleccionado: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val esHoy = fecha == LocalDate.now()
    val fondo by animateColorAsState(
        targetValue = if (seleccionado) AzulPrimario else Color(0xFFF1F5FB),
        animationSpec = tween(200),
        label = "FondoDia"
    )
    val textoDia = if (seleccionado) Color.White.copy(alpha = 0.9f) else GrisTexto
    val textoNumero = if (seleccionado) Color.White else AzulOscuro

    val escala by animateFloatAsState(
        targetValue = if (seleccionado) 1.03f else 1.0f,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "EscalaDia"
    )

    Column(
        modifier = modifier
            .graphicsLayer {
                scaleX = escala
                scaleY = escala
            }
            .height(78.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(fondo)
            .clickable { onClick() }
            .padding(vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        if (esHoy) {
            Text(
                text = "HOY",
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = if (seleccionado) Color.White else AzulPrimario,
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(if (seleccionado) Color.White.copy(alpha = 0.25f) else AzulClaro)
                    .padding(horizontal = 4.dp, vertical = 1.dp)
            )
        } else {
            Spacer(Modifier.height(12.dp))
        }

        Text(
            text = Fechas.nombreDiaCorto(fecha),
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = textoDia
        )
        Text(
            text = fecha.dayOfMonth.toString(),
            fontSize = 17.sp,
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
    val fondo by animateColorAsState(
        targetValue = if (seleccionada) AzulPrimario else Color(0xFFF1F5FB),
        animationSpec = tween(200),
        label = "FondoHora"
    )
    val colorTexto = if (seleccionada) Color.White else AzulOscuro
    val escala by animateFloatAsState(
        targetValue = if (seleccionada) 1.03f else 1.0f,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "EscalaHora"
    )

    Box(
        modifier = Modifier
            .height(50.dp)
            .graphicsLayer {
                scaleX = escala
                scaleY = escala
            }
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
