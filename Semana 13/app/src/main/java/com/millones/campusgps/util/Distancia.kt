package com.millones.campusgps.util

import android.location.Location
import com.millones.campusgps.model.Lugar
import com.millones.campusgps.model.Posicion

fun distanciaMetros(desde: Posicion, hasta: Lugar): Float =
    distanciaMetros(desde, Posicion(hasta.latitud, hasta.longitud))

fun distanciaMetros(desde: Posicion, hasta: Posicion): Float {
    val resultado = FloatArray(1)
    Location.distanceBetween(
        desde.latitud, desde.longitud,
        hasta.latitud, hasta.longitud,
        resultado
    )
    return resultado[0]
}
