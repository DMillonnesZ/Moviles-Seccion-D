package com.tecsup.pokemon.domain.model

// Lo que puede hacer un admin (con cuentas y con el catálogo)
enum class TipoAccion {
    SUSPENDER, ACTIVAR, HACER_ADMIN, QUITAR_ADMIN, RESTABLECER_CLAVE,
    DESCONTINUAR, REACTIVAR, PUBLICAR_ANUNCIO, QUITAR_ANUNCIO
}

// Una entrada del registro de acciones (colección registro_admin)
data class AccionRegistro(
    val id: String,
    val adminNombre: String,
    val usuarioNombre: String,  // usuario, Pokémon o tipo afectado (vacío en los anuncios)
    val tipo: TipoAccion,
    val detalle: String?,       // motivo, correo, "Pokémon #025", texto del anuncio...
    val fecha: Long?            // milisegundos
)