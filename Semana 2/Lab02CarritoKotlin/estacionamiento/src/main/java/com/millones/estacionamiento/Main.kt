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

const val MAX_VEHICULOS = 30
const val UMBRAL_FRECUENTE = 5

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

fun main() {
    ingresoDeDatos()

    println("\n=== RESUMEN DE INGRESO (temporal, se moverá a Fase 3) ===")
    for (placa in mapTipos.keys) {
        println("Placa: $placa | Tipo: ${mapTipos[placa]} | Ingreso: ${mapHoraIngreso[placa]?.format(formatoHora)} | Salida: ${mapHoraSalida[placa]?.format(formatoHora)} | Horas: ${mapHoras[placa]} | Cliente: ${mapClientes[placa]} | Frecuente: ${mapFrecuente[placa]}")
    }
}