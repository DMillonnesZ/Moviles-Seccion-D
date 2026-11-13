package com.tecsup.pokemon.util

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.util.Base64
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import java.io.ByteArrayOutputStream
import kotlin.math.roundToInt

// Prepara la foto de perfil: la lee, la recorta en cuadrado, la reduce a 256 px
// y la convierte en texto Base64 para guardarla en Firestore (unos 20 KB)
object ImagenPerfil {

    private const val LADO = 256
    private const val CALIDAD_JPEG = 82

    // Lee la imagen ya reducida (no carga la foto completa de 12 MP en memoria)
    fun leer(resolver: ContentResolver, uri: Uri): Bitmap =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            // ImageDecoder además respeta la rotación de las fotos de la cámara
            val fuente = ImageDecoder.createSource(resolver, uri)
            ImageDecoder.decodeBitmap(fuente) { decoder, info, _ ->
                decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
                val menor = minOf(info.size.width, info.size.height)
                if (menor > LADO * 2) {
                    val factor = LADO * 2f / menor
                    decoder.setTargetSize(
                        (info.size.width * factor).roundToInt(),
                        (info.size.height * factor).roundToInt()
                    )
                }
            }
        } else {
            // Primero solo las medidas, luego la imagen reducida
            val medidas = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            resolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, medidas) }
            var muestra = 1
            while (minOf(medidas.outWidth, medidas.outHeight) / (muestra * 2) >= LADO * 2) muestra *= 2
            val opciones = BitmapFactory.Options().apply { inSampleSize = muestra }
            resolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, opciones) }
                ?: throw IllegalStateException("No se pudo leer la imagen")
        }

    // Recorta el centro en cuadrado, lo reduce a 256x256 y lo pasa a Base64
    fun aBase64(original: Bitmap): String {
        val lado = minOf(original.width, original.height)
        val cuadrado = Bitmap.createBitmap(
            original,
            (original.width - lado) / 2,
            (original.height - lado) / 2,
            lado,
            lado
        )
        val final = Bitmap.createScaledBitmap(cuadrado, LADO, LADO, true)
        val salida = ByteArrayOutputStream()
        final.compress(Bitmap.CompressFormat.JPEG, CALIDAD_JPEG, salida)
        return Base64.encodeToString(salida.toByteArray(), Base64.NO_WRAP)
    }

    // Convierte el texto Base64 en imagen para mostrarla (null si no hay o está dañada)
    fun aImagen(base64: String?): ImageBitmap? {
        if (base64.isNullOrBlank()) return null
        return runCatching {
            val bytes = Base64.decode(base64, Base64.NO_WRAP)
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size)?.asImageBitmap()
        }.getOrNull()
    }
}