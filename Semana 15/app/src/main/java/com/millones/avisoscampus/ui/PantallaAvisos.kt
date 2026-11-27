package com.millones.avisoscampus.ui

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.millones.avisoscampus.model.PermisoUi
import com.millones.avisoscampus.notificacion.Notificador
import com.millones.avisoscampus.ui.theme.Azul
import com.millones.avisoscampus.ui.theme.BordeTarjeta
import com.millones.avisoscampus.ui.theme.GrisFondo
import com.millones.avisoscampus.ui.theme.GrisTexto
import com.millones.avisoscampus.ui.theme.RojoFondo
import com.millones.avisoscampus.ui.theme.RojoTexto
import com.millones.avisoscampus.ui.theme.VerdeFondo
import com.millones.avisoscampus.ui.theme.VerdeTexto
import com.millones.avisoscampus.viewmodel.AvisosViewModel

@Composable
fun PantallaAvisos(vm: AvisosViewModel, modifier: Modifier = Modifier) {
    val contexto = LocalContext.current
    val permiso by vm.permiso.collectAsState()

    // El ViewModel emite el evento y la pantalla (que sí tiene Context) muestra la notificación
    LaunchedEffect(Unit) {
        vm.eventos.collect { aviso ->
            val mostrado = Notificador.mostrar(contexto, aviso)
            // TODO Paso 8: si no se mostró, avisar al ViewModel
        }
    }

    // Paso 2: lanzador del cuadro de permiso que dibuja Android
    val lanzador = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { concedido ->
        vm.permisoRespondido(concedido)
    }

    // Paso 2: al iniciar, si el permiso ya estaba concedido, se lo decimos al ViewModel
    LaunchedEffect(Unit) {
        if (Notificador.tienePermiso(contexto)) {
            vm.permisoRespondido(true)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TarjetaAviso("Permiso de notificaciones") {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Estado:", style = MaterialTheme.typography.bodyLarge)
                EtiquetaPermiso(permiso)
            }
            Button(
                onClick = {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        lanzador.launch(Manifest.permission.POST_NOTIFICATIONS)
                    } else {
                        vm.permisoRespondido(true) // antes de API 33 no hay cuadro
                    }
                }
            ) {
                Text("Permitir notificaciones", fontWeight = FontWeight.Bold)
            }
            // TODO Paso 8: recuadro de error y botón "Abrir ajustes"
        }

        TarjetaAviso("Avisos") {
            // Paso 3: el botón solo avisa al ViewModel; él decide qué aviso crear
            Button(onClick = { vm.avisarAhora() }) {
                Text("Mostrar aviso", fontWeight = FontWeight.Bold)
            }
        }

        TarjetaAviso("Recordatorio") {
            // TODO Paso 5: "Recordar en 10 s", "Cancelar" y el texto "Faltan N s"
        }

        TarjetaAviso("Receptor de anuncios") {
            // TODO Paso 6: Switch "Escuchar el modo avión"
        }

        // TODO Paso 7: reemplazar esta tarjeta completa por HistorialAvisos(avisos),
        // que dibuja su propia tarjeta usando TarjetaAviso("Historial")
        TarjetaAviso("Historial") { }
    }
}

/** Etiqueta de color del estado: Sin pedir (gris), Permitido (verde) o Denegado (rojo). */
@Composable
fun EtiquetaPermiso(permiso: PermisoUi) {
    val (texto, fondo, colorTexto) = when (permiso) {
        PermisoUi.SinPedir -> Triple("Sin pedir", GrisFondo, GrisTexto)
        PermisoUi.Concedido -> Triple("Permitido", VerdeFondo, VerdeTexto)
        PermisoUi.Denegado -> Triple("Denegado", RojoFondo, RojoTexto)
    }
    Text(
        texto,
        modifier = Modifier
            .background(fondo, RoundedCornerShape(50))
            .padding(horizontal = 10.dp, vertical = 2.dp),
        color = colorTexto,
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.Bold
    )
}

/** Tarjeta blanca con borde suave; título azul a la izquierda y contenido centrado. */
@Composable
fun TarjetaAviso(
    titulo: String,
    contenido: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, BordeTarjeta)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                titulo,
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Azul
            )
            contenido()
        }
    }
}
