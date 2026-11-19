package com.millones.campusgps.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
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
import androidx.compose.foundation.layout.imePadding
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
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Event
import androidx.compose.material.icons.rounded.Forum
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.NearMe
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.PhotoCamera
import androidx.compose.material.icons.rounded.Place
import androidx.compose.material.icons.rounded.School
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.millones.campusgps.model.Evento
import com.millones.campusgps.model.Lugar
import com.millones.campusgps.model.RADIOS_CERCANOS
import com.millones.campusgps.ui.componentes.AvatarUsuario
import com.millones.campusgps.ui.componentes.AvisoFlotante
import com.millones.campusgps.ui.componentes.BotonCampus
import com.millones.campusgps.ui.componentes.BotonVidrio
import com.millones.campusgps.ui.componentes.CampoCampus
import com.millones.campusgps.ui.componentes.CampusColores
import com.millones.campusgps.ui.componentes.EntradaSuave
import com.millones.campusgps.ui.componentes.FotoLugar
import com.millones.campusgps.ui.componentes.FuenteCampus
import com.millones.campusgps.ui.componentes.InsetsCampus
import com.millones.campusgps.ui.componentes.LocalAbrirMenu
import com.millones.campusgps.ui.componentes.LocalRellenoBarra
import com.millones.campusgps.ui.componentes.VidrioMapa
import com.millones.campusgps.ui.componentes.rememberCerrarSesionCompleta
import com.millones.campusgps.ui.componentes.vidrioMapa
import com.millones.campusgps.viewmodel.PerfilViewModel

private val RojoSuave = Color(0xFFFF8A8A)

// Perfil: foto (se puede cambiar), nombre, carrera, estadísticas, mis lugares, mis eventos,
// ajuste del radio de "Cerca" y cerrar sesión
@Composable
fun PantallaPerfil(
    vm: PerfilViewModel,
    onCerrarSesion: () -> Unit,
    onAbrirLugar: (String) -> Unit,
    onAbrirEvento: (String) -> Unit
) {
    val correo by vm.correo.collectAsState()
    val nombreCuenta by vm.nombreCuenta.collectAsState()
    val perfil by vm.perfil.collectAsState()
    val estadisticas by vm.estadisticas.collectAsState()
    val misLugares by vm.misLugares.collectAsState()
    val misEventos by vm.misEventos.collectAsState()
    val radio by vm.radioCercanos.collectAsState()
    val editando by vm.editando.collectAsState()
    val guardando by vm.guardando.collectAsState()
    val subiendoFoto by vm.subiendoFoto.collectAsState()
    val aviso by vm.aviso.collectAsState()

    val abrirMenu = LocalAbrirMenu.current
    val relleno = LocalRellenoBarra.current
    // Cierra la sesión y también olvida la cuenta de Google elegida
    val cerrarSesion = rememberCerrarSesionCompleta(onCerrarSesion)
    var confirmarSalida by rememberSaveable { mutableStateOf(false) }

    // Nombre: el del perfil, el de la cuenta o la parte del correo antes de @
    val nombre = perfil?.nombre?.takeIf { it.isNotBlank() }
        ?: nombreCuenta?.takeIf { it.isNotBlank() }
        ?: correo?.substringBefore("@")
        ?: "Estudiante"
    val carrera = perfil?.carrera.orEmpty()

    val elegirFoto = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri -> if (uri != null) vm.cambiarFoto(uri.toString()) }

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
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(top = espacio.calculateTopPadding(), bottom = abajo + 20.dp)
            ) {
                // ----- Cabecera -----
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, top = 8.dp)
                ) {
                    BotonVidrio(icono = Icons.Rounded.Menu, descripcion = "Abrir menú", onClick = abrirMenu, tamano = 52.dp)
                    Spacer(Modifier.width(12.dp))
                    Text(
                        text = "Mi perfil",
                        color = Color.White,
                        fontFamily = FuenteCampus,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 26.sp,
                        modifier = Modifier.weight(1f)
                    )
                    BotonVidrio(icono = Icons.Rounded.Edit, descripcion = "Editar perfil", onClick = vm::abrirEdicion, tamano = 52.dp)
                }

                Spacer(Modifier.height(20.dp))

                // ----- Foto con botón de cámara -----
                EntradaSuave(retrasoMs = 80L) {
                    Box(contentAlignment = Alignment.Center) {
                        AvatarUsuario(
                            nombre = nombre,
                            fotoBase64 = perfil?.foto,
                            tamano = 116.dp,
                            grosorBorde = 4.dp,
                            colorBorde = Color.White,
                            modifier = Modifier.clickable {
                                elegirFoto.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                            }
                        )
                        if (subiendoFoto) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(116.dp)
                                    .clip(CircleShape)
                                    .background(Color.Black.copy(alpha = 0.45f))
                            ) {
                                CircularProgressIndicator(color = Color.White, strokeWidth = 3.dp, modifier = Modifier.size(34.dp))
                            }
                        }
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(VidrioMapa.Cian)
                                .border(3.dp, CampusColores.AzulNoche, CircleShape)
                                .clickable {
                                    elegirFoto.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                                }
                        ) {
                            Icon(Icons.Rounded.PhotoCamera, contentDescription = "Cambiar foto", tint = VidrioMapa.FondoSolido, modifier = Modifier.size(20.dp))
                        }
                    }
                }
                if (perfil?.foto != null) {
                    Text(
                        text = "Quitar foto",
                        color = Color.White.copy(alpha = 0.55f),
                        fontFamily = FuenteCampus,
                        fontSize = 12.sp,
                        modifier = Modifier
                            .padding(top = 6.dp)
                            .clip(RoundedCornerShape(50))
                            .clickable(onClick = vm::quitarFoto)
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                Spacer(Modifier.height(10.dp))

                // ----- Nombre, carrera y correo -----
                EntradaSuave(retrasoMs = 140L) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = nombre,
                            color = Color.White,
                            fontFamily = FuenteCampus,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 24.sp,
                            textAlign = TextAlign.Center
                        )
                        if (carrera.isNotBlank()) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 2.dp)) {
                                Icon(Icons.Rounded.School, contentDescription = null, tint = CampusColores.VerdeMenta, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(5.dp))
                                Text(carrera, color = CampusColores.VerdeMenta, fontFamily = FuenteCampus, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                            }
                        } else {
                            Text(
                                text = "Agrega tu carrera",
                                color = Color.White.copy(alpha = 0.5f),
                                fontFamily = FuenteCampus,
                                fontSize = 13.sp,
                                modifier = Modifier
                                    .padding(top = 2.dp)
                                    .clip(RoundedCornerShape(50))
                                    .clickable(onClick = vm::abrirEdicion)
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp)) {
                            Icon(Icons.Rounded.Email, contentDescription = null, tint = Color.White.copy(alpha = 0.5f), modifier = Modifier.size(14.dp))
                            Spacer(Modifier.width(5.dp))
                            Text(correo.orEmpty(), color = Color.White.copy(alpha = 0.6f), fontFamily = FuenteCampus, fontSize = 13.sp)
                        }
                    }
                }

                Spacer(Modifier.height(20.dp))

                // ----- Estadísticas -----
                EntradaSuave(retrasoMs = 200L) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Estadistica(Icons.Rounded.Place, estadisticas.lugares, "Lugares registrados", CampusColores.AzulCielo, Modifier.weight(1f))
                            Estadistica(Icons.Rounded.Star, estadisticas.favoritos, "Favoritos", VidrioMapa.Estrella, Modifier.weight(1f))
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Estadistica(Icons.Rounded.Event, estadisticas.asistire, "Eventos a los que irás", CampusColores.Violeta, Modifier.weight(1f))
                            Estadistica(Icons.Rounded.Forum, estadisticas.opiniones, "Opiniones publicadas", CampusColores.VerdeMenta, Modifier.weight(1f))
                        }
                    }
                }

                // ----- Mis lugares -----
                TituloSeccionPerfil("Mis lugares")
                if (misLugares.isEmpty()) {
                    TextoVacio("Mantén presionado el mapa para registrar tu primer lugar.")
                } else {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(misLugares, key = { it.id }) { lugar ->
                            TarjetaMiLugar(lugar = lugar, onClick = { onAbrirLugar(lugar.id) })
                        }
                    }
                }

                // ----- Mis eventos -----
                TituloSeccionPerfil("Mis próximos eventos")
                if (misEventos.isEmpty()) {
                    TextoVacio("Anótate a un evento con \"Asistiré\" o crea uno en Eventos.")
                } else {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(misEventos, key = { it.id }) { evento ->
                            TarjetaMiEvento(evento = evento, onClick = { onAbrirEvento(evento.id) })
                        }
                    }
                }

                // ----- Ajustes -----
                TituloSeccionPerfil("Ajustes")
                Column(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth()
                        .vidrioMapa(RoundedCornerShape(22.dp))
                        .padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.NearMe, contentDescription = null, tint = CampusColores.VerdeMenta, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Column {
                            Text("Radio de \"Cerca\"", color = Color.White, fontFamily = FuenteCampus, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Text(
                                "Los lugares a menos de esta distancia salen en verde y en la pestaña Cerca",
                                color = Color.White.copy(alpha = 0.6f),
                                fontFamily = FuenteCampus,
                                fontSize = 12.sp
                            )
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        RADIOS_CERCANOS.forEach { metros ->
                            ChipRadio(
                                texto = "$metros m",
                                activo = metros == radio,
                                onClick = { vm.cambiarRadio(metros) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                // ----- Cerrar sesión -----
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(RojoSuave.copy(alpha = 0.10f))
                        .border(1.dp, RojoSuave.copy(alpha = 0.35f), RoundedCornerShape(20.dp))
                        .clickable { confirmarSalida = true }
                        .padding(16.dp)
                ) {
                    Icon(Icons.AutoMirrored.Rounded.Logout, contentDescription = null, tint = RojoSuave, modifier = Modifier.size(22.dp))
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text("Cerrar sesión", color = RojoSuave, fontFamily = FuenteCampus, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text("Volverás a la pantalla de inicio", color = Color.White.copy(alpha = 0.55f), fontFamily = FuenteCampus, fontSize = 12.sp)
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

    // ----- Editar nombre y carrera -----
    if (editando) {
        EditarPerfil(
            nombreInicial = nombre,
            carreraInicial = carrera,
            guardando = guardando,
            onGuardar = vm::guardarDatos,
            onCancelar = vm::cerrarEdicion
        )
    }

    // ----- Confirmar cierre de sesión -----
    if (confirmarSalida) {
        AlertDialog(
            onDismissRequest = { confirmarSalida = false },
            containerColor = VidrioMapa.FondoSolido,
            titleContentColor = Color.White,
            textContentColor = Color.White.copy(alpha = 0.8f),
            title = { Text("¿Cerrar sesión?", fontFamily = FuenteCampus, fontWeight = FontWeight.Bold) },
            text = { Text("Tendrás que volver a ingresar a tu cuenta.", fontFamily = FuenteCampus) },
            confirmButton = {
                TextButton(onClick = {
                    confirmarSalida = false
                    cerrarSesion()
                }) { Text("Cerrar sesión", color = RojoSuave, fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { confirmarSalida = false }) {
                    Text("Cancelar", color = Color.White.copy(alpha = 0.8f))
                }
            }
        )
    }
}

@Composable
private fun TituloSeccionPerfil(texto: String) {
    Text(
        text = texto.uppercase(),
        color = Color.White.copy(alpha = 0.5f),
        fontFamily = FuenteCampus,
        fontWeight = FontWeight.Bold,
        fontSize = 11.sp,
        letterSpacing = 1.4.sp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, top = 22.dp, bottom = 10.dp)
    )
}

@Composable
private fun TextoVacio(texto: String) {
    Text(
        text = texto,
        color = Color.White.copy(alpha = 0.55f),
        fontFamily = FuenteCampus,
        fontSize = 13.sp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    )
}

// Recuadro con un número grande y su etiqueta
@Composable
private fun Estadistica(
    icono: ImageVector,
    numero: Int,
    etiqueta: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .vidrioMapa(RoundedCornerShape(20.dp))
            .padding(14.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(color.copy(alpha = 0.18f))
        ) {
            Icon(icono, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
        }
        Spacer(Modifier.width(10.dp))
        Column {
            Text("$numero", color = Color.White, fontFamily = FuenteCampus, fontWeight = FontWeight.ExtraBold, fontSize = 22.sp, lineHeight = 24.sp)
            Text(
                etiqueta,
                color = Color.White.copy(alpha = 0.6f),
                fontFamily = FuenteCampus,
                fontSize = 11.sp,
                lineHeight = 13.sp,
                maxLines = 2
            )
        }
    }
}

@Composable
private fun TarjetaMiLugar(lugar: Lugar, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(120.dp)
            .vidrioMapa(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .padding(12.dp)
    ) {
        FotoLugar(ruta = lugar.foto, categoria = lugar.categoria, tamano = 56.dp)
        Spacer(Modifier.height(8.dp))
        Text(
            text = lugar.nombre,
            color = Color.White,
            fontFamily = FuenteCampus,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
        Text(
            text = lugar.categoria,
            color = Color.White.copy(alpha = 0.55f),
            fontFamily = FuenteCampus,
            fontSize = 11.sp
        )
    }
}

@Composable
private fun TarjetaMiEvento(evento: Evento, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .width(230.dp)
            .vidrioMapa(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .padding(12.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .clip(RoundedCornerShape(14.dp))
                .background(CampusColores.Violeta.copy(alpha = 0.25f))
                .border(1.dp, CampusColores.Violeta.copy(alpha = 0.6f), RoundedCornerShape(14.dp))
                .padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
            Text(formatearFecha(evento.fecha, "d"), color = Color.White, fontFamily = FuenteCampus, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, lineHeight = 20.sp)
            Text(formatearFecha(evento.fecha, "MMM").uppercase().take(3), color = CampusColores.VerdeMenta, fontFamily = FuenteCampus, fontWeight = FontWeight.Bold, fontSize = 10.sp)
        }
        Spacer(Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = evento.titulo,
                color = Color.White,
                fontFamily = FuenteCampus,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = formatearFecha(evento.fecha, "HH:mm") + if (evento.lugarNombre.isNotBlank()) " · ${evento.lugarNombre}" else "",
                color = Color.White.copy(alpha = 0.6f),
                fontFamily = FuenteCampus,
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = if (evento.esMio) "Lo organizas" else "Asistirás",
                color = if (evento.esMio) VidrioMapa.Estrella else CampusColores.VerdeMenta,
                fontFamily = FuenteCampus,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
            )
        }
    }
}

@Composable
private fun ChipRadio(texto: String, activo: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val fondo by animateColorAsState(
        targetValue = if (activo) CampusColores.VerdeMenta.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.05f),
        label = "fondoRadio"
    )
    Text(
        text = texto,
        color = if (activo) CampusColores.VerdeMenta else Color.White.copy(alpha = 0.75f),
        fontFamily = FuenteCampus,
        fontWeight = if (activo) FontWeight.Bold else FontWeight.Medium,
        fontSize = 14.sp,
        textAlign = TextAlign.Center,
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(fondo)
            .border(1.dp, if (activo) CampusColores.VerdeMenta.copy(alpha = 0.6f) else Color.White.copy(alpha = 0.12f), RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp)
    )
}

// Ventana para editar nombre y carrera
@Composable
private fun EditarPerfil(
    nombreInicial: String,
    carreraInicial: String,
    guardando: Boolean,
    onGuardar: (String, String) -> Unit,
    onCancelar: () -> Unit
) {
    var nombre by rememberSaveable { mutableStateOf(nombreInicial) }
    var carrera by rememberSaveable { mutableStateOf(carreraInicial) }

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
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 24.dp)
                .imePadding()
                .fillMaxWidth()
                .graphicsLayer {
                    scaleX = escala.value
                    scaleY = escala.value
                }
                .vidrioMapa(RoundedCornerShape(28.dp), fondo = VidrioMapa.FondoSolido.copy(alpha = 0.97f))
                .padding(20.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Editar perfil",
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
                        .clickable(enabled = !guardando, onClick = onCancelar)
                        .padding(6.dp)
                )
            }
            CampoCampus(
                valor = nombre,
                onValorChange = { nombre = it },
                placeholder = "Nombre",
                icono = Icons.Rounded.Person,
                habilitado = !guardando,
                valido = nombre.trim().length >= 2,
                alto = 56.dp
            )
            CampoCampus(
                valor = carrera,
                onValorChange = { carrera = it },
                placeholder = "Carrera (opcional)",
                icono = Icons.Rounded.School,
                habilitado = !guardando,
                alto = 56.dp
            )
            Text(
                text = "Tu nombre y tu foto se ven en tus opiniones.",
                color = Color.White.copy(alpha = 0.55f),
                fontFamily = FuenteCampus,
                fontSize = 12.sp,
                modifier = Modifier.fillMaxWidth()
            )
            BotonCampus(
                texto = "Guardar",
                onClick = { onGuardar(nombre, carrera) },
                cargando = guardando,
                alto = 54.dp
            )
        }
    }
}
