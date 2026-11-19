package com.millones.campusgps.ui

import androidx.compose.ui.Modifier
import androidx.compose.runtime.Composable
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.rememberCameraPositionState
import com.millones.campusgps.model.LugarConDistancia
import com.millones.campusgps.model.UbicacionUi

@Composable
fun MapaUbicacion(
    estado: UbicacionUi,
    lugares: List<LugarConDistancia>,
    seleccionado: Int?,
    modifier: Modifier = Modifier
) {
    val camara = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(-12.0464, -77.0428), 12f)
    }

    // TODO 1: LaunchedEffect(estado): si es Lista, animar la cámara (zoom 16f)

    GoogleMap(modifier = modifier, cameraPositionState = camara) {
        // TODO 2: si el estado es Lista, un Marker "Estás aquí"
        // TODO 3 (Paso 4): un Marker por cada lugar
    }
}