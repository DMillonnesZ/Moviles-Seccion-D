package com.saludplus.citas.ui.screens.citas

import android.content.Intent
import android.provider.CalendarContract
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.saludplus.citas.data.repository.Repositorio
import com.saludplus.citas.ui.components.AvatarMedico
import com.saludplus.citas.ui.components.BarraSuperior
import com.saludplus.citas.ui.components.BotonAzul
import com.saludplus.citas.ui.components.FilaDetalle
import com.saludplus.citas.ui.components.MensajeController
import com.saludplus.citas.ui.components.TipoMensaje
import com.saludplus.citas.ui.components.efectoPresion
import com.saludplus.citas.ui.theme.AzulOscuro
import com.saludplus.citas.ui.theme.AzulPrimario
import com.saludplus.citas.ui.theme.Degradados
import com.saludplus.citas.ui.theme.Formas
import com.saludplus.citas.ui.theme.GrisTexto
import com.saludplus.citas.ui.theme.RojoError
import com.saludplus.citas.util.Fechas
import java.time.LocalDate
import java.util.Calendar

@Composable
private fun Separador() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(Color(0xFFE6EBF3))
    )
}

@Composable
fun DetalleCitaScreen(
    citaId: Int,
    onAtras: () -> Unit,
    onCancelada: () -> Unit
) {
    val context = LocalContext.current
    val cita = Repositorio.obtenerCita(citaId)
    val medico = cita?.let { Repositorio.obtenerMedico(it.medicoId) }
    val especialidad = cita?.let { Repositorio.obtenerEspecialidad(it.especialidadId) }

    LaunchedEffect(citaId) {
        Repositorio.marcarNotificacionComoLeida(citaId)
    }

    var mostrarDialogo by remember { mutableStateOf(false) }

    fun agregarAlCalendario() {
        if (cita == null) return
        val parsedFecha = try { LocalDate.parse(cita.fecha) } catch (_: Exception) { null }
        val horaPartes = cita.hora.split(":")
        val horaNum = horaPartes.getOrNull(0)?.toIntOrNull() ?: 9
        val minNum = horaPartes.getOrNull(1)?.toIntOrNull() ?: 0

        val inicioCal = Calendar.getInstance().apply {
            if (parsedFecha != null) {
                set(Calendar.YEAR, parsedFecha.year)
                set(Calendar.MONTH, parsedFecha.monthValue - 1)
                set(Calendar.DAY_OF_MONTH, parsedFecha.dayOfMonth)
            }
            set(Calendar.HOUR_OF_DAY, horaNum)
            set(Calendar.MINUTE, minNum)
        }

        val finCal = (inicioCal.clone() as Calendar).apply {
            add(Calendar.MINUTE, 30)
        }

        val intent = Intent(Intent.ACTION_INSERT).apply {
            data = CalendarContract.Events.CONTENT_URI
            putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, inicioCal.timeInMillis)
            putExtra(CalendarContract.EXTRA_EVENT_END_TIME, finCal.timeInMillis)
            putExtra(CalendarContract.Events.TITLE, "Cita médica - ${medico?.nombre ?: "SaludPlus"}")
            putExtra(CalendarContract.Events.DESCRIPTION, "Consulta presencial en Clínica SaludPlus.")
            putExtra(CalendarContract.Events.EVENT_LOCATION, "Av. Los Olivos 123, Lima")
        }
        try {
            context.startActivity(intent)
        } catch (_: Exception) {}
    }

    Scaffold(
        containerColor = Color.White,
        topBar = { BarraSuperior(titulo = "Detalle de cita", onAtras = onAtras) }
    ) { padding ->
        if (cita == null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Esta cita ya no existe",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = AzulOscuro,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(20.dp))
                BotonAzul(texto = "Volver", onClick = onAtras)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
            ) {
                // Cabecera con degradado y tarjeta del médico
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Degradados.Principal)
                        .padding(horizontal = 20.dp, vertical = 20.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(Formas.grande)
                            .background(Color.White)
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (medico != null) {
                            AvatarMedico(nombre = medico.nombre, foto = medico.foto, tamano = 56.dp)
                            Spacer(Modifier.width(14.dp))
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = medico?.nombre ?: "Médico",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = AzulOscuro
                            )
                            Text(
                                text = especialidad?.nombre ?: "",
                                fontSize = 14.sp,
                                color = GrisTexto
                            )
                            Spacer(Modifier.height(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(Formas.pildora)
                                    .background(Color(0xFFDDF7E8))
                                    .padding(horizontal = 10.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = "Confirmada",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1E9E5A)
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Datos de la cita en tarjeta estructurada
                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(Formas.grande)
                            .background(Color(0xFFF8FAFC))
                            .border(1.dp, Color(0xFFE2E8F0), Formas.grande)
                            .padding(16.dp)
                    ) {
                        Column {
                            FilaDetalle(
                                icono = Icons.Default.DateRange,
                                titulo = "Fecha",
                                valor = Fechas.fechaEnTexto(cita.fecha)
                            )
                            Spacer(Modifier.height(12.dp))
                            Separador()
                            Spacer(Modifier.height(12.dp))
                            FilaDetalle(
                                icono = Icons.Default.AccessTime,
                                titulo = "Hora",
                                valor = cita.hora
                            )
                            Spacer(Modifier.height(12.dp))
                            Separador()
                            Spacer(Modifier.height(12.dp))
                            FilaDetalle(
                                icono = Icons.Default.MedicalServices,
                                titulo = "Tipo de atención",
                                valor = "Consulta presencial"
                            )
                            Spacer(Modifier.height(12.dp))
                            Separador()
                            Spacer(Modifier.height(12.dp))
                            FilaDetalle(
                                icono = Icons.Default.LocationOn,
                                titulo = "Dirección",
                                valor = "Av. Los Olivos 123, Lima"
                            )
                        }
                    }

                    Spacer(Modifier.height(20.dp))

                    // Botón Agregar al calendario
                    OutlinedButton(
                        onClick = { agregarAlCalendario() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .efectoPresion { agregarAlCalendario() },
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.5.dp, AzulPrimario),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = AzulPrimario)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("Agregar al calendario", fontWeight = FontWeight.SemiBold)
                    }

                    Spacer(Modifier.height(12.dp))

                    OutlinedButton(
                        onClick = { mostrarDialogo = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .efectoPresion { mostrarDialogo = true },
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.5.dp, RojoError),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = RojoError)
                    ) {
                        Text("Cancelar cita", fontWeight = FontWeight.SemiBold)
                    }

                    Spacer(Modifier.height(16.dp))
                }
            }
        }
    }

    // Diálogo de confirmación antes de cancelar
    if (mostrarDialogo) {
        AlertDialog(
            onDismissRequest = { mostrarDialogo = false },
            containerColor = Color.White,
            title = {
                Text("¿Cancelar la cita?", fontWeight = FontWeight.Bold, color = AzulOscuro)
            },
            text = {
                Text(
                    text = "Esta acción no se puede deshacer. El horario quedará libre para otros pacientes.",
                    color = GrisTexto
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    mostrarDialogo = false
                    Repositorio.cancelarCita(citaId)
                    MensajeController.mostrar("Cita cancelada", TipoMensaje.INFORMACION)
                    onCancelada()
                }) {
                    Text("Sí, cancelar", color = RojoError, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogo = false }) {
                    Text("No, mantener", color = GrisTexto)
                }
            }
        )
    }
}
