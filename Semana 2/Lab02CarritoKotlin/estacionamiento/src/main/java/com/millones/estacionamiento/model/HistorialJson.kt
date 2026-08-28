package com.millones.estacionamiento.model

import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object HistorialJson {

    private const val ARCHIVO = "vehiculos_retirados.json"
    private val FORMATO_FECHA: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")

    fun contarVisitasPlaca(placa: String): Int {
        val archivo = File(ARCHIVO)
        if (!archivo.exists()) return 0
        val contenido = archivo.readText()
        val patron = Regex("\"placa\"\\s*:\\s*\"${Regex.escape(placa.uppercase())}\"")
        return patron.findAll(contenido).count()
    }

    fun agregarRegistro(vehiculo: Vehiculo, detalle: DetalleCobro, horaSalida: LocalDateTime) {
        val archivo = File(ARCHIVO)
        val registrosPrevios = if (archivo.exists()) leerRegistrosCrudos(archivo.readText()) else mutableListOf()

        val nuevoRegistro = """
            {
                "placa": "${vehiculo.placa}",
                "tipo": "${vehiculo.tipo}",
                "cliente": "${vehiculo.cliente.nombre}",
                "documentoCliente": "${vehiculo.cliente.documento}",
                "horaIngreso": "${vehiculo.horaIngreso.format(FORMATO_FECHA)}",
                "horaSalida": "${horaSalida.format(FORMATO_FECHA)}",
                "horasCobradas": ${detalle.horasCobradas},
                "tarifaBase": ${"%.2f".format(detalle.tarifaBase)},
                "recargoPorcentaje": ${detalle.recargoPorcentaje},
                "descuentoPorcentaje": ${detalle.descuentoPorcentaje},
                "visitaNumero": ${detalle.visitaNumero},
                "total": ${"%.2f".format(detalle.total)}
            }
        """.trimIndent()

        registrosPrevios.add(nuevoRegistro)

        val json = "[\n" + registrosPrevios.joinToString(",\n") + "\n]\n"
        archivo.writeText(json)
    }

    /** FASE 3: lee y parsea todo el historial guardado. */
    fun leerTodos(): List<RegistroRetiro> {
        val archivo = File(ARCHIVO)
        if (!archivo.exists()) return emptyList()

        val crudos = leerRegistrosCrudos(archivo.readText())
        return crudos.mapNotNull { parsearRegistro(it) }
    }

    private fun extraerTexto(bloque: String, clave: String): String {
        val patron = Regex("\"$clave\"\\s*:\\s*\"(.*?)\"")
        return patron.find(bloque)?.groupValues?.get(1) ?: ""
    }

    private fun extraerNumero(bloque: String, clave: String): Double {
        val patron = Regex("\"$clave\"\\s*:\\s*(-?[0-9]+(\\.[0-9]+)?)")
        return patron.find(bloque)?.groupValues?.get(1)?.toDoubleOrNull() ?: 0.0
    }

    private fun parsearRegistro(bloque: String): RegistroRetiro? {
        val placa = extraerTexto(bloque, "placa")
        if (placa.isEmpty()) return null

        return RegistroRetiro(
            placa = placa,
            tipo = extraerTexto(bloque, "tipo"),
            cliente = extraerTexto(bloque, "cliente"),
            documentoCliente = extraerTexto(bloque, "documentoCliente"),
            horaIngreso = extraerTexto(bloque, "horaIngreso"),
            horaSalida = extraerTexto(bloque, "horaSalida"),
            horasCobradas = extraerNumero(bloque, "horasCobradas").toInt(),
            tarifaBase = extraerNumero(bloque, "tarifaBase"),
            recargoPorcentaje = extraerNumero(bloque, "recargoPorcentaje"),
            descuentoPorcentaje = extraerNumero(bloque, "descuentoPorcentaje"),
            visitaNumero = extraerNumero(bloque, "visitaNumero").toInt(),
            total = extraerNumero(bloque, "total")
        )
    }

    private fun leerRegistrosCrudos(contenido: String): MutableList<String> {
        val recortado = contenido.trim().removePrefix("[").removeSuffix("]").trim()
        if (recortado.isEmpty()) return mutableListOf()

        val registros = mutableListOf<String>()
        var profundidad = 0
        val actual = StringBuilder()
        for (c in recortado) {
            if (c == '{') profundidad++
            if (c == '}') profundidad--
            actual.append(c)
            if (profundidad == 0 && c == '}') {
                registros.add(actual.toString().trim().trim(','))
                actual.clear()
            }
        }
        return registros
    }
}