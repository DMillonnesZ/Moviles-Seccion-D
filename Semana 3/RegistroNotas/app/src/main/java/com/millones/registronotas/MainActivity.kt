package com.millones.registronotas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale
import kotlin.math.roundToInt

val PrimaryPurple = Color(0xFF6C3EB5)
val BackgroundStart = Color(0xFFF4EEFB)
val BackgroundEnd = Color(0xFFEDE3F8)
val BadgeBackground = Color(0xFFEBE1F7)
val BadgeText = Color(0xFF6C3EB5)
val TextGray = Color(0xFF8A8A8E)
val ButtonDisabled = Color(0xFFD9D0EC)
val ButtonDisabledText = Color(0xFF9C93B0)
val ChipExcelenteBg = Color(0xFFB7E4C7)
val ChipExcelenteText = Color(0xFF1B5E20)
val ChipAprobadoBg = Color(0xFFDCF3DE)
val ChipAprobadoText = Color(0xFF2E7D32)
val ChipRecuperacionBg = Color(0xFFFFF1C2)
val ChipRecuperacionText = Color(0xFF8A6D00)
val ChipDesaprobadoBg = Color(0xFFFBDADA)
val ChipDesaprobadoText = Color(0xFFB71C1C)
val ConfirmGreen = Color(0xFF2E7D32)

data class Curso(val nombre: String, val peso: Float)
data class Resultado(
    val ponderado: Double,
    val promedioFinal: Double,
    val observacion: String,
    val chipBg: Color,
    val chipText: Color
)

val cursos = listOf(
    Curso("Fundamentos de Programación", 0.20f),
    Curso("Programación Orientada a Objetos", 0.25f),
    Curso("Programación en Móviles", 0.30f),
    Curso("Base de Datos", 0.25f)
)

fun calcularResultado(f: Float, poo: Float, m: Float, bd: Float, redondear: Boolean): Resultado {
    val ponderado = f * 0.20 + poo * 0.25 + m * 0.30 + bd * 0.25
    val ponderado2dec = String.format(Locale.US, "%.2f", ponderado).toDouble()
    val promedioFinal = if (redondear) ponderado.roundToInt().toDouble() else ponderado2dec

    val (obs, bg, txt) = when {
        promedioFinal >= 17.0 -> Triple("EXCELENTE", ChipExcelenteBg, ChipExcelenteText)
        promedioFinal >= 13.0 -> Triple("APROBADO", ChipAprobadoBg, ChipAprobadoText)
        promedioFinal >= 10.0 -> Triple("EN RECUPERACIÓN", ChipRecuperacionBg, ChipRecuperacionText)
        else -> Triple("DESAPROBADO", ChipDesaprobadoBg, ChipDesaprobadoText)
    }
    return Resultado(ponderado2dec, promedioFinal, obs, bg, txt)
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { RegistroNotasScreen() }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroNotasScreen() {
    var notaFundamentos by rememberSaveable { mutableFloatStateOf(0f) }
    var notaPoo by rememberSaveable { mutableFloatStateOf(0f) }
    var notaMoviles by rememberSaveable { mutableFloatStateOf(0f) }
    var notaBd by rememberSaveable { mutableFloatStateOf(0f) }
    var redondear by rememberSaveable { mutableStateOf(false) }
    var confirmado by rememberSaveable { mutableStateOf(false) }
    var mostrarResultado by rememberSaveable { mutableStateOf(false) }

    val resultado = if (mostrarResultado) {
        calcularResultado(notaFundamentos, notaPoo, notaMoviles, notaBd, redondear)
    } else null

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Registro de Notas",
                        fontSize = 21.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PrimaryPurple,
                    titleContentColor = Color.White
                )
            )
        },
        bottomBar = {
            Surface(color = BackgroundEnd) {
                Text(
                    "Desarrollado por: Daniel Alejandro Millones Vasquez",
                    color = TextGray,
                    fontSize = 11.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 5.dp, bottom = 10.dp)
                )
            }
        }
    ) { padding ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Brush.verticalGradient(listOf(BackgroundStart, BackgroundEnd)))
        ) {
            Column(
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 14.dp)
            ) {
                Text(
                    "Notas del ciclo",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF29252E)
                )
                Text("Desliza para asignar cada nota (0 a 20)", fontSize = 13.sp, color = TextGray)
                Spacer(Modifier.height(8.dp))

                CursoRow(cursos[0], notaFundamentos) { notaFundamentos = it }
                Spacer(Modifier.height(3.dp))
                CursoRow(cursos[1], notaPoo) { notaPoo = it }
                Spacer(Modifier.height(3.dp))
                CursoRow(cursos[2], notaMoviles) { notaMoviles = it }
                Spacer(Modifier.height(3.dp))
                CursoRow(cursos[3], notaBd) { notaBd = it }
                Spacer(Modifier.height(2.dp))

                Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                    Text("Redondear promedio final", fontSize = 14.sp, color = Color(0xFF403B45))
                    Switch(checked = redondear, onCheckedChange = { redondear = it })
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Checkbox(checked = confirmado, onCheckedChange = { confirmado = it })
                    Text(
                        "Confirmo que las notas son correctas",
                        fontSize = 14.sp,
                        color = Color(0xFF403B45)
                    )
                }
                Spacer(Modifier.height(4.dp))

                Button(
                    onClick = { mostrarResultado = true },
                    enabled = confirmado,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryPurple,
                        disabledContainerColor = ButtonDisabled,
                        contentColor = Color.White,
                        disabledContentColor = ButtonDisabledText
                    )
                ) { Text("CALCULAR PROMEDIO", fontSize = 15.sp, fontWeight = FontWeight.Bold) }

                if (!mostrarResultado) {
                    Spacer(Modifier.height(20.dp))
                    Text(
                        "Asigna las notas y confirma para calcular",
                        color = TextGray,
                        fontSize = 13.sp
                    )
                } else {
                    Spacer(Modifier.height(18.dp))
                    resultado?.let {
                        ResultadoCard(it, redondear)
                        Spacer(Modifier.height(10.dp))
                        Text(
                            "✓ Promedio calculado correctamente",
                            color = ConfirmGreen,
                            fontWeight = FontWeight.Medium,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(start = 18.dp)
                        )
                    }
                }
                Spacer(Modifier.height(18.dp))
            }
        }
    }
}

@Composable
fun CursoRow(curso: Curso, nota: Float, onNotaChange: (Float) -> Unit) {
    Column(Modifier.fillMaxWidth()) {
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
            Text(
                "${curso.nombre} (${(curso.peso * 100).toInt()}%)",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.width(8.dp))
            Surface(color = BadgeBackground, shape = RoundedCornerShape(8.dp)) {
                Text(
                    nota.toInt().toString(),
                    color = BadgeText,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                )
            }
        }
        Slider(
            value = nota,
            onValueChange = { onNotaChange(it) },
            valueRange = 0f..20f,
            steps = 19,
            colors = SliderDefaults.colors(
                thumbColor = PrimaryPurple,
                activeTrackColor = PrimaryPurple,
                inactiveTrackColor = BadgeBackground
            )
        )
    }
}

@Composable
fun ResultadoCard(resultado: Resultado, redondear: Boolean) {
    Surface(
        color = Color.White,
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, Color(0xFFD4C6E7)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 17.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Text(
                "Promedio ponderado:  ${String.format(Locale.US, "%.2f", resultado.ponderado)}",
                fontSize = 14.sp,
                color = Color(0xFF403B45)
            )

            val finalTexto = if (redondear) resultado.promedioFinal.roundToInt().toString()
            else String.format(Locale.US, "%.2f", resultado.promedioFinal)

            Text(
                "Promedio final:  $finalTexto",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryPurple
            )
            if (redondear) Text("(redondeado)", fontSize = 12.sp, color = TextGray)
            Spacer(Modifier.height(3.dp))

            Surface(color = resultado.chipBg, shape = RoundedCornerShape(20.dp)) {
                Text(
                    resultado.observacion,
                    color = resultado.chipText,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 17.dp, vertical = 5.dp)
                )
            }
        }
    }
}