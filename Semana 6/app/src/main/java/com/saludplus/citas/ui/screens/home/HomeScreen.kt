package com.saludplus.citas.ui.screens.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ChevronRight
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.saludplus.citas.navigation.Rutas
import com.saludplus.citas.ui.components.AvatarMedico
import com.saludplus.citas.ui.components.BarraNavegacion
import com.saludplus.citas.ui.components.Insignia
import com.saludplus.citas.ui.components.MensajeController
import com.saludplus.citas.ui.components.TarjetaSuave
import com.saludplus.citas.ui.components.TipoMensaje
import com.saludplus.citas.ui.components.efectoPresion
import com.saludplus.citas.ui.components.estiloEspecialidad
import com.saludplus.citas.ui.theme.AzulClaro
import com.saludplus.citas.ui.theme.AzulOscuro
import com.saludplus.citas.ui.theme.AzulPrimario
import com.saludplus.citas.ui.theme.ColoresSemanticos
import com.saludplus.citas.ui.theme.Degradados
import com.saludplus.citas.ui.theme.Formas
import com.saludplus.citas.ui.theme.GrisTexto
import com.saludplus.citas.util.Fechas
import kotlinx.coroutines.delay
import java.time.LocalDate
import java.time.LocalTime

@Composable
fun HomeScreen(
    onNotificaciones: () -> Unit,
    onAgendar: () -> Unit,
    onMisCitas: () -> Unit,
    onMisDatos: () -> Unit,
    onResultados: () -> Unit,
    onEspecialidad: (Int) -> Unit,
    onVerEspecialidades: () -> Unit,
    onNavegar: (String) -> Unit,
    onMenu: () -> Unit = {},
    onCita: (Int) -> Unit = { onMisCitas() }
) {
    val primerNombre = Repositorio.usuarioActual?.nombre
        ?.trim()?.split(" ")?.firstOrNull() ?: ""
    val destacadas = Repositorio.especialidadesDestacadas()

    val hoy = remember { LocalDate.now() }
    val hoyIso = remember(hoy) { hoy.toString() }
    val citasUsuario = remember { Repositorio.citasDelUsuario() }
    val proximaCita = remember(citasUsuario, hoyIso) {
        citasUsuario.filter { it.fecha >= hoyIso }.minByOrNull { it.fecha }
    }

    var mostrarCarruselAnimado by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(150)
        mostrarCarruselAnimado = true
    }

    // Lógica para disparar el mensaje de bienvenida dinámico una sola vez por inicio de sesión
    LaunchedEffect(Unit) {
        if (Repositorio.consumirMensajeBienvenida()) {
            val horaActual = LocalTime.now().hour
            val saludoTime = when {
                horaActual in 5..11 -> "Buenos días"
                horaActual in 12..18 -> "Buenas tardes"
                else -> "Buenas noches"
            }

            val mananaIso = hoy.plusDays(1).toString()

            val citaHoy = citasUsuario.find { it.fecha == hoyIso }
            val citaManana = citasUsuario.find { it.fecha == mananaIso }

            val lineaContextual = when {
                citaHoy != null -> {
                    val medico = Repositorio.obtenerMedico(citaHoy.medicoId)
                    "Hoy tienes cita con ${medico?.nombre ?: "tu médico"} a las ${citaHoy.hora}."
                }
                citaManana != null -> {
                    val medico = Repositorio.obtenerMedico(citaManana.medicoId)
                    "Mañana tienes cita con ${medico?.nombre ?: "tu médico"} a las ${citaManana.hora}."
                }
                proximaCita != null -> {
                    "Tu próxima cita es el ${Fechas.fechaEnTexto(proximaCita.fecha)}."
                }
                else -> {
                    val consejos = listOf(
                        "Consejo del día: toma agua durante el día, aunque no sientas sed.",
                        "Consejo del día: camina al menos 30 minutos diarios para cuidar tu corazón.",
                        "Consejo del día: recuerda descansar entre 7 y 8 horas diarias.",
                        "Consejo del día: consume frutas y verduras frescas para fortalecer tus defensas.",
                        "Consejo del día: realiza pausas activas durante tu jornada de trabajo.",
                        "Consejo del día: la prevención es salud, programa tus chequeos preventivos.",
                        "Consejo del día: cuida tu postura al sentarte y mantén la espalda erguida."
                    )
                    val indiceConsejo = (hoy.dayOfYear % consejos.size)
                    consejos[indiceConsejo]
                }
            }

            MensajeController.mostrar(
                texto = "$saludoTime, $primerNombre. $lineaContextual",
                tipo = TipoMensaje.BIENVENIDA,
                duracionMs = 3500L
            )
        }
    }

    Scaffold(
        containerColor = Color.White,
        bottomBar = { BarraNavegacion(rutaActual = Rutas.HOME, onNavegar = onNavegar) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            // Cabecera con degradado
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
                    .background(Degradados.Principal)
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = onMenu,
                            modifier = Modifier.efectoPresion { onMenu() }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Menú",
                                tint = Color.White
                            )
                        }

                        Box {
                            IconButton(
                                onClick = onNotificaciones,
                                modifier = Modifier.efectoPresion { onNotificaciones() }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Notifications,
                                    contentDescription = "Notificaciones",
                                    tint = Color.White
                                )
                            }
                            if (citasUsuario.isNotEmpty()) {
                                Insignia(
                                    numero = citasUsuario.size,
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(top = 6.dp, end = 6.dp)
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(8.dp))

                    Text(
                        text = "¡Hola, $primerNombre!",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                    Text(
                        text = "¿Cómo te sientes hoy?",
                        fontSize = 15.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                    Spacer(Modifier.height(12.dp))
                }
            }

            Spacer(Modifier.height(16.dp))

            // Tarjeta Próxima Cita destacada con cuenta regresiva
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                if (proximaCita != null) {
                    val medicoProximo = Repositorio.obtenerMedico(proximaCita.medicoId)
                    val espProxima = Repositorio.obtenerEspecialidad(proximaCita.especialidadId)
                    val parsedFecha = LocalDate.parse(proximaCita.fecha)
                    val etiquetaRelativa = Fechas.etiquetaRelativa(parsedFecha, hoy)

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(Formas.grande)
                            .background(Color(0xFFEFF6FF))
                            .border(1.5.dp, AzulClaro, Formas.grande)
                            .efectoPresion { onCita(proximaCita.id) }
                            .clickable { onCita(proximaCita.id) }
                            .padding(16.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.AccessTime,
                                        contentDescription = null,
                                        tint = AzulPrimario,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(Modifier.width(6.dp))
                                    Text(
                                        text = "Próxima cita",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AzulOscuro
                                    )
                                }

                                // Etiqueta de cuenta regresiva (Hoy, Mañana, En N días)
                                Box(
                                    modifier = Modifier
                                        .clip(Formas.pildora)
                                        .background(AzulPrimario)
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = etiquetaRelativa,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }

                            Spacer(Modifier.height(12.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (medicoProximo != null) {
                                    AvatarMedico(
                                        nombre = medicoProximo.nombre,
                                        foto = medicoProximo.foto,
                                        tamano = 50.dp
                                    )
                                    Spacer(Modifier.width(12.dp))
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = medicoProximo?.nombre ?: "Médico",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AzulOscuro
                                    )
                                    Text(
                                        text = espProxima?.nombre ?: "",
                                        fontSize = 13.sp,
                                        color = GrisTexto
                                    )
                                    Spacer(Modifier.height(2.dp))
                                    Text(
                                        text = "${Fechas.fechaEnTexto(parsedFecha)} · ${proximaCita.hora}",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = AzulPrimario
                                    )
                                }
                                Icon(
                                    imageVector = Icons.Default.ChevronRight,
                                    contentDescription = "Ver detalle",
                                    tint = GrisTexto
                                )
                            }
                        }
                    }
                } else {
                    // Estado sin citas
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(Formas.grande)
                            .background(Color(0xFFF8FAFC))
                            .border(1.dp, Color(0xFFE2E8F0), Formas.grande)
                            .padding(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(AzulClaro),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CalendarMonth,
                                    contentDescription = null,
                                    tint = AzulPrimario,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Sin citas próximas",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AzulOscuro
                                )
                                Text(
                                    text = "Agenda una consulta en pocos pasos",
                                    fontSize = 13.sp,
                                    color = GrisTexto
                                )
                            }
                            Text(
                                text = "Agendar",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = AzulPrimario,
                                modifier = Modifier
                                    .efectoPresion { onAgendar() }
                                    .clickable { onAgendar() }
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // Cuadrícula 2x2 de accesos rápidos
            Column(
                modifier = Modifier.padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
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
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
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
                    .padding(horizontal = 20.dp),
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
                    modifier = Modifier
                        .efectoPresion { onVerEspecialidades() }
                        .clickable { onVerEspecialidades() }
                )
            }

            Spacer(Modifier.height(12.dp))

            // LazyRow de especialidades destacadas con animación de entrada
            AnimatedVisibility(
                visible = mostrarCarruselAnimado,
                enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 })
            ) {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    itemsIndexed(destacadas) { _, especialidad ->
                        val estilo = estiloEspecialidad(especialidad.id)
                        TarjetaSuave(
                            modifier = Modifier
                                .width(120.dp)
                                .efectoPresion { onEspecialidad(especialidad.id) },
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

            Spacer(Modifier.height(16.dp))
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
        modifier = modifier
            .height(110.dp)
            .efectoPresion { onClick() },
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
                modifier = Modifier.size(40.dp)
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = titulo,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = colorIcono
            )
        }
    }
}
