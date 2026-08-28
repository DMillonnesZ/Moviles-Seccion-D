package com.millones.carrito_consola.main

import com.millones.carrito_consola.model.Cliente
import com.millones.carrito_consola.model.Producto
import com.millones.carrito_consola.model.ProductoDigital
import com.millones.carrito_consola.model.ProductoFisico
import com.millones.carrito_consola.service.Carrito

data class ItemCatalogo(val nombre: String, val precio: Double, val tipo: String)

val catalogo = listOf(
    ItemCatalogo("Laptop HP", 2500.0, "FISICO"),
    ItemCatalogo("Mouse Logitech", 45.5, "FISICO"),
    ItemCatalogo("Licencia Antivirus ESET", 120.0, "DIGITAL"),
    ItemCatalogo("USB Kingston 64GB", 25.0, "FISICO")
)

fun main() {
    println("\u001B[36m=========================================\u001B[0m")
    println("\u001B[36m     CARRITO DE COMPRAS - TIENDA TECSUP    \u001B[0m")
    println("\u001B[36m=========================================\u001B[0m")

    print("Ingrese el nombre del cliente: ")
    val nombreIngresado = readLine()
    val cliente = if (nombreIngresado.isNullOrBlank()) Cliente() else Cliente(nombreIngresado)

    val carrito = Carrito()
    var opcion: Int

    do {
        mostrarMenuPrincipal()
        opcion = readLine()?.toIntOrNull() ?: -1

        when (opcion) {
            1 -> submenuCatalogo(carrito)
            2 -> verCarrito(carrito)
            3 -> eliminarProductoMenu(carrito)
            4 -> carrito.vaciarCarrito()
            5 -> carrito.mostrarResumenFinal(cliente)
            0 -> println("\u001B[36mSaliendo del sistema...\u001B[0m")
            else -> println("\u001B[33mOpción inválida, intente nuevamente.\u001B[0m")
        }
        println()
    } while (opcion != 0)
}

fun mostrarMenuPrincipal() {
    println("========== MENU PRINCIPAL ==========")
    println("1. Agregar producto")
    println("2. Ver carrito")
    println("3. Eliminar producto")
    println("4. Vaciar carrito")
    println("5. Finalizar compra")
    println("0. Salir")
    print("Elige una opcion: ")
}

fun submenuCatalogo(carrito: Carrito) {
    var opcionCatalogo: Int
    do {
        println("\n--------- CATALOGO DE PRODUCTOS ---------")
        catalogo.forEachIndexed { i, item ->
            println(String.format("%d. %-25s [%-7s] S/ %8.2f", i + 1, item.nombre, item.tipo, item.precio))
        }
        println("0. Volver al menu principal")
        print("Elige un producto: ")

        opcionCatalogo = readLine()?.toIntOrNull() ?: -1

        if (opcionCatalogo in 1..catalogo.size) {
            val seleccionado = catalogo[opcionCatalogo - 1]
            print("Ingrese cantidad: ")
            val cantidadIngresada = readLine()?.toIntOrNull() ?: 1

            val producto: Producto = when (seleccionado.tipo) {
                "FISICO" -> {
                    print("Ingrese el peso estimado del producto (kg): ")
                    val peso = readLine()?.toDoubleOrNull() ?: 1.0
                    ProductoFisico(seleccionado.nombre, seleccionado.precio, cantidadIngresada, peso)
                }
                "DIGITAL" -> {
                    print("Ingrese el correo de destino para el enlace: ")
                    val correo = readLine() ?: "soporte@tecsup.edu.pe"
                    val correoValido = if (correo.isBlank()) "soporte@tecsup.edu.pe" else correo
                    ProductoDigital(seleccionado.nombre, seleccionado.precio, cantidadIngresada, correoValido)
                }
                else -> Producto(seleccionado.nombre, seleccionado.precio, cantidadIngresada)
            }

            carrito.agregarProducto(producto)
        } else if (opcionCatalogo != 0) {
            println("\u001B[33mOpción inválida.\u001B[0m")
        }
    } while (opcionCatalogo != 0)
}

fun verCarrito(carrito: Carrito) {
    println("\n--------- DETALLE DEL CARRITO ---------")
    if (carrito.items.isEmpty()) {
        println("El carrito está vacío.")
    } else {
        carrito.items.forEachIndexed { i, producto ->
            println(producto.mostrarDetalle(i + 1))
        }
    }
    println("---------------------------------------")
    println(String.format("%-15s: S/ %8.2f", "Subtotal", carrito.subtotal))
    println(String.format("%-15s: S/ %8.2f", "IGV (18%)", carrito.igv))
    println(String.format("%-15s: S/ %8.2f", "TOTAL A PAGAR", carrito.totalAPagar))
}

fun eliminarProductoMenu(carrito: Carrito) {
    if (carrito.items.isEmpty()) {
        println("\u001B[33mNo hay productos para eliminar.\u001B[0m")
        return
    }
    verCarrito(carrito)
    print("Ingrese el número del producto a eliminar: ")
    val indexIngresado = readLine()?.toIntOrNull()

    if (indexIngresado == null) {
        println("\u001B[33mEntrada inválida, no se eliminó ningún producto.\u001B[0m")
        return
    }
    carrito.eliminarProducto(indexIngresado)
}
