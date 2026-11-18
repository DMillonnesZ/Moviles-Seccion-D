package com.millones.campusgps

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Surface
import androidx.lifecycle.viewmodel.compose.viewModel
import com.millones.campusgps.ui.NavegacionApp
import com.millones.campusgps.ui.theme.CampusGPSTheme
import com.millones.campusgps.viewmodel.AuthViewModel
import com.millones.campusgps.viewmodel.AuthViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CampusGPSTheme {
                Surface {
                    val authVm: AuthViewModel = viewModel(factory = AuthViewModelFactory())
                    NavegacionApp(authVm)
                }
            }
        }
    }
}
