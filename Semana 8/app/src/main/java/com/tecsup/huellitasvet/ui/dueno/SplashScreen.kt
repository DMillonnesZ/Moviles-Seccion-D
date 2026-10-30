package com.tecsup.huellitasvet.ui.dueno

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tecsup.huellitasvet.model.Especie
import com.tecsup.huellitasvet.ui.components.MascotaAvatar
import com.tecsup.huellitasvet.ui.components.VetButton

@Composable
fun SplashScreen(
    onComenzar: () -> Unit,
    onYaTengoCuenta: () -> Unit
) {
    val cs = MaterialTheme.colorScheme

    Column(
        Modifier
            .fillMaxSize()
            .background(cs.background)
            .systemBarsPadding()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.weight(1f))

        // Logo 🐾
        Box(
            Modifier.size(96.dp).background(cs.primary, RoundedCornerShape(24.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Pets, contentDescription = null, tint = cs.onPrimary, modifier = Modifier.size(56.dp))
        }

        Spacer(Modifier.height(24.dp))
        Text("HuellitasVet", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(6.dp))
        Text("Cuidamos a tu mejor amigo", style = MaterialTheme.typography.bodyMedium, color = cs.onSurfaceVariant)

        Spacer(Modifier.weight(1f))

        // Avatares
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            listOf(Especie.PERRO, Especie.GATO, Especie.OTRO).forEach { e ->
                Box(
                    Modifier.size(84.dp).background(cs.primaryContainer, RoundedCornerShape(20.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    MascotaAvatar(e, tamano = 60.dp, conFondo = false)
                }
            }
        }

        Spacer(Modifier.weight(1.3f))

        VetButton(texto = "Comenzar", onClick = onComenzar)
        TextButton(onClick = onYaTengoCuenta) {
            Text("Ya tengo una cuenta", color = cs.primary, fontWeight = FontWeight.SemiBold)
        }
    }
}