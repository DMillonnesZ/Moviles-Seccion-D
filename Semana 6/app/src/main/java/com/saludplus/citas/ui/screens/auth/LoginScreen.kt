package com.saludplus.citas.ui.screens.auth

import android.widget.Toast
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.saludplus.citas.data.repository.Repositorio
import com.saludplus.citas.ui.components.BarraSuperior
import com.saludplus.citas.ui.components.BotonAzul
import com.saludplus.citas.ui.components.CampoTextoIcono
import com.saludplus.citas.ui.theme.AzulOscuro
import com.saludplus.citas.ui.theme.AzulPrimario
import com.saludplus.citas.ui.theme.GrisTexto

@Composable
fun LoginScreen(
    onLoginExitoso: () -> Unit,
    onIrRegistro: () -> Unit,
    onAtras: () -> Unit
) {
    val contexto = LocalContext.current

    var correo by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var errorCorreo by remember { mutableStateOf<String?>(null) }
    var errorContrasena by remember { mutableStateOf<String?>(null) }

    fun ingresar() {
        errorCorreo = if (correo.isBlank()) "Ingresa tu correo" else null
        errorContrasena = if (contrasena.isEmpty()) "Ingresa tu contraseña" else null
        if (errorCorreo != null || errorContrasena != null) return

        if (Repositorio.iniciarSesion(correo.trim(), contrasena)) {
            val nombre = Repositorio.usuarioActual?.nombre ?: ""
            Toast.makeText(contexto, "Bienvenido, $nombre", Toast.LENGTH_SHORT).show()
            onLoginExitoso()
        } else {
            errorContrasena = "Correo o contraseña incorrectos"
        }
    }

    Scaffold(
        containerColor = Color.White,
        topBar = { BarraSuperior(titulo = "Iniciar sesión", onAtras = onAtras) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Spacer(Modifier.height(16.dp))
            Text(
                text = "Bienvenido de nuevo",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = AzulOscuro
            )
            Text(
                text = "Ingresa para gestionar tus citas",
                fontSize = 15.sp,
                color = GrisTexto
            )
            Spacer(Modifier.height(8.dp))

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
                placeholder = "Tu contraseña",
                oculto = true,
                teclado = KeyboardType.Password,
                error = errorContrasena
            )

            Spacer(Modifier.height(4.dp))
            BotonAzul(texto = "Iniciar sesión", onClick = { ingresar() })

            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "¿No tienes cuenta?",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = AzulOscuro
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = "Regístrate",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AzulPrimario,
                    modifier = Modifier.clickable { onIrRegistro() }
                )
            }
        }
    }
}