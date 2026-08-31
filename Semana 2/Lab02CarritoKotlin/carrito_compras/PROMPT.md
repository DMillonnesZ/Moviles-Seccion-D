# PROMPT TÉCNICO — Sistema "carrito_compras" (Tienda Tecsup)

## 1. Objetivo
Implementar en Kotlin (consola) un sistema de carrito de compras orientado a objetos, aplicando abstracción, **herencia**, **polimorfismo** y encapsulamiento, con arquitectura por capas y manejo defensivo de errores (sin null pointer exceptions ni crashes por entrada inválida).

## 2. Arquitectura del Proyecto
```text
carrito-compras/
 ├── model/
 │   ├── Producto.kt        (Clase base abierta)
 │   ├── ProductoFisico.kt  (Clase hija)
 │   ├── ProductoDigital.kt (Clase hija)
 │   └── Cliente.kt
 ├── service/
 │   └── Carrito.kt
 └── main/
     └── Main.kt
```

## 3. Especificación de Clases

### 3.1 model.Producto (Clase Base Abierta)
* **Atributos:** `protected var nombre: String`, `protected var precio: Double`, `protected var cantidad: Int`
* **Constructor primario:** `Producto(nombre: String, precio: Double, cantidad: Int)`
* **Constructor secundario:** `constructor() : this("Producto sin nombre", 0.0, 1)` — valores por defecto ante datos vacíos o nulos.
* **Validaciones internas (`validar()`):** `precio < 0` → `0.0` con advertencia; `cantidad <= 0` → `1`; `nombre.isBlank()` → `"Producto sin nombre"`.
* **Propiedades derivadas:** `val subtotalProducto: Double get() = precio * cantidad`
* **Getters públicos:** `getNombre()`, `getPrecio()`, `getCantidad()`
* **Método polimórfico:** `open fun mostrarDetalle(index: Int): String` → retorna el formato base de tabla para consola (`index. nombre xcantidad S/ precio`).

### 3.2 model.ProductoFisico (Herencia de Producto)
* **Atributo propio:** `val peso: Double`
* **Constructor:** Recibe parámetros de producto más el `peso`, invocando al constructor de la superclase.
* **Polimorfismo:** Sobrescribe (`override`) `mostrarDetalle` para concatenar al formato base la etiqueta de peso: `[Físico - Peso: X.X kg]`.

### 3.3 model.ProductoDigital (Herencia de Producto)
* **Atributo propio:** `val correoEnvio: String`
* **Constructor:** Recibe parámetros de producto más el `correoEnvio`, invocando al constructor de la superclase.
* **Polimorfismo:** Sobrescribe (`override`) `mostrarDetalle` para concatenar al formato base la etiqueta de entrega virtual: `[Digital - Correo: email]`.

### 3.4 model.Cliente
* **Atributo:** `private var nombre: String`
* **Constructor primario:** `Cliente(nombre: String)`
* **Constructor secundario:** `constructor() : this("Cliente sin registrar")`
* **Validación:** Si `nombre.isBlank()` → asigna `"Cliente sin registrar"`.
* **Getter público:** `getNombre()`

### 3.5 service.Carrito (Encapsulamiento + Colecciones Polimórficas)
* **Estado interno:** `private val _items: MutableList<Producto>`
* **Exposición controlada:** `val items: List<Producto> get() = _items.toList()` (copia inmutable para evitar alteraciones externas directas).
* **Propiedades calculadas:**
  * `subtotal`: Suma de todos los subtotales de productos mediante `_items.sumOf`.
  * `igv`: `subtotal * 0.18`
  * `totalAPagar`: `subtotal + igv`
  * `totalConDescuento`: `totalAPagar - calcularDescuento()`
* **Métodos operacionales:**
  * `productoMasCaro(): Producto?`: Retorna el objeto con mayor precio unitario usando `maxByOrNull`.
  * `calcularDescuento(): Double`: Si `totalAPagar > 3000` aplica un beneficio del `5%` (`totalAPagar * 0.05`), de lo contrario `0.0`.
  * `agregarProducto(p: Producto)`: Añade un elemento polimórfico a la lista interna e imprime confirmación en color verde ANSI.
  * `eliminarProducto(index: Int): Boolean`: Recibe el índice de consola (1-N), computa internamente la posición real (base cero), valida rangos para prevenir excepciones y remueve el objeto.
  * `vaciarCarrito()`: Limpia la lista completa con `_items.clear()`.
  * `mostrarResumenFinal(cliente: Cliente)`: Renderiza el comprobante formateado con alineación de columnas, totales e impuestos, producto estrella, descuentos calculados y despedida en colores ANSI.

### 3.6 main.Main (Capa de Interfaz y Manejo Defensivo)
* **Estructura del catálogo:** `data class ItemCatalogo(val nombre: String, val precio: Double, val tipo: String)` operando con bienes tangibles (`FISICO`) y licencias de software virtuales (`DIGITAL`).
* **Manejo defensivo:** Uso obligatorio de `toIntOrNull()` y `toDoubleOrNull()` en todas las entradas de teclado para capturar anomalías sin generar *crashes* del sistema.
* **Instanciación dinámica:** El submenú evalúa la propiedad `tipo` del catálogo para guiar las preguntas en consola y fabricar el objeto específico (`ProductoFisico` o `ProductoDigital`), inyectándolo transparentemente en el servicio del carrito.
