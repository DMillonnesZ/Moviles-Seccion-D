package com.tecsup.pokemon.data.favoritos

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout

// Favoritos en usuarios/{uid}, campo "favoritos": [25, 6, 150]
class FirebaseFavoritosRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) : FavoritosRepository {

    // Documento del usuario logueado (null si no hay sesión)
    private fun documento(): DocumentReference? =
        auth.currentUser?.uid?.let { db.collection("usuarios").document(it) }

    override suspend fun leer(): Result<Set<Int>?> = runCatching {
        val ref = documento() ?: error("No hay sesión activa")
        val doc = withTimeout(5_000) { ref.get().await() }
        // Firestore guarda los números como Long
        val lista = doc.get("favoritos") as? List<*> ?: return@runCatching null
        lista.mapNotNull { (it as? Number)?.toInt() }.toSet()
    }

    override fun agregar(id: Int) {
        documento()?.set(mapOf("favoritos" to FieldValue.arrayUnion(id)), SetOptions.merge())
    }

    override fun quitar(id: Int) {
        documento()?.set(mapOf("favoritos" to FieldValue.arrayRemove(id)), SetOptions.merge())
    }

    override fun guardarTodos(ids: Set<Int>) {
        documento()?.set(mapOf("favoritos" to ids.toList()), SetOptions.merge())
    }
}