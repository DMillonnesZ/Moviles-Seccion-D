package com.millones.campusgps.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.PersonPinCircle
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.millones.campusgps.ui.componentes.BotonesVentana
import com.millones.campusgps.ui.componentes.CampusColores
import com.millones.campusgps.ui.componentes.DegradadosCampus
import com.millones.campusgps.ui.componentes.DivisorSuave
import com.millones.campusgps.ui.componentes.EntradaSuave
import com.millones.campusgps.ui.componentes.FilaOpcion
import com.millones.campusgps.ui.componentes.FondoPildora
import com.millones.campusgps.ui.componentes.FuenteCampus
import com.millones.campusgps.ui.componentes.InsetsCampus
import com.millones.campusgps.ui.componentes.LocalRellenoBarra
import com.millones.campusgps.ui.componentes.TarjetaPestana
import com.millones.campusgps.ui.componentes.VentanaCampus
import com.millones.campusgps.ui.componentes.desvanecerBordes

// Perfil: cabecera azul, avatar con la inicial del correo, datos y cerrar sesión.
// La foto real (mejora 8) reemplazará la inicial cuando exista Storage.
@Composable
fun PantallaPerfil(
    correo: String?,
    nombre: String?,
    onCerrarSesion: () -> Unit
) {
    var confirmarSalida by rememberSaveable { mutableStateOf(false) }
    val relleno = LocalRellenoBarra.current
    // Nombre del registro; las cuentas antiguas sin nombre usan la parte del correo antes de @
    val nombreVisible = nombre?.takeIf { it.isNotBlank() }
        ?: correo?.substringBefore("@")
        ?: "Invitado"

    Scaffold(
        containerColor = CampusColores.Superficie,
        contentWindowInsets = InsetsCampus
    ) { espacio ->
    val arriba = espacio.calculateTopPadding()
    val abajo = if (relleno > 0.dp) relleno else espacio.calculateBottomPadding()

    Box(modifier = Modifier.fillMaxSize()) {
        // La cabecera azul llega hasta el borde (detrás de la cámara)
        CabeceraCurva(alto = arriba + 210.dp)

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(18.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(top = arriba)
                .desvanecerBordes(arriba = 16.dp)
                .verticalScroll(rememberScrollState())
                .padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = abajo + 20.dp)
        ) {
            EntradaSuave {
                Text(
                    text = "Mi perfil",
                    color = Color.White,
                    fontFamily = FuenteCampus,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 24.sp
                )
            }

            EntradaSuave(retrasoMs = 100L) {
                Avatar(inicial = nombreVisible.firstOrNull()?.uppercaseChar() ?: '?')
            }

            EntradaSuave(retrasoMs = 180L) {
                Text(
                    text = nombreVisible,
                    color = CampusColores.TextoOscuro,
                    fontFamily = FuenteCampus,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 22.sp,
                    textAlign = TextAlign.Center
                )
            }

            EntradaSuave(retrasoMs = 260L) {
                TarjetaPestana(titulo = "Mi cuenta", icono = Icons.Rounded.Person) {
                    FilaOpcion(
                        icono = Icons.Rounded.Email,
                        titulo = "Correo",
                        subtitulo = correo ?: "Sin sesión",
                        color = CampusColores.AzulPrimario
                    )
                    DivisorSuave()
                    FilaOpcion(
                        icono = Icons.Rounded.PersonPinCircle,
                        titulo = "Compartir mi ubicación",
                        subtitulo = "Próximamente",
                        color = CampusColores.Exito
                    )
                }
            }

            EntradaSuave(retrasoMs = 340L) {
                TarjetaPestana(titulo = "Sesión", icono = Icons.Rounded.Lock) {
                    FilaOpcion(
                        icono = Icons.AutoMirrored.Rounded.Logout,
                        titulo = "Cerrar sesión",
                        subtitulo = "Volverás a la pantalla de inicio",
                        color = CampusColores.Error,
                        peligro = true,
                        onClick = { confirmarSalida = true }
                    )
                }
            }
        }
    }
    }

    if (confirmarSalida) {
        VentanaCampus(
            titulo = "¿Cerrar sesión?",
            icono = Icons.AutoMirrored.Rounded.Logout,
            colorIcono = CampusColores.Error,
            onCerrar = { confirmarSalida = false }
        ) {
            Text(
                text = "Tendrás que ingresar tu correo y contraseña otra vez.",
                color = CampusColores.TextoSecundario,
                fontFamily = FuenteCampus,
                fontSize = 15.sp,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(18.dp))
            BotonesVentana(
                textoConfirmar = "Cerrar sesión",
                fondoConfirmar = FondoPildora.Peligro,
                onCancelar = { confirmarSalida = false },
                onConfirmar = {
                    confirmarSalida = false
                    onCerrarSesion()
                }
            )
        }
    }
}

// Círculo con degradado, borde blanco y la inicial del usuario
@Composable
private fun Avatar(inicial: Char) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(112.dp)
            .shadow(12.dp, CircleShape, ambientColor = CampusColores.AzulProfundo, spotColor = CampusColores.AzulProfundo)
            .clip(CircleShape)
            .background(DegradadosCampus.Primario)
            .border(4.dp, Color.White, CircleShape)
    ) {
        Text(
            text = inicial.toString(),
            color = Color.White,
            fontFamily = FuenteCampus,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 46.sp
        )
    }
}

// Fondo azul noche arriba con las esquinas inferiores redondeadas (Perfil, Eventos, Chat)
@Composable
fun CabeceraCurva(alto: Dp) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(alto)
            .clip(RoundedCornerShape(bottomStart = 36.dp, bottomEnd = 36.dp))
            .background(DegradadosCampus.Noche)
    )
}
