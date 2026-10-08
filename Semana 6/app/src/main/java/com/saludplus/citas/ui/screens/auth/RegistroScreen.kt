package com.saludplus.citas.ui.screens.auth

import android.util.Patterns
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.saludplus.citas.data.model.Usuario
import com.saludplus.citas.data.repository.Repositorio
import com.saludplus.citas.ui.components.BotonAzul
import com.saludplus.citas.ui.components.CampoTextoIcono
import com.saludplus.citas.ui.components.MensajeController
import com.saludplus.citas.ui.components.TipoMensaje
import com.saludplus.citas.ui.theme.AzulOscuro
import com.saludplus.citas.ui.theme.AzulPrimario
import com.saludplus.citas.ui.theme.GrisTexto

@Composable
fun RegistroScreen(
    onRegistroExitoso: () -> Unit,
    onIrLogin: () -> Unit,
    onTerminos: () -> Unit
) {
    // rememberSaveable: lo escrito no se pierde al ir a leer los Términos y volver
    var nombre by rememberSaveable { mutableStateOf("") }
    var telefono by rememberSaveable { mutableStateOf("") }
    var correo by rememberSaveable { mutableStateOf("") }
    var contrasena by rememberSaveable { mutableStateOf("") }
    var aceptaTerminos by rememberSaveable { mutableStateOf(false) }

    var errorNombre by remember { mutableStateOf<String?>(null) }
    var errorTelefono by remember { mutableStateOf<String?>(null) }
    var errorCorreo by remember { mutableStateOf<String?>(null) }
    var errorContrasena by remember { mutableStateOf<String?>(null) }

    fun registrar() {
        if (!aceptaTerminos) return

        errorNombre = if (nombre.trim().length < 3) "Ingresa tu nombre completo" else null
        errorTelefono = if (telefono.length != 9) "El teléfono debe tener 9 dígitos" else null
        errorCorreo = if (!Patterns.EMAIL_ADDRESS.matcher(correo.trim()).matches())
            "Ingresa un correo válido" else null
        errorContrasena = if (contrasena.length < 6) "Mínimo 6 caracteres" else null

        val hayErrores = listOf(errorNombre, errorTelefono, errorCorreo, errorContrasena)
            .any { it != null }
        if (hayErrores) return

        val usuario = Usuario(
            nombre = nombre.trim(),
            telefono = telefono,
            correo = correo.trim(),
            contrasena = contrasena
        )
        if (Repositorio.registrarUsuario(usuario)) {
            MensajeController.mostrar("Cuenta creada correctamente", TipoMensaje.EXITO)
            onRegistroExitoso()
        } else {
            errorCorreo = "Este correo ya está registrado"
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .systemBarsPadding()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Spacer(Modifier.height(16.dp))
        Text(
            text = "Crear cuenta",
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold,
            color = AzulOscuro
        )
        Text(
            text = "Regístrate para agendar tus citas",
            fontSize = 15.sp,
            color = GrisTexto
        )
        Spacer(Modifier.height(8.dp))

        CampoTextoIcono(
            valor = nombre,
            onCambio = { nombre = it; errorNombre = null },
            etiqueta = "Nombre completo",
            icono = Icons.Default.Person,
            placeholder = "Ej. Juan Pérez",
            error = errorNombre
        )
        CampoTextoIcono(
            valor = telefono,
            onCambio = {
                if (it.all { c -> c.isDigit() } && it.length <= 9) {
                    telefono = it
                    errorTelefono = null
                }
            },
            etiqueta = "Teléfono",
            icono = Icons.Default.Phone,
            placeholder = "Ej. 987654321",
            teclado = KeyboardType.Phone,
            error = errorTelefono
        )
        CampoTextoIcono(
            valor = correo,
            onCambio = { correo = it; errorCorreo = null },
            etiqueta = "Correo electrónico",
            icono = Icons.Default.Email,
            placeholder = "Ej. juan@correo.com",
            teclado = KeyboardType.Email,
            error = errorCorreo
        )
        CampoTextoIcono(
            valor = contrasena,
            onCambio = { contrasena = it; errorContrasena = null },
            etiqueta = "Contraseña",
            icono = Icons.Default.Lock,
            placeholder = "Mínimo 6 caracteres",
            oculto = true,
            teclado = KeyboardType.Password,
            error = errorContrasena
        )

        // Casilla de aceptación con enlace a los Términos y Condiciones
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = aceptaTerminos,
                onCheckedChange = { aceptaTerminos = it },
                colors = CheckboxDefaults.colors(checkedColor = AzulPrimario)
            )
            Text(
                text = "Acepto los ",
                fontSize = 14.sp,
                color = GrisTexto
            )
            Text(
                text = "Términos y Condiciones",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = AzulPrimario,
                modifier = Modifier.clickable { onTerminos() }
            )
        }

        // Registrarme solo se habilita con la casilla marcada
        BotonAzul(
            texto = "Registrarme",
            habilitado = aceptaTerminos,
            onClick = { registrar() }
        )

        Spacer(Modifier.height(8.dp))

        // "¿Ya tienes cuenta?" en negrita + "Iniciar sesión" al costado
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "¿Ya tienes cuenta?",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = AzulOscuro
            )
            Spacer(Modifier.width(4.dp))
            Text(
                text = "Iniciar sesión",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = AzulPrimario,
                modifier = Modifier.clickable { onIrLogin() }
            )
        }
    }
}
