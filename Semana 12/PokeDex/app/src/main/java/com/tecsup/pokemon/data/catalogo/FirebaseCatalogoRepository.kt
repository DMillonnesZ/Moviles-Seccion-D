package com.tecsup.pokemon.data.catalogo

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.SetOptions
import com.tecsup.pokemon.data.admin.RegistroAdmin
import com.tecsup.pokemon.domain.model.Anuncio
import com.tecsup.pokemon.domain.model.Deshabilitados
import com.tecsup.pokemon.domain.model.TipoAccion
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeoutOrNull

private const val CATALOGO = "catalogo"
private const val DESHABILITADOS = "deshabilitados"
private const val ANUNCIO = "anuncio"
private const val ESPERA_MS = 8_000L

// Implementación con Firestore: catalogo/deshabilitados y catalogo/anuncio.
// Cada cambio se guarda junto con su entrada en el registro de acciones.
class FirebaseCatalogoRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) : CatalogoRepository {

    private val registroAdmin = RegistroAdmin(auth, db)

    private val docDeshabilitados: DocumentReference
        get() = db.collection(CATALOGO).document(DESHABILITADOS)

    private val docAnuncio: DocumentReference
        get() = db.collection(CATALOGO).document(ANUNCIO)

    override val deshabilitados: Flow<Deshabilitados> = callbackFlow {
        val escucha = docDeshabilitados.addSnapshotListener { doc, error ->
            trySend(if (error != null || doc == null) Deshabilitados() else doc.aDeshabilitados())
        }
        awaitClose { escucha.remove() }
    }
        // Empieza vacío para no hacer esperar a las listas
        .onStart { emit(Deshabilitados()) }
        .distinctUntilChanged()

    override suspend fun leerDeshabilitados(): Deshabilitados =
        runCatching {
            withTimeoutOrNull(3_000) { docDeshabilitados.get().await().aDeshabilitados() }
        }.getOrNull() ?: Deshabilitados()

    override val anuncio: Flow<Anuncio?> = callbackFlow {
        val escucha = docAnuncio.addSnapshotListener { doc, error ->
            if (error != null || doc == null || !doc.exists()) {
                trySend(null)
                return@addSnapshotListener
            }
            val texto = doc.getString("texto")?.takeIf { it.isNotBlank() }
            trySend(
                texto?.let {
                    Anuncio(
                        texto = it,
                        autor = doc.getString("autor") ?: "Admin",
                        fecha = doc.getTimestamp("fecha", DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                            ?.toDate()?.time
                    )
                }
            )
        }
        awaitClose { escucha.remove() }
    }.distinctUntilChanged()

    override suspend fun cambiarPokemon(id: Int, nombre: String, disponible: Boolean): Result<Unit> =
        guardar(
            ref = docDeshabilitados,
            datos = mapOf("pokemon" to if (disponible) FieldValue.arrayRemove(id) else FieldValue.arrayUnion(id)),
            objetivo = nombre.replaceFirstChar { it.uppercase() },
            tipo = if (disponible) TipoAccion.REACTIVAR else TipoAccion.DESCONTINUAR,
            detalle = "Pokémon #%03d".format(id)
        )

    override suspend fun cambiarTipo(nombre: String, nombreEs: String, disponible: Boolean): Result<Unit> =
        guardar(
            ref = docDeshabilitados,
            datos = mapOf("tipos" to if (disponible) FieldValue.arrayRemove(nombre) else FieldValue.arrayUnion(nombre)),
            objetivo = nombreEs,
            tipo = if (disponible) TipoAccion.REACTIVAR else TipoAccion.DESCONTINUAR,
            detalle = "Tipo"
        )

    override suspend fun publicarAnuncio(texto: String): Result<Unit> {
        val limpio = texto.trim()
        if (limpio.isEmpty()) return Result.failure(Exception("Escribe el anuncio"))
        return guardar(
            ref = docAnuncio,
            datos = mapOf(
                "texto" to limpio,
                "autor" to registroAdmin.nombreAdmin(),
                "fecha" to FieldValue.serverTimestamp()
            ),
            objetivo = "",
            tipo = TipoAccion.PUBLICAR_ANUNCIO,
            detalle = limpio
        )
    }

    override suspend fun quitarAnuncio(): Result<Unit> =
        guardar(
            ref = docAnuncio,
            datos = null,
            objetivo = "",
            tipo = TipoAccion.QUITAR_ANUNCIO,
            detalle = null
        )

    // Guarda el cambio (datos = null borra el documento) y la entrada del registro, en un lote
    private suspend fun guardar(
        ref: DocumentReference,
        datos: Map<String, Any>?,
        objetivo: String,
        tipo: TipoAccion,
        detalle: String?
    ): Result<Unit> {
        if (auth.currentUser == null) return Result.failure(Exception("No hay sesión activa"))
        return try {
            val lote = db.batch()
            if (datos == null) lote.delete(ref) else lote.set(ref, datos, SetOptions.merge())
            lote.set(registroAdmin.nuevaEntrada(), registroAdmin.datos(null, objetivo, tipo, detalle))
            // Sin internet el lote queda guardado y se sube después
            withTimeoutOrNull(ESPERA_MS) { lote.commit().await() }
            Result.success(Unit)
        } catch (e: FirebaseFirestoreException) {
            val mensaje = if (e.code == FirebaseFirestoreException.Code.PERMISSION_DENIED) {
                "Sin permiso: revisa las reglas de Firestore"
            } else {
                "No se pudo guardar el cambio"
            }
            Result.failure(Exception(mensaje))
        } catch (e: Exception) {
            Result.failure(Exception("No se pudo guardar el cambio"))
        }
    }
}

// Documento catalogo/deshabilitados -> Deshabilitados (Firestore guarda los números como Long)
private fun DocumentSnapshot.aDeshabilitados(): Deshabilitados = Deshabilitados(
    pokemon = (get("pokemon") as? List<*>).orEmpty().mapNotNull { (it as? Number)?.toInt() }.toSet(),
    tipos = (get("tipos") as? List<*>).orEmpty().mapNotNull { it as? String }.toSet()
)