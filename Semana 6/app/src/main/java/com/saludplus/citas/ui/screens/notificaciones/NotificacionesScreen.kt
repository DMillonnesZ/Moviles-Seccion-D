package com.saludplus.citas.ui.screens.notificaciones

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
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.saludplus.citas.data.repository.Repositorio
import com.saludplus.citas.ui.components.BarraSuperior
import com.saludplus.citas.ui.components.EstadoVacio
import com.saludplus.citas.ui.components.TarjetaSuave
import com.saludplus.citas.ui.components.efectoPresion
import com.saludplus.citas.ui.theme.AzulClaro
import com.saludplus.citas.ui.theme.AzulOscuro
import com.saludplus.citas.ui.theme.AzulPrimario
import com.saludplus.citas.ui.theme.ColoresSemanticos
import com.saludplus.citas.ui.theme.GrisTexto
import com.saludplus.citas.util.Fechas
import java.time.LocalDate

private data class NotificacionItemData(
    val citaId: Int,
    val titulo: String,
    val mensaje: String,
    val fechaIso: String,
    val leida: Boolean
)

@Composable
fun NotificacionesScreen(
    onAtras: () -> Unit,
    onCita: (Int) -> Unit = {}
) {
    val hoy = remember { LocalDate.now() }
    val citasUsuario = Repositorio.citasDelUsuario()

    val notificaciones = remember(citasUsuario, Repositorio.notificacionesLeidas.size) {
        citasUsuario.map { cita ->
            val medico = Repositorio.obtenerMedico(cita.medicoId)
            val especialidad = Repositorio.obtenerEspecialidad(cita.especialidadId)
            val fechaTexto = Fechas.fechaEnTexto(cita.fecha)

            NotificacionItemData(
                citaId = cita.id,
                titulo = "Cita confirmada",
                mensaje = "Tu cita con ${medico?.nombre ?: "el especialista"} (${especialidad?.nombre ?: ""}) está agendada para el $fechaTexto a las ${cita.hora}.",
                fechaIso = cita.fecha,
                leida = Repositorio.notificacionesLeidas.contains(cita.id)
            )
        }
    }

    val (notificacionesHoy, notificacionesAnteriores) = remember(notificaciones, hoy) {
        notificaciones.partition { notif ->
            try {
                val fecha = LocalDate.parse(notif.fechaIso)
                fecha.isEqual(hoy)
            } catch (e: Exception) {
                false
            }
        }
    }

    Scaffold(
        containerColor = Color.White,
        topBar = { BarraSuperior(titulo = "Notificaciones", onAtras = onAtras) }
    ) { padding ->
        if (notificaciones.isEmpty()) {
            EstadoVacio(
                titulo = "Sin notificaciones",
                mensaje = "Cuando agendes una cita médica, recibirás las alertas de confirmación aquí.",
                icono = Icons.Default.Notifications,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (notificacionesHoy.isNotEmpty()) {
                    item {
                        Text(
                            text = "Hoy",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = AzulOscuro,
                            modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                        )
                    }

                    items(notificacionesHoy, key = { it.citaId }) { item ->
                        TarjetaNotificacion(
                            item = item,
                            onClick = {
                                Repositorio.marcarNotificacionComoLeida(item.citaId)
                                onCita(item.citaId)
                            }
                        )
                    }
                }

                if (notificacionesAnteriores.isNotEmpty()) {
                    item {
                        Text(
                            text = if (notificacionesHoy.isNotEmpty()) "Anteriores" else "Todas las notificaciones",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = AzulOscuro,
                            modifier = Modifier.padding(start = 4.dp, top = if (notificacionesHoy.isNotEmpty()) 12.dp else 4.dp)
                        )
                    }

                    items(notificacionesAnteriores, key = { it.citaId }) { item ->
                        TarjetaNotificacion(
                            item = item,
                            onClick = {
                                Repositorio.marcarNotificacionComoLeida(item.citaId)
                                onCita(item.citaId)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TarjetaNotificacion(
    item: NotificacionItemData,
    onClick: () -> Unit
) {
    TarjetaSuave(
        modifier = Modifier
            .fillMaxWidth()
            .efectoPresion { onClick() },
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(if (item.leida) AzulClaro else ColoresSemanticos.ExitoFondo),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.EventAvailable,
                    contentDescription = null,
                    tint = if (item.leida) AzulPrimario else ColoresSemanticos.Exito,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = item.titulo,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = AzulOscuro,
                        modifier = Modifier.weight(1f)
                    )

                    if (!item.leida) {
                        Spacer(Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(AzulPrimario)
                        )
                    }
                }

                Spacer(Modifier.height(4.dp))

                Text(
                    text = item.mensaje,
                    style = MaterialTheme.typography.bodyMedium,
                    color = GrisTexto
                )

                Spacer(Modifier.height(6.dp))

                Text(
                    text = Fechas.fechaEnTexto(item.fechaIso),
                    style = MaterialTheme.typography.labelSmall,
                    color = AzulPrimario,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(Modifier.width(8.dp))

            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Ver detalle",
                tint = GrisTexto,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
        }
    }
}
