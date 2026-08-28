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

fun calcularDescuento(total: Double): Double {
    return when {
        total > 5000 -> total * 0.10
        total > 3000 -> total * 0.05
        else -> 0.0
    }
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

    val nombreCliente = "Daniel Millones"              // String (inferido)
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

    mostrarDetalle(carrito)
    println("Cantidad de productos: ${carrito.size}")

    val subtotal = calcularSubtotal(carrito)
    val igv = calcularIGV(subtotal)
    val total = calcularTotal(subtotal, igv)

    val descuento = calcularDescuento(total)
    val totalConDescuento = total - descuento

    println(String.format("%-15s S/ %8.2f", "Subtotal:", subtotal))
    println(String.format("%-15s S/ %8.2f", "IGV (18%):", igv))
    println(String.format("%-15s S/ %8.2f", "TOTAL A PAGAR:", total))
    println("---------------------------------------")
    val masCaro = carrito.maxByOrNull { it.precio }
    if (masCaro != null) {
        println("Producto mas caro: ${masCaro.nombre} " + String.format("(S/ %.2f)", masCaro.precio))
    }

    if (descuento > 0) {
        val porcentaje = if (total > 5000) 10 else 5
        val umbral = if (total > 5000) 5000 else 3000
        println("Descuento aplicado: $porcentaje% por compra mayor a S/ $umbral")
    } else {
        println("Descuento: No aplica")
    }
    println(String.format("%-15s S/ %8.2f", "TOTAL CON DESCUENTO:", totalConDescuento))
    println()
    println("Gracias por su compra, Daniel Millones!")
}