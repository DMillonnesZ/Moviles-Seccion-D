package com.millones.murocampus.model

import kotlinx.serialization.Serializable

// [E] Ejemplo resuelto
@Serializable
data class Usuario(
    val id: Int,
    val name: String,
    val email: String
)

// [G] Esqueletos. Una data class vacía no compila, por eso son clases vacías.
// TODO Paso 2: conviértelas en data class con userId, id, title, body
@Serializable
class Post

// TODO Paso 2: data class con postId, id, name, email, body
@Serializable
class Comentario

// TODO Paso 2: data class con userId, title, body (sin id)
@Serializable
class NuevoPost