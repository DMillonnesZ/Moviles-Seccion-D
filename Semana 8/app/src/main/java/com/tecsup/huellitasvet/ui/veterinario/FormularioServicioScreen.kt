package com.tecsup.huellitasvet.ui.veterinario

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.tecsup.huellitasvet.data.local.entity.Servicio
import com.tecsup.huellitasvet.ui.components.*
import com.tecsup.huellitasvet.ui.theme.BordeSuave
import com.tecsup.huellitasvet.viewmodel.ServiciosViewModel

@Composable
fun FormularioServicioScreen(
    vm: ServiciosViewModel,
    servicioId: Int?,               // null = nuevo · con id = editar
    onGuardado: () -> Unit,
    onCancelar: () -> Unit
) {
    val cs = MaterialTheme.colorScheme
    val errores by vm.errores.collectAsState()
    val mensaje by vm.mensaje.collectAsState()

    val existente: Servicio? = if (servicioId != null) {
        vm.servicio(servicioId).collectAsState().value
    } else null

    var nombre by rememberSaveable { mutableStateOf("") }
    var duracion by rememberSaveable { mutableStateOf<Int?>(null) }
    var precio by rememberSaveable { mutableStateOf("") }
    var descripcion by rememberSaveable { mutableStateOf("") }
    var icono by rememberSaveable { mutableStateOf("heart") }
    var activo by rememberSaveable { mutableStateOf(true) }
    var cargado by rememberSaveable { mutableStateOf(false) }
    var confirmarEliminar by remember { mutableStateOf(false) }

    // Al editar: llena el formulario una sola vez
    LaunchedEffect(existente) {
        val s = existente
        if (s != null && !cargado) {
            nombre = s.nombre
            duracion = s.duracionMin
            precio = "%.2f".format(s.precio)
            descripcion = s.descripcion
            icono = s.icono
            activo = s.activo
            cargado = true
        }
    }

    Scaffold(
        containerColor = cs.background,
        topBar = { AppTopBar(if (servicioId == null) "Nuevo servicio" else "Editar servicio", onAtras = onCancelar) }
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            VetTextField(
                valor = nombre,
                onCambio = { nombre = it; vm.limpiarError("nombre") },
                etiqueta = "Nombre del servicio",
                error = errores.nombre
            )

            // Duración
            Etiqueta("Duración")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                vm.duraciones.forEach { d ->
                    val activoChip = duracion == d
                    Box(
                        Modifier
                            .weight(1f)
                            .height(40.dp)
                            .background(if (activoChip) cs.primary else cs.surface, RoundedCornerShape(50))
                            .border(1.dp, if (activoChip) cs.primary else BordeSuave, RoundedCornerShape(50))
                            .clickable { duracion = d; vm.limpiarError("duracion") },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "$d min",
                            color = if (activoChip) cs.onPrimary else cs.onSurfaceVariant,
                            fontWeight = FontWeight.SemiBold,
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
            }
            ErrorCampo(errores.duracion)

            Spacer(Modifier.height(12.dp))
            VetTextField(
                valor = precio,
                onCambio = { precio = it; vm.limpiarError("precio") },
                etiqueta = "Precio (S/)",
                teclado = KeyboardType.Decimal,
                error = errores.precio
            )
            Spacer(Modifier.height(4.dp))
            VetTextField(
                valor = descripcion,
                onCambio = { descripcion = it },
                etiqueta = "Descripción",
                lineas = 3
            )

            // Ícono
            Etiqueta("Ícono")
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                listOf("heart", "syringe", "scissors").forEach { i ->
                    val sel = icono == i
                    Box(
                        Modifier
                            .weight(1f)
                            .height(64.dp)
                            .background(if (sel) cs.primaryContainer else cs.surface, RoundedCornerShape(14.dp))
                            .border(if (sel) 1.5.dp else 1.dp, if (sel) cs.primary else BordeSuave, RoundedCornerShape(14.dp))
                            .clickable { icono = i },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(iconoServicio(i), null, tint = if (sel) cs.primary else cs.onSurfaceVariant)
                    }
                }
            }

            // Servicio activo
            Spacer(Modifier.height(14.dp))
            GrupoPreferencias {
                FilaPreferencia(
                    titulo = "Servicio activo",
                    descripcion = "Los dueños podrán pedirlo",
                    valor = activo,
                    onCambio = { activo = it }
                )
            }

            Spacer(Modifier.height(20.dp))
            VetButton(
                texto = "Guardar servicio",
                onClick = {
                    vm.guardar(
                        id = servicioId ?: 0,
                        nombre = nombre,
                        duracionMin = duracion,
                        precioTexto = precio,
                        descripcion = descripcion,
                        icono = icono,
                        activo = activo,
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
                ) { Text("Eliminar servicio", color = cs.error) }
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

    if (confirmarEliminar && existente != null) {
        AlertDialog(
            onDismissRequest = { confirmarEliminar = false },
            title = { Text("¿Eliminar ${existente.nombre}?") },
            text = { Text("Si el servicio tiene citas, no se podrá eliminar.") },
            confirmButton = {
                TextButton(onClick = {
                    confirmarEliminar = false
                    vm.eliminar(existente, onGuardado)
                }) { Text("Eliminar", color = cs.error) }
            },
            dismissButton = { TextButton(onClick = { confirmarEliminar = false }) { Text("Cancelar") } }
        )
    }
}

@Composable
private fun Etiqueta(texto: String) {
    Text(
        texto,
        fontWeight = FontWeight.Bold,
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
    )
}

@Composable
private fun ErrorCampo(error: String?) {
    if (error != null) {
        Text(error, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 4.dp))
    }
}