package com.tecsup.huellitasvet.ui.veterinario

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tecsup.huellitasvet.model.Rol
import com.tecsup.huellitasvet.ui.components.*
import com.tecsup.huellitasvet.ui.theme.BordeSuave
import com.tecsup.huellitasvet.viewmodel.SesionViewModel

@Composable
fun AjustesScreen(
    sesionVm: SesionViewModel,
    onCambiarPerfil: () -> Unit,
    onCerrarSesion: () -> Unit,
    onAtras: (() -> Unit)? = null
) {
    val cs = MaterialTheme.colorScheme
    val usuario by sesionVm.usuarioActual.collectAsState()
    val admin by sesionVm.adminActual.collectAsState()
    val rolActivo by sesionVm.rolActivo.collectAsState()
    val oscuro by sesionVm.modoOscuro.collectAsState()
    val notificaciones by sesionVm.notificaciones.collectAsState()
    val aceptarCitas by sesionVm.aceptarCitas.collectAsState()

    Scaffold(
        containerColor = cs.background,
        contentWindowInsets = WindowInsets(0),
        topBar = { AppTopBar("Ajustes", onAtras = onAtras) }
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            if (rolActivo == Rol.ADMINISTRADOR) {
                val a = admin
                TarjetaUsuario(
                    nombre = if (a != null) "${a.nombre} ${a.apellido}" else "",
                    correo = a?.correo ?: "",
                    chip = "Administrador",
                    detalle = if (a != null) "Código: ${a.codigo}" else null
                )
            } else {
                TarjetaUsuario(
                    nombre = usuario?.nombre ?: "",
                    correo = usuario?.correo ?: "",
                    chip = "Perfil activo"
                )
                Spacer(Modifier.height(14.dp))
                TarjetaCambiarPerfil("Modo práctica: ir a vista Dueño", onCambiarPerfil)
            }

            Text(
                "Preferencias",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = 18.dp, bottom = 10.dp)
            )
            GrupoPreferencias {
                FilaPreferencia("Modo oscuro", "Cambia el tema de la app", oscuro, sesionVm::cambiarModoOscuro)
                HorizontalDivider(color = BordeSuave)
                FilaPreferencia("Notificaciones", "Avisar citas nuevas", notificaciones, sesionVm::cambiarNotificaciones)
                HorizontalDivider(color = BordeSuave)
                FilaPreferencia("Aceptar citas", "Abrir o cerrar la agenda", aceptarCitas, sesionVm::cambiarAceptarCitas)
            }

            Spacer(Modifier.height(14.dp))
            NotaDataStore()

            Spacer(Modifier.height(20.dp))
            BotonCerrarSesion(onCerrarSesion)
            Spacer(Modifier.height(24.dp))
        }
    }
}
