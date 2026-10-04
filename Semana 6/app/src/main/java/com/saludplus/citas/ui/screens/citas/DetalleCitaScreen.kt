package com.saludplus.citas.ui.screens.citas

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import com.saludplus.citas.ui.theme.AzulOscuro
import com.saludplus.citas.ui.theme.GrisTexto
import com.saludplus.citas.ui.theme.RojoError

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
    val contexto = LocalContext.current
    val cita = Repositorio.obtenerCita(citaId)
    val medico = cita?.let { Repositorio.obtenerMedico(it.medicoId) }
    val especialidad = cita?.let { Repositorio.obtenerEspecialidad(it.especialidadId) }

    var mostrarDialogo by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = Color.White,
        topBar = { BarraSuperior(titulo = "Detalle de cita", onAtras = onAtras) }
    ) { padding ->
        if (cita == null) {
            // La cita ya no existe (por ejemplo, ya fue cancelada)
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

                Spacer(Modifier.height(12.dp))

                // Etiqueta de estado
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFDDF7E8))
                        .padding(horizontal = 12.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = "Confirmada",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF1E9E5A)
                    )
                }

                Spacer(Modifier.height(4.dp))

                // Datos de la cita
                FilaDetalle(
                    icono = Icons.Default.DateRange,
                    titulo = "Fecha",
                    valor = cita.fecha,
                    modifier = Modifier.padding(vertical = 14.dp)
                )
                Separador()
                FilaDetalle(
                    icono = Icons.Default.AccessTime,
                    titulo = "Hora",
                    valor = cita.hora,
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

                Spacer(Modifier.height(24.dp))

                OutlinedButton(
                    onClick = { mostrarDialogo = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
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
                    Toast.makeText(contexto, "Cita cancelada", Toast.LENGTH_SHORT).show()
                    onCancelada()
                }) {
                    Text("Sí, cancelar", color = RojoError, fontWeight = FontWeight.SemiBold)
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