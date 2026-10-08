package com.saludplus.citas.ui.screens.auth

import android.util.Patterns
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
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
import com.saludplus.citas.data.model.Usuario
import com.saludplus.citas.data.repository.Repositorio
import com.saludplus.citas.ui.components.BotonAzul
import com.saludplus.citas.ui.components.CampoTextoIcono
import com.saludplus.citas.ui.components.MensajeController
import com.saludplus.citas.ui.components.TipoMensaje
import com.saludplus.citas.ui.components.efectoPresion
import com.saludplus.citas.ui.theme.AzulClaro
import com.saludplus.citas.ui.theme.AzulOscuro
import com.saludplus.citas.ui.theme.AzulPrimario
import com.saludplus.citas.ui.theme.ColoresSemanticos
import com.saludplus.citas.ui.theme.GrisTexto
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
private fun IndicadorFortalezaContrasena(
    contrasena: String,
    modifier: Modifier = Modifier
) {
    if (contrasena.isEmpty()) return

    val fortaleza = remember(contrasena) {
        val tieneLargo = contrasena.length >= 6
        val tieneLetraYNumero = contrasena.any { it.isLetter() } && contrasena.any { it.isDigit() }
        val tieneLargoExcelente = contrasena.length >= 8
        val tieneMayuscula = contrasena.any { it.isUpperCase() }

        when {
            tieneLargoExcelente && tieneLetraYNumero && tieneMayuscula -> 3 // Fuerte
            tieneLargo && tieneLetraYNumero -> 2 // Aceptable
            else -> 1 // Débil
        }
    }

    val (etiqueta, color, proporcion) = when (fortaleza) {
        3 -> Triple("Fortaleza: Fuerte", ColoresSemanticos.Exito, 1.0f)
        2 -> Triple("Fortaleza: Aceptable", ColoresSemanticos.Advertencia, 0.66f)
        else -> Triple("Fortaleza: Débil", ColoresSemanticos.Error, 0.33f)
    }

    val colorAnimado by animateColorAsState(
        targetValue = color,
        animationSpec = tween(300),
        label = "ColorFortaleza"
    )

    val proporcionAnimada by animateFloatAsState(
        targetValue = proporcion,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "ProporcionFortaleza"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 78.dp, top = 4.dp, end = 14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = etiqueta,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = colorAnimado
            )
        }
        Spacer(Modifier.height(3.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(Color(0xFFE5E7EB))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(proporcionAnimada)
                    .clip(RoundedCornerShape(2.dp))
                    .background(colorAnimado)
            )
        }
    }
}

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

    // Animaciones de entrada escalonada
    val iconoScale = remember { Animatable(0.5f) }
    val formAlpha = remember { Animatable(0f) }

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
        launch { formAlpha.animateTo(1f, tween(400)) }
    }

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
                imageVector = Icons.Default.PersonAdd,
                contentDescription = null,
                tint = AzulPrimario,
                modifier = Modifier.size(36.dp)
            )
        }

        Text(
            text = "Crear cuenta",
            fontSize = 26.sp,
            fontWeight = FontWeight.ExtraBold,
            color = AzulOscuro
        )
        Text(
            text = "Regístrate para agendar tus citas",
            fontSize = 15.sp,
            color = GrisTexto
        )
        Spacer(Modifier.height(8.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .graphicsLayer { alpha = formAlpha.value },
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
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
            Column {
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
                IndicadorFortalezaContrasena(contrasena = contrasena)
            }

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
                    modifier = Modifier
                        .efectoPresion { onTerminos() }
                        .clickable { onTerminos() }
                )
            }

            // Registrarme solo se habilita con la casilla marcada
            BotonAzul(
                texto = "Registrarme",
                habilitado = aceptaTerminos,
                onClick = { registrar() },
                modifier = Modifier.efectoPresion(habilitado = aceptaTerminos) { registrar() }
            )
        }

        Spacer(Modifier.height(8.dp))

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
                modifier = Modifier
                    .efectoPresion { onIrLogin() }
                    .clickable { onIrLogin() }
            )
        }
    }
}
