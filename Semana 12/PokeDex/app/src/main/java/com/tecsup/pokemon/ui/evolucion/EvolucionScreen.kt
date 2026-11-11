package com.tecsup.pokemon.ui.evolucion

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tecsup.pokemon.R
import com.tecsup.pokemon.ui.components.CafeMadera
import com.tecsup.pokemon.ui.components.FondoInicio
import com.tecsup.pokemon.ui.components.FuenteMadera
import com.tecsup.pokemon.ui.components.TituloTabla

// Pantalla base de la pestaña Evolución (las cadenas se agregan después)
@Composable
fun EvolucionScreen() {
    FondoInicio {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(top = 16.dp)
        ) {
            TituloTabla("Evolución")

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .width(260.dp)
                        .height(54.dp)
                ) {
                    Image(
                        painter = painterResource(R.drawable.tabla_enlace),
                        contentDescription = null,
                        contentScale = ContentScale.FillBounds,
                        modifier = Modifier.matchParentSize()
                    )
                    Text(
                        text = "Próximamente",
                        color = CafeMadera,
                        fontFamily = FuenteMadera,
                        fontSize = 18.sp
                    )
                }
            }
        }
    }
}