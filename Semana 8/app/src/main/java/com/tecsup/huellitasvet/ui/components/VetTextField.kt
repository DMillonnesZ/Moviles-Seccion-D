package com.tecsup.huellitasvet.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.tecsup.huellitasvet.ui.theme.BordeSuave

@Composable
fun VetTextField(
    valor: String,
    onCambio: (String) -> Unit,
    etiqueta: String,
    modifier: Modifier = Modifier,
    error: String? = null,
    esClave: Boolean = false,
    teclado: KeyboardType = KeyboardType.Text,
    icono: ImageVector? = null
) {
    val cs = MaterialTheme.colorScheme
    OutlinedTextField(
        value = valor,
        onValueChange = onCambio,
        label = { Text(etiqueta) },
        leadingIcon = icono?.let { { Icon(it, contentDescription = null, tint = cs.primary) } },
        isError = error != null,
        supportingText = if (error != null) { { Text(error) } } else null,
        singleLine = true,
        shape = RoundedCornerShape(14.dp),
        visualTransformation = if (esClave) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = if (esClave) KeyboardType.Password else teclado),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = BordeSuave,
            focusedBorderColor = cs.primary,
            unfocusedContainerColor = cs.surface,
            focusedContainerColor = cs.surface,
            unfocusedLabelColor = cs.onSurfaceVariant
        ),
        modifier = modifier.fillMaxWidth()
    )
}