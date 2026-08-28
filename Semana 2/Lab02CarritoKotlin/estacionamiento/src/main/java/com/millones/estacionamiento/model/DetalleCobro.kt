package com.millones.estacionamiento.model

data class DetalleCobro(
    val horasReales: Double,
    val horasCobradas: Int,
    val tarifaBase: Double,
    val subtotal: Double,
    val recargoPorcentaje: Double,
    val montoRecargo: Double,
    val descuentoPorcentaje: Double,
    val montoDescuento: Double,
    val total: Double,
    val visitaNumero: Int
)