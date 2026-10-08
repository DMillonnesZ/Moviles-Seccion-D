package com.saludplus.citas.ui.screens.resultados

import android.content.Intent
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.saludplus.citas.data.model.ParametroResultado
import com.saludplus.citas.data.repository.Repositorio
import com.saludplus.citas.ui.components.BarraSuperior
import com.saludplus.citas.ui.components.BotonAzul
import com.saludplus.citas.ui.components.EstadoVacio
import com.saludplus.citas.ui.components.TarjetaSuave
import com.saludplus.citas.ui.components.estiloEspecialidad
import com.saludplus.citas.ui.theme.AzulClaro
import com.saludplus.citas.ui.theme.AzulOscuro
import com.saludplus.citas.ui.theme.AzulPrimario
import com.saludplus.citas.ui.theme.ColoresSemanticos
import com.saludplus.citas.ui.theme.GrisTexto
import com.saludplus.citas.util.Fechas

@Composable
fun ResultadoDetalleScreen(
    resultadoId: Int,
    onAtras: () -> Unit
) {
    val resultado = Repositorio.obtenerResultado(resultadoId)
    val context = LocalContext.current

    Scaffold(
        containerColor = Color.White,
        topBar = {
            BarraSuperior(
                titulo = "Detalle del resultado",
                onAtras = onAtras
            )
        }
    ) { padding ->
        if (resultado == null) {
            EstadoVacio(
                titulo = "Resultado no encontrado",
                mensaje = "El examen solicitado no se encuentra en el sistema.",
                icono = Icons.Default.Description,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                accionTexto = "Volver",
                onAccion = onAtras
            )
            return@Scaffold
        }

        val especialidad = Repositorio.obtenerEspecialidad(resultado.especialidadId)
        val estilo = estiloEspecialidad(resultado.especialidadId)
        val disponible = resultado.estado == "Disponible"
        val fechaFormateada = Fechas.fechaEnTexto(resultado.fecha)

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Tarjeta de encabezado con datos generales del examen
            item {
                TarjetaSuave(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .clip(CircleShape)
                                .background(estilo.fondo),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = estilo.icono,
                                contentDescription = null,
                                tint = estilo.color,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        Spacer(Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = resultado.examen,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = AzulOscuro
                            )
                            Spacer(Modifier.height(2.dp))
                            Text(
                                text = especialidad?.nombre ?: "Especialidad médica",
                                style = MaterialTheme.typography.bodyMedium,
                                color = GrisTexto
                            )
                        }
                    }

                    Spacer(Modifier.height(16.dp))
                    HorizontalDivider(color = Color(0xFFE5E7EB))
                    Spacer(Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Fecha de emisión",
                                style = MaterialTheme.typography.labelMedium,
                                color = GrisTexto
                            )
                            Text(
                                text = fechaFormateada,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = AzulOscuro
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (disponible) ColoresSemanticos.ExitoFondo else ColoresSemanticos.AdvertenciaFondo)
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = resultado.estado,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (disponible) ColoresSemanticos.Exito else ColoresSemanticos.Advertencia
                            )
                        }
                    }
                }
            }

            // Sección de parámetros del laboratorio
            if (resultado.parametros.isNotEmpty()) {
                item {
                    Text(
                        text = "Valores de laboratorio",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = AzulOscuro,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }

                item {
                    TarjetaSuave(modifier = Modifier.fillMaxWidth()) {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            resultado.parametros.forEachIndexed { index, param ->
                                ItemParametro(parametro = param)
                                if (index < resultado.parametros.size - 1) {
                                    HorizontalDivider(color = Color(0xFFF1F5FB))
                                }
                            }
                        }
                    }
                }
            }

            // Observaciones del laboratorio
            item {
                Text(
                    text = "Observaciones",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = AzulOscuro,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }

            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(AzulClaro)
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.Assignment,
                            contentDescription = null,
                            tint = AzulPrimario,
                            modifier = Modifier
                                .size(24.dp)
                                .padding(top = 2.dp)
                        )
                        Spacer(Modifier.width(12.dp))
                        Text(
                            text = resultado.observaciones,
                            style = MaterialTheme.typography.bodyMedium,
                            color = AzulOscuro
                        )
                    }
                }
            }

            // Botón Compartir resultado
            item {
                Spacer(Modifier.height(8.dp))
                BotonAzul(
                    texto = "Compartir resultado",
                    onClick = {
                        val textoParametros = resultado.parametros.joinToString("\n") { p ->
                            "- ${p.nombre}: ${p.valor} (Ref: ${p.rangoReferencia}) [${if (p.normal) "Dentro de rango" else "Fuera de rango"}]"
                        }
                        val contenidoCompartir = """
                            *Clínica SaludPlus — Informe de Laboratorio*
                            Examen: ${resultado.examen}
                            Especialidad: ${especialidad?.nombre ?: ""}
                            Fecha: $fechaFormateada
                            Estado: ${resultado.estado}
                            
                            *Valores:*
                            $textoParametros
                            
                            *Observaciones:*
                            ${resultado.observaciones}
                        """.trimIndent()

                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_SUBJECT, "Resultado de Examen: ${resultado.examen}")
                            putExtra(Intent.EXTRA_TEXT, contenidoCompartir)
                        }
                        context.startActivity(Intent.createChooser(intent, "Compartir resultado"))
                    }
                )
            }
        }
    }
}

@Composable
private fun ItemParametro(parametro: ParametroResultado) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = parametro.nombre,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = AzulOscuro
            )
            Text(
                text = "Ref: ${parametro.rangoReferencia}",
                style = MaterialTheme.typography.bodySmall,
                color = GrisTexto
            )
        }

        Spacer(Modifier.width(8.dp))

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = parametro.valor,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = if (parametro.normal) AzulOscuro else ColoresSemanticos.Error
            )
            Spacer(Modifier.height(2.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(if (parametro.normal) ColoresSemanticos.ExitoFondo else ColoresSemanticos.ErrorFondo)
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (parametro.normal) Icons.Default.CheckCircle else Icons.Default.Warning,
                        contentDescription = null,
                        tint = if (parametro.normal) ColoresSemanticos.Exito else ColoresSemanticos.Error,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = if (parametro.normal) "Normal" else "Fuera de rango",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (parametro.normal) ColoresSemanticos.Exito else ColoresSemanticos.Error
                    )
                }
            }
        }
    }
}
