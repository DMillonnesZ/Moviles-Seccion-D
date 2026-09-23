package com.millones.clinicasaludplus.navigation

sealed class Screen(val route: String) {

    data object Inicio : Screen("inicio")

    data object PerfilMedico : Screen("perfil_medico")

    data object AgendarCita : Screen("agendar_cita")

    data object Confirmacion : Screen("confirmacion")

    data object MisCitas : Screen("mis_citas")

    data object HistorialMedico : Screen("historial_medico")
}