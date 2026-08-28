package com.millones.estacionamiento.model

class Moto(placa: String, cliente: Cliente) : Vehiculo(placa, "Moto", cliente) {
    override val tarifaHora: Double = 2.00
}