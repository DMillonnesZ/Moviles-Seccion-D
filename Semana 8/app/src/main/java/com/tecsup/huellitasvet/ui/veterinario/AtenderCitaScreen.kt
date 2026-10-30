package com.tecsup.huellitasvet.ui.veterinario

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.tecsup.huellitasvet.data.local.relation.CitaConDetalle
import com.tecsup.huellitasvet.model.Especie
import com.tecsup.huellitasvet.model.Sexo
import com.tecsup.huellitasvet.ui.components.*
import com.tecsup.huellitasvet.ui.theme.BordeSuave
import com.tecsup.huellitasvet.ui.theme.EstadoCancelada
import com.tecsup.huellitasvet.viewmodel.AtenderCitaViewModel
import java.time.LocalDate
import java.time.Year
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun AtenderCitaScreen(
    vm: AtenderCitaViewModel,
    onListo: () -> Unit,
    onAtras: () -> Unit
) {
    val cs = MaterialTheme.colorScheme
    val c by vm.cita.collectAsState()
    val errores by vm.errores.collectAsState()
    val mensaje by vm.mensaje.collectAsState()

    var peso by rememberSaveable { mutableStateOf("") }
    var diagnostico by rememberSaveable { mutableStateOf("") }
    var tratamiento by rememberSaveable { mutableStateOf("") }
    var cargado by rememberSaveable { mutableStateOf(false) }

    // Precarga el peso actual de la mascota
    LaunchedEffect(c) {
        if (c != null && !cargado) {
            peso = c!!.mascota.mascota.peso.toString()
            cargado = true
        }
    }

    Scaffold(
        containerColor = cs.background,
        topBar = { AppTopBar("Atender cita", onAtras = onAtras) }
    ) { padding ->
        val cita = c ?: return@Scaffold
        val m = cita.mascota.mascota
        val editable = vm.sePuedeAtender(cita)

        Column(
            Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            Cabecera(cita)

            Text(
                "Registro clínico",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = 20.dp, bottom = 10.dp)
            )

            // Peso + peso anterior
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.Top) {
                VetTextField(
                    valor = peso,
                    onCambio = { peso = it; vm.limpiarError("peso") },
                    etiqueta = "Peso (kg)",
                    teclado = KeyboardType.Decimal,
                    error = errores.peso,
                    modifier = Modifier.weight(1f)
                )
                Row(
                    Modifier
                        .weight(1f)
                        .padding(top = 8.dp)
                        .height(56.dp)
                        .background(cs.primaryContainer.copy(alpha = 0.6f), RoundedCornerShape(14.dp))
                        .padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Favorite, null, tint = EstadoCancelada, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Column {
                        Text("Peso anterior", style = MaterialTheme.typography.labelSmall, color = cs.onSurfaceVariant)
                        Text("${m.peso} kg", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }

            Spacer(Modifier.height(4.dp))
            VetTextField(
                valor = diagnostico,
                onCambio = { diagnostico = it; vm.limpiarError("diagnostico") },
                etiqueta = "Diagnóstico",
                error = errores.diagnostico,
                lineas = 3
            )
            Spacer(Modifier.height(4.dp))
            VetTextField(
                valor = tratamiento,
                onCambio = { tratamiento = it },
                etiqueta = "Tratamiento",
                lineas = 3
            )

            // Aviso de actualización de peso
            val pesoNuevo = peso.replace(",", ".").toDoubleOrNull()
            if (pesoNuevo != null && pesoNuevo > 0) {
                Spacer(Modifier.height(12.dp))
                Row(
                    Modifier
                        .fillMaxWidth()
                        .background(cs.primaryContainer.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Check, null, tint = cs.primary, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(10.dp))
                    Text(
                        "Se actualizará el peso de ${m.nombre} a $pesoNuevo kg",
                        style = MaterialTheme.typography.bodySmall,
                        color = cs.onSurfaceVariant
                    )
                }
            }

            if (!editable) {
                Spacer(Modifier.height(10.dp))
                Text("Esta cita ya está ${cita.cita.estado.lowercase()}", color = cs.error, style = MaterialTheme.typography.bodySmall)
            }
            if (mensaje != null) {
                Spacer(Modifier.height(10.dp))
                Text(mensaje!!, color = cs.error, style = MaterialTheme.typography.bodySmall)
            }

            Spacer(Modifier.height(20.dp))
            VetButton(
                texto = "Registrar atención",
                onClick = { vm.registrar(diagnostico, tratamiento, peso, onListo) },
                habilitado = editable
            )
            Spacer(Modifier.height(10.dp))
            OutlinedButton(
                onClick = { vm.cancelar(onListo) },
                enabled = editable,
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.5.dp, if (editable) cs.error else BordeSuave),
                modifier = Modifier.fillMaxWidth().height(52.dp)
            ) { Text("Cancelar cita", color = cs.error, fontWeight = FontWeight.SemiBold) }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun Cabecera(c: CitaConDetalle) {
    val cs = MaterialTheme.colorScheme
    val m = c.mascota.mascota
    val especie = Especie.valueOf(m.especie)
    val sexo = Sexo.valueOf(m.sexo)
    val edad = Year.now().value - m.anioNacimiento
    val dueno = c.mascota.dueno.nombre.split(" ").let { p -> if (p.size > 1) "${p[0]} ${p[1].first()}." else p[0] }
    val fecha = LocalDate.parse(c.cita.fecha)
        .format(DateTimeFormatter.ofPattern("EEE d", Locale("es")))
        .replace(".", "")
        .replaceFirstChar { it.uppercase() }

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = cs.surface),
        border = BorderStroke(1.dp, BordeSuave),
        modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
    ) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.size(64.dp).background(cs.primaryContainer, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                MascotaAvatar(especie, tamano = 56.dp, conFondo = false)
            }
            Spacer(Modifier.width(14.dp))
            Column {
                Text(
                    "${m.nombre} · ${especieTexto(especie, sexo)} ${m.raza.lowercase()}",
                    fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    "${if (edad == 1) "1 año" else "$edad años"} · ${sexo.etiqueta} · Dueño: $dueno",
                    style = MaterialTheme.typography.labelSmall, color = cs.onSurfaceVariant
                )
                Spacer(Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(iconoServicio(c.servicio.icono), null, tint = cs.primary, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(
                        "${c.servicio.nombre} · $fecha · ${c.cita.hora}",
                        color = cs.primary, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium
                    )
                }
            }
        }
    }
}

private fun especieTexto(e: Especie, s: Sexo): String = when (e) {
    Especie.PERRO -> if (s == Sexo.HEMBRA) "Perra" else "Perro"
    Especie.GATO -> if (s == Sexo.HEMBRA) "Gata" else "Gato"
    Especie.OTRO -> "Mascota"
}