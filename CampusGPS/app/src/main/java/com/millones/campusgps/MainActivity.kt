package com.millones.campusgps

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Surface
import com.millones.campusgps.ui.PantallaMapa
import com.millones.campusgps.ui.theme.CampusGPSTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CampusGPSTheme {
                Surface {
                    PantallaMapa()
                }
            }
        }
    }
}