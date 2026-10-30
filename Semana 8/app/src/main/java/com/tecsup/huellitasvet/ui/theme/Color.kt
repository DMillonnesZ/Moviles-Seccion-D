package com.tecsup.huellitasvet.ui.theme

import androidx.compose.ui.graphics.Color
import com.tecsup.huellitasvet.model.EstadoCita

// ===== Dueño (esmeralda) =====
val DuenoPrimario = Color(0xFF059669)
val DuenoContenedor = Color(0xFFD1FAE5)
val DuenoFondo = Color(0xFFF6FDF9)

// ===== Veterinario (azul) =====
val VetPrimario = Color(0xFF1D4ED8)
val VetContenedor = Color(0xFFDBEAFE)
val VetFondo = Color(0xFFF7F9FF)

// ===== Modo oscuro =====
val DuenoPrimarioOscuro = Color(0xFF34D399)
val DuenoContenedorOscuro = Color(0xFF065F46)
val VetPrimarioOscuro = Color(0xFF60A5FA)
val VetContenedorOscuro = Color(0xFF1E3A8A)
val FondoOscuro = Color(0xFF0F1419)
val SuperficieOscura = Color(0xFF1A2027)

// ===== Estados de la cita =====
val EstadoPendiente = Color(0xFFD97706)
val EstadoConfirmada = Color(0xFF16A34A)
val EstadoAtendida = Color(0xFF475569)
val EstadoCancelada = Color(0xFFDC2626)

fun EstadoCita.color(): Color = when (this) {
    EstadoCita.PENDIENTE -> EstadoPendiente
    EstadoCita.CONFIRMADA -> EstadoConfirmada
    EstadoCita.ATENDIDA -> EstadoAtendida
    EstadoCita.CANCELADA -> EstadoCancelada
}

// ===== Neutros =====
val GrisApagado = Color(0xFFE5E7EB)   // switch apagado
val BordeSuave = Color(0xFFEEF0F4)    // borde de tarjetas
val GrisSelector = Color(0xFFF1F3F5)

// ===== Recordatorio (aviso ámbar) =====
val AvisoFondo = Color(0xFFFEF3C7)
val AvisoIcono = Color(0xFFD97706)
val AvisoTexto = Color(0xFF92400E)// fondo del selector de rol