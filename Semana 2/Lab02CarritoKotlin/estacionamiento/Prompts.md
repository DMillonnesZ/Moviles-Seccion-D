# Prompts del Proyecto - Sistema de Parqueo (Kotlin)

Estos son los 3 prompts utilizados para generar cada fase del sistema, correspondientes a los 3 commits del proyecto.

---

## Prompt 1 — Fase 1: Ingreso de Datos

```
Estoy haciendo un proyecto en Kotlin (Android Studio, modulo de consola)
sin usar Programacion Orientada a Objetos (sin clases), solo con funciones
y variables/diccionarios a nivel de archivo. Es un sistema de parqueo.

Necesito la Fase 1: Ingreso de Datos, en una sola funcion llamada
ingresoDeDatos() (no crear funciones auxiliares extra).

Requisitos:
- Usar diccionarios (Map) con la PLACA del vehiculo como clave para
  guardar: tipo, horas, cliente, hora de ingreso, hora de salida,
  si es cliente frecuente.
- Pedir por consola: Placa, Tipo (MOTO/AUTO/CAMIONETA, validar),
  Horas (numero entero, minimo 1, validar).
- La hora de ingreso NO se pide al usuario, se toma automaticamente
  con la hora del sistema (LocalTime.now()).
- La hora de salida se calcula sola: hora de ingreso + horas ingresadas.
- Pedir Nombre del Cliente.
- La frecuencia del cliente NO se pregunta, se calcula automaticamente
  segun cuantas veces se ha registrado esa misma placa (puede venir
  otra persona con el mismo vehiculo). A partir del 5to registro de
  la misma placa, se marca como cliente frecuente.
- Limite maximo de 30 vehiculos registrados.
- Despues de cada registro, preguntar "Desea continuar registrando? (S/N)".
  Si responde N, terminar el registro (en el programa final esto
  regresaria al menu principal).
```

---

## Prompt 2 — Fase 2: Cálculos

```
Continuando el mismo proyecto (Kotlin, sin POO, con los diccionarios
por placa ya creados en la Fase 1), necesito la Fase 2: Calculos, en
una sola funcion llamada calculos() (sin funciones auxiliares).

Reglas de tarifa:
- Tarifa base por hora segun tipo: Moto S/2, Auto S/4, Camioneta S/10.
- Si el vehiculo permanece hasta 2 horas, paga tarifa completa (sin recargo).
- Si permanece mas de 2h hasta 5h, esas horas (3ra, 4ta, 5ta) tienen
  un recargo del 20% cada una.
- Si permanece mas de 5h, las horas posteriores a la quinta tienen
  un recargo del 50% cada una.
- El recargo se aplica por cada hora individual segun el tramo en el
  que cae, no sobre el total completo de la estadia.
- Si el cliente es frecuente (definido en la Fase 1), se aplica un
  10% de descuento sobre el total ya calculado.
- Guardar el total final a pagar de cada placa en un diccionario
  (mapTotal).

Calcula usando los 3 tramos de horas directamente (horas normales,
horas con 20%, horas con 50%) en vez de recorrer hora por hora con un bucle.
```

---

## Prompt 3 — Fase 3: Mostrar Resultados

```
Continuando el mismo proyecto (Kotlin, sin POO, con los diccionarios
de la Fase 1 y los totales calculados en la Fase 2), necesito la
Fase 3: Mostrar Resultados, en una sola funcion llamada
mostrarResultados() (sin funciones auxiliares).

Debe mostrar:
1. Una TABLA con el detalle de pagos por vehiculo, con columnas:
   PLACA, TIPO, HORAS, RECARGO (0%, 20% o 50% segun el tramo de
   horas del vehiculo), PAGO (el total calculado). Formato alineado
   tipo tabla, similar a un recibo.
2. Un "RESUMEN DEL DIA" con:
   - Total de vehiculos registrados
   - Cantidad de motos, autos y camionetas
   - Recaudacion total del dia (suma de todos los pagos)
   - Vehiculo con mayor pago (placa y monto)
   - Hora mas demandada (la hora del dia con mas registros de ingreso)

Al final, actualiza la funcion main() para que solo llame en orden:
ingresoDeDatos(), calculos(), mostrarResultados().
```