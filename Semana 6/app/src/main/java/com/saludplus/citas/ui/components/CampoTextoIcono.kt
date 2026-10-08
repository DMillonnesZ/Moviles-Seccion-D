package com.saludplus.citas.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.saludplus.citas.ui.theme.AzulOscuro
import com.saludplus.citas.ui.theme.AzulPrimario
import com.saludplus.citas.ui.theme.GrisTexto
import com.saludplus.citas.ui.theme.RojoError
import kotlin.math.roundToInt

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
    var contrasenaVisible by remember { mutableStateOf(false) }

    val shakeOffset = remember { Animatable(0f) }
    LaunchedEffect(error) {
        if (error != null) {
            repeat(2) {
                shakeOffset.animateTo(10f, tween(40))
                shakeOffset.animateTo(-10f, tween(40))
            }
            shakeOffset.animateTo(0f, spring(stiffness = Spring.StiffnessHigh))
        }
    }

    val forma = RoundedCornerShape(16.dp)
    val colorBorde = if (error != null) RojoError else Color(0xFFBFC8D6)
    val fondoIcono = Color(0xFFF7FAFF)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .offset { IntOffset(shakeOffset.value.dp.roundToPx(), 0) }
    ) {
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
                        .padding(start = 45.dp, end = if (oculto) 40.dp else 14.dp),
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
                        visualTransformation = if (oculto && !contrasenaVisible) PasswordVisualTransformation()
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

                    if (oculto) {
                        IconButton(
                            onClick = { contrasenaVisible = !contrasenaVisible },
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .size(36.dp)
                        ) {
                            Icon(
                                imageVector = if (contrasenaVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = if (contrasenaVisible) "Ocultar contraseña" else "Mostrar contraseña",
                                tint = GrisTexto,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
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

        AnimatedVisibility(
            visible = error != null,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            error?.let {
                Text(
                    text = it,
                    fontSize = 12.sp,
                    color = RojoError,
                    modifier = Modifier.padding(start = 78.dp, top = 3.dp)
                )
            }
        }
    }
}
