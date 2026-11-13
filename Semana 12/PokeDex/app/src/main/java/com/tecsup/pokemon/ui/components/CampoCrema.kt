package com.tecsup.pokemon.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val DoradoBordeCampo = Color(0xFFD9A43A)

// Campo de texto blanco con borde dorado (ventanas de Perfil y Admin).
// Si es clave, tiene botón para ver u ocultar; si no es de una línea, crece hasta 4 líneas.
@Composable
fun CampoCrema(
    valor: String,
    onCambio: (String) -> Unit,
    etiqueta: String,
    icono: ImageVector,
    modifier: Modifier = Modifier,
    esClave: Boolean = false,
    unaLinea: Boolean = true
) {
    var visible by remember { mutableStateOf(false) }
    OutlinedTextField(
        value = valor,
        onValueChange = onCambio,
        singleLine = unaLinea,
        maxLines = if (unaLinea) 1 else 4,
        label = { Text(etiqueta, fontFamily = FuenteMadera) },
        leadingIcon = { Icon(icono, contentDescription = null, tint = CafeMadera.copy(alpha = 0.8f)) },
        trailingIcon = if (esClave) {
            {
                IconButton(onClick = { visible = !visible }) {
                    Icon(
                        imageVector = if (visible) Icons.Rounded.VisibilityOff else Icons.Rounded.Visibility,
                        contentDescription = if (visible) "Ocultar" else "Mostrar",
                        tint = CafeMadera.copy(alpha = 0.7f)
                    )
                }
            }
        } else null,
        visualTransformation = if (esClave && !visible) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = if (esClave) KeyboardType.Password else KeyboardType.Text),
        textStyle = TextStyle(fontFamily = FuenteMadera, color = CafeMadera, fontSize = 16.sp),
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = DoradoBordeCampo,
            unfocusedBorderColor = CafeMadera.copy(alpha = 0.3f),
            focusedLabelColor = CafeMadera,
            unfocusedLabelColor = CafeMadera.copy(alpha = 0.6f),
            cursorColor = CafeMadera,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White
        ),
        modifier = modifier.fillMaxWidth()
    )
}