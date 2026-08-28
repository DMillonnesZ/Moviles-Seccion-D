package com.millones.carrito_consola.model
open class Producto {
    protected var nombre: String
    protected var precio: Double
    protected var cantidad: Int

    constructor(nombre: String, precio: Double, cantidad: Int) {
        this.nombre = nombre
        this.precio = precio
        this.cantidad = cantidad
        validar()
    }

    constructor() : this("Producto sin nombre", 0.0, 1)

    private fun validar() {
        if (nombre.isBlank()) {
            println("\u001B[33mAdvertencia: nombre vacío, se asigna 'Producto sin nombre'\u001B[0m")
            nombre = "Producto sin nombre"
        }
        if (precio < 0) {
            println("\u001B[33mAdvertencia: precio negativo, se asigna 0.0\u001B[0m")
            precio = 0.0
        }
        if (cantidad <= 0) {
            println("\u001B[33mAdvertencia: cantidad inválida, se asigna 1\u001B[0m")
            cantidad = 1
        }
    }

    val subtotalProducto: Double
        get() = precio * cantidad

    fun getNombre(): String = nombre
    fun getPrecio(): Double = precio
    fun getCantidad(): Int = cantidad

    open fun mostrarDetalle(index: Int): String {
        return String.format(
            "%d. %-25s x%-3d S/ %8.2f",
            index, nombre, cantidad, subtotalProducto
        )
    }
}
