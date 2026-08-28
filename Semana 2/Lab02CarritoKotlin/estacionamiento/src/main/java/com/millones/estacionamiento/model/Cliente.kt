package com.millones.estacionamiento.model

class Cliente(val nombre: String, val documento: String) {

    private val historialPlacas: MutableList<String> = mutableListOf()

    fun registrarVisita(placa: String) {
        historialPlacas.add(placa.uppercase())
    }

    fun totalVisitasPorPlaca(placa: String): Int =
        historialPlacas.count { it.equals(placa, ignoreCase = true) }

    fun historial(): List<String> = historialPlacas.toList()

    override fun toString(): String = "$nombre (DNI: $documento)"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Cliente) return false
        return documento == other.documento
    }

    override fun hashCode(): Int = documento.hashCode()
}