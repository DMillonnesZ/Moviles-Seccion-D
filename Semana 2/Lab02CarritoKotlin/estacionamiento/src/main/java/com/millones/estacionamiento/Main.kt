package com.millones.estacionamiento

import com.millones.estacionamiento.model.Auto
import com.millones.estacionamiento.model.Camioneta
import com.millones.estacionamiento.model.Moto
import com.millones.estacionamiento.model.ParqueaderoSistema
import com.millones.estacionamiento.model.Vehiculo

val REGEX_PLACA = Regex("^[A-Z]{2,3}-\\d{3,4}$")
val REGEX_NOMBRE = Regex("^[A-Za-zÁÉÍÓÚáéíóúÑñ ]{3,50}$")
val REGEX_DOCUMENTO = Regex("^\\d{8}$")

fun main() {
    val sistema = ParqueaderoSistema(capacidadMaxima = 30)
    var opcion: Int

    do {
        mostrarMenu()
        opcion = leerOpcionMenu()
        when (opcion) {
            1 -> registrarVehiculo(sistema)
            2 -> listarVehiculos(sistema)
            0 -> println("Saliendo del sistema...")
            else -> println("Opción inválida, intente nuevamente.")
        }
    } while (opcion != 0)
}

fun mostrarMenu() {
    println("\n===== SISTEMA DE TARIFAS DE ESTACIONAMIENTO =====")
    println("1. Ingresar vehículo")
    println("2. Listar vehículos en el parqueadero")
    println("0. Salir")
    print("Seleccione una opción: ")
}

fun leerOpcionMenu(): Int = readLine()?.trim()?.toIntOrNull() ?: -1

fun registrarVehiculo(sistema: ParqueaderoSistema) {
    if (sistema.estaLleno()) {
        println("\n⚠ El parqueadero está lleno (capacidad máxima: ${sistema.capacidadMaxima}). No se pueden registrar más vehículos.")
        return
    }

    println("\n--- Registro de vehículo ---")
    val tipoOpcion = leerTipoVehiculo() ?: return

    val placa = leerPlacaValida(sistema)
    val (nombre, documento) = leerDatosCliente()
    val cliente = sistema.obtenerOCrearCliente(nombre, documento)

    val vehiculo: Vehiculo = when (tipoOpcion) {
        1 -> Moto(placa, cliente)
        2 -> Auto(placa, cliente)
        3 -> Camioneta(placa, cliente)
        else -> throw IllegalStateException("Tipo de vehículo no soportado")
    }

    sistema.registrarVehiculo(vehiculo)

    println("\n✅ Vehículo registrado con éxito:")
    println(vehiculo)
    println("Vehículos actualmente en el parqueadero: ${sistema.totalRegistrados()}/${sistema.capacidadMaxima}")
}

fun leerTipoVehiculo(): Int? {
    println("Seleccione el tipo de vehículo:")
    println("1. Moto      (S/ 2.00 / hora)")
    println("2. Auto      (S/ 4.00 / hora)")
    println("3. Camioneta (S/ 10.00 / hora)")
    print("Opción: ")
    val tipoOpcion = readLine()?.trim()?.toIntOrNull()
    if (tipoOpcion == null || tipoOpcion !in 1..3) {
        println("Tipo de vehículo inválido.")
        return null
    }
    return tipoOpcion
}

fun leerPlacaValida(sistema: ParqueaderoSistema): String {
    while (true) {
        print("Ingrese la placa (formato AAA-123 o AA-1234): ")
        val entrada = readLine()?.trim()?.uppercase() ?: ""
        when {
            entrada.isEmpty() -> println("La placa no puede estar vacía.")
            !REGEX_PLACA.matches(entrada) -> println("Formato de placa inválido. Ejemplo válido: ABC-123")
            sistema.placaEstaActiva(entrada) -> println("Ya existe un vehículo activo con esa placa en el parqueadero.")
            else -> return entrada
        }
    }
}

fun leerDatosCliente(): Pair<String, String> {
    var nombre: String
    while (true) {
        print("Ingrese el nombre del cliente: ")
        nombre = readLine()?.trim() ?: ""
        if (REGEX_NOMBRE.matches(nombre)) break
        println("Nombre inválido. Use solo letras y espacios (3 a 50 caracteres).")
    }

    var documento: String
    while (true) {
        print("Ingrese el DNI del cliente (8 dígitos): ")
        documento = readLine()?.trim() ?: ""
        if (REGEX_DOCUMENTO.matches(documento)) break
        println("DNI inválido. Debe contener exactamente 8 dígitos numéricos.")
    }

    return Pair(nombre, documento)
}

fun listarVehiculos(sistema: ParqueaderoSistema) {
    val activos = sistema.listarActivos()
    if (activos.isEmpty()) {
        println("\nNo hay vehículos registrados en el parqueadero.")
        return
    }
    println("\n--- Vehículos en el parqueadero (${activos.size}) ---")
    activos.forEachIndexed { index, v -> println("${index + 1}. $v") }
}