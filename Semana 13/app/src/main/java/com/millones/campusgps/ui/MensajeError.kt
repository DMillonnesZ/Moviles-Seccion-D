package com.millones.campusgps.ui

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.millones.campusgps.model.TipoError
import com.millones.campusgps.model.UbicacionUi

private val AzulCampus = Color(0xFF1B6DA8)
private val FondoError = Color(0xFFFBE3E3)
private val TextoError = Color(0xFF9B1C1C)

@Composable
fun MensajeError(
    error: UbicacionUi.Error,
    onReintentar: () -> Unit,
    modifier: Modifier = Modifier
) {
    val contexto = LocalContext.current

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = error.mensaje,
            color = TextoError,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .background(FondoError, RoundedCornerShape(10.dp))
                .padding(16.dp)
        )

        when (error.tipo) {
            TipoError.SIN_PERMISO -> AccionBoton("Abrir ajustes") {
                contexto.startActivity(
                    Intent(
                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.fromParts("package", contexto.packageName, null)
                    )
                )
            }
            TipoError.GPS_APAGADO -> AccionBoton("Reintentar", onReintentar)
            TipoError.SIN_PLAY_SERVICES, TipoError.OTRO -> Unit
        }
    }
}

@Composable
private fun AccionBoton(texto: String, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        border = BorderStroke(1.dp, AzulCampus),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = AzulCampus)
    ) {
        Text(texto, fontWeight = FontWeight.Bold)
    }
}