package com.tecsup.huellitasvet.data.local.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.tecsup.huellitasvet.data.local.entity.Cita
import com.tecsup.huellitasvet.data.local.entity.Mascota
import com.tecsup.huellitasvet.data.local.entity.Servicio

data class CitaConDetalle(
    @Embedded val cita: Cita,

    // Relación anidada: cita → mascota → dueño
    @Relation(
        entity = Mascota::class,
        parentColumn = "mascotaId",
        entityColumn = "id"
    )
    val mascota: MascotaConDueno,

    @Relation(
        parentColumn = "servicioId",
        entityColumn = "id"
    )
    val servicio: Servicio
)