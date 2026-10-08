package com.saludplus.citas.ui.screens.agendamiento

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.saludplus.citas.data.repository.Repositorio
import com.saludplus.citas.ui.components.BarraSuperior
import com.saludplus.citas.ui.components.EstadoVacio
import com.saludplus.citas.ui.components.TarjetaSuave
import com.saludplus.citas.ui.components.efectoPresion
import com.saludplus.citas.ui.components.estiloEspecialidad
import com.saludplus.citas.ui.theme.AzulOscuro
import com.saludplus.citas.ui.theme.AzulPrimario
import com.saludplus.citas.ui.theme.GrisTexto

@Composable
private fun TextoResaltado(
    texto: String,
    busqueda: String,
    modifier: Modifier = Modifier
) {
    if (busqueda.isBlank()) {
        Text(
            text = texto,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = AzulOscuro,
            modifier = modifier
        )
        return
    }

    val annotated = remember(texto, busqueda) {
        buildAnnotatedString {
            val lowerTexto = texto.lowercase()
            val lowerQuery = busqueda.trim().lowercase()
            var startIndex = 0

            while (true) {
                val index = lowerTexto.indexOf(lowerQuery, startIndex)
                if (index == -1) {
                    append(texto.substring(startIndex))
                    break
                }
                append(texto.substring(startIndex, index))
                withStyle(style = SpanStyle(fontWeight = FontWeight.ExtraBold, color = AzulPrimario)) {
                    append(texto.substring(index, index + lowerQuery.length))
                }
                startIndex = index + lowerQuery.length
            }
        }
    }

    Text(
        text = annotated,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        color = AzulOscuro,
        modifier = modifier
    )
}

@Composable
fun EspecialidadesScreen(
    onAtras: () -> Unit,
    onEspecialidad: (Int) -> Unit
) {
    var busqueda by remember { mutableStateOf("") }
    var enFoco by remember { mutableStateOf(false) }

    val colorBordeFoco by animateColorAsState(
        targetValue = if (enFoco) AzulPrimario else Color.Transparent,
        animationSpec = tween(200),
        label = "BordeBuscadorFoco"
    )

    // Se recalcula en cada letra que escribe el usuario
    val resultados = Repositorio.buscarEspecialidades(busqueda)

    Scaffold(
        containerColor = Color.White,
        topBar = { BarraSuperior(titulo = "Especialidades", onAtras = onAtras) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Buscador con animación de foco
            OutlinedTextField(
                value = busqueda,
                onValueChange = { busqueda = it },
                placeholder = { Text("Buscar especialidades...", color = Color(0xFF9CA3AF)) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = if (enFoco) AzulPrimario else GrisTexto
                    )
                },
                trailingIcon = {
                    if (busqueda.isNotEmpty()) {
                        IconButton(onClick = { busqueda = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Limpiar", tint = GrisTexto)
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFF1F5FB),
                    unfocusedContainerColor = Color(0xFFF1F5FB),
                    focusedBorderColor = colorBordeFoco,
                    unfocusedBorderColor = Color.Transparent,
                    cursorColor = AzulPrimario
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp)
                    .onFocusChanged { enFoco = it.isFocused }
            )

            if (resultados.isEmpty()) {
                // Estado vacío amigable
                EstadoVacio(
                    titulo = "Sin coincidencias",
                    mensaje = "No encontramos especialidades que coincidan con \"$busqueda\". Intenta con otro término.",
                    icono = Icons.Default.Search,
                    modifier = Modifier.padding(top = 32.dp)
                )
            } else {
                // Lista de especialidades con animación al filtrar
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(resultados, key = { it.id }) { especialidad ->
                        val estilo = estiloEspecialidad(especialidad.id)
                        TarjetaSuave(
                            modifier = Modifier
                                .fillMaxWidth()
                                .animateItem()
                                .efectoPresion { onEspecialidad(especialidad.id) },
                            onClick = { onEspecialidad(especialidad.id) }
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .background(estilo.fondo),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = estilo.icono,
                                        contentDescription = null,
                                        tint = estilo.color,
                                        modifier = Modifier.size(26.dp)
                                    )
                                }
                                Spacer(Modifier.width(14.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    TextoResaltado(
                                        texto = especialidad.nombre,
                                        busqueda = busqueda
                                    )
                                    Spacer(Modifier.height(2.dp))
                                    Text(
                                        text = especialidad.descripcion,
                                        fontSize = 13.sp,
                                        color = GrisTexto
                                    )
                                }
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                    contentDescription = null,
                                    tint = GrisTexto
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
