package com.millones.clinicasaludplus.data

data class Medico(
    val nombre: String,
    val especialidad: String,
    val calificacion: Double,
    val experiencia: String = "12 años exp.",
    val resenas: String = "128 reseñas",
    val bio: String = "Especialista en arritmias e hipertensión, formación en la Clínica Mayo."
)

enum class EstadoCita {
    CONFIRMADA,
    COMPLETADA
}

data class Cita(
    val medicoNombre: String,
    val fechaHora: String,
    val estado: EstadoCita
)

val listaMedicos = listOf(
    Medico(
        nombre = "Dra. Ana Torres",
        especialidad = "Cardióloga",
        calificacion = 4.9,
        experiencia = "12 años exp.",
        resenas = "128 reseñas",
        bio = "Especialista en arritmias e hipertensión, formación en la Clínica Mayo."
    ),
    Medico(
        nombre = "Dr. Luis Vega",
        especialidad = "Pediatra",
        calificacion = 4.7,
        experiencia = "8 años exp.",
        resenas = "95 reseñas",
        bio = "Especialista en pediatría general y desarrollo infantil, formado en el Hospital San José."
    ),
    Medico(
        nombre = "Dra. Rosa Díaz",
        especialidad = "Dermatóloga",
        calificacion = 4.8,
        experiencia = "10 años exp.",
        resenas = "110 reseñas",
        bio = "Especialista en dermatología clínica y estética, certificada internacionalmente."
    )
)

object CitasRepository {
    val citas = mutableListOf(
        Cita(
            medicoNombre = "Dra. Ana Torres",
            fechaHora = "Viernes 27, 10:30 am",
            estado = EstadoCita.CONFIRMADA
        ),
        Cita(
            medicoNombre = "Dr. Luis Vega",
            fechaHora = "Miércoles 15, 3:00 pm",
            estado = EstadoCita.COMPLETADA
        )
    )

    fun agregarCita(medicoNombre: String, fechaHora: String) {
        citas.add(
            0,
            Cita(
                medicoNombre = medicoNombre,
                fechaHora = fechaHora,
                estado = EstadoCita.CONFIRMADA
            )
        )
    }
}
