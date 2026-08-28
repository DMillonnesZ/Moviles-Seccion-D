package com.millones.carrito_consola.model

class ProductoFisico(nombre: String, precio: Double, cantidad: Int, val peso: Double)
    : Producto(nombre, precio, cantidad) {

    override fun mostrarDetalle(index: Int): String {
        return super.mostrarDetalle(index) + String.format(" [Físico - Peso: %.1f kg]", peso)
    }
}