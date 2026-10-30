package com.tecsup.huellitasvet.ui.veterinario

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.tecsup.huellitasvet.model.Especie
import com.tecsup.huellitasvet.ui.components.AppTopBar
import com.tecsup.huellitasvet.ui.components.MascotaAvatar
import com.tecsup.huellitasvet.ui.theme.BordeSuave
import com.tecsup.huellitasvet.viewmodel.Paciente
import com.tecsup.huellitasvet.viewmodel.PacientesViewModel
import java.time.LocalDate
import java.time.Year
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun PacientesScreen(
    vm: PacientesViewModel,
    onVerPaciente: (Int) -> Unit,
    onAtras: (() -> Unit)? = null
) {
    val cs = MaterialTheme.colorScheme
    val texto by vm.texto.collectAsState()
    val especie by vm.especie.collectAsState()
    val pacientes by vm.pacientes.collectAsState()

    Scaffold(
        containerColor = cs.background,
        contentWindowInsets = WindowInsets(0),
        topBar = { AppTopBar("Pacientes", onAtras = onAtras) }
    ) { padding ->
        LazyColumn(
            Modifier.padding(padding).fillMaxSize(),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 4.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Buscador
            item {
                OutlinedTextField(
                    value = texto,
                    onValueChange = vm::buscar,
                    placeholder = { Text("Buscar mascota o dueño...", color = cs.onSurfaceVariant) },
                    leadingIcon = { Icon(Icons.Default.Search, null, tint = cs.onSurfaceVariant) },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = BordeSuave,
                        focusedBorderColor = cs.primary,
                        unfocusedContainerColor = cs.surface,
                        focusedContainerColor = cs.surface
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Chips de especie
            item {
                val opciones = listOf<Pair<String, Especie?>>(
                    "Todos" to null, "Perros" to Especie.PERRO, "Gatos" to Especie.GATO, "Otros" to Especie.OTRO
                )
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(opciones) { (etiqueta, e) ->
                        val activo = especie == e
                        Text(
                            etiqueta,
                            color = if (activo) cs.onPrimary else cs.onSurface,
                            fontWeight = FontWeight.SemiBold,
                            style = MaterialTheme.typography.labelLarge,
                            modifier = Modifier
                                .background(if (activo) cs.primary else cs.surface, RoundedCornerShape(50))
                                .border(1.dp, if (activo) cs.primary else BordeSuave, RoundedCornerShape(50))
                                .clickable { vm.filtrar(e) }
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                }
            }

            if (pacientes.isEmpty()) {
                item {
                    Text(
                        "No se encontraron pacientes",
                        color = cs.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(32.dp)
                    )
                }
            } else {
                items(pacientes, key = { it.datos.mascota.id }) { p ->
                    TarjetaPaciente(p) { onVerPaciente(p.datos.mascota.id) }
                }
            }
        }
    }
}

@Composable
private fun TarjetaPaciente(p: Paciente, onClick: () -> Unit) {
    val cs = MaterialTheme.colorScheme
    val m = p.datos.mascota
    val edad = Year.now().value - m.anioNacimiento
    val dueno = p.datos.dueno.nombre.split(" ").let { x -> if (x.size > 1) "${x[0]} ${x[1].first()}." else x[0] }
    val visita = p.ultimaVisita?.let {
        "Última visita: " + LocalDate.parse(it).format(DateTimeFormatter.ofPattern("d MMM", Locale("es"))).replace(".", "")
    } ?: "Sin visitas"

    Card(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cs.surface),
        border = BorderStroke(1.dp, BordeSuave),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.size(56.dp).background(cs.primaryContainer, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                MascotaAvatar(Especie.valueOf(m.especie), tamano = 50.dp, conFondo = false)
            }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(m.nombre, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
                Text("${m.raza} · ${if (edad == 1) "1 año" else "$edad años"}", style = MaterialTheme.typography.bodySmall, color = cs.onSurfaceVariant)
                Text("Dueño: $dueno", style = MaterialTheme.typography.bodySmall, color = cs.onSurfaceVariant)
                Text(visita, style = MaterialTheme.typography.labelSmall, color = cs.onSurfaceVariant.copy(alpha = 0.7f))
            }
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = cs.onSurfaceVariant)
        }
    }
}