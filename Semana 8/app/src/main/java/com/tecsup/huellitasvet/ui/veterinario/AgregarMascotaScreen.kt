package com.tecsup.huellitasvet.ui.veterinario

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
import com.tecsup.huellitasvet.data.local.entity.Usuario
import com.tecsup.huellitasvet.model.Especie
import com.tecsup.huellitasvet.model.Razas
import com.tecsup.huellitasvet.model.Sexo
import com.tecsup.huellitasvet.ui.components.AppTopBar
import com.tecsup.huellitasvet.ui.components.MascotaAvatar
import com.tecsup.huellitasvet.ui.components.VetButton
import com.tecsup.huellitasvet.ui.components.VetTextField
import com.tecsup.huellitasvet.ui.theme.BordeSuave
import com.tecsup.huellitasvet.viewmodel.FormMascota
import com.tecsup.huellitasvet.viewmodel.MascotaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgregarMascotaScreen(
    vm: MascotaViewModel,
    onAtras: () -> Unit
) {
    val cs = MaterialTheme.colorScheme
    val duenos by vm.duenos.collectAsState()
    val errores by vm.errores.collectAsState()

    var duenoSeleccionado by remember { mutableStateOf<Usuario?>(null) }
    var expandidoDueno by remember { mutableStateOf(false) }

    var nombre by rememberSaveable { mutableStateOf("") }
    var especie by rememberSaveable { mutableStateOf<Especie?>(null) }

    var razaSeleccionada by rememberSaveable { mutableStateOf("") }
    var expandidoRaza by remember { mutableStateOf(false) }

    var anio by rememberSaveable { mutableStateOf("") }
    var peso by rememberSaveable { mutableStateOf("") }
    var sexo by rememberSaveable { mutableStateOf<Sexo?>(null) }

    val razasDisponibles = Razas.de(especie)

    Scaffold(
        containerColor = cs.background,
        topBar = { AppTopBar("Agregar mascota", onAtras = onAtras) }
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

            // Dueño (ExposedDropdownMenuBox)
            Etiqueta("Dueño")
            ExposedDropdownMenuBox(
                expanded = expandidoDueno,
                onExpandedChange = { expandidoDueno = it }
            ) {
                OutlinedTextField(
                    value = duenoSeleccionado?.let { "${it.nombre} (${it.correo})" } ?: "",
                    onValueChange = {},
                    readOnly = true,
                    placeholder = { Text("Selecciona un dueño", color = cs.onSurfaceVariant) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandidoDueno) },
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = if (errores.dueno != null) cs.error else BordeSuave,
                        focusedBorderColor = if (errores.dueno != null) cs.error else cs.primary,
                        unfocusedContainerColor = cs.surface,
                        focusedContainerColor = cs.surface
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable).fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expandidoDueno,
                    onDismissRequest = { expandidoDueno = false }
                ) {
                    duenos.forEach { u ->
                        DropdownMenuItem(
                            text = { Text("${u.nombre} (${u.correo})") },
                            onClick = {
                                duenoSeleccionado = u
                                vm.limpiarError("dueno")
                                expandidoDueno = false
                            }
                        )
                    }
                }
            }
            ErrorCampo(errores.dueno)

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
                    ) {
                        especie = e
                        razaSeleccionada = ""
                        vm.limpiarError("especie")
                        vm.limpiarError("raza")
                    }
                }
            }
            ErrorCampo(errores.especie)

            Spacer(Modifier.height(12.dp))

            // Raza (ExposedDropdownMenuBox)
            val habilitadoRaza = especie != null
            Etiqueta("Raza")
            ExposedDropdownMenuBox(
                expanded = expandidoRaza && habilitadoRaza,
                onExpandedChange = { if (habilitadoRaza) expandidoRaza = it }
            ) {
                OutlinedTextField(
                    value = razaSeleccionada,
                    onValueChange = {},
                    readOnly = true,
                    enabled = habilitadoRaza,
                    placeholder = {
                        Text(
                            if (habilitadoRaza) "Selecciona una raza" else "Elige primero la especie",
                            color = cs.onSurfaceVariant
                        )
                    },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandidoRaza && habilitadoRaza) },
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = if (errores.raza != null) cs.error else BordeSuave,
                        focusedBorderColor = if (errores.raza != null) cs.error else cs.primary,
                        unfocusedContainerColor = cs.surface,
                        focusedContainerColor = cs.surface,
                        disabledBorderColor = BordeSuave,
                        disabledContainerColor = cs.surfaceVariant.copy(alpha = 0.3f),
                        disabledPlaceholderColor = cs.onSurfaceVariant.copy(alpha = 0.5f)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable).fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expandidoRaza && habilitadoRaza,
                    onDismissRequest = { expandidoRaza = false }
                ) {
                    razasDisponibles.forEach { r ->
                        DropdownMenuItem(
                            text = { Text(r) },
                            onClick = {
                                razaSeleccionada = r
                                vm.limpiarError("raza")
                                expandidoRaza = false
                            }
                        )
                    }
                }
            }
            ErrorCampo(errores.raza)

            Spacer(Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                VetTextField(
                    valor = anio,
                    onCambio = { anio = it.filter(Char::isDigit).take(4); vm.limpiarError("anio") },
                    etiqueta = "Año de nacimiento",
                    teclado = KeyboardType.Number,
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
                    vm.guardarComoAdministrador(
                        FormMascota(
                            id = 0,
                            duenoId = duenoSeleccionado?.id ?: 0,
                            nombre = nombre,
                            especie = especie,
                            raza = razaSeleccionada,
                            anio = anio,
                            peso = peso,
                            sexo = sexo
                        ),
                        onExito = onAtras
                    )
                }
            )
            Spacer(Modifier.height(10.dp))
            OutlinedButton(
                onClick = onAtras,
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(2.dp, cs.primary),
                modifier = Modifier.fillMaxWidth().height(52.dp)
            ) { Text("Cancelar", color = cs.primary, fontWeight = FontWeight.SemiBold) }

            Spacer(Modifier.height(24.dp))
        }
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
