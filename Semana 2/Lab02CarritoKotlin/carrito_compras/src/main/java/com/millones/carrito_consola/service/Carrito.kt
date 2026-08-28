package com.millones.carrito_consola.service

import com.millones.carrito_consola.model.Cliente
import com.millones.carrito_consola.model.Producto

class Carrito {
    private val _items: MutableList<Producto> = mutableListOf()

    val items: List<Producto>
        get() = _items.toList()

    val subtotal: Double
        get() = _items.sumOf { it.subtotalProducto }

    val igv: Double
        get() = subtotal * 0.18

    val totalAPagar: Double
        get() = subtotal + igv

    fun productoMasCaro(): Producto? {
        return _items.maxByOrNull { it.getPrecio() }
    }

    fun calcularDescuento(): Double {
        return if (totalAPagar > 3000) totalAPagar * 0.05 else 0.0
    }

    val totalConDescuento: Double
        get() = totalAPagar - calcularDescuento()

    fun agregarProducto(p: Producto) {
        _items.add(p)
        println("\u001B[32mProducto agregado: ${p.getNombre()}\u001B[0m")
    }

    fun eliminarProducto(index: Int): Boolean {
        val posicionReal = index - 1
        if (posicionReal < 0 || posicionReal >= _items.size) {
            println("\u001B[33mÍndice fuera de rango. No se eliminó ningún producto.\u001B[0m")
            return false
        }
        val eliminado = _items.removeAt(posicionReal)
        println("\u001B[32mProducto eliminado: ${eliminado.getNombre()}\u001B[0m")
        return true
    }

    fun vaciarCarrito() {
        _items.clear()
        println("\u001B[33mCarrito vaciado.\u001B[0m")
    }
}