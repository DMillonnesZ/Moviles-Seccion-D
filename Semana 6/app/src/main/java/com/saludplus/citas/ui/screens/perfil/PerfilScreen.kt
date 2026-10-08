package com.saludplus.citas.ui.screens.perfil

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.saludplus.citas.data.repository.Repositorio
import com.saludplus.citas.navigation.Rutas
import com.saludplus.citas.ui.components.BarraNavegacion
import com.saludplus.citas.ui.components.ContadorAnimado
import com.saludplus.citas.ui.components.FilaDetalle
import com.saludplus.citas.ui.components.MensajeController
import com.saludplus.citas.ui.components.TipoMensaje
import com.saludplus.citas.ui.components.TarjetaSuave
import com.saludplus.citas.ui.components.efectoPresion
import com.saludplus.citas.ui.theme.AzulClaro
import com.saludplus.citas.ui.theme.AzulOscuro
import com.saludplus.citas.ui.theme.AzulPrimario
import com.saludplus.citas.ui.theme.Degradados
import com.saludplus.citas.ui.theme.GrisTexto
import com.saludplus.citas.ui.theme.RojoError
import java.time.LocalDate

@Composable
fun PerfilScreen(
    onNavegar: (String) -> Unit,
    onCerrarSesion: () -> Unit
) {
    val usuario = Repositorio.usuarioActual
    var mostrarDialogoEditar by remember { mutableStateOf(false) }
    var sonidoHabilitado by remember { mutableStateOf(Repositorio.sonidoNotificacionesHabilitado) }

    val hoy = remember { LocalDate.now() }
    val citasUsuario = Repositorio.citasDelUsuario()
    val citasProximasCount = remember(citasUsuario) {
        citasUsuario.count { cita ->
            try {
                val fecha = LocalDate.parse(cita.fecha)
                !fecha.isBefore(hoy)
            } catch (e: Exception) {
                true
            }
        }
    }
    val totalCitasCount = citasUsuario.size
    val resultadosCount = remember { Repositorio.resultados.count { it.estado == "Disponible" } }

    val inicial = usuario?.nombre?.trim()?.firstOrNull()?.uppercaseChar()?.toString() ?: "P"

    Scaffold(
        containerColor = Color.White,
        bottomBar = { BarraNavegacion(rutaActual = Rutas.PERFIL, onNavegar = onNavegar) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            // Cabecera con degradado
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Degradados.Principal)
                    .padding(top = 36.dp, bottom = 28.dp, start = 20.dp, end = 20.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(90.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.25f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = inicial,
                            fontSize = 38.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                    }

                    Spacer(Modifier.height(12.dp))

                    Text(
                        text = usuario?.nombre ?: "Paciente",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(Modifier.height(2.dp))

                    Text(
                        text = usuario?.correo ?: "",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Tarjetas de estadísticas con ContadorAnimado
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    TarjetaEstadistica(
                        titulo = "Próximas",
                        valor = citasProximasCount,
                        modifier = Modifier.weight(1f)
                    )
                    TarjetaEstadistica(
                        titulo = "Totales",
                        valor = totalCitasCount,
                        modifier = Modifier.weight(1f)
                    )
                    TarjetaEstadistica(
                        titulo = "Resultados",
                        valor = resultadosCount,
                        modifier = Modifier.weight(1f)
                    )
                }

                // Sección Información Personal
                Text(
                    text = "Información personal",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = AzulOscuro,
                    modifier = Modifier.padding(start = 4.dp)
                )

                TarjetaSuave(modifier = Modifier.fillMaxWidth()) {
                    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        FilaDetalle(
                            icono = Icons.Default.Person,
                            titulo = "Nombre completo",
                            valor = usuario?.nombre ?: ""
                        )
                        FilaDetalle(
                            icono = Icons.Default.Phone,
                            titulo = "Teléfono",
                            valor = usuario?.telefono ?: ""
                        )
                        FilaDetalle(
                            icono = Icons.Default.Email,
                            titulo = "Correo electrónico",
                            valor = usuario?.correo ?: ""
                        )

                        Spacer(Modifier.height(4.dp))

                        OutlinedButton(
                            onClick = { mostrarDialogoEditar = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.5.dp, AzulPrimario),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = AzulPrimario)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text("Editar datos personales", fontWeight = FontWeight.SemiBold)
                        }
                    }
                }

                // Sección Preferencias
                Text(
                    text = "Preferencias",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = AzulOscuro,
                    modifier = Modifier.padding(start = 4.dp)
                )

                TarjetaSuave(modifier = Modifier.fillMaxWidth()) {
                    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(AzulClaro),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.VolumeUp,
                                        contentDescription = null,
                                        tint = AzulPrimario,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "Sonido de notificaciones",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = AzulOscuro
                                    )
                                    Text(
                                        text = if (sonidoHabilitado) "Activado para alertas" else "Silencioso",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = GrisTexto
                                    )
                                }
                            }

                            Switch(
                                checked = sonidoHabilitado,
                                onCheckedChange = { nuevoEstado ->
                                    sonidoHabilitado = nuevoEstado
                                    Repositorio.sonidoNotificacionesHabilitado = nuevoEstado
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = AzulPrimario
                                )
                            )
                        }

                        OutlinedButton(
                            onClick = {
                                MensajeController.mostrar(
                                    if (sonidoHabilitado) "¡Sonido de notificación activado correctamente!"
                                    else "Notificación de prueba emitida en modo silencioso.",
                                    TipoMensaje.INFORMACION
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, Color(0xFFD1D5DB)),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = AzulOscuro)
                        ) {
                            Icon(
                                imageVector = Icons.Default.NotificationsActive,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text("Probar notificación", fontWeight = FontWeight.Medium)
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))

                // Botón Cerrar Sesión
                OutlinedButton(
                    onClick = {
                        Repositorio.cerrarSesion()
                        onCerrarSesion()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .efectoPresion {
                            Repositorio.cerrarSesion()
                            onCerrarSesion()
                        },
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.5.dp, RojoError),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = RojoError)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Logout,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Cerrar sesión", fontWeight = FontWeight.Bold)
                }

                Spacer(Modifier.height(16.dp))
            }
        }
    }

    // Diálogo de Edición de Datos
    if (mostrarDialogoEditar) {
        DialogoEditarDatos(
            nombreActual = usuario?.nombre ?: "",
            telefonoActual = usuario?.telefono ?: "",
            onDismiss = { mostrarDialogoEditar = false },
            onGuardar = { nuevoNombre, nuevoTelefono ->
                val ok = Repositorio.actualizarUsuario(nuevoNombre, nuevoTelefono)
                if (ok) {
                    MensajeController.mostrar("Datos actualizados con éxito", TipoMensaje.EXITO)
                    mostrarDialogoEditar = false
                } else {
                    MensajeController.mostrar("Error al actualizar los datos", TipoMensaje.ERROR)
                }
            }
        )
    }
}

@Composable
private fun TarjetaEstadistica(
    titulo: String,
    valor: Int,
    modifier: Modifier = Modifier
) {
    TarjetaSuave(modifier = modifier) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            ContadorAnimado(valor = valor)
            Spacer(Modifier.height(2.dp))
            Text(
                text = titulo,
                style = MaterialTheme.typography.labelMedium,
                color = GrisTexto,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun DialogoEditarDatos(
    nombreActual: String,
    telefonoActual: String,
    onDismiss: () -> Unit,
    onGuardar: (String, String) -> Unit
) {
    var nombre by remember { mutableStateOf(nombreActual) }
    var telefono by remember { mutableStateOf(telefonoActual) }
    var errorNombre by remember { mutableStateOf<String?>(null) }
    var errorTelefono by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Editar datos personales",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = AzulOscuro
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = {
                        nombre = it
                        errorNombre = if (it.isBlank()) "El nombre no puede estar vacío" else null
                    },
                    label = { Text("Nombre completo") },
                    isError = errorNombre != null,
                    supportingText = errorNombre?.let { { Text(it, color = RojoError) } },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AzulPrimario,
                        cursorColor = AzulPrimario
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = telefono,
                    onValueChange = {
                        telefono = it
                        errorTelefono = if (it.isBlank()) "El teléfono no puede estar vacío" else null
                    },
                    label = { Text("Teléfono") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    isError = errorTelefono != null,
                    supportingText = errorTelefono?.let { { Text(it, color = RojoError) } },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AzulPrimario,
                        cursorColor = AzulPrimario
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val esValidoNombre = nombre.trim().length >= 2
                    val esValidoTelefono = telefono.trim().length >= 7

                    if (!esValidoNombre) {
                        errorNombre = "Nombre inválido (mínimo 2 letras)"
                    }
                    if (!esValidoTelefono) {
                        errorTelefono = "Teléfono inválido (mínimo 7 dígitos)"
                    }

                    if (esValidoNombre && esValidoTelefono) {
                        onGuardar(nombre.trim(), telefono.trim())
                    }
                }
            ) {
                Text("Guardar", fontWeight = FontWeight.Bold, color = AzulPrimario)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = GrisTexto)
            }
        },
        containerColor = Color.White,
        shape = RoundedCornerShape(20.dp)
    )
}