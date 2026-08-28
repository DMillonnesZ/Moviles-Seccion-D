package com.millones.estacionamiento.model

class Camioneta(placa: String, cliente: Cliente) : Vehiculo(placa, "Camioneta", cliente) {
    override val tarifaHora: Double = 10.00
}