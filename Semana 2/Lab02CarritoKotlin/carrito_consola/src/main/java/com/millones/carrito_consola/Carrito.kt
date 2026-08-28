package com.millones.carrito_consola

data class Producto(
    val nombre: String,
    val precio: Double,
    var cantidad: Int
)

fun calcularSubtotal(productos: List<Producto>): Double {
    var subtotal = 0.0
    for (p in productos) {
        subtotal += p.precio * p.cantidad
    }
    return subtotal
}

fun calcularIGV(subtotal: Double): Double {
    return subtotal * 0.18
}

fun calcularTotal(subtotal: Double, igv: Double): Double {
    return subtotal + igv
}

fun mostrarDetalle(productos: List<Producto>) {
    println("--------- DETALLE DEL CARRITO ---------")
    var i = 1
    for (p in productos) {
        val importe = p.precio * p.cantidad
        println(String.format("%d. %-20s x%d  S/ %8.2f", i, p.nombre, p.cantidad, importe))
        i++
    }
    println("---------------------------------------")
}

fun main() {
    println("=========================================")
    println("     CARRITO DE COMPRAS - TIENDA TECSUP    ")
    println("=========================================")

    val nombreCliente = "Juan Leon"              // String (inferido)
    val carrito = mutableListOf<Producto>()      // lista vacía de productos
    println("Cliente: $nombreCliente")
    println()

    carrito.add(Producto("Laptop HP", 2500.0, 1))
    carrito.add(Producto("Mouse Logitech", 45.5, 2))
    carrito.add(Producto("Teclado Mecánico", 120.0, 1))
    carrito.add(Producto("Monitor Samsung", 650.0, 3))

    for (producto in carrito) {
        println("Producto agregado: ${producto.nombre}")
    }

    println()

    val subtotal = calcularSubtotal(carrito)
    val igv = calcularIGV(subtotal)
    val total = calcularTotal(subtotal, igv)

    println("Subtotal: S/ %.2f".format(subtotal))
    println("IGV (18%%): S/ %.2f".format(igv))
    println("Total: S/ %.2f".format(total))
}