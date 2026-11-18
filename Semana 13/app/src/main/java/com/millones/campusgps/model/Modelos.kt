package com.millones.campusgps.model

data class Posicion(val latitud: Double, val longitud: Double)

// Lugar que registra el usuario manteniendo presionado el mapa.
// foto = ruta del archivo guardado en la app (null = se muestra el ícono de la categoría)
data class Lugar(
    val id: Int,
    val nombre: String,
    val descripcion: String,
    val categoria: String,
    val latitud: Double,
    val longitud: Double,
    val favorito: Boolean = false,
    val foto: String? = null
)

// metros = null mientras todavía no se conoce la ubicación del usuario
data class LugarConDistancia(val lugar: Lugar, val metros: Float?)

// Punto elegido en el mapa que todavía no se guarda (abre el formulario)
data class BorradorLugar(
    val latitud: Double,
    val longitud: Double,
    val direccion: String? = null
)

// Categorías que se pueden elegir al guardar un lugar
val CATEGORIAS_LUGAR = listOf("Estudio", "Comida", "Eventos", "Servicios", "Otro")

// Mensaje corto que se muestra encima del mapa (lugar guardado, foto, errores)
data class AvisoMapa(val texto: String, val exito: Boolean)

enum class TipoError { SIN_PERMISO, GPS_APAGADO, SIN_PLAY_SERVICES, OTRO }

sealed interface UbicacionUi {
    data object Inicial : UbicacionUi
    data object Buscando : UbicacionUi
    data class Lista(val posicion: Posicion) : UbicacionUi
    data class Error(val mensaje: String, val tipo: TipoError) : UbicacionUi
}
