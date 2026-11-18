package com.millones.campusgps.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material.icons.rounded.GpsOff
import androidx.compose.material.icons.rounded.LocationOff
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.millones.campusgps.model.TipoError
import com.millones.campusgps.model.UbicacionUi
import com.millones.campusgps.ui.componentes.EstadoError

// Tarjeta de error según el tipo: permiso denegado, GPS apagado o sin Play Services
@Composable
fun MensajeError(
    error: UbicacionUi.Error,
    onReintentar: () -> Unit,
    modifier: Modifier = Modifier
) {
    val contexto = LocalContext.current

    val (icono, titulo) = when (error.tipo) {
        TipoError.SIN_PERMISO -> Icons.Rounded.LocationOff to "Permiso denegado"
        TipoError.GPS_APAGADO -> Icons.Rounded.GpsOff to "GPS apagado"
        TipoError.SIN_PLAY_SERVICES -> Icons.Rounded.ErrorOutline to "Sin Google Play Services"
        TipoError.OTRO -> Icons.Rounded.ErrorOutline to "Algo salió mal"
    }

    // Sin Play Services no hay nada que reintentar
    val textoAccion: String? = when (error.tipo) {
        TipoError.SIN_PERMISO -> "Abrir ajustes"
        TipoError.GPS_APAGADO, TipoError.OTRO -> "Reintentar"
        TipoError.SIN_PLAY_SERVICES -> null
    }

    EstadoError(
        titulo = titulo,
        mensaje = error.mensaje,
        icono = icono,
        textoAccion = textoAccion,
        onAccion = {
            when (error.tipo) {
                TipoError.SIN_PERMISO -> abrirAjustes(contexto)
                else -> onReintentar()
            }
        },
        modifier = modifier
    )
}

// Abre la pantalla de ajustes de la app para que el usuario conceda el permiso
private fun abrirAjustes(contexto: Context) {
    contexto.startActivity(
        Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", contexto.packageName, null)
        )
    )
}
