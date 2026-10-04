package com.saludplus.citas.ui.screens.agendamiento

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.saludplus.citas.data.repository.Repositorio
import com.saludplus.citas.ui.components.AvatarMedico
import com.saludplus.citas.ui.components.BarraSuperior
import com.saludplus.citas.ui.components.BotonAzul
import com.saludplus.citas.ui.components.FilaDetalle
import com.saludplus.citas.ui.theme.AzulOscuro
import com.saludplus.citas.ui.theme.AzulPrimario
import com.saludplus.citas.ui.theme.GrisTexto
import com.saludplus.citas.ui.theme.RojoError

// La cita dura 30 minutos: la hora de fin es la hora elegida + 30 min
private fun horaFin(hora: String): String {
    val partes = hora.split(":")
    val minutos = partes[0].toInt() * 60 + partes[1].toInt() + 30
    return "%02d:%02d".format(minutos / 60, minutos % 60)
}

// Código CMP de relleno, calculado a partir del id del médico
private fun codigoCmp(medicoId: Int): Int = 11111 + medicoId * 1234

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
fun ConfirmarCitaScreen(
    medicoId: Int,
    fecha: String,
    hora: String,
    onAtras: () -> Unit,
    onConfirmada: () -> Unit
) {
    val medico = Repositorio.obtenerMedico(medicoId)
    val especialidad = medico?.let { Repositorio.obtenerEspecialidad(it.especialidadId) }

    var motivo by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    Scaffold(
        containerColor = Color.White,
        topBar = { BarraSuperior(titulo = "Confirmar cita", onAtras = onAtras) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .imePadding()
                .padding(horizontal = 20.dp)
        ) {
            // Zona con scroll: así el teclado no tapa el cuadro de motivo
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
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
                            Text(
                                text = "CMP: ${codigoCmp(medico.id)}",
                                fontSize = 13.sp,
                                color = GrisTexto
                            )
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))

                // Datos de la cita: fecha y hora llegan por parámetro
                FilaDetalle(
                    icono = Icons.Default.DateRange,
                    titulo = "Fecha",
                    valor = fecha,
                    modifier = Modifier.padding(vertical = 14.dp)
                )
                Separador()
                FilaDetalle(
                    icono = Icons.Default.AccessTime,
                    titulo = "Hora",
                    valor = "$hora a ${horaFin(hora)}",
                    modifier = Modifier.padding(vertical = 14.dp)
                )
                Separador()
                FilaDetalle(
                    icono = Icons.Default.MedicalServices,
                    titulo = "Tipo de atención",
                    valor = "Consulta presencial",
                    modifier = Modifier.padding(vertical = 14.dp)
                )
                Separador()
                FilaDetalle(
                    icono = Icons.Default.LocationOn,
                    titulo = "Dirección",
                    valor = "Av. Los Olivos 123, Lima",
                    modifier = Modifier.padding(vertical = 14.dp)
                )

                Spacer(Modifier.height(16.dp))

                // Motivo de consulta (opcional)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Motivo de consulta",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = AzulOscuro
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(text = "(opcional)", fontSize = 13.sp, color = GrisTexto)
                }
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = motivo,
                    onValueChange = { motivo = it },
                    placeholder = { Text("Ej. Consulta de rutina", color = Color(0xFF9CA3AF)) },
                    minLines = 3,
                    maxLines = 4,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = AzulPrimario,
                        unfocusedBorderColor = Color(0xFFD5DEEC),
                        cursorColor = AzulPrimario
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                if (error != null) {
                    Spacer(Modifier.height(12.dp))
                    Text(text = error ?: "", fontSize = 14.sp, color = RojoError)
                }

                Spacer(Modifier.height(16.dp))
            }

            BotonAzul(
                texto = "Agendar cita",
                onClick = {
                    if (medico != null) {
                        val guardada = Repositorio.agendarCita(
                            medicoId = medico.id,
                            especialidadId = medico.especialidadId,
                            fecha = fecha,
                            hora = hora
                        )
                        if (guardada) {
                            onConfirmada()
                        } else {
                            error = "Ese horario ya no está disponible. Regresa y elige otro."
                        }
                    }
                }
            )

            Spacer(Modifier.height(16.dp))
        }
    }
}