package com.tecsup.huellitasvet.ui.veterinario

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Warning
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
import com.tecsup.huellitasvet.ui.components.graficos.GraficoBarras
import com.tecsup.huellitasvet.ui.components.graficos.GraficoDona
import com.tecsup.huellitasvet.ui.theme.*
import com.tecsup.huellitasvet.viewmodel.PanelViewModel
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun PanelScreen(vm: PanelViewModel, onVerPendientes: () -> Unit) {
    val cs = MaterialTheme.colorScheme
    val citasHoy by vm.citasHoy.collectAsState()
    val pendientes by vm.pendientes.collectAsState()
    val atendidas by vm.atendidas.collectAsState()
    val ingresos by vm.ingresosMes.collectAsState()
    val porEstado by vm.porEstado.collectAsState()
    val porServicio by vm.porServicio.collectAsState()
    val porConfirmar by vm.porConfirmar.collectAsState()

    val fechaTexto = vm.hoy
        .format(DateTimeFormatter.ofPattern("EEE d MMM", Locale("es")))
        .replace(".", "")
        .replaceFirstChar { it.uppercase() }

    Column(
        Modifier
            .fillMaxSize()
            .background(cs.background)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        // ===== Encabezado =====
        Row(verticalAlignment = Alignment.Top) {
            Column(Modifier.weight(1f)) {
                Text("Panel de la clínica", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Text("Hoy · $fechaTexto", style = MaterialTheme.typography.bodyMedium, color = cs.onSurfaceVariant)
            }
            Icon(Icons.Filled.Notifications, "Notificaciones", modifier = Modifier.padding(top = 4.dp))
        }

        Spacer(Modifier.height(18.dp))

        // ===== Contadores =====
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Contador(citasHoy, "Citas hoy", cs.primaryContainer, cs.primary, Modifier.weight(1f))
            Contador(pendientes, "Pendientes", AvisoFondo, EstadoPendiente, Modifier.weight(1f))
            Contador(atendidas, "Atendidas", GrisAtendida, EstadoAtendida, Modifier.weight(1f))
        }

        Spacer(Modifier.height(12.dp))

        // ===== Ingresos del mes =====
        Column(
            Modifier
                .fillMaxWidth()
                .background(cs.primary, RoundedCornerShape(18.dp))
                .padding(18.dp)
        ) {
            Text("Ingresos del mes", color = cs.onPrimary.copy(alpha = 0.85f), style = MaterialTheme.typography.bodySmall)
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    "S/ " + String.format(Locale.US, "%,.2f", ingresos),
                    color = cs.onPrimary, fontSize = 26.sp, fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                Text("$atendidas atenciones", color = cs.onPrimary, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
            }
        }

        // ===== Citas por estado =====
        Seccion("Citas por estado") {
            GraficoDona(porEstado)
        }

        // ===== Citas por servicio =====
        Seccion("Citas por servicio") {
            GraficoBarras(porServicio)
        }

        // ===== Alerta =====
        if (porConfirmar > 0) {
            Spacer(Modifier.height(16.dp))
            Row(
                Modifier
                    .fillMaxWidth()
                    .background(AvisoFondo, RoundedCornerShape(16.dp))
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Filled.Warning, null, tint = AvisoIcono)
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        "$porConfirmar ${if (porConfirmar == 1) "cita" else "citas"} por confirmar",
                        color = AvisoTexto, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium
                    )
                    Text("para hoy y mañana", color = AvisoTexto, style = MaterialTheme.typography.bodySmall)
                }
                Button(
                    onClick = onVerPendientes,
                    colors = ButtonDefaults.buttonColors(containerColor = AvisoIcono, contentColor = Color.White),
                    shape = RoundedCornerShape(50),
                    contentPadding = PaddingValues(horizontal = 22.dp, vertical = 6.dp)
                ) { Text("Ver", fontWeight = FontWeight.SemiBold) }
            }
        }
        Spacer(Modifier.height(12.dp))
    }
}

@Composable
private fun Contador(valor: Int, etiqueta: String, fondo: Color, color: Color, modifier: Modifier) {
    Column(
        modifier
            .height(88.dp)
            .background(fondo, RoundedCornerShape(16.dp)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("$valor", color = color, fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Text(etiqueta, color = color, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun Seccion(titulo: String, contenido: @Composable () -> Unit) {
    val cs = MaterialTheme.colorScheme
    Text(
        titulo,
        fontWeight = FontWeight.Bold,
        style = MaterialTheme.typography.bodyLarge,
        modifier = Modifier.padding(top = 18.dp, bottom = 10.dp)
    )
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cs.surface),
        border = BorderStroke(1.dp, BordeSuave),
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(Modifier.padding(16.dp)) { contenido() }
    }
}