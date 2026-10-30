package com.tecsup.huellitasvet.ui.dueno

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.tecsup.huellitasvet.ui.components.AppTopBar
import com.tecsup.huellitasvet.ui.components.FechaChip
import com.tecsup.huellitasvet.ui.components.HoraChip
import com.tecsup.huellitasvet.ui.components.VetButton
import com.tecsup.huellitasvet.ui.theme.BordeSuave
import com.tecsup.huellitasvet.viewmodel.ReprogramarCitaViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun ReprogramarCitaScreen(
    vm: ReprogramarCitaViewModel,
    citaId: Int,
    onConfirmado: () -> Unit,
    onAtras: () -> Unit
) {
    val cs = MaterialTheme.colorScheme
    val cita by vm.cita.collectAsState()
    val fechaSeleccionada by vm.fechaSeleccionada.collectAsState()
    val horaSeleccionada by vm.horaSeleccionada.collectAsState()
    val horasDeshabilitadas by vm.horasDeshabilitadas.collectAsState()
    val puedeConfirmar by vm.puedeConfirmar.collectAsState()
    val mensaje by vm.mensaje.collectAsState()

    LaunchedEffect(citaId) {
        vm.cargar(citaId)
    }

    Scaffold(
        containerColor = cs.background,
        contentWindowInsets = WindowInsets(0),
        topBar = { AppTopBar("Reprogramar cita", onAtras = onAtras) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Tarjeta cita actual
            item {
                cita?.let { c ->
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = cs.surface),
                        border = BorderStroke(1.dp, BordeSuave),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text(
                                "Cita actual",
                                style = MaterialTheme.typography.labelMedium,
                                color = cs.onSurfaceVariant
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                "${c.mascota.mascota.nombre} · ${c.servicio.nombre}",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Spacer(Modifier.height(2.dp))
                            Text(
                                "${diaCorto(c.cita.fecha)} · ${c.cita.hora}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = cs.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Nueva fecha
            item {
                Column {
                    Text(
                        "Nueva fecha",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(Modifier.height(10.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(vm.fechas) { f ->
                            FechaChip(
                                fecha = f,
                                seleccionado = f == fechaSeleccionada,
                                onClick = { vm.elegirFecha(f) }
                            )
                        }
                    }
                }
            }

            // Nueva hora (filas de 4)
            item {
                Column {
                    Text(
                        "Nueva hora",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(Modifier.height(10.dp))
                    val filasHoras = vm.horarios.chunked(4)
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        filasHoras.forEach { fila ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                fila.forEach { h ->
                                    val ocupado = h in horasDeshabilitadas
                                    val seleccionado = h == horaSeleccionada
                                    HoraChip(
                                        hora = h,
                                        seleccionado = seleccionado,
                                        ocupado = ocupado,
                                        onClick = { vm.elegirHora(h) },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                                repeat(4 - fila.size) {
                                    Spacer(Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }
            }

            // Mensaje de error y Botón de confirmar
            item {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    mensaje?.let { msg ->
                        Text(
                            text = msg,
                            color = cs.error,
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                    }

                    VetButton(
                        texto = "Confirmar cambio",
                        habilitado = puedeConfirmar,
                        onClick = { vm.confirmar(onConfirmado) }
                    )
                }
            }
        }
    }
}

/** "Mié 21" */
private fun diaCorto(fecha: String): String =
    LocalDate.parse(fecha)
        .format(DateTimeFormatter.ofPattern("EEE d", Locale.forLanguageTag("es")))
        .replace(".", "")
        .replaceFirstChar { it.uppercase() }
