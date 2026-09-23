package com.millones.navlab.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.millones.navlab.components.GradientDirection
import com.millones.navlab.components.GradientHeaderWithAvatar
import com.millones.navlab.components.InfoRow
import com.millones.navlab.data.MockData
import com.millones.navlab.ui.theme.Blanco
import com.millones.navlab.ui.theme.LavandaClaro
import com.millones.navlab.ui.theme.MoradoMedio
import com.millones.navlab.ui.theme.MoradoOscuro
import com.millones.navlab.ui.theme.PlomoClaro

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpedienteAcademicoScreen(navController: NavController, studentId: String) {
    val student = MockData.usuarios.find { it.id == studentId } ?: MockData.usuarios.first()

    Scaffold(
        containerColor = Blanco,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Expediente Académico",
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
                avatarUrl = student.avatarUrl,
                gradientColors = listOf(MoradoOscuro, LavandaClaro),
                direction = GradientDirection.VERTICAL,
                roundedBottom = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = student.nombreCompleto,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = student.facultad,
                    style = MaterialTheme.typography.titleMedium,
                    color = MoradoMedio,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = PlomoClaro),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        InfoRow(
                            icon = Icons.Filled.Badge,
                            label = "ID Estudiante",
                            value = student.id,
                            iconColor = MoradoMedio,
                            useIconBox = false
                        )
                        InfoRow(
                            icon = Icons.Filled.Email,
                            label = "Correo Electrónico",
                            value = student.email,
                            iconColor = MoradoMedio,
                            useIconBox = false
                        )
                        InfoRow(
                            icon = Icons.Filled.School,
                            label = "Facultad",
                            value = student.facultad,
                            iconColor = MoradoMedio,
                            useIconBox = false
                        )

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 12.dp),
                            color = Color(0xFFCCCCCC)
                        )

                        Text(
                            text = "Biografía",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = student.biografia,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Black
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}