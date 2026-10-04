package com.saludplus.citas.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.saludplus.citas.data.repository.Repositorio
import com.saludplus.citas.ui.components.TarjetaSuave
import com.saludplus.citas.ui.components.estiloEspecialidad
import com.saludplus.citas.ui.theme.AzulOscuro
import com.saludplus.citas.ui.theme.AzulPrimario
import com.saludplus.citas.ui.theme.GrisTexto

@Composable
fun HomeScreen(
    onNotificaciones: () -> Unit,
    onAgendar: () -> Unit,
    onMisCitas: () -> Unit,
    onMisDatos: () -> Unit,
    onResultados: () -> Unit,
    onEspecialidad: (Int) -> Unit,
    onVerEspecialidades: () -> Unit,
    onMenu: () -> Unit = {}
) {
    val primerNombre = Repositorio.usuarioActual?.nombre
        ?.trim()?.split(" ")?.firstOrNull() ?: ""
    val destacadas = Repositorio.especialidadesDestacadas()

    Scaffold(containerColor = Color.White) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(vertical = 8.dp)
        ) {
            // Menú (hamburguesa) y campana
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onMenu) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Menú",
                        tint = AzulOscuro
                    )
                }
                IconButton(onClick = onNotificaciones) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notificaciones",
                        tint = AzulOscuro
                    )
                }
            }

            // Saludo
            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                Text(
                    text = "¡Hola, $primerNombre!",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = AzulOscuro
                )
                Text(
                    text = "¿Qué deseas hacer hoy?",
                    fontSize = 15.sp,
                    color = GrisTexto
                )
            }

            Spacer(Modifier.height(20.dp))

            // Cuadrícula 2x2 de accesos rápidos
            Column(
                modifier = Modifier.padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    AccesoRapido(
                        titulo = "Agendar cita",
                        icono = Icons.Default.CalendarMonth,
                        colorIcono = Color(0xFF2563EB),
                        fondo = Color(0xFFE3EDFF),
                        onClick = onAgendar,
                        modifier = Modifier.weight(1f)
                    )
                    AccesoRapido(
                        titulo = "Mis citas",
                        icono = Icons.Default.EventAvailable,
                        colorIcono = Color(0xFF22A05B),
                        fondo = Color(0xFFDDF3E6),
                        onClick = onMisCitas,
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    AccesoRapido(
                        titulo = "Mis datos",
                        icono = Icons.Default.Person,
                        colorIcono = Color(0xFF7C4DFF),
                        fondo = Color(0xFFEBE3FF),
                        onClick = onMisDatos,
                        modifier = Modifier.weight(1f)
                    )
                    AccesoRapido(
                        titulo = "Resultados",
                        icono = Icons.Default.Description,
                        colorIcono = Color(0xFFF28C28),
                        fondo = Color(0xFFFFEBD6),
                        onClick = onResultados,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            // Título de la sección y "Ver todas"
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Especialidades destacadas",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = AzulOscuro,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "Ver todas",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AzulPrimario,
                    modifier = Modifier.clickable { onVerEspecialidades() }
                )
            }

            Spacer(Modifier.height(12.dp))

            // LazyRow de especialidades destacadas
            LazyRow(
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(destacadas) { especialidad ->
                    val estilo = estiloEspecialidad(especialidad.id)
                    TarjetaSuave(
                        modifier = Modifier.width(120.dp),
                        onClick = { onEspecialidad(especialidad.id) }
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(estilo.fondo),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = estilo.icono,
                                    contentDescription = null,
                                    tint = estilo.color,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = especialidad.nombre,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = AzulOscuro,
                                textAlign = TextAlign.Center,
                                minLines = 2,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
    }
}

// Tarjeta de color con ícono centrado y título
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AccesoRapido(
    titulo: String,
    icono: ImageVector,
    colorIcono: Color,
    fondo: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.height(120.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = fondo),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icono,
                contentDescription = null,
                tint = colorIcono,
                modifier = Modifier.size(44.dp)
            )
            Spacer(Modifier.height(10.dp))
            Text(
                text = titulo,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = colorIcono
            )
        }
    }
}