package com.millones.estacionamiento.model

import java.time.LocalDateTime

class Camioneta(
    placa: String,
    cliente: Cliente,
    horaIngreso: LocalDateTime = LocalDateTime.now()
) : Vehiculo(placa, "Camioneta", cliente, horaIngreso) {
    override val tarifaHora: Double = 10.00
}