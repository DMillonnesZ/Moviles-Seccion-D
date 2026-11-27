package com.millones.avisoscampus

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewmodel.compose.viewModel
import com.millones.avisoscampus.ui.PantallaAvisos
import com.millones.avisoscampus.ui.theme.AvisosCampusTheme
import com.millones.avisoscampus.ui.theme.Azul
import com.millones.avisoscampus.ui.theme.FondoPantalla
import com.millones.avisoscampus.viewmodel.AvisosViewModel
import com.millones.avisoscampus.viewmodel.AvisosViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // La barra azul llega hasta la barra de estado; íconos (hora, batería) en blanco
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT)
        )
        setContent {
            AvisosCampusTheme {
                val vm: AvisosViewModel = viewModel(factory = AvisosViewModelFactory())
                Scaffold(
                    containerColor = FondoPantalla,
                    topBar = {
                        TopAppBar(
                            title = {
                                Text(
                                    "AvisosCampus",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = Azul,
                                titleContentColor = Color.White
                            )
                        )
                    }
                ) { padding ->
                    PantallaAvisos(vm, Modifier.padding(padding))
                }
            }
        }
    }
}