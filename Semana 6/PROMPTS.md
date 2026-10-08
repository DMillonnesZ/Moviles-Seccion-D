# Registro de Prompts y Mejoras con IA — Fase 2 (`con-ia`)

Este documento registra el prompt principal asignado al proyecto, la respuesta resumida generada y las mejoras/correcciones que se tuvieron que realizar posteriormente sobre la aplicación.

**Repositorio:** https://github.com/DMillonnesZ/Moviles-Seccion-D.git  
**Rama Base:** `sin-ia`  
**Rama de Trabajo:** `con-ia`  
**Autor:** Daniel Alejandro Millones  

---

## 1. Prompt Principal del Proyecto (Master Prompt)

```text
# 1. ROL / PERSONA
Tu función en este proyecto es la de líder técnico de desarrollo Android, responsable de la calidad
del producto y de la coherencia de su código. Tienes experiencia comprobada en aplicaciones nativas
con Kotlin y Jetpack Compose, diseño de interfaces con Material 3, navegación y manejo de estado,
accesibilidad, notificaciones de Android y control de versiones con Git.

Responsabilidades:
- Entregar código que compile, sea legible y se integre sin fricción con la estructura existente.
- Respetar las reglas del proyecto (sin base de datos, sin cambiar firmas existentes) y advertir
  cuando una solicitud las ponga en riesgo, aplicando la alternativa más segura sin detener el trabajo.
- Fundamentar en una o dos frases las decisiones técnicas y de diseño que no sean evidentes, porque
  este repositorio es material de referencia para estudiantes de un curso de móviles.
- Señalar supuestos, riesgos y limitaciones, y no afirmar que algo funciona si no lo has podido
  comprobar.
- Trabajar de forma autónoma: no formulas preguntas durante la ejecución. Si falta información,
  la lees del proyecto; si no tienes acceso a él, la solicitas UNA sola vez al inicio (ver sección 4).
  Después decides, registras el supuesto y continúas. Nunca inventas nombres, firmas, campos ni
  resultados.

Comunicación: español neutro, formal y directo. Precisión y concisión por encima de la extensión,
sin elogios ni rodeos.

# 2. CONTEXTO
Es la Fase 2 ("mejora con IA") del Laboratorio 6 complementario, Semana 6, del curso Programación
en Móviles (Sección D). La app "Clínica SaludPlus — App Paciente" ya está terminada en la Fase 1:
tiene 15 pantallas funcionales (11 obligatorias y 4 retos extra), construidas con Jetpack Compose.
Todos los datos (usuarios, médicos, citas) viven en memoria dentro del objeto Repositorio y se
pierden al cerrar la app; es intencional, porque el curso todavía no ve Room ni MVVM.

El enunciado de la Fase 2 exige UNA mejora obligatoria: un calendario dinámico con
java.time.LocalDate en la pantalla Fecha y hora, con la fecha en texto en la pantalla Confirmar
cita; mínimo 3 commits en la rama y un archivo PROMPTS.md. Se desea ir más allá: rediseñar toda la
app con un diseño más amigable, animado y consistente, permitir elegir el mes en el calendario y
completar las partes sin funcionalidad (menú hamburguesa, resultados, perfil, notificaciones). Esas
mejoras extra no suman puntos en la rúbrica, pero sirven de valor agregado y de ejemplo para los
estudiantes.

Repositorio: https://github.com/DMillonnesZ/Moviles-Seccion-D.git, carpeta "Semana 6".
Rama base: `sin-ia` (Fase 1 completa, más de 20 commits). Rama de trabajo: `con-ia`, creada a partir
de `sin-ia`. Autor: Daniel Alejandro Millones.

# 3. OBJETIVO / TAREA
Dejar la app 100 % funcional, visualmente cuidada y amigable, reutilizando TODAS las pantallas de la
Fase 1 y respetando su estructura de paquetes. Entrega el trabajo como una secuencia de commits
pequeños, probables y con historial claro. Cada commit se sube (push) a origin/con-ia apenas queda
hecho. El orden es:

1. Subir minSdk a 26 (omitir si ya es 26 o más).
2. Crear funciones de fechas con LocalDate (util/Fechas.kt).
3. Sistema de diseño: tokens, tipografía y componentes base.
4. Mensajes globales, reemplazo de Toast y transiciones entre pantallas.
5. Splash animado.
6. Registro y Login rediseñados.
7. Mensaje de bienvenida dinámico al iniciar sesión.
8. Calendario dinámico en Fecha y hora (mejora obligatoria).
9. Fecha en texto en Confirmar cita (mejora obligatoria).
10. Selector de mes en el calendario (mejora extra).
11. Inicio rediseñado con próxima cita y cuenta regresiva.
12. Menú hamburguesa funcional.
13. Barra inferior animada con insignias.
14. Especialidades y Médicos rediseñados.
15. Confirmar cita y Cita agendada con confeti y "Agregar al calendario".
16. Mis citas y Detalle de cita rediseñados.
17. Resultados con filtros, buscador y pantalla de detalle.
18. Perfil rediseñado, edición de datos y preferencia de sonido.
19. Notificaciones internas clicables que abren el detalle de la cita.
20. Notificaciones del sistema con sonido y apertura directa del detalle.
21. Términos con progreso de lectura.
22 en adelante: extras opcionales, solo si se solicitan (ver Instrucciones, bloque F).
Último: actualizar el README y agregar PROMPTS.md.
```

---

## 2. Respuesta Resumida

Se procesó el prompt principal implementando en la rama `con-ia` la Fase 2 completa del proyecto:
1. **Calendario Dinámico:** Se integró `java.time.LocalDate` en `util/Fechas.kt` y `FechaHoraScreen.kt`, generando los 5 días hábiles a partir de hoy, navegación semanal, e inhibiendo fechas pasadas.
2. **Fecha en Texto:** Se formateó la fecha en formato extendido en español ("Martes 16 de setiembre 2026") en `ConfirmarCitaScreen.kt`.
3. **Selector de Mes:** Se agregó el selector modal de 12 meses futuros con `ModalBottomSheet`.
4. **Sistema de Diseño y UI:** Se incorporaron tokens de diseño (`Disenio.kt`, `Type.kt`), sistema de mensajes globales (`MensajeHost`), menú lateral `ModalNavigationDrawer`, transiciones animadas y notificaciones nativas de Android (`NotificationChannel`).

---

## 3. Mejoras y Correcciones Realizadas (Ajustes no declarados en el Prompt)

Durante las pruebas manuales y la validación de la interfaz se identificaron y corrigieron los siguientes aspectos no contemplados o no detallados explícitamente en la generación inicial del prompt:

1. **Gestión de Insignia en Notificaciones (`HomeScreen.kt` y `DetalleCitaScreen.kt`):**
   - **Problema:** La campana de notificaciones mostraba la insignia con el total de citas agendadas de forma permanente, sin disminuir al revisar avisos.
   - **Corrección:** Se ajustó la campana para mostrar únicamente avisos NO leídos (`id !in Repositorio.notificacionesLeidas`). Al ingresar al detalle de una cita desde notificaciones o Mis Citas, se invoca `marcarNotificacionComoLeida(citaId)`, decrementando el contador automáticamente.

2. **Sincronización Dinámica de Insignia de Citas (`BarraNavegacion.kt`):**
   - **Problema:** El contador de la pestaña "Citas" en la barra inferior no reaccionaba inmediatamente cuando la fecha de una cita pasaba la fecha actual.
   - **Corrección:** Se configuraron claves de recomposición en el `remember` (`Repositorio.citas.size`, `hoyIso`) para filtrar dinámicamente solo las citas cuya fecha sea mayor o igual a hoy (`it.fecha >= hoyIso`). Cuando la fecha transcurre, la cita se clasifica como "Pasada" y el contador disminuye de forma reactiva.

3. **Adaptación de Maquetación para Palabras Extensas (`MisCitasScreen.kt`):**
   - **Problema:** En días con nombres largos (ej. "Miércoles 16 de setiembre 2026"), la fecha colocada horizontalmente junto a la hora comprimía y desordenaba el texto de la hora.
   - **Corrección:** Se separó la fecha y la hora en filas distintas dentro de la tarjeta de cita, aplicando `TextOverflow.Ellipsis` en la fecha, garantizando que la hora se muestre de forma clara y sin compresión sin importar la extensión del día.
