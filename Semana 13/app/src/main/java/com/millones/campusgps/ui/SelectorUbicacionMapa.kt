package com.millones.campusgps.ui

import android.Manifest
import android.content.pm.PackageManager
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Place
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.CameraMoveStartedReason
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState
import com.millones.campusgps.R
import com.millones.campusgps.model.ResultadoBusqueda
import com.millones.campusgps.ui.componentes.BotonCampus
import com.millones.campusgps.ui.componentes.BotonVidrio
import com.millones.campusgps.ui.componentes.CampusColores
import com.millones.campusgps.ui.componentes.CianCampus
import com.millones.campusgps.ui.componentes.FuenteCampus
import com.millones.campusgps.ui.componentes.InsetsCampus
import com.millones.campusgps.ui.componentes.VidrioMapa
import com.millones.campusgps.ui.componentes.vidrioMapa
import kotlinx.coroutines.launch

// Mapa a pantalla completa para elegir un punto, de dos formas:
// - buscando por nombre (lugares registrados y direcciones) y tocando un resultado,
// - arrastrando el mapa hasta dejar el pin del centro en el lugar.
// "Usar este punto" devuelve las coordenadas y, si vino de la búsqueda, su nombre.
@Composable
fun SelectorUbicacionMapa(
    inicial: LatLng,
    resultados: List<ResultadoBusqueda>,
    buscando: Boolean,
    busquedaHecha: Boolean,
    onBuscar: (texto: String, latitud: Double, longitud: Double, inmediato: Boolean) -> Unit,
    onLimpiarBusqueda: () -> Unit,
    onElegir: (punto: LatLng, nombre: String?) -> Unit,
    onCancelar: () -> Unit
) {
    val contexto = LocalContext.current
    val foco = LocalFocusManager.current
    val alcance = rememberCoroutineScope()
    val camara = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(inicial, 17f)
    }
    var texto by rememberSaveable { mutableStateOf("") }
    // Nombre del resultado elegido; se borra si luego se arrastra el mapa a otro lado
    var nombreElegido by rememberSaveable { mutableStateOf<String?>(null) }
    var verResultados by remember { mutableStateOf(false) }

    // Con permiso se muestra el punto azul y el botón nativo para ir a tu posición
    val conPermiso = remember {
        ContextCompat.checkSelfPermission(contexto, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(contexto, Manifest.permission.ACCESS_COARSE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED
    }
    val propiedades = remember {
        MapProperties(
            isMyLocationEnabled = conPermiso,
            mapStyleOptions = MapStyleOptions.loadRawResourceStyle(contexto, R.raw.mapa_oscuro)
        )
    }

    // El pin se levanta mientras se arrastra el mapa
    val levantado by animateFloatAsState(
        targetValue = if (camara.isMoving) 1f else 0f,
        animationSpec = spring(dampingRatio = 0.5f),
        label = "pinLevantado"
    )
    // Si el usuario arrastra el mapa, el punto ya no es el del resultado buscado
    LaunchedEffect(camara.isMoving) {
        if (camara.isMoving && camara.cameraMoveStartedReason == CameraMoveStartedReason.GESTURE) {
            nombreElegido = null
            verResultados = false
            foco.clearFocus()
        }
    }

    fun buscar(inmediato: Boolean) {
        val centro = camara.position.target
        verResultados = true
        onBuscar(texto, centro.latitude, centro.longitude, inmediato)
    }

    fun irA(resultado: ResultadoBusqueda) {
        nombreElegido = resultado.titulo
        texto = resultado.titulo
        verResultados = false
        foco.clearFocus()
        onLimpiarBusqueda()
        alcance.launch {
            camara.animate(
                CameraUpdateFactory.newLatLngZoom(LatLng(resultado.latitud, resultado.longitud), 18f),
                700
            )
        }
    }

    Dialog(
        onDismissRequest = onCancelar,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = camara,
                properties = propiedades,
                uiSettings = MapUiSettings(
                    zoomControlsEnabled = false,
                    mapToolbarEnabled = false,
                    myLocationButtonEnabled = conPermiso
                ),
                onMapClick = {
                    verResultados = false
                    foco.clearFocus()
                }
            )

            // Pin fijo en el centro (la punta marca el punto exacto)
            Icon(
                Icons.Rounded.LocationOn,
                contentDescription = null,
                tint = CampusColores.Violeta,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(bottom = 46.dp)
                    .size(48.dp)
                    .graphicsLayer { translationY = -14.dp.toPx() * levantado }
            )
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.4f + 0.3f * (1f - levantado)))
            )

            // ----- Cabecera: volver, título y buscador -----
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(VidrioMapa.FondoSolido, VidrioMapa.FondoSolido.copy(alpha = 0.6f), Color.Transparent)
                        )
                    )
                    .windowInsetsPadding(InsetsCampus)
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    BotonVidrio(
                        icono = Icons.AutoMirrored.Rounded.ArrowBack,
                        descripcion = "Volver",
                        onClick = onCancelar,
                        tamano = 48.dp
                    )
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "¿Dónde será el evento?",
                            color = Color.White,
                            fontFamily = FuenteCampus,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 18.sp
                        )
                        Text(
                            text = "Busca un lugar o mueve el mapa hasta el pin",
                            color = Color.White.copy(alpha = 0.7f),
                            fontFamily = FuenteCampus,
                            fontSize = 13.sp
                        )
                    }
                }

                Spacer(Modifier.height(12.dp))

                BarraBusqueda(
                    texto = texto,
                    buscando = buscando,
                    onTexto = {
                        texto = it
                        buscar(inmediato = false)
                    },
                    onBuscar = { buscar(inmediato = true) },
                    onLimpiar = {
                        texto = ""
                        nombreElegido = null
                        verResultados = false
                        onLimpiarBusqueda()
                    },
                    onEnfocar = { if (texto.isNotBlank()) verResultados = true }
                )

                // Resultados debajo del buscador
                AnimatedVisibility(
                    visible = verResultados && (resultados.isNotEmpty() || (busquedaHecha && !buscando)),
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    ListaResultados(
                        resultados = resultados,
                        onElegir = ::irA,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }

            // ----- Confirmar -----
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .windowInsetsPadding(InsetsCampus)
                    .padding(16.dp)
                    .fillMaxWidth()
                    .vidrioMapa(RoundedCornerShape(24.dp))
                    .padding(14.dp)
            ) {
                nombreElegido?.let {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(start = 4.dp, bottom = 10.dp)
                    ) {
                        Icon(Icons.Rounded.Place, contentDescription = null, tint = CianCampus, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = it,
                            color = Color.White,
                            fontFamily = FuenteCampus,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                BotonCampus(
                    texto = "Usar este punto",
                    onClick = { onElegir(camara.position.target, nombreElegido) },
                    alto = 52.dp
                )
            }
        }
    }
}

// Campo de búsqueda de vidrio con lupa, ruedita mientras busca y botón para borrar
@Composable
private fun BarraBusqueda(
    texto: String,
    buscando: Boolean,
    onTexto: (String) -> Unit,
    onBuscar: () -> Unit,
    onLimpiar: () -> Unit,
    onEnfocar: () -> Unit
) {
    val forma = RoundedCornerShape(18.dp)
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .vidrioMapa(forma)
            .border(1.dp, CianCampus.copy(alpha = 0.35f), forma)
            .padding(horizontal = 14.dp)
    ) {
        Icon(Icons.Rounded.Search, contentDescription = null, tint = CianCampus, modifier = Modifier.size(22.dp))
        Spacer(Modifier.width(10.dp))
        Box(
            contentAlignment = Alignment.CenterStart,
            modifier = Modifier.weight(1f)
        ) {
            if (texto.isEmpty()) {
                Text(
                    text = "Buscar lugar o dirección",
                    color = Color.White.copy(alpha = 0.55f),
                    fontFamily = FuenteCampus,
                    fontSize = 15.sp
                )
            }
            BasicTextField(
                value = texto,
                onValueChange = onTexto,
                singleLine = true,
                textStyle = TextStyle(color = Color.White, fontFamily = FuenteCampus, fontSize = 15.sp),
                cursorBrush = SolidColor(CianCampus),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { onBuscar() }),
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { if (it.isFocused) onEnfocar() }
            )
        }
        when {
            buscando -> CircularProgressIndicator(
                color = CianCampus,
                strokeWidth = 2.dp,
                modifier = Modifier.size(18.dp)
            )
            texto.isNotEmpty() -> Icon(
                Icons.Rounded.Close,
                contentDescription = "Borrar búsqueda",
                tint = Color.White.copy(alpha = 0.7f),
                modifier = Modifier
                    .size(22.dp)
                    .clip(CircleShape)
                    .clickable(onClick = onLimpiar)
            )
        }
    }
}

// Lista de resultados: lugares registrados (estrella) y direcciones (pin)
@Composable
private fun ListaResultados(
    resultados: List<ResultadoBusqueda>,
    onElegir: (ResultadoBusqueda) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(max = 300.dp)
            .vidrioMapa(RoundedCornerShape(18.dp), fondo = VidrioMapa.FondoSolido.copy(alpha = 0.96f))
            .verticalScroll(rememberScrollState())
            .padding(vertical = 6.dp)
    ) {
        if (resultados.isEmpty()) {
            Text(
                text = "Sin resultados. Prueba con otro nombre o mueve el mapa.",
                color = Color.White.copy(alpha = 0.65f),
                fontFamily = FuenteCampus,
                fontSize = 13.sp,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
            )
        }
        resultados.forEach { resultado ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onElegir(resultado) }
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                val color = if (resultado.esLugarRegistrado) VidrioMapa.Estrella else CianCampus
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(color.copy(alpha = 0.15f))
                ) {
                    Icon(
                        imageVector = if (resultado.esLugarRegistrado) Icons.Rounded.Star else Icons.Rounded.Place,
                        contentDescription = null,
                        tint = color,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = resultado.titulo,
                        color = Color.White,
                        fontFamily = FuenteCampus,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (resultado.detalle.isNotBlank()) {
                        Text(
                            text = resultado.detalle,
                            color = Color.White.copy(alpha = 0.6f),
                            fontFamily = FuenteCampus,
                            fontSize = 12.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}
