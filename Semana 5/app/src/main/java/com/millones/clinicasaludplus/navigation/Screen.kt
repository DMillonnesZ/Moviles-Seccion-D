package com.millones.clinicasaludplus.navigation

sealed class Screen(val route: String) {

    data object Inicio : Screen("inicio")

    data object PerfilMedico :
        Screen("perfil_medico/{nombre}/{especialidad}/{calificacion}")

    data object AgendarCita :
        Screen("agendar_cita/{nombre}/{especialidad}")

    data object Confirmacion :
        Screen("confirmacion/{nombre}/{especialidad}/{fecha}/{horario}")

    data object MisCitas : Screen("mis_citas")

    data object HistorialMedico : Screen("historial_medico")
}