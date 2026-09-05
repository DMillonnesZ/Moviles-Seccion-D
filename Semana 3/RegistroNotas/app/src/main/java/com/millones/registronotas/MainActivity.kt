package com.millones.registronotas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

data class Curso(val nombre: String, val peso: Float)

val cursos = listOf(
    Curso("Fundamentos de Programación", 0.20f),
    Curso("Programación Orientada a Objetos", 0.25f),
    Curso("Programación en Móviles", 0.30f),
    Curso("Base de Datos", 0.25f)
)

data class Resultado(
    val ponderado: Double,
    val final: Double,
    val observacion: String,
    val chipBg: Color,
    val chipText: Color
)

fun calcularResultado(
    notaFundamentos: Float,
    notaPoo: Float,
    notaMoviles: Float,
    notaBd: Float,
    redondear: Boolean
): Resultado {
    val ponderado = notaFundamentos * cursos[0].peso +
            notaPoo * cursos[1].peso +
            notaMoviles * cursos[2].peso +
            notaBd * cursos[3].peso

    val ponderadoRedondeado2 = (ponderado * 100.0).roundToInt() / 100.0

    val final: Double = if (redondear) {
        ponderado.roundToInt().toDouble()
    } else {
        ponderadoRedondeado2
    }

    val (observacion, chipBg, chipText) = when {
        final >= 17.0 -> Triple("EXCELENTE", ChipExcelenteBg, ChipExcelenteText)
        final >= 13.0 -> Triple("APROBADO", ChipAprobadoBg, ChipAprobadoText)
        final >= 10.0 -> Triple("EN RECUPERACIÓN", ChipRecuperacionBg, ChipRecuperacionText)
        else -> Triple("DESAPROBADO", ChipDesaprobadoBg, ChipDesaprobadoText)
    }

    return Resultado(ponderadoRedondeado2, final, observacion, chipBg, chipText)
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RegistroNotasScreen()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroNotasScreen() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Registro de Notas", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PrimaryPurple,
                    titleContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Brush.verticalGradient(listOf(BackgroundStart, BackgroundEnd)))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(text = "Notas del ciclo", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text(
                    text = "Desliza para asignar cada nota (0 a 20)",
                    fontSize = 13.sp,
                    color = TextGray
                )

                Spacer(modifier = Modifier.height(8.dp))

                var notaFundamentos by remember { mutableFloatStateOf(0f) }
                var notaPoo by remember { mutableFloatStateOf(0f) }
                var notaMoviles by remember { mutableFloatStateOf(0f) }
                var notaBd by remember { mutableFloatStateOf(0f) }

                var redondear by remember { mutableStateOf(false) }
                var confirmado by remember { mutableStateOf(false) }

                CursoRow(cursos[0], notaFundamentos) { notaFundamentos = it }
                Spacer(modifier = Modifier.height(4.dp))
                CursoRow(cursos[1], notaPoo) { notaPoo = it }
                Spacer(modifier = Modifier.height(4.dp))
                CursoRow(cursos[2], notaMoviles) { notaMoviles = it }
                Spacer(modifier = Modifier.height(4.dp))
                CursoRow(cursos[3], notaBd) { notaBd = it }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Redondear promedio final")
                    Switch(checked = redondear, onCheckedChange = { redondear = it })
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = confirmado, onCheckedChange = { confirmado = it })
                    Text(text = "Confirmo que las notas son correctas")
                }

                Spacer(modifier = Modifier.height(8.dp))

                var mostrarResultado by remember { mutableStateOf(false) }
                var resultado by remember { mutableStateOf<Resultado?>(null) }

                Button(
                    onClick = {
                        resultado = calcularResultado(
                            notaFundamentos, notaPoo, notaMoviles, notaBd, redondear
                        )
                        mostrarResultado = true
                    },
                    enabled = confirmado,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryPurple,
                        disabledContainerColor = ButtonDisabled,
                        disabledContentColor = ButtonDisabledText
                    ),
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(text = "CALCULAR PROMEDIO", fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (!mostrarResultado) {
                    Text(
                        text = "Asigna las notas y confirma para calcular",
                        color = TextGray,
                        fontSize = 13.sp
                    )
                } else {
                    resultado?.let { r ->
                        Text(text = "Promedio ponderado: ${"%.2f".format(r.ponderado)}")
                        Text(text = "Promedio final: ${if (redondear) r.final.toInt() else r.final}")
                        Text(text = "Observación: ${r.observacion}")
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Desarrollado por: Daniel Millones",
                    color = TextGray,
                    fontSize = 12.sp,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun CursoRow(curso: Curso, nota: Float, onNotaChange: (Float) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${curso.nombre} (${(curso.peso * 100).toInt()}%)",
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                modifier = Modifier.weight(1f)
            )
            Surface(color = BadgeBackground, shape = RoundedCornerShape(8.dp)) {
                Text(
                    text = nota.toInt().toString(),
                    color = BadgeText,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
        }
        Slider(
            value = nota,
            onValueChange = onNotaChange,
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