package com.millones.avisoscampus.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.millones.avisoscampus.notificacion.Notificador
import com.millones.avisoscampus.ui.theme.Azul
import com.millones.avisoscampus.ui.theme.BordeTarjeta
import com.millones.avisoscampus.viewmodel.AvisosViewModel

@Composable
fun PantallaAvisos(vm: AvisosViewModel, modifier: Modifier = Modifier) {
    val contexto = LocalContext.current

    // El ViewModel emite el evento y la pantalla (que sí tiene Context) muestra la notificación
    LaunchedEffect(Unit) {
        vm.eventos.collect { aviso ->
            val mostrado = Notificador.mostrar(contexto, aviso)
            // TODO Paso 8: si no se mostró, avisar al ViewModel
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
            // TODO Paso 2: fila "Estado:" + etiqueta y botón "Permitir notificaciones"
            // TODO Paso 8: recuadro de error y botón "Abrir ajustes"
        }

        TarjetaAviso("Avisos") {
            // TODO Paso 3: botón "Mostrar aviso" -> vm.avisarAhora()
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
