package com.millones.estacionamiento.model

class ParqueaderoSistema(val capacidadMaxima: Int = 30) {

    private val vehiculosActivos: MutableList<Vehiculo> = mutableListOf()
    private val clientes: MutableMap<String, Cliente> = mutableMapOf()

    fun estaLleno(): Boolean = vehiculosActivos.size >= capacidadMaxima

    fun placaEstaActiva(placa: String): Boolean =
        vehiculosActivos.any { it.placa.equals(placa, ignoreCase = true) }

    fun obtenerOCrearCliente(nombre: String, documento: String): Cliente =
        clientes.getOrPut(documento) { Cliente(nombre, documento) }

    fun registrarVehiculo(vehiculo: Vehiculo) {
        vehiculosActivos.add(vehiculo)
        vehiculo.cliente.registrarVisita(vehiculo.placa)
    }

    fun listarActivos(): List<Vehiculo> = vehiculosActivos.toList()

    fun totalRegistrados(): Int = vehiculosActivos.size
}