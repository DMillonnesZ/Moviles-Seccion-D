package com.millones.campusgps.ui.componentes

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.millones.campusgps.R

// Fondo de las pantallas de acceso: imagen de diseño con velo azul profundo
// y encima la ruta viva, que llega al pin del logo
@Composable
fun FondoAuth(content: @Composable () -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.login_fondo),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            CampusColores.AzulProfundo.copy(alpha = 0.90f),
                            CampusColores.AzulProfundo.copy(alpha = 0.72f),
                            CampusColores.AzulProfundo.copy(alpha = 0.90f)
                        )
                    )
                )
        )
        ConRutaViva {
            content()
        }
    }
}
