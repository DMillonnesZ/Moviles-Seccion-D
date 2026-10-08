package com.saludplus.citas.ui.screens.auth

import android.provider.Settings
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.saludplus.citas.R
import com.saludplus.citas.ui.components.BotonAzul
import com.saludplus.citas.ui.components.EnlaceTexto
import com.saludplus.citas.ui.theme.AzulOscuro
import com.saludplus.citas.ui.theme.GrisTexto
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SplashScreen(
    onComenzar: () -> Unit,
    onYaTengoCuenta: () -> Unit
) {
    val context = LocalContext.current
    val animacionesReducidas = remember {
        try {
            Settings.Global.getFloat(context.contentResolver, Settings.Global.ANIMATOR_DURATION_SCALE, 1f) == 0f
        } catch (_: Exception) {
            false
        }
    }

    var animacionEjecutada by rememberSaveable { mutableStateOf(false) }

    val logoScale = remember { Animatable(if (animacionesReducidas || animacionEjecutada) 1f else 0.7f) }
    val logoAlpha = remember { Animatable(if (animacionesReducidas || animacionEjecutada) 1f else 0f) }
    val textosAlpha = remember { Animatable(if (animacionesReducidas || animacionEjecutada) 1f else 0f) }
    val medicoOffsetY = remember { Animatable(if (animacionesReducidas || animacionEjecutada) 0f else 80f) }
    val medicoAlpha = remember { Animatable(if (animacionesReducidas || animacionEjecutada) 1f else 0f) }
    val botonesAlpha = remember { Animatable(if (animacionesReducidas || animacionEjecutada) 1f else 0f) }

    LaunchedEffect(Unit) {
        if (!animacionesReducidas && !animacionEjecutada) {
            launch {
                logoScale.animateTo(
                    1f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                )
            }
            launch {
                logoAlpha.animateTo(1f, animationSpec = tween(400))
            }
            delay(150)
            launch {
                textosAlpha.animateTo(1f, animationSpec = tween(350))
            }
            delay(150)
            launch {
                medicoOffsetY.animateTo(
                    0f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioNoBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                )
            }
            launch {
                medicoAlpha.animateTo(1f, animationSpec = tween(400))
            }
            delay(150)
            launch {
                botonesAlpha.animateTo(1f, animationSpec = tween(350))
            }
            animacionEjecutada = true
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "pulsoComenzar")
    val pulsoEscala by if (!animacionesReducidas) {
        infiniteTransition.animateFloat(
            initialValue = 1f,
            targetValue = 1.025f,
            animationSpec = infiniteRepeatable(
                animation = tween(1200),
                repeatMode = RepeatMode.Reverse
            ),
            label = "escalaPulso"
        )
    } else {
        remember { mutableStateOf(1f) }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Formas suaves difuminadas de fondo
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                color = Color(0xFFE8F0FE).copy(alpha = 0.6f),
                radius = size.width * 0.45f,
                center = Offset(size.width * 0.85f, size.height * 0.1f)
            )
            drawCircle(
                color = Color(0xFFCCFBF1).copy(alpha = 0.4f),
                radius = size.width * 0.35f,
                center = Offset(size.width * 0.1f, size.height * 0.75f)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Logo, nombre y lema
            Column(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .graphicsLayer {
                        scaleX = logoScale.value
                        scaleY = logoScale.value
                        alpha = logoAlpha.value
                    },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo_saludplus),
                    contentDescription = "Logo SaludPlus",
                    modifier = Modifier.height(110.dp)
                )
                Text(
                    text = "Clínica",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AzulOscuro
                )
                Text(
                    text = "SaludPlus",
                    fontSize = 34.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = AzulOscuro
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Tu salud, nuestra prioridad",
                    fontSize = 15.sp,
                    color = GrisTexto,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.alpha(textosAlpha.value)
                )
            }

            Spacer(Modifier.weight(1f))

            // Ilustración del médico a todo el ancho que sube
            Image(
                painter = painterResource(id = R.drawable.medico_splash),
                contentDescription = "Médico de la clínica",
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .fillMaxWidth()
                    .offset { IntOffset(0, medicoOffsetY.value.dp.roundToPx()) }
                    .alpha(medicoAlpha.value)
            )

            // Botones pegados a la parte inferior de la imagen
            Column(
                modifier = Modifier
                    .padding(horizontal = 24.dp, vertical = 8.dp)
                    .alpha(botonesAlpha.value),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                BotonAzul(
                    texto = "Comenzar",
                    onClick = onComenzar,
                    modifier = Modifier.graphicsLayer {
                        scaleX = pulsoEscala
                        scaleY = pulsoEscala
                    }
                )
                Spacer(Modifier.height(4.dp))
                EnlaceTexto(texto = "Ya tengo una cuenta", onClick = onYaTengoCuenta)
            }

            Spacer(Modifier.weight(1f))
        }
    }
}
