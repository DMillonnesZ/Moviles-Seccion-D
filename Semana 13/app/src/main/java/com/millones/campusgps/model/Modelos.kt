package com.millones.campusgps.model

data class Posicion(val latitud: Double, val longitud: Double)

// Lugar registrado por cualquier usuario (compartido en Firestore).
// foto = ruta del archivo local de la foto (null = ícono de la categoría).
// esMio = lo registró el usuario actual (solo él puede cambiar la foto o borrarlo).
data class Lugar(
    val id: String,
    val nombre: String,
    val descripcion: String,
    val categoria: String,
    val latitud: Double,
    val longitud: Double,
    val favorito: Boolean = false,
    val foto: String? = null,
    val autorUid: String = "",
    val autorNombre: String = "",
    val esMio: Boolean = false
)

// metros = null mientras todavía no se conoce la ubicación del usuario
data class LugarConDistancia(val lugar: Lugar, val metros: Float?)

// Punto elegido en el mapa que todavía no se guarda (abre el formulario)
data class BorradorLugar(
    val latitud: Double,
    val longitud: Double,
    val direccion: String? = null
)

// Otra persona que decidió compartir su ubicación en vivo
data class PersonaEnVivo(
    val uid: String,
    val nombre: String,
    val latitud: Double,
    val longitud: Double,
    val actualizadoEn: Long
)

// Evento del campus. foto = imagen en Base64 (null = sin imagen).
data class Evento(
    val id: String,
    val titulo: String,
    val descripcion: String,
    val fecha: Long,
    val lugarNombre: String,
    val latitud: Double?,
    val longitud: Double?,
    val foto: String?,
    val autorUid: String,
    val autorNombre: String,
    val asistentes: Int,
    val asisto: Boolean,
    val esMio: Boolean
)

enum class FiltroEventos(val etiqueta: String) {
    PROXIMOS("Próximos"),
    HOY("Hoy"),
    SEMANA("Esta semana"),
    ASISTIRE("Asistiré")
}

// Categorías que se pueden elegir al guardar un lugar
val CATEGORIAS_LUGAR = listOf("Estudio", "Comida", "Eventos", "Servicios", "Otro")

// Mensaje corto que se muestra encima de la pantalla (guardado, foto, errores)
data class AvisoMapa(val texto: String, val exito: Boolean)

enum class TipoError { SIN_PERMISO, GPS_APAGADO, SIN_PLAY_SERVICES, OTRO }

sealed interface UbicacionUi {
    data object Inicial : UbicacionUi
    data object Buscando : UbicacionUi
    data class Lista(val posicion: Posicion) : UbicacionUi
    data class Error(val mensaje: String, val tipo: TipoError) : UbicacionUi
}
