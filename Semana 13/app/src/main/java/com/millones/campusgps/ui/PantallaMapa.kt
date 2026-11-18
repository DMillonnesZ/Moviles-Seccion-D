package com.millones.campusgps.ui

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.AddLocationAlt
import androidx.compose.material.icons.rounded.BookmarkAdd
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.MyLocation
import androidx.compose.material.icons.rounded.NearMe
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.rememberCameraPositionState
import com.millones.campusgps.model.LugarConDistancia
import com.millones.campusgps.model.UbicacionUi
import com.millones.campusgps.ui.componentes.AvisoTemporal
import com.millones.campusgps.ui.componentes.BotonVidrio
import com.millones.campusgps.ui.componentes.CampusColores
import com.millones.campusgps.ui.componentes.DegradadosCampus
import com.millones.campusgps.ui.componentes.EntradaSuave
import com.millones.campusgps.ui.componentes.FotoLugar
import com.millones.campusgps.ui.componentes.FuenteCampus
import com.millones.campusgps.ui.componentes.InsetsCampus
import com.millones.campusgps.ui.componentes.LocalAbrirMenu
import com.millones.campusgps.ui.componentes.LocalRellenoBarra
import com.millones.campusgps.ui.componentes.VidrioMapa
import com.millones.campusgps.ui.componentes.vidrioMapa
import com.millones.campusgps.viewmodel.CampusViewModelFactory
import com.millones.campusgps.viewmodel.MapaViewModel
import com.millones.campusgps.viewmodel.RADIO_CERCANOS
import kotlinx.coroutines.launch

private val ALTO_PANEL_ABIERTO = 372.dp
private val ALTO_PANEL_CERRADO = 64.dp

// Pestañas del panel
private const val PESTANA_FAVORITOS = 0
private const val PESTANA_CERCA = 1

// Mapa oscuro a pantalla completa: cabecera de vidrio, menú lateral, zoom, "Tu ubicación"
// con la calle y el panel de Lugares Favoritos. Mantener presionado el mapa registra un lugar.
@Composable
fun PantallaMapa() {
    val contexto = LocalContext.current
    val vm: MapaViewModel = viewModel(factory = CampusViewModelFactory(contexto))
    val estado by vm.estado.collectAsState()
    val lugares by vm.lugares.collectAsState()
    val favoritos by vm.favoritos.collectAsState()
    val cercanos by vm.cercanos.collectAsState()
    val seleccionado by vm.seleccionado.collectAsState()
    val direccion by vm.direccion.collectAsState()
    val posicion by vm.posicion.collectAsState()
    val lugarActual by vm.lugarActual.collectAsState()
    val borrador by vm.borrador.collectAsState()
    val guardando by vm.guardando.collectAsState()
    val aviso by vm.aviso.collectAsState()

    val abrirMenu = LocalAbrirMenu.current
    val alcance = rememberCoroutineScope()
    val camara = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(-12.0464, -77.0428), 12f)
    }

    // ----- Permiso de ubicación -----
    val lanzador = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { resultado ->
        if (resultado.values.any { it }) vm.buscar() else vm.permisoDenegado()
    }

    // Busca tu ubicación actual (pide el permiso si falta); el mapa viaja hasta ahí
    fun irAMiUbicacion() {
        val tienePermiso = ContextCompat.checkSelfPermission(
            contexto, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    contexto, Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED

        if (tienePermiso) {
            vm.buscar()
        } else {
            lanzador.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    // ----- Cambiar la foto de un lugar guardado -----
    var lugarFoto by rememberSaveable { mutableStateOf<Int?>(null) }
    val elegirFoto = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        val id = lugarFoto
        if (uri != null && id != null) vm.subirFoto(id, uri.toString())
        lugarFoto = null
    }

    fun cambiarFoto(id: Int) {
        lugarFoto = id
        elegirFoto.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    // ----- Panel -----
    val relleno = LocalRellenoBarra.current
    var panelAbierto by rememberSaveable { mutableStateOf(true) }
    var pestana by rememberSaveable { mutableIntStateOf(PESTANA_FAVORITOS) }
    val altoPanel by animateDpAsState(
        targetValue = if (panelAbierto) ALTO_PANEL_ABIERTO else ALTO_PANEL_CERRADO,
        animationSpec = spring(dampingRatio = 0.8f, stiffness = Spring.StiffnessMediumLow),
        label = "altoPanel"
    )

    // Lugar a eliminar (ventana de confirmación)
    var porEliminar by remember { mutableStateOf<LugarConDistancia?>(null) }
    // Lugar cuyo detalle está abierto
    var detalleId by rememberSaveable { mutableStateOf<Int?>(null) }

    // Lugar registrado dentro del radio = tu ubicación actual
    val aquiId = lugarActual?.lugar?.id
    val error = estado as? UbicacionUi.Error

    // Guarda el último error para que se vea mientras la tarjeta se desvanece
    var ultimoError by remember { mutableStateOf<UbicacionUi.Error?>(null) }
    if (error != null) ultimoError = error
    // Igual con el color del aviso
    var avisoExito by remember { mutableStateOf(true) }
    aviso?.let { avisoExito = it.exito }

    Scaffold(
        containerColor = CampusColores.AzulNoche,
        contentWindowInsets = InsetsCampus
    ) { espacio ->
        val arriba = espacio.calculateTopPadding()
        // Con la barra visible, su alto ya incluye la parte de abajo de la pantalla
        val abajo = if (relleno > 0.dp) relleno else espacio.calculateBottomPadding()

        Box(modifier = Modifier.fillMaxSize()) {
            MapaUbicacion(
                estado = estado,
                posicion = posicion,
                lugares = lugares,
                lugarActualId = aquiId,
                seleccionado = seleccionado,
                borrador = borrador,
                camara = camara,
                onMantenerPresionado = { punto -> vm.nuevoLugarEn(punto.latitude, punto.longitude) },
                onTocarLugar = { detalleId = it },
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(top = arriba + 96.dp, bottom = abajo + altoPanel + 80.dp)
            )

            // Velo oscuro arriba para que la cabecera se despegue del mapa
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(arriba + 120.dp)
                    .background(
                        Brush.verticalGradient(
                            listOf(CampusColores.AzulNoche.copy(alpha = 0.75f), Color.Transparent)
                        )
                    )
            )

            // ----- Cabecera: menú + tarjeta con el estado y el botón azul -----
            EntradaSuave(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(start = 16.dp, end = 16.dp, top = arriba + 8.dp)
            ) {
                CabeceraMapa(
                    estado = estado,
                    onMenu = abrirMenu,
                    onUbicar = { irAMiUbicacion() }
                )
            }

            // ----- Aviso (lugar guardado, foto, errores...) -----
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(start = 16.dp, end = 16.dp, top = arriba + 92.dp)
            ) {
                AvisoTemporal(
                    mensaje = aviso?.texto,
                    onOcultar = vm::limpiarAviso,
                    exito = avisoExito
                )
            }

            // ----- Tu ubicación: calle donde estás (izquierda, encima del panel) -----
            EntradaSuave(
                retrasoMs = 100L,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 16.dp, bottom = abajo + altoPanel + 14.dp)
            ) {
                ChipUbicacion(
                    estado = estado,
                    direccion = direccion,
                    lugarActual = lugarActual,
                    onClick = {
                        val actual = lugarActual
                        if (actual != null) detalleId = actual.lugar.id else irAMiUbicacion()
                    },
                    onMarcar = vm::marcarLugarActual
                )
            }

            // ----- Ir a mi ubicación + zoom (derecha, encima del panel) -----
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 16.dp, bottom = abajo + altoPanel + 14.dp)
            ) {
                BotonVidrio(
                    icono = Icons.Rounded.NearMe,
                    descripcion = "Ir a mi ubicación actual",
                    onClick = { irAMiUbicacion() },
                    cargando = estado is UbicacionUi.Buscando
                )
                ControlesZoom(
                    onAcercar = { alcance.launch { camara.animate(CameraUpdateFactory.zoomIn()) } },
                    onAlejar = { alcance.launch { camara.animate(CameraUpdateFactory.zoomOut()) } }
                )
            }

            // ----- Panel Lugares Favoritos -----
            EntradaSuave(
                retrasoMs = 150L,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(start = 12.dp, end = 12.dp, bottom = abajo + 8.dp)
            ) {
                PanelLugares(
                    favoritos = favoritos,
                    cercanos = cercanos,
                    hayLugares = lugares.isNotEmpty(),
                    ubicado = posicion != null,
                    pestana = pestana,
                    onPestana = { pestana = it },
                    seleccionado = seleccionado,
                    aquiId = aquiId,
                    abierto = panelAbierto,
                    onAlternar = { panelAbierto = !panelAbierto },
                    onSeleccionar = { detalleId = it },
                    onFavorito = { vm.alternarFavorito(it) },
                    onCambiarFoto = { cambiarFoto(it) },
                    onEliminar = { id -> porEliminar = lugares.firstOrNull { it.lugar.id == id } },
                    modifier = Modifier.height(altoPanel)
                )
            }

            // ----- Error encima del mapa -----
            AnimatedVisibility(
                visible = error != null,
                enter = fadeIn(tween(250)) + scaleIn(initialScale = 0.92f),
                exit = fadeOut(tween(200)) + scaleOut(targetScale = 0.92f),
                modifier = Modifier.fillMaxSize()
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .background(CampusColores.AzulNoche.copy(alpha = 0.65f))
                        // Bloquea los toques al mapa mientras se ve el error
                        .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { }
                        .padding(start = 24.dp, end = 24.dp, top = arriba, bottom = abajo)
                ) {
                    ultimoError?.let {
                        MensajeError(error = it, onReintentar = { irAMiUbicacion() })
                    }
                }
            }
        }
    }

    // ----- Registrar lugar (mantener presionado el mapa o "Guardar aquí") -----
    borrador?.let { punto ->
        FormularioLugar(
            borrador = punto,
            guardando = guardando,
            onGuardar = vm::guardarLugar,
            onCancelar = vm::cancelarBorrador
        )
    }

    // ----- Detalle del lugar (tocar su marcador o su tarjeta) -----
    val detalle = lugares.firstOrNull { it.lugar.id == detalleId }
    if (detalle != null) {
        DetalleLugar(
            item = detalle,
            esActual = detalle.lugar.id == aquiId,
            onCerrar = { detalleId = null },
            onFavorito = { vm.alternarFavorito(detalle.lugar.id) },
            onCambiarFoto = { cambiarFoto(detalle.lugar.id) },
            onVerEnMapa = {
                vm.seleccionar(detalle.lugar.id)
                detalleId = null
            },
            onEliminar = {
                porEliminar = detalle
                detalleId = null
            }
        )
    }

    // ----- Confirmar eliminación -----
    porEliminar?.let { item ->
        AlertDialog(
            onDismissRequest = { porEliminar = null },
            containerColor = VidrioMapa.FondoSolido,
            titleContentColor = Color.White,
            textContentColor = Color.White.copy(alpha = 0.8f),
            title = { Text("¿Eliminar lugar?", fontFamily = FuenteCampus, fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "Se borrará \"${item.lugar.nombre}\" con su foto. No se puede deshacer.",
                    fontFamily = FuenteCampus
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    vm.eliminar(item.lugar.id)
                    porEliminar = null
                }) {
                    Text("Eliminar", color = Color(0xFFFF8A8A), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                Row {
                    // Si es favorito también se puede solo sacar de favoritos
                    if (item.lugar.favorito) {
                        TextButton(onClick = {
                            vm.alternarFavorito(item.lugar.id)
                            porEliminar = null
                        }) {
                            Text("Quitar de favoritos", color = VidrioMapa.Estrella)
                        }
                    }
                    TextButton(onClick = { porEliminar = null }) {
                        Text("Cancelar", color = Color.White.copy(alpha = 0.8f))
                    }
                }
            }
        )
    }
}

// Cabecera: botón de menú y tarjeta de vidrio con logo, nombre, estado y botón azul de ubicarme
@Composable
private fun CabeceraMapa(
    estado: UbicacionUi,
    onMenu: () -> Unit,
    onUbicar: () -> Unit
) {
    val (texto, color) = when (estado) {
        UbicacionUi.Inicial -> "Toca el botón azul para ubicarte" to CampusColores.VerdeMenta
        UbicacionUi.Buscando -> "Buscando tu ubicación..." to CampusColores.Ambar
        is UbicacionUi.Lista -> "Mantén presionado el mapa para guardar" to CampusColores.VerdeMenta
        is UbicacionUi.Error -> "Sin ubicación" to Color(0xFFFF8A8A)
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        BotonVidrio(
            icono = Icons.Rounded.Menu,
            descripcion = "Abrir menú",
            onClick = onMenu,
            tamano = 56.dp
        )

        Spacer(Modifier.width(10.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .weight(1f)
                .vidrioMapa(RoundedCornerShape(24.dp))
                .padding(horizontal = 10.dp, vertical = 9.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(DegradadosCampus.Primario)
                    .border(1.dp, Color.White.copy(alpha = 0.3f), CircleShape)
            ) {
                Icon(Icons.Rounded.LocationOn, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
            }

            Spacer(Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = buildAnnotatedString {
                        withStyle(SpanStyle(color = Color.White)) { append("Campus") }
                        withStyle(SpanStyle(color = CampusColores.VerdeMenta)) { append("GPS") }
                    },
                    fontFamily = FuenteCampus,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 19.sp
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    PuntoEstado(
                        color = color,
                        latiendo = estado is UbicacionUi.Buscando || estado is UbicacionUi.Lista
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = texto,
                        color = Color.White.copy(alpha = 0.75f),
                        fontFamily = FuenteCampus,
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(Modifier.width(8.dp))

            BotonVidrio(
                icono = Icons.Rounded.MyLocation,
                descripcion = "Obtener mi ubicación",
                onClick = onUbicar,
                tamano = 44.dp,
                forma = RoundedCornerShape(14.dp),
                fondo = DegradadosCampus.Primario,
                cargando = estado is UbicacionUi.Buscando
            )
        }
    }
}

// Punto de color; si late, su opacidad sube y baja
@Composable
private fun PuntoEstado(color: Color, latiendo: Boolean) {
    val transicion = rememberInfiniteTransition(label = "puntoEstado")
    val pulso by transicion.animateFloat(
        initialValue = 0.35f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(800), RepeatMode.Reverse),
        label = "pulso"
    )
    Box(
        modifier = Modifier
            .size(8.dp)
            .alpha(if (latiendo) pulso else 1f)
            .clip(CircleShape)
            .background(color)
    )
}

// "Tu ubicación": si estás dentro del radio de un lugar que registraste muestra ese lugar
// (con su foto) y la estrella para registrarlo en favoritos; si no, la calle donde estás.
@Composable
private fun ChipUbicacion(
    estado: UbicacionUi,
    direccion: String?,
    lugarActual: LugarConDistancia?,
    onClick: () -> Unit,
    onMarcar: () -> Unit
) {
    val subtitulo = when {
        lugarActual != null -> lugarActual.lugar.nombre
        estado is UbicacionUi.Inicial -> "Toca para ubicarte"
        estado is UbicacionUi.Buscando -> "Buscando..."
        estado is UbicacionUi.Lista -> direccion ?: "Buscando la calle..."
        else -> "Sin ubicación"
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .widthIn(max = 240.dp)
            .vidrioMapa(RoundedCornerShape(22.dp))
            .clickable(onClick = onClick)
            .padding(start = 8.dp, end = 4.dp, top = 8.dp, bottom = 8.dp)
    ) {
        if (lugarActual != null) {
            // Estás en un lugar registrado: su foto
            FotoLugar(ruta = lugarActual.lugar.foto, categoria = lugarActual.lugar.categoria, tamano = 40.dp)
        } else {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(DegradadosCampus.Primario)
            ) {
                Icon(Icons.Rounded.NearMe, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
            }
        }
        Spacer(Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f, fill = false)) {
            Text(
                text = if (lugarActual != null) "Estás en" else "Tu ubicación",
                color = Color.White,
                fontFamily = FuenteCampus,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Text(
                text = subtitulo,
                color = VidrioMapa.Cian,
                fontFamily = FuenteCampus,
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Spacer(Modifier.width(4.dp))
        if (lugarActual != null) {
            // Solo dentro del radio se puede registrar el lugar actual en favoritos
            EstrellaFavorito(activa = lugarActual.lugar.favorito, onClick = onMarcar)
        } else {
            Icon(
                Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.7f),
                modifier = Modifier.padding(end = 6.dp)
            )
        }
    }
}

// Botones + y − en una sola columna de vidrio
@Composable
private fun ControlesZoom(onAcercar: () -> Unit, onAlejar: () -> Unit) {
    Column(
        modifier = Modifier
            .width(52.dp)
            .vidrioMapa(RoundedCornerShape(18.dp))
    ) {
        BotonZoom(Icons.Rounded.Add, "Acercar", onAcercar)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
                .height(1.dp)
                .background(Color.White.copy(alpha = 0.15f))
        )
        BotonZoom(Icons.Rounded.Remove, "Alejar", onAlejar)
    }
}

@Composable
private fun BotonZoom(icono: ImageVector, descripcion: String, onClick: () -> Unit) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .clickable(onClick = onClick)
    ) {
        Icon(icono, contentDescription = descripcion, tint = Color.White, modifier = Modifier.size(24.dp))
    }
}

// Panel de vidrio: título "Lugares Favoritos", pestañas Favoritos / Todos y la lista.
// Se despliega o se recoge tocando la cabecera.
@Composable
private fun PanelLugares(
    favoritos: List<LugarConDistancia>,
    cercanos: List<LugarConDistancia>,
    hayLugares: Boolean,
    ubicado: Boolean,
    pestana: Int,
    onPestana: (Int) -> Unit,
    seleccionado: Int?,
    aquiId: Int?,
    abierto: Boolean,
    onAlternar: () -> Unit,
    onSeleccionar: (Int) -> Unit,
    onFavorito: (Int) -> Unit,
    onCambiarFoto: (Int) -> Unit,
    onEliminar: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val giro by animateFloatAsState(
        targetValue = if (abierto) 180f else 0f,
        label = "giroFlecha"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .vidrioMapa(RoundedCornerShape(28.dp))
    ) {
        // Cabecera (toca para desplegar / recoger)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .height(ALTO_PANEL_CERRADO)
                .clickable(onClick = onAlternar)
                .padding(horizontal = 16.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(CampusColores.VerdeMenta.copy(alpha = 0.16f))
                    .border(1.dp, CampusColores.VerdeMenta.copy(alpha = 0.5f), CircleShape)
            ) {
                Icon(Icons.Rounded.Star, contentDescription = null, tint = CampusColores.VerdeMenta, modifier = Modifier.size(19.dp))
            }
            Spacer(Modifier.width(12.dp))
            Text(
                text = "Lugares Favoritos",
                color = Color.White,
                fontFamily = FuenteCampus,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 19.sp,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "${favoritos.size}",
                color = Color.White,
                fontFamily = FuenteCampus,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(Color.White.copy(alpha = 0.08f))
                    .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(50))
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            )
            Spacer(Modifier.width(8.dp))
            Icon(
                imageVector = Icons.Rounded.KeyboardArrowUp,
                contentDescription = if (abierto) "Recoger panel" else "Desplegar panel",
                tint = Color.White.copy(alpha = 0.8f),
                modifier = Modifier.graphicsLayer { rotationZ = giro }
            )
        }

        // Pestañas Favoritos / Cerca
        SelectorPestanas(
            pestana = pestana,
            totalFavoritos = favoritos.size,
            totalCerca = cercanos.size,
            onPestana = onPestana,
            modifier = Modifier.padding(horizontal = 12.dp)
        )

        Leyenda(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))

        Crossfade(
            targetState = pestana,
            label = "pestanaLugares",
            modifier = Modifier.weight(1f)
        ) { actual ->
            val lista = if (actual == PESTANA_FAVORITOS) favoritos else cercanos
            when {
                !hayLugares -> PanelVacio(
                    icono = Icons.Rounded.AddLocationAlt,
                    titulo = "Aún no guardas lugares",
                    mensaje = "Mantén presionado un punto del mapa para guardar su título, descripción y foto."
                )
                lista.isEmpty() && actual == PESTANA_FAVORITOS -> PanelVacio(
                    icono = Icons.Rounded.StarBorder,
                    titulo = "Aún no tienes favoritos",
                    mensaje = "Marca la estrella de un lugar para tenerlo aquí.",
                    accion = "Ver lugares cerca",
                    onAccion = { onPestana(PESTANA_CERCA) }
                )
                lista.isEmpty() && !ubicado -> PanelVacio(
                    icono = Icons.Rounded.NearMe,
                    titulo = "Ubícate primero",
                    mensaje = "Toca el botón azul para ver los lugares a menos de ${RADIO_CERCANOS.toInt()} m."
                )
                lista.isEmpty() -> PanelVacio(
                    icono = Icons.Rounded.NearMe,
                    titulo = "No hay lugares cerca",
                    mensaje = "Ninguno de tus lugares está a menos de ${RADIO_CERCANOS.toInt()} m. Los ves en el mapa en azul o amarillo."
                )
                else -> ListaLugares(
                    lugares = lista,
                    seleccionado = seleccionado,
                    aquiId = aquiId,
                    onClick = onSeleccionar,
                    onFavorito = onFavorito,
                    onCambiarFoto = onCambiarFoto,
                    onEliminar = onEliminar
                )
            }
        }
    }
}

// Dos pestañas en píldora; la activa se pinta de menta
@Composable
private fun SelectorPestanas(
    pestana: Int,
    totalFavoritos: Int,
    totalCerca: Int,
    onPestana: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(50))
            .background(Color.White.copy(alpha = 0.05f))
            .border(1.dp, Color.White.copy(alpha = 0.10f), RoundedCornerShape(50))
            .padding(4.dp)
    ) {
        listOf("Favoritos · $totalFavoritos", "Cerca · $totalCerca").forEachIndexed { indice, texto ->
            val activa = indice == pestana
            val fondo by animateColorAsState(
                targetValue = if (activa) CampusColores.VerdeMenta.copy(alpha = 0.18f) else Color.Transparent,
                label = "fondoPestana"
            )
            val color by animateColorAsState(
                targetValue = if (activa) CampusColores.VerdeMenta else Color.White.copy(alpha = 0.65f),
                label = "colorPestana"
            )
            Text(
                text = texto,
                color = color,
                fontFamily = FuenteCampus,
                fontWeight = if (activa) FontWeight.Bold else FontWeight.Medium,
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(50))
                    .background(fondo)
                    .clickable { onPestana(indice) }
                    .padding(vertical = 7.dp)
            )
        }
    }
}

// Qué significa cada color (marcadores y tarjetas)
@Composable
private fun Leyenda(modifier: Modifier = Modifier) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier.fillMaxWidth()
    ) {
        listOf(
            ColoresLugar.Actual to "Aquí",
            ColoresLugar.Cerca to "Cerca",
            ColoresLugar.Favorito to "Favorito",
            ColoresLugar.Registrado to "Registrado"
        ).forEach { (color, texto) ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(color)
                )
                Spacer(Modifier.width(5.dp))
                Text(
                    text = texto,
                    color = Color.White.copy(alpha = 0.65f),
                    fontFamily = FuenteCampus,
                    fontSize = 11.sp
                )
            }
        }
    }
}

// Panel sin lugares: explica qué hacer
@Composable
private fun PanelVacio(
    icono: ImageVector,
    titulo: String,
    mensaje: String,
    accion: String? = null,
    onAccion: () -> Unit = {}
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 12.dp)
    ) {
        Icon(icono, contentDescription = null, tint = VidrioMapa.Estrella, modifier = Modifier.size(40.dp))
        Spacer(Modifier.height(8.dp))
        Text(
            text = titulo,
            color = Color.White,
            fontFamily = FuenteCampus,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = mensaje,
            color = Color.White.copy(alpha = 0.7f),
            fontFamily = FuenteCampus,
            fontSize = 13.sp,
            textAlign = TextAlign.Center
        )
        if (accion != null) {
            Spacer(Modifier.height(10.dp))
            Text(
                text = accion,
                color = CampusColores.VerdeMenta,
                fontFamily = FuenteCampus,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .clickable(onClick = onAccion)
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            )
        }
    }
}
