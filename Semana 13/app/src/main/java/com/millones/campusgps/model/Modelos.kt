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

// Resultado al buscar una ubicación por nombre (lugar registrado o dirección del Geocoder)
data class ResultadoBusqueda(
    val titulo: String,
    val detalle: String,
    val latitud: Double,
    val longitud: Double,
    val esLugarRegistrado: Boolean
)

// ----- Perfil -----

// Perfil público de un usuario (usuarios/{uid}). foto = imagen en Base64 (null = inicial)
data class Perfil(
    val uid: String,
    val nombre: String,
    val carrera: String,
    val foto: String?
)

data class EstadisticasPerfil(
    val lugares: Int = 0,
    val favoritos: Int = 0,
    val asistire: Int = 0,
    val opiniones: Int = 0
)

// Radios de "Cerca" que se pueden elegir en el perfil
val RADIOS_CERCANOS = listOf(100, 300, 500)

// ----- Opiniones (Chat) -----

enum class TipoObjetivo(val valor: String) {
    LUGAR("lugar"),
    EVENTO("evento");

    companion object {
        fun de(valor: String?): TipoObjetivo? = entries.firstOrNull { it.valor == valor }
    }
}

// Opinión con calificación (1 a 5) sobre un lugar o un evento
data class Comentario(
    val id: String,
    val texto: String,
    val calificacion: Int,
    val autorUid: String,
    val autorNombre: String,
    val creadoEn: Long,
    val tipo: TipoObjetivo,
    val objetivoId: String,
    val objetivoNombre: String,
    val esMio: Boolean
)

data class ResumenCalificacion(val promedio: Float, val total: Int)

// Lugar o evento sobre el que se puede opinar. destacado = favorito o "asistiré"
data class ObjetivoOpinion(
    val id: String,
    val nombre: String,
    val tipo: TipoObjetivo,
    val detalle: String,
    val destacado: Boolean
)

// Lugar o evento mejor calificado (carrusel de arriba del Chat)
data class Destacado(
    val id: String,
    val nombre: String,
    val tipo: TipoObjetivo,
    val resumen: ResumenCalificacion
)

enum class FiltroOpiniones(val etiqueta: String) {
    TODAS("Todas"),
    FAVORITOS("Mis favoritos"),
    LUGARES("Lugares"),
    EVENTOS("Eventos"),
    MIAS("Mías")
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
