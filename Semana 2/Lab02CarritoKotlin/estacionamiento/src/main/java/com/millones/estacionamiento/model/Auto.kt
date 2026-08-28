package com.millones.estacionamiento.model

import java.time.LocalDateTime

class Auto(
    placa: String,
    cliente: Cliente,
    horaIngreso: LocalDateTime = LocalDateTime.now()
) : Vehiculo(placa, "Auto", cliente, horaIngreso) {
    override val tarifaHora: Double = 4.00
}