package com.millones.murocampus

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewmodel.compose.viewModel
import com.millones.murocampus.ui.PantallaLista
import com.millones.murocampus.ui.theme.Azul
import com.millones.murocampus.ui.theme.FondoPantalla
import com.millones.murocampus.ui.theme.MuroCampusTheme
import com.millones.murocampus.viewmodel.PostsViewModel
import com.millones.murocampus.viewmodel.PostsViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MuroCampusTheme {
                val vm: PostsViewModel = viewModel(factory = PostsViewModelFactory())
                Scaffold(
                    containerColor = FondoPantalla,
                    topBar = {
                        TopAppBar(
                            title = { Text("MuroCampus", fontWeight = FontWeight.Bold) },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = Azul,
                                titleContentColor = Color.White
                            )
                        )
                    }
                ) { padding ->
                    PantallaLista(vm, Modifier.padding(padding))
                }
            }
        }
    }
}