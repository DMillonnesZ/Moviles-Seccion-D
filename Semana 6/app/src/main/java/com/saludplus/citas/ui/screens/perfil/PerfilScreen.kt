package com.saludplus.citas.ui.screens.perfil

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.saludplus.citas.data.repository.Repositorio
import com.saludplus.citas.navigation.Rutas
import com.saludplus.citas.ui.components.BarraNavegacion
import com.saludplus.citas.ui.components.BarraSuperior
import com.saludplus.citas.ui.components.FilaDetalle
import com.saludplus.citas.ui.components.TarjetaSuave
import com.saludplus.citas.ui.theme.AzulClaro
import com.saludplus.citas.ui.theme.AzulOscuro
import com.saludplus.citas.ui.theme.AzulPrimario
import com.saludplus.citas.ui.theme.GrisTexto
import com.saludplus.citas.ui.theme.RojoError

@Composable
fun PerfilScreen(
    onNavegar: (String) -> Unit,
    onCerrarSesion: () -> Unit
) {
    // Datos del usuario en sesión
    val usuario = Repositorio.usuarioActual
    val totalCitas = Repositorio.citasDelUsuario().size
    val inicial = usuario?.nombre?.trim()?.firstOrNull()?.uppercase() ?: "?"

    Scaffold(
        containerColor = Color.White,
        topBar = { BarraSuperior(titulo = "Mis datos") },
        bottomBar = { BarraNavegacion(rutaActual = Rutas.PERFIL, onNavegar = onNavegar) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Encabezado: inicial, nombre y correo
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape)
                    .background(AzulClaro),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = inicial,
                    fontSize = 40.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = AzulPrimario
                )
            }
            Spacer(Modifier.height(12.dp))
            Text(
                text = usuario?.nombre ?: "",
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                color = AzulOscuro
            )
            Text(
                text = usuario?.correo ?: "",
                fontSize = 14.sp,
                color = GrisTexto
            )

            Spacer(Modifier.height(24.dp))

            // Datos personales
            TarjetaSuave(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    FilaDetalle(Icons.Default.Person, "Nombre completo", usuario?.nombre ?: "")
                    FilaDetalle(Icons.Default.Phone, "Teléfono", usuario?.telefono ?: "")
                    FilaDetalle(Icons.Default.Email, "Correo electrónico", usuario?.correo ?: "")
                    FilaDetalle(Icons.Default.DateRange, "Citas agendadas", totalCitas.toString())
                }
            }

            Spacer(Modifier.height(28.dp))

            // Cerrar sesión
            OutlinedButton(
                onClick = {
                    Repositorio.cerrarSesion()
                    onCerrarSesion()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.5.dp, RojoError),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = RojoError)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Logout,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text("Cerrar sesión", fontWeight = FontWeight.SemiBold)
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}