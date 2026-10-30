package com.tecsup.huellitasvet.ui.dueno

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tecsup.huellitasvet.data.local.entity.Mascota
import com.tecsup.huellitasvet.model.Especie
import com.tecsup.huellitasvet.ui.components.AppTopBar
import com.tecsup.huellitasvet.ui.components.MascotaAvatar
import com.tecsup.huellitasvet.ui.theme.BordeSuave
import com.tecsup.huellitasvet.viewmodel.InicioViewModel
import java.time.Year

@Composable
fun MascotasScreen(
    vm: InicioViewModel,
    onVerMascota: (Int) -> Unit,
    onAgregar: () -> Unit
) {
    val mascotas by vm.mascotas.collectAsState()
    val cs = MaterialTheme.colorScheme

    Scaffold(
        containerColor = cs.background,
        contentWindowInsets = WindowInsets(0),
        topBar = { AppTopBar("Mis mascotas") },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAgregar,
                containerColor = cs.primary,
                contentColor = cs.onPrimary,
                shape = RoundedCornerShape(18.dp)
            ) { Icon(Icons.Default.Add, "Agregar mascota") }
        }
    ) { padding ->
        if (mascotas.isEmpty()) {
            Box(Modifier.padding(padding).fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Aún no registras mascotas", color = cs.onSurfaceVariant)
            }
        } else {
            LazyColumn(
                Modifier.padding(padding).fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(mascotas, key = { it.id }) { m ->
                    MascotaItem(m) { onVerMascota(m.id) }
                }
            }
        }
    }
}

@Composable
private fun MascotaItem(m: Mascota, onClick: () -> Unit) {
    val cs = MaterialTheme.colorScheme
    val especie = Especie.valueOf(m.especie)
    val edad = Year.now().value - m.anioNacimiento

    Card(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cs.surface),
        border = BorderStroke(1.dp, BordeSuave),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.size(52.dp).background(cs.primaryContainer, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                MascotaAvatar(especie, tamano = 46.dp, conFondo = false)
            }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(m.nombre, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text("${especie.etiqueta} · ${m.raza}", style = MaterialTheme.typography.bodySmall, color = cs.onSurfaceVariant)
                Text(
                    "${if (edad == 1) "1 año" else "$edad años"} · ${m.peso} kg",
                    style = MaterialTheme.typography.bodySmall,
                    color = cs.onSurfaceVariant
                )
            }
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = cs.onSurfaceVariant)
        }
    }
}