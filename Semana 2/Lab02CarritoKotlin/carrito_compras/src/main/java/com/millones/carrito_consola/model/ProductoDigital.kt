package com.millones.carrito_consola.model

class ProductoDigital(nombre: String, precio: Double, cantidad: Int, val correoEnvio: String)
    : Producto(nombre, precio, cantidad) {

    override fun mostrarDetalle(index: Int): String {
        return super.mostrarDetalle(index) + " [Digital - Correo: $correoEnvio]"
    }
}