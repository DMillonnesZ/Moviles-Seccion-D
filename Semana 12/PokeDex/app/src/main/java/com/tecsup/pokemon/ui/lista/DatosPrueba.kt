package com.tecsup.pokemon.ui.lista

import com.tecsup.pokemon.domain.model.Item

object DatosPrueba {
    private fun imagen(id: Int) =
        "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/$id.png"

    val items = listOf(
        Item(4, "charmander", imagen(4)),
        Item(5, "charmeleon", imagen(5)),
        Item(6, "charizard", imagen(6), esFavorito = true),
        Item(37, "vulpix", imagen(37)),
        Item(58, "growlithe", imagen(58)),
        Item(77, "ponyta", imagen(77))
    )
}