package com.tecsup.pokemon

import android.app.Application

class PokeDexApp : Application() {
    val container: AppContainer by lazy { AppContainer(this) }
}