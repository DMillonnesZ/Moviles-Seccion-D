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

    fun mostrarResumenFinal(cliente: Cliente) {
        println("\u001B[36m=============================================================\u001B[0m")
        println("\u001B[36m             CARRITO DE COMPRAS - TIENDA TECSUP              \u001B[0m")
        println("\u001B[36m=============================================================\u001B[0m")
        println("Cliente: ${cliente.getNombre()}")
        println()

        println("\u001B[33m-------------------- DETALLE DEL CARRITO --------------------\u001B[0m")
        if (_items.isEmpty()) {
            println("El carrito está vacío.")
        } else {
            _items.forEachIndexed { i, producto ->
                println(producto.mostrarDetalle(i + 1))
            }
        }
        println("\u001B[33m-------------------------------------------------------------\u001B[0m")

        println("Cantidad de productos distintos: ${_items.size}")
        println(String.format("%-20s: S/ %8.2f", "Subtotal", subtotal))
        println(String.format("%-20s: S/ %8.2f", "IGV (18%)", igv))
        println("\u001B[32m" + String.format("%-20s: S/ %8.2f", "TOTAL A PAGAR", totalAPagar) + "\u001B[0m")
        println("\u001B[33m-------------------------------------------------------------\u001B[0m")

        val masCaro = productoMasCaro()
        if (masCaro != null) {
            println("\u001B[36mProducto más caro: ${masCaro.getNombre()} " +
                    String.format("(S/ %.2f)", masCaro.getPrecio()) + "\u001B[0m")
        }

        val descuento = calcularDescuento()
        if (descuento > 0) {
            println("\u001B[33mDescuento aplicado: 5% por compra mayor a S/ 3000\u001B[0m")
        } else {
            println("Descuento: No aplica")
        }
        println("\u001B[33m" + String.format("%-20s: S/ %8.2f", "TOTAL CON DESCUENTO", totalConDescuento) + "\u001B[0m")

        println()
        println("\u001B[36m¡Gracias por su compra, ${cliente.getNombre()}!\u001B[0m")
    }

}