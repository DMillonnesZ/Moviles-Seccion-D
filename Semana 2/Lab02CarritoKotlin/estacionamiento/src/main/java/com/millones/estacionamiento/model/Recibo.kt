package com.millones.estacionamiento.model

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object Recibo {

    private val FORMATO_FECHA: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
    private const val ANCHO = 46

    fun generar(vehiculo: Vehiculo, detalle: DetalleCobro, horaSalida: LocalDateTime): String {
        val sb = StringBuilder()
        val linea = "=".repeat(ANCHO)

        sb.appendLine(linea)
        sb.appendLine(centrar("RECIBO DE ESTACIONAMIENTO"))
        sb.appendLine(linea)
        sb.appendLine(fila("Placa", vehiculo.placa))
        sb.appendLine(fila("Tipo", vehiculo.tipo))
        sb.appendLine(fila("Cliente", vehiculo.cliente.nombre))
        sb.appendLine(fila("DNI", vehiculo.cliente.documento))
        sb.appendLine("-".repeat(ANCHO))
        sb.appendLine(fila("Ingreso", vehiculo.horaIngreso.format(FORMATO_FECHA)))
        sb.appendLine(fila("Salida", horaSalida.format(FORMATO_FECHA)))
        sb.appendLine(fila("Horas cobradas", "${detalle.horasCobradas} h (real: ${"%.2f".format(detalle.horasReales)} h)"))
        sb.appendLine("-".repeat(ANCHO))
        sb.appendLine(fila("Tarifa base", "S/ ${"%.2f".format(detalle.tarifaBase)}"))
        if (detalle.recargoPorcentaje > 0.0) {
            sb.appendLine(fila("Recargo (${(detalle.recargoPorcentaje * 100).toInt()}%)", "+ S/ ${"%.2f".format(detalle.montoRecargo)}"))
        } else {
            sb.appendLine(fila("Recargo", "No aplica"))
        }
        sb.appendLine(fila("Subtotal", "S/ ${"%.2f".format(detalle.subtotal)}"))
        sb.appendLine(fila("Visita N°", "${detalle.visitaNumero}"))
        if (detalle.descuentoPorcentaje > 0.0) {
            sb.appendLine(fila("Descuento (10%)", "- S/ ${"%.2f".format(detalle.montoDescuento)}"))
        } else {
            sb.appendLine(fila("Descuento", "No aplica"))
        }
        sb.appendLine(linea)
        sb.appendLine(fila("TOTAL A PAGAR", "S/ ${"%.2f".format(detalle.total)}"))
        sb.appendLine(linea)
        sb.appendLine(centrar("¡Gracias por su preferencia!"))
        sb.appendLine(linea)

        return sb.toString()
    }

    fun generarReporteHistorial(registros: List<RegistroRetiro>): String {
        val sb = StringBuilder()
        if (registros.isEmpty()) {
            sb.appendLine("Aún no hay vehículos retirados registrados en el historial.")
            return sb.toString()
        }

        val encabezado = "%-9s %-11s %-20s %6s %8s %8s".format(
            "PLACA", "TIPO", "CLIENTE", "VISITA", "HORAS", "TOTAL"
        )
        sb.appendLine("--- Historial de vehículos retirados (${registros.size}) ---")
        sb.appendLine(encabezado)
        sb.appendLine("-".repeat(encabezado.length))

        var totalRecaudado = 0.0
        registros.forEach { r ->
            sb.appendLine(
                "%-9s %-11s %-20s %6d %8d %8s".format(
                    r.placa, r.tipo, r.cliente.take(20), r.visitaNumero, r.horasCobradas,
                    "S/ ${"%.2f".format(r.total)}"
                )
            )
            totalRecaudado += r.total
        }

        sb.appendLine("-".repeat(encabezado.length))
        sb.appendLine("TOTAL RECAUDADO: S/ ${"%.2f".format(totalRecaudado)}")
        return sb.toString()
    }

    private fun fila(etiqueta: String, valor: String): String {
        val punto = ANCHO - valor.length
        val relleno = if (punto > etiqueta.length) " ".repeat(punto - etiqueta.length) else " "
        return "$etiqueta$relleno$valor"
    }

    private fun centrar(texto: String): String {
        val espacios = (ANCHO - texto.length) / 2
        return if (espacios > 0) " ".repeat(espacios) + texto else texto
    }
}