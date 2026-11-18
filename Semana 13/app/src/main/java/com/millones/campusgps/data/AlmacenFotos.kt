package com.millones.campusgps.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import kotlin.math.max

// Copia la foto elegida a la memoria interna de la app (files/lugares), reducida a 1280 px
// (se ve bien en pantalla completa) y derecha. Así no depende de la galería y ocupa poco. Devuelve la ruta del archivo.
class AlmacenFotos(contexto: Context) {

    private val app = contexto.applicationContext
    private val carpeta = File(app.filesDir, "lugares").apply { mkdirs() }

    suspend fun guardar(uri: String, nombreBase: String): String = withContext(Dispatchers.IO) {
        val origen = Uri.parse(uri)

        // 1) Medir sin cargar la imagen entera
        val medidas = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        abrir(origen).use { BitmapFactory.decodeStream(it, null, medidas) }
        if (medidas.outWidth <= 0 || medidas.outHeight <= 0) error("El archivo no es una imagen")

        // 2) Cargarla reducida
        var muestra = 1
        while (max(medidas.outWidth, medidas.outHeight) / (muestra * 2) >= LADO_MAXIMO) muestra *= 2
        val reducida = abrir(origen).use {
            BitmapFactory.decodeStream(it, null, BitmapFactory.Options().apply { inSampleSize = muestra })
        } ?: error("No se pudo leer la imagen")

        // 3) Enderezarla si la cámara la guardó de lado
        val grados = abrir(origen).use {
            when (ExifInterface(it).getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)) {
                ExifInterface.ORIENTATION_ROTATE_90 -> 90f
                ExifInterface.ORIENTATION_ROTATE_180 -> 180f
                ExifInterface.ORIENTATION_ROTATE_270 -> 270f
                else -> 0f
            }
        }
        val final = if (grados == 0f) {
            reducida
        } else {
            Bitmap.createBitmap(
                reducida, 0, 0, reducida.width, reducida.height,
                Matrix().apply { postRotate(grados) }, true
            )
        }

        // 4) Guardar con nombre nuevo (así la pantalla nota el cambio)
        val destino = File(carpeta, "${nombreBase}_${System.currentTimeMillis()}.jpg")
        FileOutputStream(destino).use { final.compress(Bitmap.CompressFormat.JPEG, 85, it) }
        if (final !== reducida) reducida.recycle()
        final.recycle()
        destino.absolutePath
    }

    suspend fun borrar(ruta: String?) = withContext(Dispatchers.IO) {
        if (ruta != null) File(ruta).delete()
    }

    private fun abrir(uri: Uri) =
        app.contentResolver.openInputStream(uri) ?: error("No se pudo abrir la imagen")

    private companion object {
        const val LADO_MAXIMO = 1280
    }
}
