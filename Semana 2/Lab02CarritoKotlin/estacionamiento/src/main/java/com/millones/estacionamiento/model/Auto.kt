package com.millones.estacionamiento.model

class Auto(placa: String, cliente: Cliente) : Vehiculo(placa, "Auto", cliente) {
    override val tarifaHora: Double = 4.00
}