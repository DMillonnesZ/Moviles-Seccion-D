package com.tecsup.millones.model

data class Producto(
    val id: Int,
    val nombre: String,
    val precio: Double
)

val productosEjemplo = listOf(
    Producto(1, "Audífonos", 89.0),
    Producto(2, "Smartwatch", 199.0),
    Producto(3, "Funda celular", 25.0)
)