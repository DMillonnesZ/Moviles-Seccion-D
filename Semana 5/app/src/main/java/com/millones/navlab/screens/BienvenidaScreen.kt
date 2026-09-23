package com.millones.navlab.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.millones.navlab.components.GradientBackground
import com.millones.navlab.components.OptionCard
import com.millones.navlab.data.UserSession
import com.millones.navlab.navigation.Screen
import com.millones.navlab.ui.theme.Blanco
import com.millones.navlab.ui.theme.LavandaClaro
import com.millones.navlab.ui.theme.RojoOscuro

@Composable
fun BienvenidaScreen(navController: NavController) {
    val currentUser = UserSession.currentUser
    val nombre = currentUser?.nombreCompleto ?: "Usuario"

    Scaffold { padding ->
        GradientBackground(
            modifier = Modifier.padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Bienvenido, $nombre",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Blanco,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "¿Qué deseas gestionar hoy?",
                        style = MaterialTheme.typography.titleMedium,
                        color = LavandaClaro,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    OptionCard(
                        title = "Directorio de Alumnos",
                        description = "Ver y gestionar estudiantes",
                        icon = Icons.Filled.Group,
                        onClick = { navController.navigate(Screen.DirectorioAlumnos.route) }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OptionCard(
                        title = "Mi Perfil Académico",
                        description = "Datos personales y progreso",
                        icon = Icons.Filled.Person,
                        onClick = { navController.navigate(Screen.ConfiguracionPerfil.route) }
                    )
                }

                Row(
                    modifier = Modifier
                        .padding(bottom = 16.dp)
                        .clickable {
                            UserSession.currentUser = null
                            navController.navigate(Screen.Login.route) {
                                popUpTo(Screen.Login.route) { inclusive = true }
                            }
                        },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Logout,
                        contentDescription = "Cerrar Sesión Segura",
                        tint = RojoOscuro
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Cerrar Sesión Segura",
                        color = RojoOscuro,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}