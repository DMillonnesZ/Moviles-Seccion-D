package com.saludplus.citas.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.saludplus.citas.ui.theme.AzulOscuro
import com.saludplus.citas.ui.theme.AzulPrimario
import com.saludplus.citas.ui.theme.GrisTexto
import com.saludplus.citas.ui.theme.RojoError

// Caja grande con el ícono (fondo casi blanco) y, pegada a ella, una caja de texto más baja
// con el título encima. Muestra un texto de ejemplo (placeholder) mientras el campo está vacío.
@Composable
fun CampoTextoIcono(
    valor: String,
    onCambio: (String) -> Unit,
    etiqueta: String,
    icono: ImageVector,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    oculto: Boolean = false,
    teclado: KeyboardType = KeyboardType.Text,
    error: String? = null
) {
    val forma = RoundedCornerShape(16.dp)
    val colorBorde = if (error != null) RojoError else Color(0xFFBFC8D6)
    val fondoIcono = Color(0xFFF7FAFF)

    Column(modifier = modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(66.dp)
        ) {
            // Título + caja de texto (empieza a la mitad del ícono y queda detrás de él)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(start = 33.dp),
                verticalArrangement = Arrangement.Bottom
            ) {
                Text(
                    text = etiqueta,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = GrisTexto,
                    modifier = Modifier.padding(start = 45.dp, bottom = 4.dp)
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .clip(forma)
                        .background(Color.White)
                        .border(1.5.dp, colorBorde, forma)
                        .padding(start = 45.dp, end = 14.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    BasicTextField(
                        value = valor,
                        onValueChange = onCambio,
                        singleLine = true,
                        textStyle = TextStyle(
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                            color = AzulOscuro
                        ),
                        cursorBrush = SolidColor(AzulPrimario),
                        visualTransformation = if (oculto) PasswordVisualTransformation()
                        else VisualTransformation.None,
                        keyboardOptions = KeyboardOptions(keyboardType = teclado),
                        modifier = Modifier.fillMaxWidth(),
                        decorationBox = { campoInterno ->
                            Box(contentAlignment = Alignment.CenterStart) {
                                if (valor.isEmpty()) {
                                    Text(
                                        text = placeholder,
                                        fontSize = 15.sp,
                                        color = Color(0xFF9CA3AF)
                                    )
                                }
                                campoInterno()
                            }
                        }
                    )
                }
            }

            // Caja grande del ícono, dibujada encima
            Box(
                modifier = Modifier
                    .size(66.dp)
                    .align(Alignment.CenterStart)
                    .clip(forma)
                    .background(fondoIcono)
                    .border(1.5.dp, colorBorde, forma),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icono,
                    contentDescription = null,
                    tint = AzulPrimario,
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        if (error != null) {
            Text(
                text = error,
                fontSize = 12.sp,
                color = RojoError,
                modifier = Modifier.padding(start = 78.dp, top = 3.dp)
            )
        }
    }
}