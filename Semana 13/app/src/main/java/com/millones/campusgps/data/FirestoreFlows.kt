package com.millones.campusgps.data

import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

// Escucha una consulta de Firestore en tiempo real como Flow.
// El listener se quita solo cuando nadie recolecta el Flow.
fun Query.escuchar(): Flow<QuerySnapshot> = callbackFlow {
    val registro = addSnapshotListener { instantanea, error ->
        if (error != null) {
            close(error)
        } else if (instantanea != null) {
            trySend(instantanea)
        }
    }
    awaitClose { registro.remove() }
}
