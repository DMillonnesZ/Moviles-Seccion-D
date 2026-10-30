package com.tecsup.huellitasvet.model

import java.time.LocalTime

object Horarios {
    // 18 horarios: 09:00, 09:30 ... 17:30
    val delDia: List<String> = (0 until 18).map { i ->
        LocalTime.of(9, 0).plusMinutes(i * 30L).toString()
    }
}