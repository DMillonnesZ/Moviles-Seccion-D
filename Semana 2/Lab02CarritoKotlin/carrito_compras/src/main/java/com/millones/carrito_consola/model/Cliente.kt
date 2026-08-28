package com.millones.carrito_consola.model

class Cliente {
    private var nombre: String

    constructor(nombre: String) {
        this.nombre = if (nombre.isBlank()) "Cliente sin registrar" else nombre
    }

    constructor() : this("Cliente sin registrar")

    fun getNombre(): String = nombre
}