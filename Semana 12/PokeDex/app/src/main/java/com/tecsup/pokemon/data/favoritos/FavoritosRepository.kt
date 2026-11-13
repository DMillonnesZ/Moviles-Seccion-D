package com.tecsup.pokemon.data.favoritos

// Favoritos de la cuenta en la nube (cada usuario tiene los suyos)
interface FavoritosRepository {
    // Éxito con null = la cuenta aún no tiene la lista guardada
    suspend fun leer(): Result<Set<Int>?>

    // Se guardan en segundo plano (sin internet, Firestore los sube después)
    fun agregar(id: Int)
    fun quitar(id: Int)
    fun guardarTodos(ids: Set<Int>)
}