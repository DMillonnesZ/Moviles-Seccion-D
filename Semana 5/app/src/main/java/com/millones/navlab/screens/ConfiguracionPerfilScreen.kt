package com.millones.navlab.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.millones.navlab.components.GradientDirection
import com.millones.navlab.components.GradientHeaderWithAvatar
import com.millones.navlab.components.InfoRow
import com.millones.navlab.data.MockData
import com.millones.navlab.data.UserSession
import com.millones.navlab.navigation.Screen
import com.millones.navlab.ui.theme.Blanco
import com.millones.navlab.ui.theme.MoradoMedio
import com.millones.navlab.ui.theme.NaranjaGradiente
import com.millones.navlab.ui.theme.RojoClaro
import com.millones.navlab.ui.theme.RojoOscuro

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfiguracionPerfilScreen(navController: NavController) {
    val user = UserSession.currentUser ?: MockData.usuarios.first()

    Scaffold(
        containerColor = Blanco,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Configuración de Perfil",
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.Black
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Blanco)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            GradientHeaderWithAvatar(
                avatarUrl = user.avatarUrl,
                gradientColors = listOf(MoradoMedio, NaranjaGradiente),
                direction = GradientDirection.HORIZONTAL,
                roundedBottom = false,
                userName = user.nombreCompleto
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Text(
                    text = "INFORMACIÓN PERSONAL",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MoradoMedio
                )

                Spacer(modifier = Modifier.height(8.dp))

                InfoRow(
                    icon = Icons.Filled.Person,
                    label = "Nombre Completo",
                    value = user.nombreCompleto,
                    useIconBox = true
                )
                InfoRow(
                    icon = Icons.Filled.Email,
                    label = "Correo",
                    value = user.email,
                    useIconBox = true
                )
                InfoRow(
                    icon = Icons.Filled.Phone,
                    label = "Teléfono",
                    value = user.telefono,
                    useIconBox = true
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "ACADÉMICO",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MoradoMedio
                )

                Spacer(modifier = Modifier.height(8.dp))

                InfoRow(
                    icon = Icons.Filled.MenuBook,
                    label = "Carrera",
                    value = user.carrera,
                    useIconBox = true
                )
                InfoRow(
                    icon = Icons.Filled.CalendarMonth,
                    label = "Ciclo Actual",
                    value = user.ciclo,
                    useIconBox = true
                )

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        UserSession.currentUser = null
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = RojoClaro),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Logout,
                            contentDescription = "Cerrar Sesión",
                            tint = RojoOscuro
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Cerrar Sesión",
                            color = RojoOscuro,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}