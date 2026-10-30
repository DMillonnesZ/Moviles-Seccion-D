package com.tecsup.huellitasvet.ui.dueno

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tecsup.huellitasvet.data.local.entity.Mascota
import com.tecsup.huellitasvet.model.Especie
import com.tecsup.huellitasvet.model.Sexo
import com.tecsup.huellitasvet.ui.components.AppTopBar
import com.tecsup.huellitasvet.ui.components.HistorialItem
import com.tecsup.huellitasvet.ui.components.MascotaAvatar
import com.tecsup.huellitasvet.ui.components.graficos.GraficoLineas
import com.tecsup.huellitasvet.ui.theme.BordeSuave
import com.tecsup.huellitasvet.ui.theme.EstadoConfirmada
import com.tecsup.huellitasvet.ui.theme.GrisSelector
import com.tecsup.huellitasvet.viewmodel.MascotaViewModel
import java.time.LocalDate
import java.time.Year
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun FichaMascotaScreen(
    vm: MascotaViewModel,
    mascotaId: Int,
    onPedirCita: ((Int) -> Unit)? = null,    // null = sin botón (vista del veterinario)
    onEditar: ((Int) -> Unit)? = null,
    onAtras: () -> Unit
) {
    val cs = MaterialTheme.colorScheme
    val mascota by vm.mascota(mascotaId).collectAsState()
    val historial by vm.historial(mascotaId).collectAsState()
    val pesos by vm.pesos(mascotaId).collectAsState()
    var pestana by rememberSaveable { mutableIntStateOf(1) }   // 0 = Datos · 1 = Historial

    Scaffold(
        containerColor = cs.background,
        contentWindowInsets = WindowInsets(0),
        topBar = { AppTopBar(mascota?.nombre ?: "", onAtras = onAtras) }
    ) { padding ->
        val m = mascota ?: return@Scaffold

        LazyColumn(
            Modifier.padding(padding).fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp)
        ) {
            // ===== Cabecera =====
            item { Cabecera(m, vacunasAlDia = vm.vacunasAlDia(historial)) }

            // ===== Botones (solo dueño) =====
            if (onPedirCita != null && onEditar != null) {
                item {
                    Row(Modifier.padding(vertical = 16.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Button(
                            onClick = { onPedirCita(m.id) },
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.weight(1f).height(48.dp)
                        ) {
                            Icon(Icons.Outlined.CalendarMonth, null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Pedir cita", fontWeight = FontWeight.SemiBold)
                        }
                        OutlinedButton(
                            onClick = { onEditar(m.id) },
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(2.dp, cs.primary),
                            modifier = Modifier.weight(1f).height(48.dp)
                        ) {
                            Icon(Icons.Outlined.Edit, null, tint = cs.primary, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Editar", color = cs.primary, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            } else {
                item { Spacer(Modifier.height(16.dp)) }
            }

            // ===== Pestañas =====
            item {
                Pestanas(pestana) { pestana = it }
                Spacer(Modifier.height(16.dp))
            }

            if (pestana == 0) {
                item { Datos(m) }
            } else {
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = cs.surface),
                        border = BorderStroke(1.dp, BordeSuave),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text(
                                "Evolución del peso",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.height(12.dp))
                            GraficoLineas(puntos = pesos)
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                }

                if (historial.isEmpty()) {
                    item {
                        Text(
                            "Aún no tiene atenciones",
                            color = cs.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth().padding(24.dp)
                        )
                    }
                } else {
                    itemsIndexed(historial, key = { _, c -> c.cita.id }) { i, c ->
                        HistorialItem(
                            fecha = fechaDiaMes(c.cita.fecha),
                            servicio = c.servicio.nombre,
                            diagnostico = c.cita.diagnostico ?: "",
                            peso = c.cita.pesoRegistrado,
                            esUltimo = i == historial.lastIndex
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun Cabecera(m: Mascota, vacunasAlDia: Boolean) {
    val cs = MaterialTheme.colorScheme
    val especie = Especie.valueOf(m.especie)
    val sexo = Sexo.valueOf(m.sexo)
    val edad = Year.now().value - m.anioNacimiento

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = cs.surface),
        border = BorderStroke(1.dp, BordeSuave),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.size(84.dp).background(cs.primaryContainer, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                MascotaAvatar(especie, tamano = 72.dp, conFondo = false)
            }
            Spacer(Modifier.width(16.dp))
            Column {
                Text(m.nombre, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Text(
                    "${especieTexto(especie, sexo)} ${m.raza.lowercase()} · ${if (edad == 1) "1 año" else "$edad años"}",
                    style = MaterialTheme.typography.bodySmall, color = cs.onSurfaceVariant
                )
                Text("${sexo.etiqueta} · ${m.peso} kg", style = MaterialTheme.typography.bodySmall, color = cs.onSurfaceVariant)
                if (vacunasAlDia) {
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "Vacunas al día",
                        color = EstadoConfirmada,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier
                            .background(cs.primaryContainer, RoundedCornerShape(50))
                            .padding(horizontal = 10.dp, vertical = 3.dp)
                    )
                }
            }
        }
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
        listOf("Datos", "Historial clínico").forEachIndexed { i, texto ->
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

@Composable
private fun Datos(m: Mascota) {
    val cs = MaterialTheme.colorScheme
    val edad = Year.now().value - m.anioNacimiento
    val filas = listOf(
        "Especie" to Especie.valueOf(m.especie).etiqueta,
        "Raza" to m.raza.ifBlank { "—" },
        "Sexo" to Sexo.valueOf(m.sexo).etiqueta,
        "Año de nacimiento" to m.anioNacimiento.toString(),
        "Edad" to if (edad == 1) "1 año" else "$edad años",
        "Peso actual" to "${m.peso} kg"
    )
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cs.surface),
        border = BorderStroke(1.dp, BordeSuave),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
            filas.forEachIndexed { i, (k, v) ->
                Row(Modifier.fillMaxWidth().padding(vertical = 12.dp)) {
                    Text(k, color = cs.onSurfaceVariant, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
                    Text(v, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyMedium)
                }
                if (i < filas.lastIndex) HorizontalDivider(color = BordeSuave)
            }
        }
    }
}

/** "Gata", "Perro", "Mascota" según especie y sexo */
private fun especieTexto(e: Especie, s: Sexo): String = when (e) {
    Especie.PERRO -> if (s == Sexo.HEMBRA) "Perra" else "Perro"
    Especie.GATO -> if (s == Sexo.HEMBRA) "Gata" else "Gato"
    Especie.OTRO -> "Mascota"
}

/** "12 oct" */
private fun fechaDiaMes(fecha: String): String =
    LocalDate.parse(fecha).format(DateTimeFormatter.ofPattern("d MMM", Locale("es"))).replace(".", "")