package com.saludplus.citas.ui.screens.agendamiento

import android.content.Intent
import android.provider.CalendarContract
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.saludplus.citas.data.repository.Repositorio
import com.saludplus.citas.ui.components.AvatarMedico
import com.saludplus.citas.ui.components.BotonAzul
import com.saludplus.citas.ui.components.CheckAnimado
import com.saludplus.citas.ui.components.ConfeteAnimado
import com.saludplus.citas.ui.components.EnlaceTexto
import com.saludplus.citas.ui.components.FilaDetalle
import com.saludplus.citas.ui.components.efectoPresion
import com.saludplus.citas.ui.theme.AzulOscuro
import com.saludplus.citas.ui.theme.AzulPrimario
import com.saludplus.citas.ui.theme.Formas
import com.saludplus.citas.ui.theme.GrisTexto
import com.saludplus.citas.util.Fechas
import java.time.LocalDate
import java.util.Calendar

@Composable
fun CitaExitosaScreen(
    onVerMisCitas: () -> Unit,
    onIrInicio: () -> Unit
) {
    // El botón Atrás del celular lleva a Inicio
    BackHandler { onIrInicio() }

    val context = LocalContext.current

    // La cita recién creada es la última del usuario en la lista
    val correo = Repositorio.usuarioActual?.correo ?: ""
    val cita = Repositorio.citas.lastOrNull { it.correoUsuario.equals(correo, ignoreCase = true) }
    val medico = cita?.let { Repositorio.obtenerMedico(it.medicoId) }
    val especialidad = cita?.let { Repositorio.obtenerEspecialidad(it.especialidadId) }

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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .systemBarsPadding()
    ) {
        // Confeti animado por 2s
        ConfeteAnimado(
            activo = true,
            duracionMs = 2000L,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(16.dp))

            // Check animado que se dibuja
            CheckAnimado(
                activo = true,
                tamano = 88.dp
            )

            Spacer(Modifier.height(20.dp))

            Text(
                text = "¡Cita agendada!",
                fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold,
                color = AzulOscuro
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Tu cita fue registrada correctamente",
                fontSize = 15.sp,
                color = GrisTexto,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(24.dp))

            // Resumen tipo ticket
            if (cita != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(Formas.grande)
                        .background(Color(0xFFF8FAFC))
                        .border(1.5.dp, Color(0xFFE2E8F0), Formas.grande)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        if (medico != null) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AvatarMedico(nombre = medico.nombre, foto = medico.foto)
                                Spacer(Modifier.width(14.dp))
                                Column {
                                    Text(
                                        text = medico.nombre,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AzulOscuro
                                    )
                                    Text(
                                        text = especialidad?.nombre ?: "",
                                        fontSize = 13.sp,
                                        color = GrisTexto
                                    )
                                }
                            }

                            // Línea de corte (punteada)
                            Canvas(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(1.dp)
                                    .padding(horizontal = 12.dp)
                            ) {
                                drawLine(
                                    color = Color(0xFFCBD5E1),
                                    start = Offset(0f, 0f),
                                    end = Offset(size.width, 0f),
                                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f), 0f),
                                    strokeWidth = 2f
                                )
                            }
                        }

                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            FilaDetalle(Icons.Default.DateRange, "Fecha", Fechas.fechaEnTexto(cita.fecha))
                            FilaDetalle(Icons.Default.AccessTime, "Hora", cita.hora)
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

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

            BotonAzul(
                texto = "Ver mis citas",
                onClick = onVerMisCitas,
                modifier = Modifier.efectoPresion { onVerMisCitas() }
            )

            Spacer(Modifier.height(8.dp))

            EnlaceTexto(texto = "Volver al inicio", onClick = onIrInicio)

            Spacer(Modifier.height(16.dp))
        }
    }
}
