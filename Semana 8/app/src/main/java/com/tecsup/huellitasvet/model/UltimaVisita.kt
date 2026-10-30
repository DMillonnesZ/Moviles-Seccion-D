package com.tecsup.huellitasvet.model

/** Resultado de: SELECT mascotaId, MAX(fecha) AS fecha ... GROUP BY mascotaId */
data class UltimaVisita(
    val mascotaId: Int,
    val fecha: String
)