package com.millones.estacionamiento.model

import java.time.LocalDateTime

class Moto(
    placa: String,
    cliente: Cliente,
    horaIngreso: LocalDateTime = LocalDateTime.now()
) : Vehiculo(placa, "Moto", cliente, horaIngreso) {
    override val tarifaHora: Double = 2.00
}