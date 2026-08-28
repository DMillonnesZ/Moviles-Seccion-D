package com.millones.estacionamiento.model

import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

abstract class Vehiculo(
    val placa: String,
    val tipo: String,
    val cliente: Cliente
) {
    val horaIngreso: LocalDateTime = LocalDateTime.now()

    abstract val tarifaHora: Double


    fun horasTranscurridas(horaReferencia: LocalDateTime = LocalDateTime.now()): Double {
        val minutos = Duration.between(horaIngreso, horaReferencia).toMinutes()
        return minutos / 60.0
    }

    override fun toString(): String {
        val formato = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
        return "Placa: $placa | Tipo: $tipo | Cliente: ${cliente.nombre} | " +
                "Tarifa base: S/ ${"%.2f".format(tarifaHora)} | Ingreso: ${horaIngreso.format(formato)}"
    }
}