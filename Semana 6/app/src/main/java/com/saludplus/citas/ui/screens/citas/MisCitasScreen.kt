package com.saludplus.citas.ui.screens.citas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.saludplus.citas.data.repository.Repositorio
import com.saludplus.citas.navigation.Rutas
import com.saludplus.citas.ui.components.AvatarMedico
import com.saludplus.citas.ui.components.BarraNavegacion
import com.saludplus.citas.ui.components.BarraSuperior
import com.saludplus.citas.ui.components.BotonAzul
import com.saludplus.citas.ui.components.TarjetaSuave
import com.saludplus.citas.ui.theme.AzulClaro
import com.saludplus.citas.ui.theme.AzulOscuro
import com.saludplus.citas.ui.theme.AzulPrimario
import com.saludplus.citas.ui.theme.GrisTexto

@Composable
fun MisCitasScreen(
    onNavegar: (String) -> Unit,
    onAgendar: () -> Unit,
    onCita: (Int) -> Unit
) {
    // Citas del usuario en sesión, de la más próxima a la más lejana
    val citas = Repositorio.citasDelUsuario()

    Scaffold(
        containerColor = Color.White,
        topBar = { BarraSuperior(titulo = "Mis citas") },
        bottomBar = { BarraNavegacion(rutaActual = Rutas.MIS_CITAS, onNavegar = onNavegar) }
    ) { padding ->
        if (citas.isEmpty()) {
            // Mensaje de lista vacía
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .clip(CircleShape)
                        .background(AzulClaro),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = null,
                        tint = AzulPrimario,
                        modifier = Modifier.size(56.dp)
                    )
                }
                Spacer(Modifier.height(20.dp))
                Text(
                    text = "Aún no tienes citas",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = AzulOscuro
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "Agenda tu primera cita médica en pocos pasos",
                    fontSize = 14.sp,
                    color = GrisTexto,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(24.dp))
                BotonAzul(texto = "Agendar cita", onClick = onAgendar)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    Text(
                        text = if (citas.size == 1) "1 cita programada"
                        else "${citas.size} citas programadas",
                        fontSize = 14.sp,
                        color = GrisTexto
                    )
                }
                items(citas, key = { it.id }) { cita ->
                    val medico = Repositorio.obtenerMedico(cita.medicoId)
                    val especialidad = Repositorio.obtenerEspecialidad(cita.especialidadId)

                    TarjetaSuave(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { onCita(cita.id) }
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (medico != null) {
                                AvatarMedico(nombre = medico.nombre, foto = medico.foto)
                                Spacer(Modifier.width(14.dp))
                            }
                            Column {
                                Text(
                                    text = medico?.nombre ?: "Médico",
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

                        Spacer(Modifier.height(12.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(Color(0xFFE6EBF3))
                        )
                        Spacer(Modifier.height(12.dp))

                        Row(modifier = Modifier.fillMaxWidth()) {
                            DatoCita(
                                icono = Icons.Default.DateRange,
                                texto = cita.fecha,
                                modifier = Modifier.weight(1f)
                            )
                            DatoCita(
                                icono = Icons.Default.AccessTime,
                                texto = cita.hora,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }
    }
}

// Ícono pequeño con un dato de la cita (fecha u hora)
@Composable
private fun DatoCita(
    icono: ImageVector,
    texto: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icono,
            contentDescription = null,
            tint = AzulPrimario,
            modifier = Modifier.size(18.dp)
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = texto,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = AzulOscuro
        )
    }
}