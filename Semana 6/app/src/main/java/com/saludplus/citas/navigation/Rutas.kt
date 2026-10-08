package com.saludplus.citas.navigation

object Rutas {
    const val SPLASH = "splash"
    const val REGISTRO = "registro"
    const val LOGIN = "login"
    const val TERMINOS = "terminos"
    const val HOME = "home"
    const val ESPECIALIDADES = "especialidades"
    const val MEDICOS = "medicos/{especialidadId}"
    const val FECHA_HORA = "fechaHora/{medicoId}"
    const val CONFIRMAR_CITA = "confirmarCita/{medicoId}/{fecha}/{hora}"
    const val CITA_EXITOSA = "citaExitosa"
    const val MIS_CITAS = "misCitas"
    const val DETALLE_CITA = "detalleCita/{citaId}"
    const val PERFIL = "perfil"
    const val RESULTADOS = "resultados"
    const val RESULTADO_DETALLE = "resultadoDetalle/{resultadoId}"
    const val NOTIFICACIONES = "notificaciones"

    // Funciones que arman la ruta con el parámetro ya puesto
    fun medicos(especialidadId: Int) = "medicos/$especialidadId"
    fun fechaHora(medicoId: Int) = "fechaHora/$medicoId"
    fun confirmarCita(medicoId: Int, fecha: String, hora: String) =
        "confirmarCita/$medicoId/$fecha/$hora"
    fun detalleCita(citaId: Int) = "detalleCita/$citaId"
    fun resultadoDetalle(resultadoId: Int) = "resultadoDetalle/$resultadoId"
}