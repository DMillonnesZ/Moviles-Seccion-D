# Sistema de Parqueo (Kotlin - Consola)

Proyecto desarrollado en Kotlin (módulo de consola, Android Studio) **sin Programación Orientada a Objetos**. Toda la lógica se implementa con funciones a nivel de archivo y diccionarios (`Map`) como estructura de datos principal, usando la **placa del vehículo como clave**.

## Estructura del proyecto

El programa se divide en 3 fases (una función explícita por fase), ejecutadas en secuencia desde `main()`:

```
main()
 ├── ingresoDeDatos()     -> Fase 1
 ├── calculos()           -> Fase 2
 └── mostrarResultados()  -> Fase 3
```

## Fase 1: Ingreso de Datos

Registra los vehículos que ingresan al parqueo. Por cada vehículo solicita:

- **Placa**
- **Tipo** (MOTO, AUTO o CAMIONETA — validado)
- **Horas** de permanencia (número entero, mínimo 1 — validado)
- **Nombre del Cliente**

Datos que se calculan automáticamente (no se piden al usuario):

- **Hora de Ingreso**: se toma directamente de la hora del sistema (`LocalTime.now()`).
- **Hora de Salida**: hora de ingreso + horas ingresadas.
- **Cliente Frecuente**: se determina según cuántas veces se ha registrado esa misma placa (no cuántas veces la misma persona), ya que el vehículo puede ser traído por un familiar u otra persona. A partir del **5to registro** de la misma placa, se marca como cliente frecuente.

Reglas de control:

- Límite máximo de **30 vehículos** por sesión.
- Después de cada registro se pregunta `¿Desea continuar registrando? (S/N)`. Si la respuesta es `N`, se termina el ingreso (en el flujo completo, esto regresaría al menú principal).

## Fase 2: Cálculos

Calcula el monto a pagar por cada vehículo registrado, según las siguientes reglas:

**Tarifa base por hora:**

| Tipo | Tarifa |
|---|---|
| Moto | S/ 2.00 |
| Auto | S/ 4.00 |
| Camioneta | S/ 10.00 |

**Recargos por tramo de horas** (el recargo se aplica por cada hora individual, según el tramo en el que cae esa hora, no sobre el total de la estadía):

| Tramo de horas | Recargo |
|---|---|
| Horas 1 - 2 | 0% (tarifa completa) |
| Horas 3 - 5 | 20% |
| Horas posteriores a la 5ta | 50% |

**Descuento por cliente frecuente:** si la placa fue marcada como frecuente en la Fase 1, se aplica un **10% de descuento** sobre el total ya calculado.

El resultado final de cada vehículo se guarda en un diccionario (`mapTotal`), listo para mostrarse en la Fase 3.

## Fase 3: Mostrar Resultados

Presenta dos bloques de información:

**1. Tabla de Detalle de Pagos**, con columnas:

```
PLACA | TIPO | HORAS | RECARGO | PAGO
```

**2. Resumen del Día**, con:

- Total de vehículos registrados
- Cantidad de motos, autos y camionetas
- Recaudación total del día
- Vehículo con mayor pago (placa y monto)
- Hora más demandada (hora del día con más registros de ingreso)

## Estructuras de datos utilizadas

Todos los diccionarios usan la **placa** como clave, lo que permite acceso directo a los datos de un vehículo específico sin recorrer listas:

| Diccionario | Contenido |
|---|---|
| `mapTipos` | Tipo de vehículo |
| `mapHoras` | Horas de permanencia ingresadas |
| `mapHoraIngreso` | Hora de ingreso (sistema) |
| `mapHoraSalida` | Hora de salida calculada |
| `mapClientes` | Nombre del cliente |
| `mapFrecuente` | Si el cliente es frecuente (booleano) |
| `mapVisitas` | Historial de veces que la placa fue registrada |
| `mapTotal` | Monto final a pagar |

## Requisito académico

El proyecto se desarrolló **sin Programación Orientada a Objetos** (sin clases), por requerimiento explícito del docente. Se optó por funciones y diccionarios en su lugar.