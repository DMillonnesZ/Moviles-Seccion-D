package com.millones.campusgps.data

import android.content.Context
import androidx.core.content.edit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// Ajustes en SharedPreferences. Una sola instancia para toda la app (obtener), así el mapa
// y el perfil ven el mismo radio al instante.
class AjustesRepositoryLocal private constructor(contexto: Context) : AjustesRepository {

    private val prefs = contexto.applicationContext
        .getSharedPreferences("ajustes_campus", Context.MODE_PRIVATE)

    private val _radio = MutableStateFlow(prefs.getInt(CLAVE_RADIO, 300))
    override val radioCercanos: StateFlow<Int> = _radio.asStateFlow()

    private val _ultimaVisita = MutableStateFlow(prefs.getLong(CLAVE_VISITA, 0L))
    override val ultimaVisitaOpiniones: StateFlow<Long> = _ultimaVisita.asStateFlow()

    override fun cambiarRadio(metros: Int) {
        prefs.edit { putInt(CLAVE_RADIO, metros) }
        _radio.value = metros
    }

    override fun marcarOpinionesVistas(hasta: Long) {
        if (hasta <= _ultimaVisita.value) return
        prefs.edit { putLong(CLAVE_VISITA, hasta) }
        _ultimaVisita.value = hasta
    }

    companion object {
        private const val CLAVE_RADIO = "radio_cercanos"
        private const val CLAVE_VISITA = "ultima_visita_opiniones"

        @Volatile
        private var instancia: AjustesRepositoryLocal? = null

        fun obtener(contexto: Context): AjustesRepositoryLocal =
            instancia ?: synchronized(this) {
                instancia ?: AjustesRepositoryLocal(contexto).also { instancia = it }
            }
    }
}
