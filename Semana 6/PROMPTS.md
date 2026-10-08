# Registro de Prompts y Mejoras con IA — Fase 2 (`con-ia`)

Este documento registra los prompts, la secuencia de desarrollo y los resúmenes de cada commit realizado durante la Fase 2 del Laboratorio 6 (Semana 6) del curso Programación en Móviles (Sección D).

**Repositorio:** https://github.com/DMillonnesZ/Moviles-Seccion-D.git  
**Rama Base:** `sin-ia`  
**Rama de Trabajo:** `con-ia`  
**Autor:** Daniel Alejandro Millones  

---

## Historial de Prompts y Commits

### Commit 1: Actualizar minSdk a 26
- **Prompt:** Se solicitó actualizar `minSdk` a 26 en `app/build.gradle.kts` para permitir el uso nativo de las APIs de `java.time` (`LocalDate`, `YearMonth`).
- **Resumen:** Subió `minSdk` a 26 en la configuración de Gradle para habilitar soporte completo de fechas modernas.

### Commit 2: Crear utilitario Fechas con java.time.LocalDate
- **Prompt:** Se solicitó crear `util/Fechas.kt` con funciones para cálculo de días hábiles, semanas de calendario, formateo en texto y etiquetas relativas.
- **Resumen:** Se implementó `util/Fechas.kt` con soporte para `LocalDate`, `esDiaHabil`, `semanaDeCalendario`, `fechaEnTexto` y utilitarios para el selector de mes.

### Commit 3: Crear sistema de diseño con tokens, tipografía y componentes base
- **Prompt:** Se solicitó definir el sistema de diseño en `Disenio.kt` y `Type.kt`, creando además `EstadoVacio`, `ChipFiltro`, `Insignia`, `Esqueleto`, `ContadorAnimado`, `ConfeteAnimado` y `CheckAnimado`.
- **Resumen:** Agregó tokens visuales de espaciado, formas, duraciones y degradados, además de componentes reutilizables para toda la app.

### Commit 4: Implementar sistema de mensajes globales y transiciones entre pantallas
- **Prompt:** Se solicitó reemplazar los `Toast` con un host superpuesto `MensajeHost` y configurar las transiciones animadas de entrada y salida en `AppNavigation.kt`.
- **Resumen:** Se creó `MensajeController` y `MensajeHost` con animaciones de deslizamiento + fundido y transiciones fluidas en la navegación.

### Commit 5: Rediseñar Splash con entrada animada coreografiada y pulso en Comenzar
- **Prompt:** Se solicitó rediseñar `SplashScreen.kt` con entrada coreografiada tipo spring, formas suaves de fondo y microinteracciones en los botones.
- **Resumen:** Rediseñó el Splash con animación de escala, fundido en cascada y pulso suave en el botón principal.

### Commit 6: Rediseñar Login y Registro con animaciones de entrada, temblor en errores e indicador de contraseña
- **Prompt:** Se solicitó rediseñar `LoginScreen.kt` y `RegistroScreen.kt` incorporando visibilidad de contraseña, indicador de fortaleza y temblor horizontal en campos con error.
- **Resumen:** Agregó microinteracciones, validaciones suaves y efectos visuales al cometer errores en los formularios de autenticación.

### Commit 7: Agregar mensaje de bienvenida dinámico tras el inicio de sesión
- **Prompt:** Se solicitó mostrar un banner flotante dinámico según la hora del día y la próxima cita del usuario al iniciar sesión (una sola vez por inicio).
- **Resumen:** Implementó `consumirMensajeBienvenida` en `Repositorio` y renderizó el banner dinámico personalizado mediante `MensajeHost`.

### Commit 8: Implementar calendario dinámico con LocalDate en FechaHoraScreen (Mejora Obligatoria)
- **Prompt:** Se solicitó reemplazar la lista fija de días en `FechaHoraScreen.kt` por un calendario dinámico con `LocalDate` que muestre 5 días hábiles a partir de hoy y navegación semanal.
- **Resumen:** Implementó el calendario dinámico con días hábiles, recálculo automático de horarios disponibles e inhibición de fechas pasadas.

### Commit 9: Mostrar la fecha de la cita en formato de texto en ConfirmarCitaScreen (Mejora Obligatoria)
- **Prompt:** Se solicitó formatear la fecha recibida en ISO a formato extendido en español (ej. "Martes 16 de setiembre 2026") en `ConfirmarCitaScreen.kt`.
- **Resumen:** Integró `Fechas.fechaEnTexto` para mostrar la fecha de la cita formateada en el resumen.

### Commit 10: Agregar selector desplegable de mes en el calendario de FechaHoraScreen (Mejora Extra)
- **Prompt:** Se solicitó permitir tocar el título del mes en el calendario para desplegar un selector con 12 meses futuros y saltar directamente a la semana correspondiente.
- **Resumen:** Se creó el selector modal de meses con `ModalBottomSheet`, saltando automáticamente a la primera semana del mes seleccionado.

### Commit 11: Rediseñar pantalla de Inicio con próxima cita destacada y cuenta regresiva
- **Prompt:** Se solicitó rediseñar `HomeScreen.kt` agregando tarjeta destacada de "Próxima cita" con etiqueta relativa ("Hoy", "Mañana", "En N días"), accesos rápidos y campana con contador.
- **Resumen:** Rediseñó Inicio con tarjetas suaves, cuenta regresiva de citas y accesos rápidos animados.

### Commit 12: Implementar menú hamburguesa funcional con ModalNavigationDrawer
- **Prompt:** Se solicitó conectar la acción de menú de `HomeScreen` con un `ModalNavigationDrawer` en `AppNavigation.kt` con cabecera de usuario y navegación completa.
- **Resumen:** Implementó el menú lateral deslizable con datos del perfil activo y acceso a todas las secciones de la app.

### Commit 13: Animar la barra inferior de navegación y agregar insignia en Citas
- **Prompt:** Se solicitó animar la selección de íconos en `BarraNavegacion.kt` e incluir una `Insignia` con el número de citas próximas sobre el ícono de Citas.
- **Resumen:** Se añadió la píldora animada de selección, escala de íconos y contador de citas pendientes.

### Commit 14: Rediseñar EspecialidadesScreen y MedicosScreen con resaltado de búsqueda, filtros y estrellas
- **Prompt:** Se solicitó incluir resaltado en negrita del texto buscado, chips de ordenamiento por calificación/experiencia, estrellas de valoración y estado vacío con `EstadoVacio`.
- **Resumen:** Rediseñó el flujo de especialidades y médicos con resaltado de coincidencias, filtros dinámicos y esqueleto shimmer.

### Commit 15: Rediseñar ConfirmarCitaScreen y CitaExitosaScreen con ticket, confeti y calendar intent
- **Prompt:** Se solicitó aplicar diseño de ticket con borde punteado en confirmación, confeti animado de 2 segundos en éxito y botón "Agregar al calendario" con `ACTION_INSERT`.
- **Resumen:** Implementó el diseño de ticket, la animación de confeti en Canvas e integración nativa con el calendario del dispositivo.

### Commit 16: Rediseñar MisCitasScreen y DetalleCitaScreen con pestañas, franja semántica y swipe to dismiss
- **Prompt:** Se solicitó organizar Mis Citas en pestañas "Próximas" y "Pasadas", franja de color por especialidad y opción de deslizar/cancelar cita.
- **Resumen:** Rediseñó la gestión de citas con filtrado por pestañas relativas y diálogo/deslizamiento de cancelación.

### Commit 17: Rediseñar ResultadosScreen y agregar ResultadoDetalleScreen con filtros y compartir
- **Prompt:** Se solicitó agregar buscador de exámenes, chips de filtro por estado y la nueva pantalla `ResultadoDetalleScreen` con tabla de parámetros e Intent `ACTION_SEND`.
- **Resumen:** Se rediseñó Resultados con filtros por estado, resaltado de texto y pantalla de detalle con valores de laboratorio y opción de compartir.

### Commit 18: Rediseñar PerfilScreen con estadísticas, edición de datos y preferencia de sonido
- **Prompt:** Se solicitó incluir cabecera con degradado, tarjetas de estadísticas con `ContadorAnimado`, edición de datos con `Repositorio.actualizarUsuario` e interruptor de sonido.
- **Resumen:** Rediseñó Perfil con edición validada de nombre/teléfono, contadores animados y preferencia de sonido en memoria.

### Commit 19: Rediseñar NotificacionesScreen con tarjetas clicables, punto azul de no leído y agrupación por fecha
- **Prompt:** Se solicitó hacer clicables las tarjetas para ir directo a `DetalleCitaScreen`, añadir un punto azul para no leídas y agrupar las alertas en "Hoy / Anteriores".
- **Resumen:** Rediseñó Notificaciones con navegación al detalle, indicador de lectura en memoria y organización por fecha.

### Commit 20: Implementar notificaciones del sistema con sonido y apertura directa del detalle
- **Prompt:** Se solicitó declarar el permiso `POST_NOTIFICATIONS`, registrar los canales `citas_sonido` y `citas_silencioso`, emitir la notificación real al agendar y procesar el Intent en `MainActivity`.
- **Resumen:** Integró el sistema de notificaciones nativas de Android con canal sonoro/silencioso y apertura directa del detalle de la cita.

### Commit 21: Rediseñar TerminosScreen con barra de progreso de lectura y botón Entendido fijo
- **Prompt:** Se solicitó agregar una barra de progreso calculada con `ScrollState` y mantener el botón "Entendido" fijo en la parte inferior.
- **Resumen:** Agregó la barra de progreso de lectura animada y fijó el botón de aceptación en la parte inferior de la pantalla.
