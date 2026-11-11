package com.tecsup.pokemon.data.remote.dto

// Respuesta de GET evolution-chain/?limit=... : solo trae la URL de cada cadena
data class ListaUrlsDto(
    val count: Int,
    val results: List<UrlDto>
)