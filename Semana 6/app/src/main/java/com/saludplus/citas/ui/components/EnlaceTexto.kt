package com.saludplus.citas.ui.components

import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.saludplus.citas.ui.theme.AzulPrimario

@Composable
fun EnlaceTexto(texto: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    TextButton(onClick = onClick, modifier = modifier) {
        Text(texto, color = AzulPrimario, fontWeight = FontWeight.SemiBold)
    }
}