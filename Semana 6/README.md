# TECSUP Store - App Android con Jetpack Compose (Material 3)

Aplicación móvil desarrollada en **Kotlin** y **Jetpack Compose** (Material 3) como parte de la mejora de arquitectura, estado y componentes interactivos para la TECSUP Store.

---

## 📱 Capturas de Pantalla y Demostración

| Pantalla Principal (Inicio) | Menú Lateral con Badge | Pantalla de Favoritos |
| :---: | :---: | :---: |
| ![Inicio](docs/screenshots/01_inicio.png) | ![Drawer](docs/screenshots/02_drawer_badge.png) | ![Favoritos](docs/screenshots/03_favoritos.png) |

| Estado Vacío de Favoritos | Mis Pedidos | Perfil de Usuario |
| :---: | :---: | :---: |
| ![Favoritos Vacío](docs/screenshots/04_favoritos_vacio.png) | ![Pedidos](docs/screenshots/05_pedidos.png) | ![Perfil](docs/screenshots/06_perfil.png) |

| Diálogo Reportar Producto | Diálogo Cerrar Sesión | Compartir Producto |
| :---: | :---: | :---: |
| ![Diálogo Reportar](docs/screenshots/07_dialog_reportar.png) | ![Diálogo Cerrar Sesión](docs/screenshots/08_dialog_cerrar_sesion.png) | ![Compartir](docs/screenshots/09_compartir.png) |

> *Nota: Coloca las imágenes correspondientes en la carpeta `docs/screenshots/` con los nombres indicados.*

---

## ✨ Características Principales

### 1. Gestión de Favoritos (State Hoisting)
- Estado centralizado que sobrevive a rotaciones de pantalla mediante `rememberSaveable`.
- Indicador visual con corazón rojo en cada tarjeta de producto seleccionada.
- Badge o globo morado con contador dinámico en el menú lateral (*Drawer*). Muestra `99+` si supera los 99 elementos.
- Notificaciones emergentes (*Snackbar*) al agregar o remover un favorito.

### 2. Navegación Completa
- **Inicio (Más vendidos):** Catálogo de productos interactivo.
- **Mis Pedidos:** Lista de compras realizadas con estado y detalle de totales en tarjetas tipo lavanda.
- **Favoritos:** Vista filtrada con los productos marcados; incluye estado vacío personalizado.
- **Perfil:** Datos de usuario centralizados con avatar dinámico basado en las iniciales.

### 3. Acciones del Menú Desplegable (3 Puntos)
- **Favoritos:** Agrega o quita el producto de la lista centralizada.
- **Compartir:** Abre el selector nativo de Android mediante un `Intent` `ACTION_SEND` de texto plano.
- **Reportar:** Muestra un cuadro de diálogo de confirmación (`AlertDialog`) y notifica al usuario con un `Snackbar` al confirmar.

### 4. Cierre de Sesión Seguro
- Cuadro de diálogo de confirmación antes de salir.
- Limpia los datos de sesión en memoria, redirige a la pantalla principal e informa mediante una notificación emergente.

---

## 🛠️ Tecnologías Utilizadas

- **Lenguaje:** Kotlin
- **UI Toolkit:** Jetpack Compose (Material 3)
- **Navegación:** Jetpack Navigation Compose
- **Íconos:** Material Icons Extended
- **Arquitectura:** Componentes desacoplados, State Hoisting, Recomposición eficiente.

---

## 📂 Estructura del Proyecto

```text
com.tecsup.millones
├── MainActivity.kt
├── components/
│   ├── CerrarSesionDialog.kt
│   ├── DrawerHeader.kt
│   ├── MenuProducto.kt
│   ├── ReportarDialog.kt
│   ├── TarjetaProducto.kt
│   └── TiendaTopBar.kt
├── model/
│   ├── Producto.kt
│   └── Usuario.kt
├── navigation/
│   ├── AppDrawer.kt
│   ├── AppNavegacion.kt
│   └── Destinos.kt
├── screens/
│   ├── FavoritosScreen.kt
│   ├── InicioScreen.kt
│   ├── PedidosScreen.kt
│   └── PerfilScreen.kt
└── ui/
    └── theme/
        ├── Color.kt
        ├── Theme.kt
        └── Type.kt
```

---

## 📄 Documentación Adicional
- [PROMPTS.md](PROMPTS.md): Registro de prompts y tabla de control de commits de la Fase 2.
