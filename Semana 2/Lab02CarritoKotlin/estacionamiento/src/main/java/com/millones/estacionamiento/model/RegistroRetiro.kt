package com.millones.estacionamiento.model

data class RegistroRetiro(
    val placa: String,
    val tipo: String,
    val cliente: String,
    val documentoCliente: String,
    val horaIngreso: String,
    val horaSalida: String,
    val horasCobradas: Int,
    val tarifaBase: Double,
    val recargoPorcentaje: Double,
    val descuentoPorcentaje: Double,
    val visitaNumero: Int,
    val total: Double
)