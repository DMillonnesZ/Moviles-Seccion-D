package com.millones.campusgps.data

import android.annotation.SuppressLint
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.os.Looper
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.millones.campusgps.model.Posicion
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.Locale
import kotlin.coroutines.resume

class UbicacionRepositoryGoogle(private val context: Context) : UbicacionRepository {

    private val fused = LocationServices.getFusedLocationProviderClient(context)

    override fun playServicesDisponible(): Boolean =
        GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context) ==
                ConnectionResult.SUCCESS

    @SuppressLint("MissingPermission")
    override suspend fun obtenerPosicion(): Result<Posicion> = runCatching {
        val location = fused.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY,
            CancellationTokenSource().token
        ).await()
        requireNotNull(location) { "GPS_APAGADO" }
        Posicion(location.latitude, location.longitude)
    }

    // Actualizaciones cada ~4 s (o al moverse 3 m). Se detienen solas cuando nadie escucha.
    @SuppressLint("MissingPermission")
    override fun seguirPosicion(): Flow<Posicion> = callbackFlow {
        val solicitud = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 4000L)
            .setMinUpdateDistanceMeters(3f)
            .build()
        val oyente = object : LocationCallback() {
            override fun onLocationResult(resultado: LocationResult) {
                resultado.lastLocation?.let { trySend(Posicion(it.latitude, it.longitude)) }
            }
        }
        fused.requestLocationUpdates(solicitud, oyente, Looper.getMainLooper())
            .addOnFailureListener { close(it) }
        awaitClose { fused.removeLocationUpdates(oyente) }
    }

    // Geocoder del sistema (gratis, necesita internet). Si falla devuelve null y la UI
    // muestra "Ubicación activa" en lugar de la calle.
    override suspend fun obtenerDireccion(posicion: Posicion): String? = withContext(Dispatchers.IO) {
        if (!Geocoder.isPresent()) return@withContext null
        runCatching {
            val geocoder = Geocoder(context, Locale.forLanguageTag("es-PE"))
            val direcciones: List<Address> = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                suspendCancellableCoroutine { continuacion ->
                    geocoder.getFromLocation(
                        posicion.latitud,
                        posicion.longitud,
                        1,
                        object : Geocoder.GeocodeListener {
                            override fun onGeocode(lista: MutableList<Address>) {
                                if (continuacion.isActive) continuacion.resume(lista)
                            }

                            override fun onError(mensaje: String?) {
                                if (continuacion.isActive) continuacion.resume(emptyList())
                            }
                        }
                    )
                }
            } else {
                @Suppress("DEPRECATION")
                geocoder.getFromLocation(posicion.latitud, posicion.longitud, 1).orEmpty()
            }
            direcciones.firstOrNull()?.let(::textoCalle)
        }.getOrNull()
    }

    // "Calle Número"; si no hay calle, la primera parte de la dirección completa
    private fun textoCalle(direccion: Address): String? {
        val calle = listOfNotNull(direccion.thoroughfare, direccion.subThoroughfare)
            .joinToString(" ")
            .trim()
        return calle.ifBlank { direccion.getAddressLine(0)?.substringBefore(",")?.trim() }
            ?.takeIf { it.isNotBlank() }
    }
}
