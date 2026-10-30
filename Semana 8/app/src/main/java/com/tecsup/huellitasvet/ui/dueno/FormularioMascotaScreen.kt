package com.tecsup.huellitasvet.ui.dueno

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.tecsup.huellitasvet.data.local.entity.Mascota
import com.tecsup.huellitasvet.model.Especie
import com.tecsup.huellitasvet.model.Sexo
import com.tecsup.huellitasvet.ui.components.AppTopBar
import com.tecsup.huellitasvet.ui.components.CampoFechaNacimiento
import com.tecsup.huellitasvet.ui.components.MascotaAvatar
import com.tecsup.huellitasvet.ui.components.VetButton
import com.tecsup.huellitasvet.ui.components.VetTextField
import com.tecsup.huellitasvet.ui.theme.BordeSuave
import com.tecsup.huellitasvet.viewmodel.FormMascota
import com.tecsup.huellitasvet.viewmodel.MascotaViewModel

@Composable
fun FormularioMascotaScreen(
    vm: MascotaViewModel,
    duenoId: Int,
    mascotaId: Int?,                 // null = nueva · con id = editar
    onGuardado: () -> Unit,
    onEliminada: () -> Unit,
    onCancelar: () -> Unit
) {
    val cs = MaterialTheme.colorScheme
    val errores by vm.errores.collectAsState()
    val mensaje by vm.mensaje.collectAsState()

    // Si es edición, observa la mascota; si es nueva, null
    val existente: Mascota? = if (mascotaId != null) {
        vm.mascota(mascotaId).collectAsState().value
    } else null

    var nombre by rememberSaveable { mutableStateOf("") }
    var especie by rememberSaveable { mutableStateOf<Especie?>(null) }
    var raza by rememberSaveable { mutableStateOf("") }
    var anio by rememberSaveable { mutableStateOf("") }
    var peso by rememberSaveable { mutableStateOf("") }
    var sexo by rememberSaveable { mutableStateOf<Sexo?>(null) }
    var cargado by rememberSaveable { mutableStateOf(false) }
    var confirmarEliminar by remember { mutableStateOf(false) }

    // Al editar: llena el formulario una sola vez
    LaunchedEffect(existente) {
        val m = existente
        if (m != null && !cargado) {
            nombre = m.nombre
            especie = Especie.valueOf(m.especie)
            raza = m.raza
            anio = m.anioNacimiento.toString()
            peso = m.peso.toString()
            sexo = Sexo.valueOf(m.sexo)
            cargado = true
        }
    }

    Scaffold(
        containerColor = cs.background,
        topBar = { AppTopBar(if (mascotaId == null) "Nueva mascota" else "Editar mascota", onAtras = onCancelar) }
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            // Avatar según especie
            Box(Modifier.align(Alignment.CenterHorizontally).padding(vertical = 8.dp)) {
                Box(
                    Modifier.size(110.dp).background(cs.primaryContainer, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    MascotaAvatar(especie ?: Especie.PERRO, tamano = 96.dp, conFondo = false)
                }
                Box(
                    Modifier
                        .align(Alignment.BottomEnd)
                        .size(32.dp)
                        .background(cs.primary, CircleShape)
                        .border(3.dp, cs.background, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Add, null, tint = cs.onPrimary, modifier = Modifier.size(18.dp))
                }
            }

            Spacer(Modifier.height(12.dp))

            VetTextField(
                valor = nombre,
                onCambio = { nombre = it; vm.limpiarError("nombre") },
                etiqueta = "Nombre",
                error = errores.nombre
            )

            // Especie
            Etiqueta("Especie")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Especie.entries.forEach { e ->
                    OpcionChip(
                        texto = e.etiqueta,
                        seleccionado = especie == e,
                        relleno = true,
                        modifier = Modifier.weight(1f)
                    ) { especie = e; vm.limpiarError("especie") }
                }
            }
            ErrorCampo(errores.especie)

            Spacer(Modifier.height(12.dp))
            VetTextField(valor = raza, onCambio = { raza = it }, etiqueta = "Raza")

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                CampoFechaNacimiento(
                    anio = anio,
                    onAnioElegido = { anio = it; vm.limpiarError("anio") },
                    error = errores.anio,
                    modifier = Modifier.weight(1f)
                )
                VetTextField(
                    valor = peso,
                    onCambio = { peso = it; vm.limpiarError("peso") },
                    etiqueta = "Peso (kg)",
                    teclado = KeyboardType.Decimal,
                    error = errores.peso,
                    modifier = Modifier.weight(1f)
                )
            }

            // Sexo
            Etiqueta("Sexo")
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Sexo.entries.forEach { s ->
                    OpcionChip(
                        texto = s.etiqueta,
                        seleccionado = sexo == s,
                        relleno = false,
                        modifier = Modifier.weight(1f)
                    ) { sexo = s; vm.limpiarError("sexo") }
                }
            }
            ErrorCampo(errores.sexo)

            Spacer(Modifier.height(24.dp))

            VetButton(
                texto = "Guardar mascota",
                onClick = {
                    vm.guardar(
                        FormMascota(
                            id = mascotaId ?: 0,
                            duenoId = existente?.duenoId ?: duenoId,
                            nombre = nombre, especie = especie, raza = raza,
                            anio = anio, peso = peso, sexo = sexo
                        ),
                        onExito = onGuardado
                    )
                }
            )
            Spacer(Modifier.height(10.dp))
            OutlinedButton(
                onClick = onCancelar,
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(2.dp, cs.primary),
                modifier = Modifier.fillMaxWidth().height(52.dp)
            ) { Text("Cancelar", color = cs.primary, fontWeight = FontWeight.SemiBold) }

            // Eliminar (solo al editar)
            if (existente != null) {
                TextButton(
                    onClick = { confirmarEliminar = true },
                    modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 6.dp)
                ) { Text("Eliminar mascota", color = cs.error) }
            }
            if (mensaje != null) {
                Text(
                    mensaje!!,
                    color = cs.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
            Spacer(Modifier.height(24.dp))
        }
    }

    // Confirmación antes de eliminar
    if (confirmarEliminar && existente != null) {
        AlertDialog(
            onDismissRequest = { confirmarEliminar = false },
            title = { Text("¿Eliminar a ${existente.nombre}?") },
            text = { Text("Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(onClick = {
                    confirmarEliminar = false
                    vm.eliminar(existente, onEliminada)
                }) { Text("Eliminar", color = cs.error) }
            },
            dismissButton = {
                TextButton(onClick = { confirmarEliminar = false }) { Text("Cancelar") }
            }
        )
    }
}

@Composable
private fun Etiqueta(texto: String) {
    Text(
        texto,
        fontWeight = FontWeight.Bold,
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)
    )
}

@Composable
private fun ErrorCampo(error: String?) {
    if (error != null) {
        Text(
            error,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

/** relleno = true → verde sólido (Especie) · false → verde claro con borde (Sexo) */
@Composable
private fun OpcionChip(
    texto: String,
    seleccionado: Boolean,
    relleno: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val cs = MaterialTheme.colorScheme
    val fondo = when {
        !seleccionado -> cs.surface
        relleno -> cs.primary
        else -> cs.primaryContainer
    }
    val borde = if (seleccionado) cs.primary else BordeSuave
    val color = when {
        !seleccionado -> cs.onSurfaceVariant
        relleno -> cs.onPrimary
        else -> cs.primary
    }
    Box(
        modifier
            .height(44.dp)
            .background(fondo, RoundedCornerShape(12.dp))
            .border(if (seleccionado && !relleno) 1.5.dp else 1.dp, borde, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(texto, color = color, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.labelLarge)
    }
}
