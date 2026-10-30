package com.tecsup.huellitasvet.ui.dueno

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tecsup.huellitasvet.ui.components.*
import com.tecsup.huellitasvet.ui.theme.AvisoFondo
import com.tecsup.huellitasvet.ui.theme.BordeSuave
import com.tecsup.huellitasvet.ui.theme.EstadoPendiente
import com.tecsup.huellitasvet.ui.theme.VetContenedor
import com.tecsup.huellitasvet.ui.theme.VetPrimario
import com.tecsup.huellitasvet.viewmodel.InicioViewModel
import com.tecsup.huellitasvet.viewmodel.MisCitasViewModel
import com.tecsup.huellitasvet.viewmodel.SesionViewModel

@Composable
fun PerfilScreen(
    sesionVm: SesionViewModel,
    inicioVm: InicioViewModel,
    citasVm: MisCitasViewModel,
    onCambiarPerfil: () -> Unit,
    onCerrarSesion: () -> Unit,
    onAtras: (() -> Unit)? = null
) {
    val cs = MaterialTheme.colorScheme
    val usuario by sesionVm.usuarioActual.collectAsState()
    val oscuro by sesionVm.modoOscuro.collectAsState()
    val notificaciones by sesionVm.notificaciones.collectAsState()
    val mascotas by inicioVm.mascotas.collectAsState()
    val proximas by citasVm.proximas.collectAsState()
    val atenciones by citasVm.atenciones.collectAsState()

    Scaffold(
        containerColor = cs.background,
        contentWindowInsets = WindowInsets(0),
        topBar = { AppTopBar("Perfil", onAtras = onAtras) }
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            TarjetaUsuario(
                nombre = usuario?.nombre ?: "",
                correo = usuario?.correo ?: "",
                chip = "Dueño"
            )

            // Contadores
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Contador(mascotas.size, "Mascotas", cs.primaryContainer, cs.primary, Modifier.weight(1f))
                Contador(proximas.size, "Próximas", AvisoFondo, EstadoPendiente, Modifier.weight(1f))
                Contador(atenciones, "Atenciones", VetContenedor, VetPrimario, Modifier.weight(1f))
            }

            Spacer(Modifier.height(14.dp))
            TarjetaCambiarPerfil("Modo práctica: ir a vista Veterinario", onCambiarPerfil)

            Text(
                "Preferencias",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = 18.dp, bottom = 10.dp)
            )
            GrupoPreferencias {
                FilaPreferencia("Modo oscuro", "Cambia el tema de la app", oscuro, sesionVm::cambiarModoOscuro)
                HorizontalDivider(color = BordeSuave)
                FilaPreferencia("Recordatorios", "Avisar citas y vacunas", notificaciones, sesionVm::cambiarNotificaciones)
            }

            Spacer(Modifier.height(14.dp))
            NotaDataStore()

            Spacer(Modifier.height(20.dp))
            BotonCerrarSesion(onCerrarSesion)
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun Contador(valor: Int, etiqueta: String, fondo: Color, color: Color, modifier: Modifier) {
    Column(
        modifier
            .height(84.dp)
            .background(fondo, RoundedCornerShape(16.dp)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("$valor", color = color, fontSize = 26.sp, fontWeight = FontWeight.Bold)
        Text(etiqueta, color = color, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.SemiBold)
    }
}