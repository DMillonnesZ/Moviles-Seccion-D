package com.millones.campusgps.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.util.Base64
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import kotlin.math.max

// Fotos de lugares y eventos.
// - comprimirBase64: la foto elegida se reduce (~480 px) y se pasa a texto para guardarla
//   dentro del documento de Firestore (no necesita Firebase Storage ni el plan Blaze).
// - guardarBase64: la foto que llega de Firestore se guarda como archivo para mostrarla rápido.
class AlmacenFotos(contexto: Context) {

    private val app = contexto.applicationContext
    private val carpeta = File(app.filesDir, "lugares").apply { mkdirs() }

    suspend fun comprimirBase64(
        uri: String,
        ladoMaximo: Int = 480,
        calidad: Int = 70
    ): String = withContext(Dispatchers.IO) {
        val imagen = cargarDerecha(Uri.parse(uri), ladoMaximo)
        val salida = ByteArrayOutputStream()
        imagen.compress(Bitmap.CompressFormat.JPEG, calidad, salida)
        imagen.recycle()
        Base64.encodeToString(salida.toByteArray(), Base64.NO_WRAP)
    }

    // prefijo identifica al dueño (por ejemplo "remoto_<idLugar>"); si la foto cambia,
    // se borra la anterior. Si ya existe la misma, no se vuelve a escribir.
    suspend fun guardarBase64(base64: String, prefijo: String): String = withContext(Dispatchers.IO) {
        val nombre = "${prefijo}_${base64.hashCode().toUInt()}.jpg"
        val destino = File(carpeta, nombre)
        if (!destino.exists()) {
            carpeta.listFiles { archivo -> archivo.name.startsWith("${prefijo}_") }
                ?.forEach { it.delete() }
            destino.writeBytes(Base64.decode(base64, Base64.NO_WRAP))
        }
        destino.absolutePath
    }

    // Carga la imagen reducida (potencia de 2) y la endereza si la cámara la guardó de lado
    private fun cargarDerecha(origen: Uri, ladoMaximo: Int): Bitmap {
        val medidas = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        abrir(origen).use { BitmapFactory.decodeStream(it, null, medidas) }
        if (medidas.outWidth <= 0 || medidas.outHeight <= 0) error("El archivo no es una imagen")

        var muestra = 1
        while (max(medidas.outWidth, medidas.outHeight) / (muestra * 2) >= ladoMaximo) muestra *= 2
        val cargada = abrir(origen).use {
            BitmapFactory.decodeStream(it, null, BitmapFactory.Options().apply { inSampleSize = muestra })
        } ?: error("No se pudo leer la imagen")

        // Ajuste fino al tamaño pedido
        val escala = ladoMaximo.toFloat() / max(cargada.width, cargada.height)
        val reducida = if (escala < 1f) {
            Bitmap.createScaledBitmap(
                cargada,
                (cargada.width * escala).toInt(),
                (cargada.height * escala).toInt(),
                true
            ).also { if (it !== cargada) cargada.recycle() }
        } else {
            cargada
        }

        val grados = abrir(origen).use {
            when (ExifInterface(it).getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)) {
                ExifInterface.ORIENTATION_ROTATE_90 -> 90f
                ExifInterface.ORIENTATION_ROTATE_180 -> 180f
                ExifInterface.ORIENTATION_ROTATE_270 -> 270f
                else -> 0f
            }
        }
        if (grados == 0f) return reducida
        return Bitmap.createBitmap(
            reducida, 0, 0, reducida.width, reducida.height,
            Matrix().apply { postRotate(grados) }, true
        ).also { if (it !== reducida) reducida.recycle() }
    }

    private fun abrir(uri: Uri) =
        app.contentResolver.openInputStream(uri) ?: error("No se pudo abrir la imagen")
}
