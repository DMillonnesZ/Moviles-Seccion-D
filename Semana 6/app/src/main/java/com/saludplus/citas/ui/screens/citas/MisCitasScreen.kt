package com.saludplus.citas.ui.screens.citas

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.saludplus.citas.data.model.Cita
import com.saludplus.citas.data.repository.Repositorio
import com.saludplus.citas.navigation.Rutas
import com.saludplus.citas.ui.components.AvatarMedico
import com.saludplus.citas.ui.components.BarraNavegacion
import com.saludplus.citas.ui.components.BarraSuperior
import com.saludplus.citas.ui.components.EstadoVacio
import com.saludplus.citas.ui.components.MensajeController
import com.saludplus.citas.ui.components.TipoMensaje
import com.saludplus.citas.ui.components.efectoPresion
import com.saludplus.citas.ui.components.estiloEspecialidad
import com.saludplus.citas.ui.theme.AzulClaro
import com.saludplus.citas.ui.theme.AzulOscuro
import com.saludplus.citas.ui.theme.AzulPrimario
import com.saludplus.citas.ui.theme.Formas
import com.saludplus.citas.ui.theme.GrisTexto
import com.saludplus.citas.ui.theme.RojoError
import com.saludplus.citas.util.Fechas
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MisCitasScreen(
    onNavegar: (String) -> Unit,
    onAgendar: () -> Unit,
    onCita: (Int) -> Unit
) {
    var pestañaSeleccionada by remember { mutableIntStateOf(0) } // 0: Próximas, 1: Pasadas
    var citaACancelar by remember { mutableStateOf<Cita?>(null) }

    val hoy = remember { LocalDate.now() }
    val hoyIso = remember(hoy) { hoy.toString() }

    val todasCitas = Repositorio.citasDelUsuario()
    val citasProximas = remember(todasCitas, hoyIso) { todasCitas.filter { it.fecha >= hoyIso } }
    val citasPasadas = remember(todasCitas, hoyIso) { todasCitas.filter { it.fecha < hoyIso } }

    val listaActual = if (pestañaSeleccionada == 0) citasProximas else citasPasadas

    Scaffold(
        containerColor = Color.White,
        topBar = { BarraSuperior(titulo = "Mis citas") },
        bottomBar = { BarraNavegacion(rutaActual = Rutas.MIS_CITAS, onNavegar = onNavegar) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Pestañas Próximas y Pasadas
            TabRow(
                selectedTabIndex = pestañaSeleccionada,
                containerColor = Color.White,
                contentColor = AzulPrimario,
                indicator = { tabPositions ->
                    if (pestañaSeleccionada < tabPositions.size) {
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[pestañaSeleccionada]),
                            color = AzulPrimario
                        )
                    }
                }
            ) {
                Tab(
                    selected = pestañaSeleccionada == 0,
                    onClick = { pestañaSeleccionada = 0 },
                    text = {
                        Text(
                            text = "Próximas (${citasProximas.size})",
                            fontSize = 14.sp,
                            fontWeight = if (pestañaSeleccionada == 0) FontWeight.Bold else FontWeight.Medium,
                            color = if (pestañaSeleccionada == 0) AzulPrimario else GrisTexto
                        )
                    }
                )
                Tab(
                    selected = pestañaSeleccionada == 1,
                    onClick = { pestañaSeleccionada = 1 },
                    text = {
                        Text(
                            text = "Pasadas (${citasPasadas.size})",
                            fontSize = 14.sp,
                            fontWeight = if (pestañaSeleccionada == 1) FontWeight.Bold else FontWeight.Medium,
                            color = if (pestañaSeleccionada == 1) AzulPrimario else GrisTexto
                        )
                    }
                )
            }

            if (listaActual.isEmpty()) {
                if (pestañaSeleccionada == 0) {
                    EstadoVacio(
                        titulo = "Sin citas próximas",
                        mensaje = "No tienes citas médicas programadas. Agenda tu próxima atención en pocos pasos.",
                        icono = Icons.Default.CalendarMonth,
                        accionTexto = "Agendar cita",
                        onAccion = onAgendar,
                        modifier = Modifier.padding(top = 40.dp)
                    )
                } else {
                    EstadoVacio(
                        titulo = "Sin citas pasadas",
                        mensaje = "Aún no has completado ni registrado atenciones anteriores.",
                        icono = Icons.Default.History,
                        modifier = Modifier.padding(top = 40.dp)
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(listaActual, key = { it.id }) { cita ->
                        val dismissState = rememberSwipeToDismissBoxState(
                            confirmValueChange = { dismissValue ->
                                if (dismissValue == SwipeToDismissBoxValue.EndToStart && pestañaSeleccionada == 0) {
                                    citaACancelar = cita
                                    false
                                } else false
                            }
                        )

                        SwipeToDismissBox(
                            state = dismissState,
                            enableDismissFromStartToEnd = false,
                            enableDismissFromEndToStart = pestañaSeleccionada == 0,
                            backgroundContent = {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(Formas.grande)
                                        .background(RojoError.copy(alpha = 0.85f))
                                        .padding(horizontal = 20.dp),
                                    contentAlignment = Alignment.CenterEnd
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "Cancelar",
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp
                                        )
                                        Spacer(Modifier.width(6.dp))
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Cancelar cita",
                                            tint = Color.White
                                        )
                                    }
                                }
                            }
                        ) {
                            TarjetaCita(
                                cita = cita,
                                hoy = hoy,
                                onClick = { onCita(cita.id) }
                            )
                        }
                    }
                }
            }
        }
    }

    // Diálogo de confirmación al deslizar para cancelar
    citaACancelar?.let { cita ->
        AlertDialog(
            onDismissRequest = { citaACancelar = null },
            containerColor = Color.White,
            title = {
                Text("¿Cancelar la cita?", fontWeight = FontWeight.Bold, color = AzulOscuro)
            },
            text = {
                Text("Esta acción eliminará la reserva y liberará el horario.", color = GrisTexto)
            },
            confirmButton = {
                TextButton(onClick = {
                    Repositorio.cancelarCita(cita.id)
                    MensajeController.mostrar("Cita cancelada", TipoMensaje.INFORMACION)
                    citaACancelar = null
                }) {
                    Text("Sí, cancelar", color = RojoError, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { citaACancelar = null }) {
                    Text("No, mantener", color = GrisTexto)
                }
            }
        )
    }
}

@Composable
private fun TarjetaCita(
    cita: Cita,
    hoy: LocalDate,
    onClick: () -> Unit
) {
    val medico = Repositorio.obtenerMedico(cita.medicoId)
    val especialidad = Repositorio.obtenerEspecialidad(cita.especialidadId)
    val estilo = estiloEspecialidad(cita.especialidadId)
    val parsedFecha = try { LocalDate.parse(cita.fecha) } catch (_: Exception) { hoy }
    val etiquetaRelativa = Fechas.etiquetaRelativa(parsedFecha, hoy)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(Formas.grande)
            .background(Color.White)
            .border(1.dp, Color(0xFFE2E8F0), Formas.grande)
            .efectoPresion { onClick() }
            .clickable { onClick() }
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            // Franja de color semántica por especialidad
            Box(
                modifier = Modifier
                    .width(6.dp)
                    .fillMaxHeight()
                    .background(estilo.color)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = especialidad?.nombre ?: "Especialidad",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = estilo.color
                    )

                    Box(
                        modifier = Modifier
                            .clip(Formas.pildora)
                            .background(if (parsedFecha >= hoy) AzulClaro else Color(0xFFF1F5FB))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = etiquetaRelativa,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (parsedFecha >= hoy) AzulPrimario else GrisTexto
                        )
                    }
                }

                Spacer(Modifier.height(10.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (medico != null) {
                        AvatarMedico(nombre = medico.nombre, foto = medico.foto, tamano = 46.dp)
                        Spacer(Modifier.width(12.dp))
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = medico?.nombre ?: "Médico",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = AzulOscuro
                        )
                        Spacer(Modifier.height(2.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = null,
                                tint = GrisTexto,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = Fechas.fechaEnTexto(cita.fecha),
                                fontSize = 12.sp,
                                color = GrisTexto
                            )
                            Spacer(Modifier.width(10.dp))
                            Icon(
                                imageVector = Icons.Default.AccessTime,
                                contentDescription = null,
                                tint = GrisTexto,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = cita.hora,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = AzulOscuro
                            )
                        }
                    }
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Ver detalle",
                        tint = GrisTexto
                    )
                }
            }
        }
    }
}
