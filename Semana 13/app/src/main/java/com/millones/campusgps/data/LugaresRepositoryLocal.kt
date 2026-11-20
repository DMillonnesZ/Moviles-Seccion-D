package com.millones.campusgps.data

import com.millones.campusgps.model.Lugar

class LugaresRepositoryLocal : LugaresRepository {
    override fun obtenerLugares(): List<Lugar> = listOf(
        Lugar(1, "Biblioteca Central", "Estudio", -12.044947, -76.952843),
        Lugar(2, "Cafetería Campus", "Comida", -12.044064, -76.952468),
        Lugar(3, "Laboratorio de Cómputo", "Estudio", -12.044421, -76.952455),
        Lugar(4, "Auditorio Principal", "Eventos", -12.044758, -76.952689),
        Lugar(5, "Estacionamiento Norte", "Servicios", -12.044470, -76.951689)
    )
}