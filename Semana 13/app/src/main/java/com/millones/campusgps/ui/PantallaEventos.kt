package com.millones.campusgps.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.Directions
import androidx.compose.material.icons.rounded.Event
import androidx.compose.material.icons.rounded.Group
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.Place
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import com.millones.campusgps.model.Evento
import com.millones.campusgps.model.FiltroEventos
import com.millones.campusgps.ui.componentes.AvisoFlotante
import com.millones.campusgps.ui.componentes.BotonCampus
import com.millones.campusgps.ui.componentes.BotonVidrio
import com.millones.campusgps.ui.componentes.CampusColores
import com.millones.campusgps.ui.componentes.DegradadosCampus
import com.millones.campusgps.ui.componentes.EntradaSuave
import com.millones.campusgps.ui.componentes.FuenteCampus
import com.millones.campusgps.ui.componentes.InsetsCampus
import com.millones.campusgps.ui.componentes.LocalAbrirMenu
import com.millones.campusgps.ui.componentes.LocalRellenoBarra
import com.millones.campusgps.ui.componentes.VidrioMapa
import com.millones.campusgps.ui.componentes.rememberImagenBase64
import com.millones.campusgps.ui.componentes.vidrioMapa
import com.millones.campusgps.viewmodel.CampusViewModelFactory
import com.millones.campusgps.viewmodel.EventosViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val LOCALE_ES = Locale.forLanguageTag("es-PE")

fun formatearFecha(millis: Long, patron: String): String =
    SimpleDateFormat(patron, LOCALE_ES).format(Date(millis)).replaceFirstChar { it.uppercase() }

// Eventos del campus en tiempo real: filtros, tarjetas con imagen, asistir y crear
@Composable
fun PantallaEventos() {
    val contexto = LocalContext.current
    val vm: EventosViewModel = viewModel(factory = CampusViewModelFactory(contexto))
    val eventos by vm.eventos.collectAsState()
    val filtro by vm.filtro.collectAsState()
    val cargando by vm.cargando.collectAsState()
    val aviso by vm.aviso.collectAsState()
    val creando by vm.creando.collectAsState()
    val guardando by vm.guardando.collectAsState()
    val lugares by vm.lugares.collectAsState()
    val resultados by vm.resultados.collectAsState()
    val buscando by vm.buscando.collectAsState()
    val busquedaHecha by vm.busquedaHecha.collectAsState()

    val abrirMenu = LocalAbrirMenu.current
    val relleno = LocalRellenoBarra.current

    var detalleId by rememberSaveable { mutableStateOf<String?>(null) }
    var porEliminar by remember { mutableStateOf<Evento?>(null) }

    Scaffold(
        containerColor = CampusColores.AzulNoche,
        contentWindowInsets = InsetsCampus
    ) { espacio ->
        val abajo = if (relleno > 0.dp) relleno else espacio.calculateBottomPadding()

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(Color(0xFF15306A), CampusColores.AzulNoche)))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = espacio.calculateTopPadding())
            ) {
                // ----- Cabecera -----
                EntradaSuave {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, end = 16.dp, top = 8.dp)
                    ) {
                        BotonVidrio(icono = Icons.Rounded.Menu, descripcion = "Abrir menú", onClick = abrirMenu, tamano = 52.dp)
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Eventos",
                                color = Color.White,
                                fontFamily = FuenteCampus,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 26.sp
                            )
                            Text(
                                text = "Lo que pasa en el campus",
                                color = Color.White.copy(alpha = 0.65f),
                                fontFamily = FuenteCampus,
                                fontSize = 13.sp
                            )
                        }
                        BotonVidrio(
                            icono = Icons.Rounded.Add,
                            descripcion = "Crear evento",
                            onClick = vm::abrirFormulario,
                            tamano = 52.dp,
                            fondo = DegradadosCampus.Primario
                        )
                    }
                }

                // ----- Filtros -----
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 14.dp)
                ) {
                    FiltroEventos.entries.forEach { opcion ->
                        ChipFiltro(
                            texto = opcion.etiqueta,
                            activo = opcion == filtro,
                            onClick = { vm.elegirFiltro(opcion) }
                        )
                    }
                }

                // ----- Lista -----
                Box(modifier = Modifier.weight(1f)) {
                    when {
                        cargando -> CircularProgressIndicator(
                            color = VidrioMapa.Cian,
                            modifier = Modifier.align(Alignment.Center)
                        )
                        eventos.isEmpty() -> SinEventos(
                            filtro = filtro,
                            onCrear = vm::abrirFormulario,
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(bottom = abajo)
                        )
                        else -> LazyColumn(
                            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 2.dp, bottom = abajo + 16.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            items(eventos, key = { it.id }) { evento ->
                                TarjetaEvento(
                                    evento = evento,
                                    onClick = { detalleId = evento.id },
                                    onAsistir = { vm.alternarAsistencia(evento) },
                                    modifier = Modifier.animateItem()
                                )
                            }
                        }
                    }
                }
            }

            // ----- Aviso en insignia flotante -----
            AvisoFlotante(
                aviso = aviso,
                onOcultar = vm::limpiarAviso,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(start = 16.dp, end = 16.dp, top = espacio.calculateTopPadding() + 76.dp)
            )
        }
    }

    // ----- Crear evento -----
    if (creando) {
        FormularioEvento(
            lugares = lugares,
            guardando = guardando,
            onPublicar = vm::crear,
            onCancelar = vm::cerrarFormulario,
            resultados = resultados,
            buscando = buscando,
            busquedaHecha = busquedaHecha,
            onBuscar = vm::buscarUbicacion,
            onLimpiarBusqueda = vm::limpiarBusqueda
        )
    }

    // ----- Detalle -----
    eventos.firstOrNull { it.id == detalleId }?.let { evento ->
        DetalleEvento(
            evento = evento,
            onCerrar = { detalleId = null },
            onAsistir = { vm.alternarAsistencia(evento) },
            onEliminar = {
                porEliminar = evento
                detalleId = null
            }
        )
    }

    // ----- Confirmar eliminación -----
    porEliminar?.let { evento ->
        AlertDialog(
            onDismissRequest = { porEliminar = null },
            containerColor = VidrioMapa.FondoSolido,
            titleContentColor = Color.White,
            textContentColor = Color.White.copy(alpha = 0.8f),
            title = { Text("¿Eliminar evento?", fontFamily = FuenteCampus, fontWeight = FontWeight.Bold) },
            text = { Text("Se borrará \"${evento.titulo}\" para todos.", fontFamily = FuenteCampus) },
            confirmButton = {
                TextButton(onClick = {
                    vm.eliminar(evento)
                    porEliminar = null
                }) { Text("Eliminar", color = Color(0xFFFF8A8A), fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { porEliminar = null }) {
                    Text("Cancelar", color = Color.White.copy(alpha = 0.8f))
                }
            }
        )
    }
}

@Composable
private fun ChipFiltro(texto: String, activo: Boolean, onClick: () -> Unit) {
    val fondo by animateColorAsState(
        targetValue = if (activo) CampusColores.VerdeMenta.copy(alpha = 0.18f) else Color.White.copy(alpha = 0.05f),
        label = "fondoFiltro"
    )
    val forma = RoundedCornerShape(50)
    Text(
        text = texto,
        color = if (activo) CampusColores.VerdeMenta else Color.White.copy(alpha = 0.75f),
        fontFamily = FuenteCampus,
        fontWeight = if (activo) FontWeight.Bold else FontWeight.Medium,
        fontSize = 14.sp,
        modifier = Modifier
            .clip(forma)
            .background(fondo)
            .border(1.dp, if (activo) CampusColores.VerdeMenta.copy(alpha = 0.6f) else Color.White.copy(alpha = 0.12f), forma)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

// Tarjeta de un evento: imagen con la fecha, título, hora, lugar, asistentes y botón Asistiré
@Composable
private fun TarjetaEvento(
    evento: Evento,
    onClick: () -> Unit,
    onAsistir: () -> Unit,
    modifier: Modifier = Modifier
) {
    val imagen = rememberImagenBase64(evento.foto)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .vidrioMapa(RoundedCornerShape(24.dp))
            .clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .background(Brush.linearGradient(listOf(CampusColores.Violeta.copy(alpha = 0.7f), CampusColores.AzulPrimario.copy(alpha = 0.6f))))
        ) {
            if (imagen != null) {
                Image(
                    bitmap = imagen,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Icon(
                    Icons.Rounded.Event,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.5f),
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(56.dp)
                )
            }
            InsigniaFecha(
                fecha = evento.fecha,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(12.dp)
            )
            if (evento.asisto) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                        .clip(RoundedCornerShape(50))
                        .background(CampusColores.VerdeMenta)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Icon(Icons.Rounded.CheckCircle, contentDescription = null, tint = VidrioMapa.FondoSolido, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Asistirás", color = VidrioMapa.FondoSolido, fontFamily = FuenteCampus, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }

        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = evento.titulo,
                color = Color.White,
                fontFamily = FuenteCampus,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(6.dp))
            DatoEvento(Icons.Rounded.Schedule, formatearFecha(evento.fecha, "EEE d MMM · HH:mm"))
            if (evento.lugarNombre.isNotBlank()) {
                DatoEvento(Icons.Rounded.Place, evento.lugarNombre)
            }
            Spacer(Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.Group, contentDescription = null, tint = VidrioMapa.Cian, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text(
                    text = textoAsistentes(evento.asistentes),
                    color = Color.White.copy(alpha = 0.75f),
                    fontFamily = FuenteCampus,
                    fontSize = 13.sp,
                    modifier = Modifier.weight(1f)
                )
                BotonAsistir(asisto = evento.asisto, onClick = onAsistir)
            }
        }
    }
}

private fun textoAsistentes(cantidad: Int) = when (cantidad) {
    0 -> "Sé el primero en asistir"
    1 -> "1 asistirá"
    else -> "$cantidad asistirán"
}

// Día y mes en un recuadro de vidrio
@Composable
private fun InsigniaFecha(fecha: Long, modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(VidrioMapa.FondoSolido.copy(alpha = 0.8f))
            .border(1.dp, Color.White.copy(alpha = 0.25f), RoundedCornerShape(14.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = formatearFecha(fecha, "d"),
            color = Color.White,
            fontFamily = FuenteCampus,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 20.sp,
            lineHeight = 22.sp
        )
        Text(
            text = formatearFecha(fecha, "MMM").uppercase().take(3),
            color = CampusColores.VerdeMenta,
            fontFamily = FuenteCampus,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp
        )
    }
}

@Composable
private fun DatoEvento(icono: ImageVector, texto: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(top = 4.dp)
    ) {
        Icon(icono, contentDescription = null, tint = Color.White.copy(alpha = 0.6f), modifier = Modifier.size(16.dp))
        Spacer(Modifier.width(6.dp))
        Text(
            text = texto,
            color = Color.White.copy(alpha = 0.8f),
            fontFamily = FuenteCampus,
            fontSize = 13.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

// "Asistiré" (azul) o "Asistirás" (menta)
@Composable
private fun BotonAsistir(asisto: Boolean, onClick: () -> Unit) {
    val forma = RoundedCornerShape(50)
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(forma)
            .then(
                if (asisto) {
                    Modifier
                        .background(CampusColores.VerdeMenta.copy(alpha = 0.15f))
                        .border(1.dp, CampusColores.VerdeMenta, forma)
                } else {
                    Modifier.background(DegradadosCampus.Primario)
                }
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        if (asisto) {
            Icon(Icons.Rounded.CheckCircle, contentDescription = null, tint = CampusColores.VerdeMenta, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(4.dp))
        }
        Text(
            text = if (asisto) "Asistirás" else "Asistiré",
            color = if (asisto) CampusColores.VerdeMenta else Color.White,
            fontFamily = FuenteCampus,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp
        )
    }
}

@Composable
private fun SinEventos(filtro: FiltroEventos, onCrear: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.padding(32.dp)
    ) {
        Icon(Icons.Rounded.Event, contentDescription = null, tint = VidrioMapa.Cian, modifier = Modifier.size(56.dp))
        Spacer(Modifier.height(10.dp))
        Text(
            text = if (filtro == FiltroEventos.ASISTIRE) "Aún no te anotas a ningún evento" else "No hay eventos aquí",
            color = Color.White,
            fontFamily = FuenteCampus,
            fontWeight = FontWeight.Bold,
            fontSize = 17.sp,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = "Crea uno con el botón + y aparecerá para todos al instante.",
            color = Color.White.copy(alpha = 0.65f),
            fontFamily = FuenteCampus,
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = "Crear evento",
            color = CampusColores.VerdeMenta,
            fontFamily = FuenteCampus,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .clickable(onClick = onCrear)
                .padding(horizontal = 14.dp, vertical = 8.dp)
        )
    }
}

// Detalle completo del evento (también se abre al tocar un evento en el mapa)
@Composable
fun DetalleEvento(
    evento: Evento,
    onCerrar: () -> Unit,
    onAsistir: () -> Unit,
    onEliminar: () -> Unit
) {
    val contexto = LocalContext.current
    val imagen = rememberImagenBase64(evento.foto)
    val escala = remember { Animatable(0.85f) }
    LaunchedEffect(Unit) {
        escala.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
    }

    Dialog(
        onDismissRequest = onCerrar,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 18.dp, vertical = 24.dp)
                .fillMaxWidth()
                .graphicsLayer {
                    scaleX = escala.value
                    scaleY = escala.value
                }
                .vidrioMapa(RoundedCornerShape(28.dp), fondo = VidrioMapa.FondoSolido.copy(alpha = 0.97f))
                .verticalScroll(rememberScrollState())
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(210.dp)
                    .background(Brush.linearGradient(listOf(CampusColores.Violeta.copy(alpha = 0.7f), CampusColores.AzulPrimario.copy(alpha = 0.6f))))
            ) {
                if (imagen != null) {
                    Image(
                        bitmap = imagen,
                        contentDescription = evento.titulo,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Icon(
                        Icons.Rounded.Event,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.5f),
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(64.dp)
                    )
                }
                InsigniaFecha(
                    fecha = evento.fecha,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(12.dp)
                )
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.45f))
                        .clickable(onClick = onCerrar)
                ) {
                    Icon(Icons.Rounded.Close, contentDescription = "Cerrar", tint = Color.White, modifier = Modifier.size(20.dp))
                }
            }

            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = evento.titulo,
                    color = Color.White,
                    fontFamily = FuenteCampus,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 23.sp,
                    lineHeight = 27.sp
                )
                Text(
                    text = if (evento.esMio) "Lo organizas tú" else "Organiza ${evento.autorNombre.ifBlank { "otro usuario" }}",
                    color = Color.White.copy(alpha = 0.6f),
                    fontFamily = FuenteCampus,
                    fontSize = 13.sp
                )

                Spacer(Modifier.height(10.dp))
                DatoEvento(Icons.Rounded.Schedule, formatearFecha(evento.fecha, "EEEE d 'de' MMMM, HH:mm"))
                if (evento.lugarNombre.isNotBlank()) DatoEvento(Icons.Rounded.Place, evento.lugarNombre)
                DatoEvento(Icons.Rounded.Group, textoAsistentes(evento.asistentes))

                Spacer(Modifier.height(14.dp))
                Text(
                    text = evento.descripcion.ifBlank { "Sin descripción." },
                    color = Color.White.copy(alpha = if (evento.descripcion.isBlank()) 0.5f else 0.9f),
                    fontFamily = FuenteCampus,
                    fontSize = 15.sp,
                    lineHeight = 21.sp
                )

                Spacer(Modifier.height(20.dp))
                BotonCampus(
                    texto = if (evento.asisto) "Ya no asistiré" else "Asistiré",
                    onClick = onAsistir,
                    alto = 52.dp
                )

                // Cómo llegar: abre Google Maps con la ruta hasta el lugar del evento
                if (evento.latitud != null && evento.longitud != null) {
                    Spacer(Modifier.height(8.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(50))
                            .border(1.dp, VidrioMapa.Cian.copy(alpha = 0.5f), RoundedCornerShape(50))
                            .clickable {
                                val destino = Uri.parse(
                                    "google.navigation:q=${evento.latitud},${evento.longitud}&mode=w"
                                )
                                runCatching { contexto.startActivity(Intent(Intent.ACTION_VIEW, destino)) }
                            }
                            .padding(vertical = 12.dp)
                    ) {
                        Icon(Icons.Rounded.Directions, contentDescription = null, tint = VidrioMapa.Cian, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Cómo llegar", color = VidrioMapa.Cian, fontFamily = FuenteCampus, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }

                if (evento.esMio) {
                    Spacer(Modifier.height(4.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .clip(RoundedCornerShape(50))
                            .clickable(onClick = onEliminar)
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                    ) {
                        Icon(Icons.Rounded.DeleteOutline, contentDescription = null, tint = Color(0xFFFF8A8A), modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Eliminar evento", color = Color(0xFFFF8A8A), fontFamily = FuenteCampus, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    }
                }
            }
        }
    }
}
