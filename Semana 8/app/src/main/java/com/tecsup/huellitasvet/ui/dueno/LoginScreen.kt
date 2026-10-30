package com.tecsup.huellitasvet.ui.dueno

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tecsup.huellitasvet.model.Rol
import com.tecsup.huellitasvet.ui.components.VetButton
import com.tecsup.huellitasvet.ui.components.VetTextField
import com.tecsup.huellitasvet.ui.theme.GrisSelector
import com.tecsup.huellitasvet.ui.theme.VetTheme
import com.tecsup.huellitasvet.viewmodel.SesionViewModel

@Composable
fun LoginScreen(
    vm: SesionViewModel,
    onIngresar: (Rol) -> Unit
) {
    var rolSeleccionado by rememberSaveable { mutableStateOf(Rol.CLIENTE) }
    var correo by rememberSaveable { mutableStateOf("") }
    var clave by rememberSaveable { mutableStateOf("") }
    val error by vm.error.collectAsState()
    val oscuro by vm.modoOscuro.collectAsState()

    // El color cambia según la pestaña elegida (verde / azul)
    VetTheme(rol = rolSeleccionado, oscuro = oscuro) {
        val cs = MaterialTheme.colorScheme
        Column(
            Modifier
                .fillMaxSize()
                .background(cs.surface)
                .systemBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
        ) {
            Spacer(Modifier.height(40.dp))

            // Título
            Text("Bienvenido", fontSize = 30.sp, fontWeight = FontWeight.Bold, color = cs.onSurface)
            Spacer(Modifier.height(4.dp))
            Text("Ingresa para continuar", style = MaterialTheme.typography.bodyMedium, color = cs.onSurfaceVariant)

            Spacer(Modifier.height(28.dp))

            // Selector Dueño / Veterinario / Administrador
            SelectorRol(
                seleccionado = rolSeleccionado,
                onCambio = { rolSeleccionado = it; vm.limpiarError() }
            )

            Spacer(Modifier.height(24.dp))

            VetTextField(
                valor = correo,
                onCambio = { correo = it; vm.limpiarError() },
                etiqueta = "Correo",
                teclado = KeyboardType.Email,
                icono = Icons.Default.Email
            )
            Spacer(Modifier.height(10.dp))
            VetTextField(
                valor = clave,
                onCambio = { clave = it; vm.limpiarError() },
                etiqueta = "Contraseña",
                esClave = true,
                icono = Icons.Default.Lock
            )

            // ¿Olvidaste tu contraseña?
            Text(
                "¿Olvidaste tu contraseña?",
                color = cs.primary,
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(top = 12.dp)
            )

            // Error
            if (error != null) {
                Spacer(Modifier.height(8.dp))
                Text(error!!, color = cs.error, style = MaterialTheme.typography.bodyMedium)
            }

            Spacer(Modifier.height(20.dp))

            VetButton(
                texto = "Ingresar",
                onClick = { vm.login(correo, clave, rolSeleccionado, onIngresar) }
            )

            Spacer(Modifier.height(20.dp))

            when (rolSeleccionado) {
                Rol.CLIENTE -> {
                    // Dueño → registrarse
                    Text(
                        "¿No tienes cuenta?  Regístrate",
                        color = cs.primary,
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
                Rol.ADMIN -> {
                    // Veterinario → tarjeta Modo práctica
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .background(cs.primaryContainer.copy(alpha = 0.6f), RoundedCornerShape(14.dp))
                            .padding(16.dp)
                    ) {
                        Text("Modo práctica", color = cs.primary, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "Puedes cambiar de perfil desde Ajustes para probar ambos roles.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = cs.onSurfaceVariant
                        )
                    }
                }
                Rol.ADMINISTRADOR -> {
                    // Administrador → tarjeta informativa
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .background(cs.primaryContainer.copy(alpha = 0.6f), RoundedCornerShape(14.dp))
                            .padding(16.dp)
                    ) {
                        Text("Acceso de administración", color = cs.primary, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "Acceso de administración: gestiona pacientes y mascotas de la clínica.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = cs.onSurfaceVariant
                        )
                    }
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun SelectorRol(seleccionado: Rol, onCambio: (Rol) -> Unit) {
    val cs = MaterialTheme.colorScheme
    Row(
        Modifier
            .fillMaxWidth()
            .background(GrisSelector, RoundedCornerShape(14.dp))
            .padding(4.dp)
    ) {
        Rol.entries.forEach { rol ->
            val activo = rol == seleccionado
            Box(
                Modifier
                    .weight(1f)
                    .background(if (activo) cs.primary else GrisSelector, RoundedCornerShape(10.dp))
                    .clickable { onCambio(rol) }
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    rol.etiqueta,
                    color = if (activo) cs.onPrimary else cs.onSurfaceVariant,
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.labelMedium,
                    maxLines = 1
                )
            }
        }
    }
}
