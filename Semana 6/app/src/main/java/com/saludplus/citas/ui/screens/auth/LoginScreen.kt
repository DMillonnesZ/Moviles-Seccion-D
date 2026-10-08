package com.saludplus.citas.ui.screens.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockPerson
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.saludplus.citas.data.repository.Repositorio
import com.saludplus.citas.ui.components.BarraSuperior
import com.saludplus.citas.ui.components.BotonAzul
import com.saludplus.citas.ui.components.CampoTextoIcono
import com.saludplus.citas.ui.components.MensajeController
import com.saludplus.citas.ui.components.TipoMensaje
import com.saludplus.citas.ui.components.efectoPresion
import com.saludplus.citas.ui.theme.AzulClaro
import com.saludplus.citas.ui.theme.AzulOscuro
import com.saludplus.citas.ui.theme.AzulPrimario
import com.saludplus.citas.ui.theme.GrisTexto
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    onLoginExitoso: () -> Unit,
    onIrRegistro: () -> Unit,
    onAtras: () -> Unit
) {
    var correo by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var errorCorreo by remember { mutableStateOf<String?>(null) }
    var errorContrasena by remember { mutableStateOf<String?>(null) }

    // Animaciones de entrada escalonada
    val iconoScale = remember { Animatable(0.5f) }
    val campo1Alpha = remember { Animatable(0f) }
    val campo2Alpha = remember { Animatable(0f) }
    val botonAlpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        launch {
            iconoScale.animateTo(
                1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
        }
        delay(100)
        launch { campo1Alpha.animateTo(1f, tween(300)) }
        delay(100)
        launch { campo2Alpha.animateTo(1f, tween(300)) }
        delay(100)
        launch { botonAlpha.animateTo(1f, tween(300)) }
    }

    fun ingresar() {
        errorCorreo = if (correo.isBlank()) "Ingresa tu correo" else null
        errorContrasena = if (contrasena.isEmpty()) "Ingresa tu contraseña" else null
        if (errorCorreo != null || errorContrasena != null) return

        if (Repositorio.iniciarSesion(correo.trim(), contrasena)) {
            val nombre = Repositorio.usuarioActual?.nombre ?: ""
            MensajeController.mostrar("Bienvenido, $nombre", TipoMensaje.BIENVENIDA)
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
            Spacer(Modifier.height(12.dp))

            // Encabezado con ícono animado
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .graphicsLayer {
                        scaleX = iconoScale.value
                        scaleY = iconoScale.value
                    }
                    .clip(CircleShape)
                    .background(AzulClaro),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.LockPerson,
                    contentDescription = null,
                    tint = AzulPrimario,
                    modifier = Modifier.size(36.dp)
                )
            }

            Text(
                text = "Bienvenido de nuevo",
                fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold,
                color = AzulOscuro
            )
            Text(
                text = "Ingresa para gestionar tus citas",
                fontSize = 15.sp,
                color = GrisTexto
            )
            Spacer(Modifier.height(8.dp))

            // Campos escalonados
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer { alpha = campo1Alpha.value }
            ) {
                CampoTextoIcono(
                    valor = correo,
                    onCambio = { correo = it; errorCorreo = null },
                    etiqueta = "Correo electrónico",
                    icono = Icons.Default.Email,
                    placeholder = "Ej. juan@correo.com",
                    teclado = KeyboardType.Email,
                    error = errorCorreo
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer { alpha = campo2Alpha.value }
            ) {
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
            }

            Spacer(Modifier.height(4.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer { alpha = botonAlpha.value }
            ) {
                BotonAzul(
                    texto = "Iniciar sesión",
                    onClick = { ingresar() },
                    modifier = Modifier.efectoPresion { ingresar() }
                )
            }

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
                    modifier = Modifier
                        .efectoPresion { onIrRegistro() }
                        .clickable { onIrRegistro() }
                )
            }
        }
    }
}
