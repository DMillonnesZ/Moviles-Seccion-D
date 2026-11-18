package com.millones.campusgps.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.Circle
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerComposable
import com.google.maps.android.compose.rememberUpdatedMarkerState
import com.millones.campusgps.R
import com.millones.campusgps.model.BorradorLugar
import com.millones.campusgps.model.LugarConDistancia
import com.millones.campusgps.model.Posicion
import com.millones.campusgps.model.UbicacionUi
import com.millones.campusgps.ui.componentes.PinFoto
import com.millones.campusgps.ui.componentes.VidrioMapa
import com.millones.campusgps.ui.componentes.rememberImagenLugar
import com.millones.campusgps.viewmodel.RADIO_CERCANOS
import com.millones.campusgps.viewmodel.RADIO_LUGAR_ACTUAL

// Controles nativos ocultos: la pantalla usa sus propios botones de vidrio
private val AjustesMapa = MapUiSettings(
    zoomControlsEnabled = false,
    mapToolbarEnabled = false,
    myLocationButtonEnabled = false,
    compassEnabled = false
)

// Mapa oscuro con:
// - el punto azul de Google (tu posición en tiempo real) y el radio de RADIO_LUGAR_ACTUAL m,
// - cada lugar como un círculo con su foto (el lugar actual con borde rojo),
// - mantener presionado = registrar un lugar; tocar un lugar = abrir su detalle.
@Composable
fun MapaUbicacion(
    estado: UbicacionUi,
    posicion: Posicion?,
    lugares: List<LugarConDistancia>,
    lugarActualId: Int?,
    seleccionado: Int?,
    borrador: BorradorLugar?,
    camara: CameraPositionState,
    onMantenerPresionado: (LatLng) -> Unit,
    onTocarLugar: (Int) -> Unit,
    modifier: Modifier = Modifier,
    // Espacio que tapan la cabecera, el panel y la barra: así la cámara centra en la zona visible
    contentPadding: PaddingValues = PaddingValues()
) {
    val contexto = LocalContext.current

    // Con ubicación activa el permiso ya está concedido: se enciende el punto azul de Google
    val ubicacionActiva = posicion != null
    val propiedades = remember(ubicacionActiva) {
        MapProperties(
            isMyLocationEnabled = ubicacionActiva,
            mapStyleOptions = MapStyleOptions.loadRawResourceStyle(contexto, R.raw.mapa_oscuro)
        )
    }

    // Al pedir la ubicación, el mapa viaja hasta ella
    LaunchedEffect(estado) {
        if (estado is UbicacionUi.Lista) {
            camara.animate(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(estado.posicion.latitud, estado.posicion.longitud),
                    17f
                )
            )
        }
    }

    LaunchedEffect(seleccionado) {
        val lugar = lugares.firstOrNull { it.lugar.id == seleccionado }?.lugar
        if (lugar != null) {
            camara.animate(
                CameraUpdateFactory.newLatLngZoom(LatLng(lugar.latitud, lugar.longitud), 18f)
            )
        }
    }

    GoogleMap(
        modifier = modifier,
        cameraPositionState = camara,
        properties = propiedades,
        uiSettings = AjustesMapa,
        contentPadding = contentPadding,
        onMapLongClick = onMantenerPresionado
    ) {
        // Radio alrededor de tu posición: el lugar registrado más cercano dentro de él es tu ubicación actual
        if (posicion != null) {
            // Zona de lugares cercanos (verde tenue)
            Circle(
                center = LatLng(posicion.latitud, posicion.longitud),
                radius = RADIO_CERCANOS.toDouble(),
                fillColor = ColoresLugar.Cerca.copy(alpha = 0.05f),
                strokeColor = ColoresLugar.Cerca.copy(alpha = 0.35f),
                strokeWidth = 2f
            )
            Circle(
                center = LatLng(posicion.latitud, posicion.longitud),
                radius = RADIO_LUGAR_ACTUAL.toDouble(),
                fillColor = VidrioMapa.Cian.copy(alpha = 0.10f),
                strokeColor = VidrioMapa.Cian.copy(alpha = 0.6f),
                strokeWidth = 3f
            )
        }

        // Punto que se está registrando (formulario abierto): violeta
        if (borrador != null) {
            Marker(
                state = rememberUpdatedMarkerState(position = LatLng(borrador.latitud, borrador.longitud)),
                title = "Nuevo lugar",
                icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET),
                zIndex = 3f
            )
        }

        // Lugares guardados: círculo con su foto.
        // Rojo = estás ahí, verde = cerca, amarillo = favorito lejos, azul = registrado lejos
        lugares.forEach { item ->
            key(item.lugar.id) {
                val lugar = item.lugar
                val esActual = lugar.id == lugarActualId
                val imagen = rememberImagenLugar(lugar.foto, ladoMaximo = 160)
                val colorBorde = colorDeLugar(item, esActual)

                MarkerComposable(
                    // Se vuelve a dibujar cuando cambian la foto, el color o la categoría
                    lugar.id, lugar.foto ?: "", imagen != null, colorBorde, lugar.categoria,
                    state = rememberUpdatedMarkerState(position = LatLng(lugar.latitud, lugar.longitud)),
                    title = lugar.nombre,
                    zIndex = if (esActual) 2f else 1f,
                    onClick = {
                        onTocarLugar(lugar.id)
                        true   // no muestra la ventanita de Google: se abre el detalle
                    }
                ) {
                    PinFoto(
                        imagen = imagen,
                        categoria = lugar.categoria,
                        colorBorde = colorBorde
                    )
                }
            }
        }
    }
}
