package com.tecsup.millones

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.tecsup.millones.navigation.AppNavegacion
import com.tecsup.millones.ui.theme.TecsupStoreTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TecsupStoreTheme {
                AppNavegacion()
            }
        }
    }
}