package com.millones.estacionamiento

import java.time.LocalTime
import java.time.format.DateTimeFormatter

val formatoHora: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")

val mapTipos = mutableMapOf<String, String>()
val mapHoras = mutableMapOf<String, Int>()
val mapHoraIngreso = mutableMapOf<String, LocalTime>()
val mapHoraSalida = mutableMapOf<String, LocalTime>()
val mapClientes = mutableMapOf<String, String>()
val mapFrecuente = mutableMapOf<String, Boolean>()
val mapVisitas = mutableMapOf<String, Int>()

val mapTotal = mutableMapOf<String, Double>()
const val MAX_VEHICULOS = 30
const val UMBRAL_FRECUENTE = 5

const val DESCUENTO_FRECUENTE = 0.10
fun ingresoDeDatos() {
    var continuar = true

    while (continuar && mapTipos.size < MAX_VEHICULOS) {
        println("\n--- Registro de Vehiculo (${mapTipos.size + 1}/$MAX_VEHICULOS) ---")

        print("Ingrese Placa: ")
        var placa = readLine()?.trim()?.uppercase() ?: ""
        while (placa.isEmpty()) {
            print("La placa no puede estar vacia. Ingrese Placa: ")
            placa = readLine()?.trim()?.uppercase() ?: ""
        }

        val horaIngreso = LocalTime.now()
        println("Hora de ingreso (sistema): ${horaIngreso.format(formatoHora)}")

        var tipo: String
        while (true) {
            print("Ingrese Tipo (MOTO / AUTO / CAMIONETA): ")
            tipo = readLine()?.trim()?.uppercase() ?: ""
            if (tipo == "MOTO" || tipo == "AUTO" || tipo == "CAMIONETA") break
            println("Tipo invalido. Intente nuevamente.")
        }

        var horas: Int
        while (true) {
            print("Ingrese Horas: ")
            horas = readLine()?.trim()?.toIntOrNull() ?: -1
            if (horas >= 1) break
            println("Las horas deben ser un numero entero mayor o igual a 1.")
        }

        val horaSalida = horaIngreso.plusHours(horas.toLong())
        println("Hora de salida estimada: ${horaSalida.format(formatoHora)}")

        print("Ingrese Nombre del Cliente: ")
        val cliente = readLine()?.trim() ?: ""

        val visitasPrevias = mapVisitas.getOrDefault(placa, 0)
        val totalVisitas = visitasPrevias + 1
        val esFrecuente = totalVisitas >= UMBRAL_FRECUENTE
        mapVisitas[placa] = totalVisitas

        if (esFrecuente) {
            println("Placa con $totalVisitas registros. Se aplica CLIENTE FRECUENTE.")
        }

        mapTipos[placa] = tipo
        mapHoras[placa] = horas
        mapHoraIngreso[placa] = horaIngreso
        mapHoraSalida[placa] = horaSalida
        mapClientes[placa] = cliente
        mapFrecuente[placa] = esFrecuente

        if (mapTipos.size >= MAX_VEHICULOS) {
            println("\nSe alcanzo el limite maximo de $MAX_VEHICULOS vehiculos registrados.")
            continuar = false
        } else {
            print("\nDesea continuar registrando? (S/N): ")
            val respuesta = readLine()?.trim()?.uppercase() ?: "N"
            if (respuesta != "S") {
                println("Regresando al menu principal...")
                continuar = false
            }
        }
    }
}

fun calculos() {
    for (placa in mapTipos.keys) {
        val tipo = mapTipos[placa] ?: ""
        val horas = mapHoras[placa] ?: 0
        val tarifaBase = when (tipo) {
            "MOTO" -> 2.0
            "AUTO" -> 4.0
            "CAMIONETA" -> 10.0
            else -> 0.0
        }
        val horasNormales = minOf(horas, 2)
        val horasCon20 = if (horas > 2) minOf(horas, 5) - 2 else 0
        val horasCon50 = if (horas > 5) horas - 5 else 0
        var total = horasNormales * tarifaBase +
                horasCon20 * tarifaBase * 1.20 +
                horasCon50 * tarifaBase * 1.50
        if (mapFrecuente[placa] == true) {
            total -= total * DESCUENTO_FRECUENTE
        }
        mapTotal[placa] = total
    }
}

fun mostrarResultados() {
    println("\n===================================================")
    println("                 DETALLE DE PAGOS")
    println("===================================================")
    println(String.format("%-10s %-12s %-7s %-10s %-10s", "PLACA", "TIPO", "HORAS", "RECARGO", "PAGO"))
    println("---------------------------------------------------")
    for (placa in mapTipos.keys) {
        val horas = mapHoras[placa] ?: 0
        val recargo = when {
            horas <= 2 -> "0%"
            horas <= 5 -> "20%"
            else -> "50%"
        }
        val pago = mapTotal[placa] ?: 0.0
        println(String.format("%-10s %-12s %-7d %-10s S/ %.2f", placa, mapTipos[placa], horas, recargo, pago))
    }
    println("---------------------------------------------------")

    val totalVehiculos = mapTipos.size
    val totalMotos = mapTipos.values.count { it == "MOTO" }
    val totalAutos = mapTipos.values.count { it == "AUTO" }
    val totalCamionetas = mapTipos.values.count { it == "CAMIONETA" }
    val recaudacionTotal = mapTotal.values.sum()

    var placaMayorPago = ""
    var mayorPago = -1.0
    for (placa in mapTotal.keys) {
        if ((mapTotal[placa] ?: 0.0) > mayorPago) {
            mayorPago = mapTotal[placa] ?: 0.0
            placaMayorPago = placa
        }
    }

    val conteoHoras = mutableMapOf<Int, Int>()
    for (placa in mapHoraIngreso.keys) {
        val hora = mapHoraIngreso[placa]?.hour ?: -1
        conteoHoras[hora] = conteoHoras.getOrDefault(hora, 0) + 1
    }
    var horaMasDemandada = -1
    var mayorConteo = -1
    for (hora in conteoHoras.keys) {
        if ((conteoHoras[hora] ?: 0) > mayorConteo) {
            mayorConteo = conteoHoras[hora] ?: 0
            horaMasDemandada = hora
        }
    }

    println("\n===================================================")
    println("                RESUMEN DEL DIA")
    println("===================================================")
    println("Vehiculos: $totalVehiculos")
    println("  Motos: $totalMotos")
    println("  Autos: $totalAutos")
    println("  Camionetas: $totalCamionetas")
    println("Recaudacion Total: S/ ${"%.2f".format(recaudacionTotal)}")
    if (placaMayorPago.isNotEmpty()) {
        println("Vehiculo con Mayor Pago: $placaMayorPago (S/ ${"%.2f".format(mayorPago)})")
    }
    if (horaMasDemandada >= 0) {
        println("Hora mas Demandada: ${String.format("%02d:00", horaMasDemandada)} ($mayorConteo registros)")
    }
    println("===================================================")
}

fun main() {
    ingresoDeDatos()
    calculos()
    mostrarResultados()
}