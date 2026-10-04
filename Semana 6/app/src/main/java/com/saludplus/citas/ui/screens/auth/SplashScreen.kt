package com.saludplus.citas.ui.screens.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.saludplus.citas.R
import com.saludplus.citas.ui.components.BotonAzul
import com.saludplus.citas.ui.components.EnlaceTexto
import com.saludplus.citas.ui.theme.AzulOscuro
import com.saludplus.citas.ui.theme.GrisTexto

@Composable
fun SplashScreen(
    onComenzar: () -> Unit,
    onYaTengoCuenta: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .systemBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Logo, nombre y lema
        Column(
            modifier = Modifier.padding(top = 16.dp),
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
                textAlign = TextAlign.Center
            )
        }

        // El espacio sobrante se reparte arriba y abajo del bloque médico + botones
        Spacer(Modifier.weight(1f))

        // Ilustración del médico a todo el ancho
        Image(
            painter = painterResource(id = R.drawable.medico_splash),
            contentDescription = "Médico de la clínica",
            contentScale = ContentScale.FillWidth,
            modifier = Modifier.fillMaxWidth()
        )

        // Botones pegados a la parte inferior de la imagen
        Column(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            BotonAzul(texto = "Comenzar", onClick = onComenzar)
            Spacer(Modifier.height(4.dp))
            EnlaceTexto(texto = "Ya tengo una cuenta", onClick = onYaTengoCuenta)
        }

        Spacer(Modifier.weight(1f))
    }
}