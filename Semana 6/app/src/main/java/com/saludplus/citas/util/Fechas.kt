package com.saludplus.citas.util

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeParseException

object Fechas {

    fun esDiaHabil(fecha: LocalDate): Boolean {
        return fecha.dayOfWeek != DayOfWeek.SATURDAY && fecha.dayOfWeek != DayOfWeek.SUNDAY
    }

    fun primerDiaHabil(desde: LocalDate): LocalDate {
        var actual = desde
        while (!esDiaHabil(actual)) {
            actual = actual.plusDays(1)
        }
        return actual
    }

    fun diasHabiles(desde: LocalDate, cantidad: Int = 5): List<LocalDate> {
        val resultado = mutableListOf<LocalDate>()
        var actual = primerDiaHabil(desde)
        while (resultado.size < cantidad) {
            if (esDiaHabil(actual)) {
                resultado.add(actual)
            }
            actual = actual.plusDays(1)
        }
        return resultado
    }

    fun semanaDeCalendario(hoy: LocalDate, offsetSemanas: Long): List<LocalDate> {
        val inicioBase = primerDiaHabil(hoy).plusDays(offsetSemanas * 7)
        return diasHabiles(inicioBase, 5)
    }

    fun nombreDiaCorto(fecha: LocalDate): String {
        return when (fecha.dayOfWeek) {
            DayOfWeek.MONDAY -> "Lun"
            DayOfWeek.TUESDAY -> "Mar"
            DayOfWeek.WEDNESDAY -> "Mié"
            DayOfWeek.THURSDAY -> "Jue"
            DayOfWeek.FRIDAY -> "Vie"
            DayOfWeek.SATURDAY -> "Sáb"
            DayOfWeek.SUNDAY -> "Dom"
        }
    }

    fun nombreDiaLargo(dayOfWeek: DayOfWeek): String {
        return when (dayOfWeek) {
            DayOfWeek.MONDAY -> "Lunes"
            DayOfWeek.TUESDAY -> "Martes"
            DayOfWeek.WEDNESDAY -> "Miércoles"
            DayOfWeek.THURSDAY -> "Jueves"
            DayOfWeek.FRIDAY -> "Viernes"
            DayOfWeek.SATURDAY -> "Sábado"
            DayOfWeek.SUNDAY -> "Domingo"
        }
    }

    fun nombreMes(mes: Int): String {
        return when (mes) {
            1 -> "Enero"
            2 -> "Febrero"
            3 -> "Marzo"
            4 -> "Abril"
            5 -> "Mayo"
            6 -> "Junio"
            7 -> "Julio"
            8 -> "Agosto"
            9 -> "Setiembre"
            10 -> "Octubre"
            11 -> "Noviembre"
            12 -> "Diciembre"
            else -> ""
        }
    }

    fun nombreMesMinuscula(mes: Int): String {
        return nombreMes(mes).lowercase()
    }

    fun mesYAnio(fecha: LocalDate): String {
        return "${nombreMes(fecha.monthValue)} ${fecha.year}"
    }

    fun fechaEnTexto(fecha: LocalDate): String {
        val diaNombre = nombreDiaLargo(fecha.dayOfWeek)
        val diaNum = fecha.dayOfMonth
        val mesNombre = nombreMesMinuscula(fecha.monthValue)
        val anio = fecha.year
        return "$diaNombre $diaNum de $mesNombre $anio"
    }

    fun fechaEnTexto(fechaIso: String): String {
        return try {
            val parsed = LocalDate.parse(fechaIso)
            fechaEnTexto(parsed)
        } catch (e: DateTimeParseException) {
            fechaIso
        }
    }

    fun mesesDisponibles(hoy: LocalDate, cantidad: Int = 12): List<YearMonth> {
        val inicio = YearMonth.from(hoy)
        return (0 until cantidad).map { inicio.plusMonths(it.toLong()) }
    }

    fun indiceSemanaDeMes(hoy: LocalDate, mesTarget: YearMonth): Long {
        val mesActual = YearMonth.from(hoy)
        if (mesTarget <= mesActual) return 0L

        var offset = 0L
        val maxOffset = 52L
        while (offset < maxOffset) {
            val dias = semanaDeCalendario(hoy, offset)
            val primerDia = dias.firstOrNull() ?: break
            if (YearMonth.from(primerDia) >= mesTarget) {
                return offset
            }
            offset++
        }
        return offset
    }

    fun puedeAvanzar(hoy: LocalDate, offsetSemanas: Long, limiteMeses: Int = 12): Boolean {
        val meses = mesesDisponibles(hoy, limiteMeses)
        val ultimoMes = meses.lastOrNull() ?: return true
        val siguienteSemana = semanaDeCalendario(hoy, offsetSemanas + 1)
        val primerDiaSiguiente = siguienteSemana.firstOrNull() ?: return false
        return YearMonth.from(primerDiaSiguiente) <= ultimoMes
    }

    fun etiquetaRelativa(fecha: LocalDate, hoy: LocalDate = LocalDate.now()): String {
        val diferencia = java.time.temporal.ChronoUnit.DAYS.between(hoy, fecha)
        return when {
            diferencia == 0L -> "Hoy"
            diferencia == 1L -> "Mañana"
            diferencia in 2..7 -> "En $diferencia días"
            diferencia < 0 -> "Pasada"
            else -> fechaEnTexto(fecha)
        }
    }
}
