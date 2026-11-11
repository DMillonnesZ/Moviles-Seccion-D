package com.tecsup.pokemon.ui.perfil

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tecsup.pokemon.R
import com.tecsup.pokemon.domain.model.Rol
import com.tecsup.pokemon.domain.model.Sesion
import com.tecsup.pokemon.ui.components.BotonMadera
import com.tecsup.pokemon.ui.components.CafeMadera
import com.tecsup.pokemon.ui.components.FondoInicio
import com.tecsup.pokemon.ui.components.FuenteMadera
import com.tecsup.pokemon.ui.components.LocalRellenoBarra
import com.tecsup.pokemon.ui.components.Pokebola
import com.tecsup.pokemon.ui.components.TituloTabla

// Perfil del entrenador: datos de la sesión, Panel Admin (solo admin) y Cerrar sesión
@Composable
fun PerfilScreen(
    sesion: Sesion?,
    onAdminClick: () -> Unit,
    onCerrarSesion: () -> Unit
) {
    val esAdmin = sesion?.rol == Rol.ADMIN

    FondoInicio {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(start = 32.dp, end = 32.dp, top = 16.dp, bottom = 16.dp + LocalRellenoBarra.current)
        ) {
            TituloTabla("Perfil")

            Spacer(Modifier.height(24.dp))

            Pokebola(apertura = 0f, brillo = 0f, modifier = Modifier.size(96.dp))

            Spacer(Modifier.height(20.dp))

            DatoPerfil(etiqueta = "Correo", valor = sesion?.correo ?: "-")
            Spacer(Modifier.height(10.dp))
            DatoPerfil(etiqueta = "Rol", valor = if (esAdmin) "Administrador" else "Entrenador")

            Spacer(Modifier.height(28.dp))

            if (esAdmin) {
                BotonMadera(texto = "Panel Admin", onClick = onAdminClick)
                Spacer(Modifier.height(12.dp))
            }
            BotonMadera(texto = "Cerrar sesión", onClick = onCerrarSesion)
        }
    }
}

@Composable
private fun DatoPerfil(etiqueta: String, valor: String) {
    Box(
        contentAlignment = Alignment.CenterStart,
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp)
    ) {
        Image(
            painter = painterResource(R.drawable.tabla_campo),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.matchParentSize()
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 20.dp)
        ) {
            Text(
                text = "$etiqueta:",
                color = CafeMadera.copy(alpha = 0.7f),
                fontFamily = FuenteMadera,
                fontSize = 15.sp
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = valor,
                color = CafeMadera,
                fontFamily = FuenteMadera,
                fontSize = 16.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}