# Registro de Prompts y Análisis de IA (PROMPTS.md) 🤖

Este documento registra los prompts utilizados durante la **Fase 2 (rama `mejora-ia`)** del proyecto **MuroCampus**, detallando las respuestas brindadas por la IA, los cambios/descartes realizados y la justificación técnica de cada decisión.

---

## 📊 Bitácora de Prompts y Criterio Técnico

| Prompt que escribí | Qué respondió Gemini / IA | Qué cambié o descarté | Por qué (Criterio técnico) |
| :--- | :--- | :--- | :--- |
| *"¿Cómo puedo implementar la eliminación de una publicación con Retrofit usando @DELETE y SwipeToDismissBox en Compose?"* | Sugirió retornar `Unit` directamente en Retrofit: `@DELETE("posts/{id}") suspend fun borrar(...): Unit` y borrar el item directamente del StateFlow en la UI. | Cambié el tipo de retorno en `PostApi` a `Response<Unit>` y envolví la llamada en un diálogo de confirmación o confirmación previa en `SwipeToDismissBox`. | Al usar `Response<Unit>`, Retrofit no lanza automáticamente una `HttpException` ante respuestas con cuerpo vacío (200 OK / 204 No Content) o errores de estado HTTP. Permite evaluar `respuesta.isSuccessful` manualmente en el Repository sin llenar el código de bloques `try-catch` desordenados. |
| *"Genera una función para buscar por título en la lista de publicaciones descargadas."* | Propuso hacer una llamada `@GET("posts")` a la API con un `@Query("q")` cada vez que el usuario escribe un carácter en el campo de texto. | Descarté las llamadas de red por cada tecla presionada y mantuve la búsqueda en memoria local usando `combine` y `StateFlow` en el ViewModel. | Realizar peticiones de red por cada carácter introduce latencia excesiva y satura la API. Como la lista de publicaciones ya está descargada en memoria, filtrar con `title.contains(texto, ignoreCase = true)` mediante `combine` en `PostsViewModel` ofrece una respuesta instantánea y fluida al usuario sin consumir datos extra. |
| *"Dame un ejemplo de cómo manejar las excepciones en el Repository al llamar a Retrofit."* | Sugirió un bloque `try-catch (e: Exception)` genérico devolviendo `null` o una lista vacía cuando ocurra un error. | Añadí re-lanzamiento explícito para `CancellationException` (`catch (e: CancellationException) { throw e }`) y retorné `Result.failure(e)` para otras excepciones. | Si se captura `CancellationException` sin re-lanzarla, las corrutinas de Kotlin no pueden cancelarse correctamente cuando el ViewModel o el ciclo de vida del Composable se destruyen, generando fugas de memoria y tareas en segundo plano huérfanas. Además, devolver `Result.failure` permite propagar el mensaje de error real a la UI. |
| *"¿Cómo muestro un Snackbar cuando la publicación se elimina correctamente desde la pantalla?"* | Sugirió llamar a `rememberCoroutineScope().launch { snackbarHostState.showSnackbar(...) }` directamente dentro de la función de callback del botón "Eliminar". | Creé un evento unidireccional de un solo uso mediante una `sealed interface EventoUi` (`EventoUi.Exito` / `EventoUi.Error`) emitida desde el ViewModel y consumida con `LaunchedEffect` en la UI. | Disparar efectos secundarios directamente desde listeners de UI rompe la arquitectura MVVM. La UI debe ser reactiva al estado del ViewModel. Al emitir un `EventoUi` con ID único y consumirlo mediante `LaunchedEffect`, la UI reacciona correctamente a la lógica de negocio sin duplicar mensajes tras recomposiciones. |
| *"Agrega paginación para la lista de publicaciones en Compose."* | Sugirió un listener de scroll en el `LazyColumn` usando `LazyListState.firstVisibleItemIndex` que llamaba directamente a `api.pagina(...)` desde la vista Compose. | Descarté las llamadas a Retrofit desde la vista y moví toda la lógica de paginación al ViewModel (`cargarMas()`), exponiendo estados `hayMas` y `cargandoMas`. | Las vistas Composables **nunca deben invocar directamente las interfaces de red ni Retrofit**. Tocar la API desde la vista rompe la separación de capas y provoca llamadas duplicadas durante recomposiciones de Compose. El ViewModel gestiona el estado acumulado (`UiState.Exito(actual + nuevos)`). |

---

## 🧠 Análisis Crítico y Buenas Prácticas Aplicadas

Durante el desarrollo asistido por IA se identificaron y corrigieron los siguientes antipatrones comunes:

1. **Evitar la supresión ciega de excepciones (`catch` vacíos)**:
   - **Antipatrón detectado:** Bloques `try { ... } catch (e: Exception) { }` que tragan los errores sin notificar al usuario.
   - **Solución implementada:** Encapsulamiento en `Result<T>` en la capa `Repository` y transformación a `UiState.Error(mensaje)` para mostrar alertas/snackbars claros en la interfaz.

2. **Respeto a la cancelación de corrutinas (`CancellationException`)**:
   - **Antipatrón detectado:** `catch (e: Exception)` atrapando la cancelación de Corrutinas.
   - **Solución implementada:** Re-lanzamiento explícito de `CancellationException` en `PostRepositoryRemote.llamar(...)` para asegurar que las tareas se cancelen adecuadamente al cambiar de pantalla.

3. **Recolección consciente del estado en Compose**:
   - **Antipatrón detectado:** Uso de `collectAsState()` estándar que continúa escuchando emisiones en segundo plano cuando la app está minimizada.
   - **Solución implementada:** Uso de `collectAsStateWithLifecycle()` en todas las pantallas Compose para detener la recolección cuando la UI no es visible, ahorrando recursos y batería.

4. **Persistencia local y reactividad sin backend real**:
   - **Antipatrón detectado:** Asumir que JSONPlaceholder guarda borrados o creaciones.
   - **Solución implementada:** Implementación de un estado reactivo local (`_creados` y `_borrados`) en `PostsViewModel` combinado mediante `combine(...)`, permitiendo una experiencia de usuario instantánea y consistente.
