package com.millones.campusgps.util

import java.util.Locale
import kotlin.math.roundToInt

fun formatearDistancia(metros: Float): String =
    if (metros < 1000f) {
        "${metros.roundToInt()} m"
    } else {
        "%.1f km".format(Locale.US, metros / 1000f)
    }