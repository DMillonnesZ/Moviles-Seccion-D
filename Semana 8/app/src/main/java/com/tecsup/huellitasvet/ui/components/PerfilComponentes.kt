package com.tecsup.huellitasvet.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tecsup.huellitasvet.ui.theme.BordeSuave
import com.tecsup.huellitasvet.ui.theme.GrisApagado

/** Avatar + nombre + correo + chip + detalle opcional */
@Composable
fun TarjetaUsuario(
    nombre: String,
    correo: String,
    chip: String,
    detalle: String? = null
) {
    val cs = MaterialTheme.colorScheme
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = cs.surface),
        border = BorderStroke(1.dp, BordeSuave),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.size(64.dp).background(cs.primaryContainer, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Outlined.Person, null, tint = cs.primary, modifier = Modifier.size(32.dp))
            }
            Spacer(Modifier.width(14.dp))
            Column {
                Text(nombre, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text(correo, style = MaterialTheme.typography.bodySmall, color = cs.onSurfaceVariant)
                if (detalle != null) {
                    Text(detalle, style = MaterialTheme.typography.labelSmall, color = cs.onSurfaceVariant)
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    chip,
                    color = cs.primary,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .background(cs.primaryContainer, RoundedCornerShape(50))
                        .padding(horizontal = 10.dp, vertical = 2.dp)
                )
            }
        }
    }
}

/** Tarjeta de color primario "Cambiar de perfil" */
@Composable
fun TarjetaCambiarPerfil(subtitulo: String, onClick: () -> Unit) {
    val cs = MaterialTheme.colorScheme
    Row(
        Modifier
            .fillMaxWidth()
            .background(cs.primary, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Default.SwapHoriz, null, tint = cs.onPrimary)
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f)) {
            Text("Cambiar de perfil", color = cs.onPrimary, fontWeight = FontWeight.Bold)
            Text(subtitulo, color = cs.onPrimary.copy(alpha = 0.85f), style = MaterialTheme.typography.labelSmall)
        }
        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = cs.onPrimary)
    }
}

/** Fila con título, descripción y switch */
@Composable
fun FilaPreferencia(titulo: String, descripcion: String, valor: Boolean, onCambio: (Boolean) -> Unit) {
    val cs = MaterialTheme.colorScheme
    Row(
        Modifier.fillMaxWidth().padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text(titulo, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
            Text(descripcion, style = MaterialTheme.typography.labelSmall, color = cs.onSurfaceVariant)
        }
        Switch(
            checked = valor,
            onCheckedChange = onCambio,
            thumbContent = { },
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = cs.primary,
                checkedBorderColor = Color.Transparent,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = GrisApagado,
                uncheckedBorderColor = Color.Transparent
            )
        )
    }
}

/** Tarjeta blanca que agrupa las preferencias con divisores */
@Composable
fun GrupoPreferencias(contenido: @Composable ColumnScope.() -> Unit) {
    val cs = MaterialTheme.colorScheme
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cs.surface),
        border = BorderStroke(1.dp, BordeSuave),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(horizontal = 16.dp, vertical = 4.dp), content = contenido)
    }
}

/** "Se guardan en el dispositivo con DataStore" */
@Composable
fun NotaDataStore() {
    val cs = MaterialTheme.colorScheme
    Text(
        buildAnnotatedString {
            append("Se guardan en el dispositivo\n")
            withStyle(SpanStyle(color = cs.primary, fontWeight = FontWeight.Bold)) { append("con DataStore") }
        },
        style = MaterialTheme.typography.labelSmall,
        color = cs.onSurfaceVariant,
        modifier = Modifier
            .fillMaxWidth()
            .background(cs.primaryContainer.copy(alpha = 0.45f), RoundedCornerShape(14.dp))
            .padding(14.dp)
    )
}

/** Botón rojo con borde "Cerrar sesión" */
@Composable
fun BotonCerrarSesion(onClick: () -> Unit) {
    val cs = MaterialTheme.colorScheme
    OutlinedButton(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.5.dp, cs.error),
        modifier = Modifier.fillMaxWidth().height(52.dp)
    ) { Text("Cerrar sesión", color = cs.error, fontWeight = FontWeight.SemiBold) }
}
