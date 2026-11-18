package com.millones.campusgps.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.millones.campusgps.ui.componentes.CampusColores
import com.millones.campusgps.ui.componentes.EntradaSuave
import com.millones.campusgps.ui.componentes.FuenteCampus
import com.millones.campusgps.ui.componentes.InsetsCampus
import com.millones.campusgps.ui.componentes.LocalRellenoBarra
import com.millones.campusgps.ui.componentes.VacioView

// Maqueta de las pestañas que aún no existen (Eventos, Chat).
// Se reemplaza por la pantalla real cuando se implemente cada mejora.
@Composable
fun PantallaProximamente(
    titulo: String,
    subtitulo: String,
    icono: ImageVector,
    mensaje: String
) {
    val relleno = LocalRellenoBarra.current

    Scaffold(
        containerColor = CampusColores.Superficie,
        contentWindowInsets = InsetsCampus
    ) { espacio ->
        val arriba = espacio.calculateTopPadding()
        val abajo = if (relleno > 0.dp) relleno else espacio.calculateBottomPadding()

        Box(modifier = Modifier.fillMaxSize()) {
            // La cabecera azul llega hasta el borde (detrás de la cámara)
            CabeceraCurva(alto = arriba + 180.dp)

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 20.dp, end = 20.dp, top = arriba + 20.dp, bottom = abajo + 20.dp)
            ) {
                EntradaSuave {
                    Column {
                        Text(
                            text = titulo,
                            color = Color.White,
                            fontFamily = FuenteCampus,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 24.sp
                        )
                        Text(
                            text = subtitulo,
                            color = Color.White.copy(alpha = 0.75f),
                            fontFamily = FuenteCampus,
                            fontSize = 14.sp
                        )
                    }
                }

                Spacer(Modifier.height(48.dp))

                EntradaSuave(retrasoMs = 150L, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                    VacioView(
                        titulo = "Próximamente",
                        mensaje = mensaje,
                        icono = icono
                    )
                }
            }
        }
    }
}
