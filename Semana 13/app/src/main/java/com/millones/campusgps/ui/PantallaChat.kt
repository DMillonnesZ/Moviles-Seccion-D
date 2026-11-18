package com.millones.campusgps.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.EditNote
import androidx.compose.material.icons.rounded.Event
import androidx.compose.material.icons.rounded.Forum
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.Place
import androidx.compose.material.icons.rounded.Star
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
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.millones.campusgps.model.Comentario
import com.millones.campusgps.model.Destacado
import com.millones.campusgps.model.FiltroOpiniones
import com.millones.campusgps.model.ObjetivoOpinion
import com.millones.campusgps.model.Perfil
import com.millones.campusgps.model.ResumenCalificacion
import com.millones.campusgps.model.TipoObjetivo
import com.millones.campusgps.ui.componentes.AvatarUsuario
import com.millones.campusgps.ui.componentes.AvisoFlotante
import com.millones.campusgps.ui.componentes.BotonCampus
import com.millones.campusgps.ui.componentes.BotonVidrio
import com.millones.campusgps.ui.componentes.CampusColores
import com.millones.campusgps.ui.componentes.DegradadosCampus
import com.millones.campusgps.ui.componentes.EntradaSuave
import com.millones.campusgps.ui.componentes.Estrellas
import com.millones.campusgps.ui.componentes.FuenteCampus
import com.millones.campusgps.ui.componentes.InsetsCampus
import com.millones.campusgps.ui.componentes.LocalAbrirMenu
import com.millones.campusgps.ui.componentes.LocalRellenoBarra
import com.millones.campusgps.ui.componentes.SelectorEstrellas
import com.millones.campusgps.ui.componentes.VidrioMapa
import com.millones.campusgps.ui.componentes.vidrioMapa
import com.millones.campusgps.viewmodel.OpinionesViewModel
import java.util.Locale

// "Chat" de opiniones: cada usuario califica (1 a 5 estrellas) y comenta lugares y eventos.
// Todo en tiempo real; cada uno puede borrar solo sus opiniones.
@Composable
fun PantallaChat(
    vm: OpinionesViewModel,
    onAbrirLugar: (String) -> Unit,
    onAbrirEvento: (String) -> Unit
) {
    val comentarios by vm.comentarios.collectAsState()
    val filtro by vm.filtro.collectAsState()
    val cargando by vm.cargando.collectAsState()
    val perfiles by vm.perfiles.collectAsState()
    val resumenes by vm.resumenes.collectAsState()
    val destacados by vm.destacados.collectAsState()
    val objetivos by vm.objetivos.collectAsState()
    val formularioAbierto by vm.formularioAbierto.collectAsState()
    val publicando by vm.publicando.collectAsState()
    val aviso by vm.aviso.collectAsState()

    val abrirMenu = LocalAbrirMenu.current
    val relleno = LocalRellenoBarra.current
    var porEliminar by remember { mutableStateOf<Comentario?>(null) }

    // Mientras la pantalla está abierta, lo que llega se da por leído (apaga la insignia)
    LaunchedEffect(comentarios) { vm.marcarVistas() }

    fun abrir(tipo: TipoObjetivo, id: String) {
        if (tipo == TipoObjetivo.LUGAR) onAbrirLugar(id) else onAbrirEvento(id)
    }

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
            LazyColumn(
                contentPadding = PaddingValues(top = espacio.calculateTopPadding(), bottom = abajo + 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                // ----- Cabecera -----
                item {
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
                                    text = "Opiniones",
                                    color = Color.White,
                                    fontFamily = FuenteCampus,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 26.sp
                                )
                                Text(
                                    text = "Califica lugares y eventos del campus",
                                    color = Color.White.copy(alpha = 0.65f),
                                    fontFamily = FuenteCampus,
                                    fontSize = 13.sp
                                )
                            }
                            BotonVidrio(
                                icono = Icons.Rounded.EditNote,
                                descripcion = "Escribir opinión",
                                onClick = vm::abrirFormulario,
                                tamano = 52.dp,
                                fondo = DegradadosCampus.Primario
                            )
                        }
                    }
                }

                // ----- Mejor calificados -----
                if (destacados.isNotEmpty()) {
                    item {
                        Column {
                            TituloSeccionChat("Mejor calificados")
                            LazyRow(
                                contentPadding = PaddingValues(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                items(destacados, key = { it.id }) { destacado ->
                                    TarjetaDestacado(destacado, onClick = { abrir(destacado.tipo, destacado.id) })
                                }
                            }
                        }
                    }
                }

                // ----- Filtros -----
                item {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp)
                    ) {
                        FiltroOpiniones.entries.forEach { opcion ->
                            ChipFiltroChat(
                                texto = opcion.etiqueta,
                                activo = opcion == filtro,
                                onClick = { vm.elegirFiltro(opcion) }
                            )
                        }
                    }
                }

                // ----- Opiniones -----
                when {
                    cargando -> item {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(48.dp)
                        ) {
                            CircularProgressIndicator(color = VidrioMapa.Cian)
                        }
                    }

                    comentarios.isEmpty() -> item {
                        SinOpiniones(filtro = filtro, onOpinar = vm::abrirFormulario)
                    }

                    else -> items(comentarios, key = { it.id }) { comentario ->
                        TarjetaOpinion(
                            comentario = comentario,
                            perfil = perfiles[comentario.autorUid],
                            resumen = resumenes[comentario.objetivoId],
                            onAbrirObjetivo = { abrir(comentario.tipo, comentario.objetivoId) },
                            onEliminar = { porEliminar = comentario },
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .animateItem()
                        )
                    }
                }
            }

            AvisoFlotante(
                aviso = aviso,
                onOcultar = vm::limpiarAviso,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(start = 16.dp, end = 16.dp, top = espacio.calculateTopPadding() + 76.dp)
            )
        }
    }

    // ----- Nueva opinión -----
    if (formularioAbierto) {
        FormularioOpinion(
            objetivos = objetivos,
            publicando = publicando,
            onPublicar = vm::publicar,
            onCancelar = vm::cerrarFormulario
        )
    }

    // ----- Confirmar eliminación -----
    porEliminar?.let { comentario ->
        AlertDialog(
            onDismissRequest = { porEliminar = null },
            containerColor = VidrioMapa.FondoSolido,
            titleContentColor = Color.White,
            textContentColor = Color.White.copy(alpha = 0.8f),
            title = { Text("¿Eliminar tu opinión?", fontFamily = FuenteCampus, fontWeight = FontWeight.Bold) },
            text = { Text("Se borrará tu opinión sobre \"${comentario.objetivoNombre}\".", fontFamily = FuenteCampus) },
            confirmButton = {
                TextButton(onClick = {
                    vm.eliminar(comentario)
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
private fun TituloSeccionChat(texto: String) {
    Text(
        text = texto.uppercase(),
        color = Color.White.copy(alpha = 0.5f),
        fontFamily = FuenteCampus,
        fontWeight = FontWeight.Bold,
        fontSize = 11.sp,
        letterSpacing = 1.4.sp,
        modifier = Modifier.padding(start = 20.dp, bottom = 8.dp, top = 4.dp)
    )
}

@Composable
private fun ChipFiltroChat(texto: String, activo: Boolean, onClick: () -> Unit) {
    val fondo by animateColorAsState(
        targetValue = if (activo) CampusColores.VerdeMenta.copy(alpha = 0.18f) else Color.White.copy(alpha = 0.05f),
        label = "fondoFiltroChat"
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

// Tarjeta del carrusel: lugar o evento con su promedio
@Composable
private fun TarjetaDestacado(destacado: Destacado, onClick: () -> Unit) {
    val esLugar = destacado.tipo == TipoObjetivo.LUGAR
    val color = if (esLugar) CampusColores.AzulCielo else CampusColores.Violeta
    Column(
        modifier = Modifier
            .width(150.dp)
            .vidrioMapa(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .padding(12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.2f))
            ) {
                Icon(
                    if (esLugar) Icons.Rounded.Place else Icons.Rounded.Event,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(Modifier.weight(1f))
            Icon(Icons.Rounded.Star, contentDescription = null, tint = VidrioMapa.Estrella, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(2.dp))
            Text(
                text = "%.1f".format(Locale.US, destacado.resumen.promedio),
                color = Color.White,
                fontFamily = FuenteCampus,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 15.sp
            )
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text = destacado.nombre,
            color = Color.White,
            fontFamily = FuenteCampus,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.heightIn(min = 36.dp)
        )
        Text(
            text = "${destacado.resumen.total} ${if (destacado.resumen.total == 1) "opinión" else "opiniones"}",
            color = Color.White.copy(alpha = 0.55f),
            fontFamily = FuenteCampus,
            fontSize = 12.sp
        )
    }
}

// Una opinión: autor con foto, hace cuánto, a qué lugar o evento (con su promedio),
// estrellas y el texto. Si es tuya: papelera (o mantener presionado) para borrarla.
@Composable
private fun TarjetaOpinion(
    comentario: Comentario,
    perfil: Perfil?,
    resumen: ResumenCalificacion?,
    onAbrirObjetivo: () -> Unit,
    onEliminar: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptico = LocalHapticFeedback.current
    val nombre = perfil?.nombre?.takeIf { it.isNotBlank() } ?: comentario.autorNombre.ifBlank { "Estudiante" }
    val esLugar = comentario.tipo == TipoObjetivo.LUGAR
    val colorObjetivo = if (esLugar) CampusColores.AzulCielo else CampusColores.Violeta

    Column(
        modifier = modifier
            .fillMaxWidth()
            .vidrioMapa(
                RoundedCornerShape(22.dp),
                fondo = if (comentario.esMio) Color(0xFF13305E).copy(alpha = 0.9f) else VidrioMapa.Fondo
            )
            .combinedClickable(
                onClick = onAbrirObjetivo,
                onLongClick = if (comentario.esMio) {
                    {
                        haptico.performHapticFeedback(HapticFeedbackType.LongPress)
                        onEliminar()
                    }
                } else null
            )
            .padding(14.dp)
    ) {
        // Autor
        Row(verticalAlignment = Alignment.CenterVertically) {
            AvatarUsuario(nombre = nombre, fotoBase64 = perfil?.foto, tamano = 40.dp)
            Spacer(Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = nombre,
                        color = Color.White,
                        fontFamily = FuenteCampus,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    if (comentario.esMio) {
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = "Tú",
                            color = CampusColores.VerdeMenta,
                            fontFamily = FuenteCampus,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(CampusColores.VerdeMenta.copy(alpha = 0.16f))
                                .padding(horizontal = 7.dp, vertical = 2.dp)
                        )
                    }
                }
                Text(
                    text = haceCuanto(comentario.creadoEn) + (perfil?.carrera?.takeIf { it.isNotBlank() }?.let { " · $it" } ?: ""),
                    color = Color.White.copy(alpha = 0.55f),
                    fontFamily = FuenteCampus,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            if (comentario.esMio) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .clickable(onClick = onEliminar)
                ) {
                    Icon(Icons.Rounded.DeleteOutline, contentDescription = "Eliminar opinión", tint = Color(0xFFFF8A8A), modifier = Modifier.size(20.dp))
                }
            }
        }

        Spacer(Modifier.height(10.dp))

        // Sobre qué (toca para abrirlo en el mapa o en eventos)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .background(colorObjetivo.copy(alpha = 0.14f))
                .border(1.dp, colorObjetivo.copy(alpha = 0.45f), RoundedCornerShape(50))
                .clickable(onClick = onAbrirObjetivo)
                .padding(start = 8.dp, end = 12.dp, top = 5.dp, bottom = 5.dp)
        ) {
            Icon(
                if (esLugar) Icons.Rounded.Place else Icons.Rounded.Event,
                contentDescription = null,
                tint = colorObjetivo,
                modifier = Modifier.size(16.dp)
            )
            Spacer(Modifier.width(5.dp))
            Text(
                text = comentario.objetivoNombre,
                color = Color.White,
                fontFamily = FuenteCampus,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.widthIn(max = 180.dp)
            )
            if (resumen != null) {
                Spacer(Modifier.width(8.dp))
                Icon(Icons.Rounded.Star, contentDescription = null, tint = VidrioMapa.Estrella, modifier = Modifier.size(13.dp))
                Text(
                    text = " %.1f (%d)".format(Locale.US, resumen.promedio, resumen.total),
                    color = Color.White.copy(alpha = 0.8f),
                    fontFamily = FuenteCampus,
                    fontSize = 12.sp
                )
            }
        }

        Spacer(Modifier.height(10.dp))
        Estrellas(valor = comentario.calificacion.toFloat(), tamano = 18.dp)
        Spacer(Modifier.height(6.dp))
        Text(
            text = comentario.texto,
            color = Color.White.copy(alpha = 0.92f),
            fontFamily = FuenteCampus,
            fontSize = 15.sp,
            lineHeight = 21.sp
        )
    }
}

@Composable
private fun SinOpiniones(filtro: FiltroOpiniones, onOpinar: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp)
    ) {
        Icon(Icons.Rounded.Forum, contentDescription = null, tint = VidrioMapa.Cian, modifier = Modifier.size(56.dp))
        Spacer(Modifier.height(10.dp))
        Text(
            text = when (filtro) {
                FiltroOpiniones.MIAS -> "Aún no publicas opiniones"
                FiltroOpiniones.FAVORITOS -> "Nadie opinó sobre tus favoritos"
                else -> "Aún no hay opiniones"
            },
            color = Color.White,
            fontFamily = FuenteCampus,
            fontWeight = FontWeight.Bold,
            fontSize = 17.sp,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = "Califica un lugar o un evento y cuenta qué te pareció.",
            color = Color.White.copy(alpha = 0.65f),
            fontFamily = FuenteCampus,
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = "Escribir opinión",
            color = CampusColores.VerdeMenta,
            fontFamily = FuenteCampus,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .clickable(onClick = onOpinar)
                .padding(horizontal = 14.dp, vertical = 8.dp)
        )
    }
}

// "Ahora", "hace 5 min", "hace 2 h", "ayer" o la fecha
fun haceCuanto(millis: Long): String {
    val diferencia = System.currentTimeMillis() - millis
    val minutos = diferencia / 60_000
    val horas = minutos / 60
    return when {
        minutos < 1 -> "Ahora"
        minutos < 60 -> "hace $minutos min"
        horas < 24 -> "hace $horas h"
        horas < 48 -> "ayer"
        else -> formatearFecha(millis, "d MMM")
    }
}

// Ventana para publicar una opinión: sobre qué (lugares / eventos), estrellas y texto
@Composable
private fun FormularioOpinion(
    objetivos: List<ObjetivoOpinion>,
    publicando: Boolean,
    onPublicar: (ObjetivoOpinion?, Int, String) -> Unit,
    onCancelar: () -> Unit
) {
    var tipo by rememberSaveable { mutableStateOf(TipoObjetivo.LUGAR) }
    var elegidoId by rememberSaveable { mutableStateOf<String?>(null) }
    var calificacion by rememberSaveable { mutableIntStateOf(0) }
    var texto by rememberSaveable { mutableStateOf("") }

    val lista = objetivos.filter { it.tipo == tipo }
    val elegido = objetivos.firstOrNull { it.id == elegidoId }

    val escala = remember { Animatable(0.85f) }
    LaunchedEffect(Unit) {
        escala.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
    }

    Dialog(
        onDismissRequest = onCancelar,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(horizontal = 18.dp, vertical = 24.dp)
                .imePadding()
                .fillMaxWidth()
                .graphicsLayer {
                    scaleX = escala.value
                    scaleY = escala.value
                }
                .vidrioMapa(RoundedCornerShape(28.dp), fondo = VidrioMapa.FondoSolido.copy(alpha = 0.97f))
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Nueva opinión",
                    color = Color.White,
                    fontFamily = FuenteCampus,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 22.sp,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    Icons.Rounded.Close,
                    contentDescription = "Cerrar",
                    tint = Color.White.copy(alpha = 0.7f),
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable(enabled = !publicando, onClick = onCancelar)
                        .padding(6.dp)
                )
            }

            Spacer(Modifier.height(14.dp))

            // ¿Sobre qué? Lugares o eventos
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(50))
                    .background(Color.White.copy(alpha = 0.05f))
                    .border(1.dp, Color.White.copy(alpha = 0.10f), RoundedCornerShape(50))
                    .padding(4.dp)
            ) {
                listOf(TipoObjetivo.LUGAR to "Lugares", TipoObjetivo.EVENTO to "Eventos").forEach { (opcion, etiqueta) ->
                    val activa = opcion == tipo
                    Text(
                        text = etiqueta,
                        color = if (activa) CampusColores.VerdeMenta else Color.White.copy(alpha = 0.65f),
                        fontFamily = FuenteCampus,
                        fontWeight = if (activa) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(50))
                            .background(if (activa) CampusColores.VerdeMenta.copy(alpha = 0.18f) else Color.Transparent)
                            .clickable { tipo = opcion }
                            .padding(vertical = 8.dp)
                    )
                }
            }

            Spacer(Modifier.height(10.dp))

            // Lista para elegir (favoritos y eventos a los que asistirás van primero)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 220.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color.White.copy(alpha = 0.04f))
                    .border(1.dp, Color.White.copy(alpha = 0.10f), RoundedCornerShape(18.dp))
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = 4.dp)
            ) {
                if (lista.isEmpty()) {
                    Text(
                        text = if (tipo == TipoObjetivo.LUGAR) "Aún no hay lugares registrados." else "Aún no hay eventos.",
                        color = Color.White.copy(alpha = 0.6f),
                        fontFamily = FuenteCampus,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(14.dp)
                    )
                }
                lista.forEach { objetivo ->
                    val activo = objetivo.id == elegidoId
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(if (activo) CampusColores.AzulCielo.copy(alpha = 0.18f) else Color.Transparent)
                            .clickable { elegidoId = objetivo.id }
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Icon(
                            if (objetivo.tipo == TipoObjetivo.LUGAR) Icons.Rounded.Place else Icons.Rounded.Event,
                            contentDescription = null,
                            tint = if (activo) CampusColores.AzulCielo else Color.White.copy(alpha = 0.6f),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = objetivo.nombre,
                                color = Color.White,
                                fontFamily = FuenteCampus,
                                fontWeight = if (activo) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 14.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            if (objetivo.detalle.isNotBlank()) {
                                Text(
                                    text = objetivo.detalle,
                                    color = Color.White.copy(alpha = 0.5f),
                                    fontFamily = FuenteCampus,
                                    fontSize = 12.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                        if (objetivo.destacado) {
                            Text(
                                text = if (objetivo.tipo == TipoObjetivo.LUGAR) "Favorito" else "Asistirás",
                                color = VidrioMapa.Estrella,
                                fontFamily = FuenteCampus,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            SelectorEstrellas(valor = calificacion, onCambiar = { calificacion = it })

            Spacer(Modifier.height(14.dp))

            CampoDescripcion(
                valor = texto,
                onValorChange = { texto = it },
                habilitado = !publicando,
                maximo = 300,
                placeholder = if (elegido != null) "¿Qué te pareció ${elegido.nombre}?" else "¿Qué te pareció?"
            )

            Spacer(Modifier.height(18.dp))

            BotonCampus(
                texto = "Publicar opinión",
                onClick = { onPublicar(elegido, calificacion, texto) },
                cargando = publicando,
                alto = 54.dp
            )
        }
    }
}
