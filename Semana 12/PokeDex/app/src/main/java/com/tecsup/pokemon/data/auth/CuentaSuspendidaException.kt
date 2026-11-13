package com.tecsup.pokemon.data.auth

// Se lanza cuando la cuenta está suspendida (usuarios/{uid}.activo = false).
// Lleva el motivo que escribió el admin para mostrarlo en el aviso.
class CuentaSuspendidaException(
    val motivo: String?
) : Exception("Tu cuenta está suspendida")