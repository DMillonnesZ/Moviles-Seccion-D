package com.tecsup.huellitasvet.ui.dueno

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.tecsup.huellitasvet.data.local.relation.CitaConDetalle
import com.tecsup.huellitasvet.model.EstadoCita
import com.tecsup.huellitasvet.ui.components.AppTopBar
import com.tecsup.huellitasvet.ui.components.CitaCard
import com.tecsup.huellitasvet.ui.theme.AvisoFondo
import com.tecsup.huellitasvet.ui.theme.AvisoIcono
import com.tecsup.huellitasvet.ui.theme.AvisoTexto
import com.tecsup.huellitasvet.ui.theme.GrisSelector
import com.tecsup.huellitasvet.viewmodel.MisCitasViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun MisCitasScreen(
    vm: MisCitasViewModel,
    onNuevaCita: () -> Unit,
    onReprogramar: (Int) -> Unit,
    onAtras: (() -> Unit)? = null
) {
    val cs = MaterialTheme.colorScheme
    val proximas by vm.proximas.collectAsState()
    val historial by vm.historial.collectAsState()
    val mensaje by vm.mensaje.collectAsState()

    var pestana by rememberSaveable { mutableIntStateOf(0) }     // 0 = Próximas · 1 = Historial
    var aCancelar by remember { mutableStateOf<CitaConDetalle?>(null) }
    val snackbar = remember { SnackbarHostState() }

    LaunchedEffect(mensaje) {
        mensaje?.let {
            snackbar.showSnackbar(it)
            vm.limpiarMensaje()
        }
    }

    Scaffold(
        containerColor = cs.background,
        contentWindowInsets = WindowInsets(0),
        topBar = { AppTopBar("Mis citas", onAtras = onAtras) },
        snackbarHost = { SnackbarHost(snackbar) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNuevaCita,
                containerColor = cs.primary,
                contentColor = cs.onPrimary,
                shape = RoundedCornerShape(18.dp)
            ) { Icon(Icons.Default.Add, "Pedir cita") }
        }
    ) { padding ->
        LazyColumn(
            Modifier.padding(padding).fillMaxSize(),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Pestanas(pestana) { pestana = it }
                Spacer(Modifier.height(6.dp))
            }

            val lista = if (pestana == 0) proximas else historial

            if (lista.isEmpty()) {
                item {
                    Text(
                        if (pestana == 0) "No tienes citas próximas" else "Aún no hay citas en tu historial",
                        color = cs.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(32.dp)
                    )
                }
            } else {
                items(lista, key = { it.cita.id }) { c ->
                    CitaCard(
                        servicio = c.servicio.nombre,
                        icono = c.servicio.icono,
                        detalle = "${c.mascota.mascota.nombre} · ${diaCorto(c.cita.fecha)} · ${c.cita.hora}",
                        estado = EstadoCita.valueOf(c.cita.estado),
                        accion = if (pestana == 0) {
                            {
                                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                    Text(
                                        "Reprogramar",
                                        color = cs.primary,
                                        fontWeight = FontWeight.SemiBold,
                                        style = MaterialTheme.typography.labelMedium,
                                        modifier = Modifier.clickable {
                                            onReprogramar(c.cita.id)
                                        }
                                    )
                                    Text(
                                        "Cancelar",
                                        color = cs.error,
                                        fontWeight = FontWeight.SemiBold,
                                        style = MaterialTheme.typography.labelMedium,
                                        modifier = Modifier.clickable {
                                            if (vm.puedeCancelar(c)) aCancelar = c
                                            else vm.cancelar(c)   // muestra "Ya no puedes cancelar esta cita"
                                        }
                                    )
                                }
                            }
                        } else null
                    )
                }
            }

            // Aviso de la regla de 2 horas
            if (pestana == 0) {
                item {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(top = 6.dp)
                            .background(AvisoFondo, RoundedCornerShape(14.dp))
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Outlined.Schedule, null, tint = AvisoIcono, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(10.dp))
                        Text("Puedes cancelar hasta 2 horas antes", color = AvisoTexto, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }

    // Confirmación
    aCancelar?.let { c ->
        AlertDialog(
            onDismissRequest = { aCancelar = null },
            title = { Text("¿Cancelar cita?") },
            text = {
                Text("${c.servicio.nombre} de ${c.mascota.mascota.nombre} · ${diaCorto(c.cita.fecha)} · ${c.cita.hora}")
            },
            confirmButton = {
                TextButton(onClick = {
                    vm.cancelar(c)
                    aCancelar = null
                }) { Text("Sí, cancelar", color = cs.error) }
            },
            dismissButton = {
                TextButton(onClick = { aCancelar = null }) { Text("No") }
            }
        )
    }
}

@Composable
private fun Pestanas(seleccion: Int, onCambio: (Int) -> Unit) {
    val cs = MaterialTheme.colorScheme
    Row(
        Modifier
            .fillMaxWidth()
            .background(GrisSelector, RoundedCornerShape(14.dp))
            .padding(4.dp)
    ) {
        listOf("Próximas", "Historial").forEachIndexed { i, texto ->
            val activo = i == seleccion
            Box(
                Modifier
                    .weight(1f)
                    .background(if (activo) cs.surface else GrisSelector, RoundedCornerShape(10.dp))
                    .clickable { onCambio(i) }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    texto,
                    color = if (activo) cs.primary else cs.onSurfaceVariant,
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

/** "Mié 21" */
private fun diaCorto(fecha: String): String =
    LocalDate.parse(fecha)
        .format(DateTimeFormatter.ofPattern("EEE d", Locale("es")))
        .replace(".", "")
        .replaceFirstChar { it.uppercase() }