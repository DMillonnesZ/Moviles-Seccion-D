package com.millones.campusgps.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MarkerInfoWindow
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberUpdatedMarkerState
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

    LaunchedEffect(estado) {
        if (estado is UbicacionUi.Lista) {
            camara.animate(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(estado.posicion.latitud, estado.posicion.longitud),
                    16f
                )
            )
        }
    }

    GoogleMap(modifier = modifier, cameraPositionState = camara) {
        if (estado is UbicacionUi.Lista) {
            val posicion = LatLng(estado.posicion.latitud, estado.posicion.longitud)
            val markerState = rememberUpdatedMarkerState(position = posicion)

            LaunchedEffect(posicion) {
                markerState.showInfoWindow()
            }

            MarkerInfoWindow(state = markerState) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Estás aquí",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            // Margen para que el difuminado no se corte
                            .padding(12.dp)
                            // Sombra gris suave y difuminada alrededor de la etiqueta
                            .shadow(
                                elevation = 10.dp,
                                shape = RoundedCornerShape(8.dp),
                                ambientColor = Color(0x80000000),
                                spotColor = Color(0x80000000)
                            )
                            .background(Color.White, RoundedCornerShape(8.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    )

                    // Separación entre la etiqueta y el pin rojo
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }

        // TODO 3 (Paso 4): un Marker por cada lugar
    }
}