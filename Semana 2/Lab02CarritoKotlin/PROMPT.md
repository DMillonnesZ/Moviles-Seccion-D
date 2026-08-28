# CarritoKt — Sistema de Carrito de Compras (Tienda Tecsup)

## 1. Objetivo

Implementar en **Kotlin** (consola) un sistema de carrito de compras orientado a objetos, aplicando **abstracción, herencia, polimorfismo y encapsulamiento**, con arquitectura por capas y manejo defensivo de errores (sin `NullPointerException` ni crashes por entrada inválida).

## 2. Arquitectura del proyecto

```
carrito-compras/
 ├── model/
 │   ├── Producto.kt
 │   └── Cliente.kt
 ├── service/
 │   └── Carrito.kt
 └── main/
     └── Main.kt
```

## 3. Especificación de clases

### 3.1 `model.Producto`

| Elemento | Detalle |
|---|---|
| Atributos | `private var nombre: String`, `private var precio: Double`, `private var cantidad: Int` |
| Constructor primario | `Producto(nombre: String, precio: Double, cantidad: Int)` |
| Constructor secundario | `constructor() : this("Producto sin nombre", 0.0, 1)` — valores por defecto ante datos vacíos/nulos |
| Validaciones (`init {}`) | `precio < 0 → 0.0` con advertencia; `cantidad <= 0 → 1`; `nombre.isBlank() → "Producto sin nombre"` |
| Propiedades derivadas | `val subtotalProducto: Double get() = precio * cantidad` |
| Getters públicos | `getNombre()`, `getPrecio()`, `getCantidad()` (o properties de solo lectura expuestas) |
| Método | `fun mostrarDetalle(index: Int): String` → formato tabla (`"$index. $nombre  x$cantidad  S/ $precio"`) |

### 3.2 `model.Cliente`

| Elemento | Detalle |
|---|---|
| Atributo | `private var nombre: String` |
| Constructor secundario | `constructor() : this("Cliente sin registrar")` |
| Validación | `nombre.isBlank() → "Cliente sin registrar"` |

### 3.3 `service.Carrito` (Encapsulamiento + Polimorfismo)

| Elemento | Detalle |
|---|---|
| Estado interno | `private val _items: MutableList<Producto>` |
| Exposición controlada | `val items: List<Producto> get() = _items.toList()` |
| `val subtotal: Double` | `_items.sumOf { it.subtotalProducto }` |
| `val igv: Double` | `subtotal * 0.18` |
| `val totalAPagar: Double` | `subtotal + igv` |
| `fun productoMasCaro(): Producto?` | `_items.maxByOrNull { it.precio }` |
| `fun calcularDescuento(): Double` | `if (totalAPagar > 3000) totalAPagar * 0.05 else 0.0` |
| `val totalConDescuento: Double` | `totalAPagar - calcularDescuento()` |
| `fun agregarProducto(p: Producto)` | agrega a `_items`, imprime `"Producto agregado: <nombre>"` (verde) |
| `fun eliminarProducto(index: Int): Boolean` | elimina por posición, valida índice fuera de rango |
| `fun vaciarCarrito()` | `_items.clear()` |
| `fun mostrarResumenFinal(cliente: Cliente)` | imprime encabezado, tabla numerada, subtotal, IGV, total, producto más caro, descuento condicional (>S/3000) y despedida — formato ANSI según captura de referencia |

### 3.4 `main.Main` — Flujo de interacción

**Inicio:** solicitar nombre de cliente → `Cliente(nombre)` (constructor vacío si viene en blanco).

**Menú principal** (bucle `do-while` hasta opción `0`):

```
1. Agregar producto
2. Ver carrito
3. Eliminar producto
4. Vaciar carrito
5. Finalizar compra (resumen final)
0. Salir
```

**Submenú de catálogo (opción 1) — bucle anidado:**

- Muestra lista numerada de productos predefinidos (catálogo fijo: Laptop HP, Mouse Logitech, Audífonos Sony, USB Kingston 64GB, etc., con precio unitario).
- Usuario ingresa número → se solicita `cantidad` → se instancia/agrega el `Producto` correspondiente al `Carrito` vía `agregarProducto()`.
- **Tras cada selección, el submenú se vuelve a mostrar automáticamente**, repitiendo el ciclo.
- Opción `0` dentro del submenú → retorna al menú principal.

**Validación de entradas:** toda lectura de `readLine()?.toIntOrNull()` / `toDoubleOrNull()` debe manejar `null` (texto inválido/vacío) asignando el valor por defecto del constructor vacío de `Producto`, sin detener la ejecución.

## 4. Requisitos no funcionales

- Salida con colores ANSI: `\u001B[32m` verde (éxito), `\u001B[33m` amarillo (destacado/descuento), `\u001B[36m` cian (encabezados/despedida), `\u001B[0m` reset.
- Formato numérico: `"S/ %.2f"`.
- El programa debe compilar sin errores (`kotlinc`) y ejecutarse sin excepciones ante cualquier combinación de entradas inválidas.
