package com.tecsup.pokemon

import com.tecsup.pokemon.data.remote.RetrofitClient
import com.tecsup.pokemon.data.repository.ItemRepository
import com.tecsup.pokemon.data.repository.ItemRepositoryImpl

// Crea las dependencias una sola vez y las comparte con los ViewModel
class AppContainer {
    val itemRepository: ItemRepository = ItemRepositoryImpl(RetrofitClient.api)
}