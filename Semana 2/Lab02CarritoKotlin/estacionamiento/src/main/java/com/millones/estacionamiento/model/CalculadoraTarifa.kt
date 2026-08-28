package com.millones.estacionamiento.model

import kotlin.math.ceil

object CalculadoraTarifa {

    fun calcular(vehiculo: Vehiculo, horasReales: Double, visitaNumero: Int): DetalleCobro {
        val horasCobradas = if (horasReales <= 0.0) 1 else ceil(horasReales).toInt()
        val tarifaBase = vehiculo.tarifaHora * horasCobradas

        val recargoPorcentaje = when {
            horasReales > 4.0 -> 0.50
            horasReales > 2.0 -> 0.20
            else -> 0.0
        }
        val montoRecargo = tarifaBase * recargoPorcentaje
        val subtotal = tarifaBase + montoRecargo

        val descuentoPorcentaje = if (visitaNumero % 5 == 0) 0.10 else 0.0
        val montoDescuento = subtotal * descuentoPorcentaje
        val total = subtotal - montoDescuento

        return DetalleCobro(
            horasReales = horasReales,
            horasCobradas = horasCobradas,
            tarifaBase = tarifaBase,
            subtotal = subtotal,
            recargoPorcentaje = recargoPorcentaje,
            montoRecargo = montoRecargo,
            descuentoPorcentaje = descuentoPorcentaje,
            montoDescuento = montoDescuento,
            total = total,
            visitaNumero = visitaNumero
        )
    }
}