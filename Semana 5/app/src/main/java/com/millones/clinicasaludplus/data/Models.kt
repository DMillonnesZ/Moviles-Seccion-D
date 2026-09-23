package com.millones.clinicasaludplus.data

data class ResenaOpinion(
    val autor: String,
    val calificacion: Double,
    val comentario: String,
    val fecha: String
)

data class Medico(
    val nombre: String,
    val especialidad: String,
    val calificacion: Double,
    val experiencia: String = "12 años exp.",
    val resenas: String = "128 reseñas",
    val bio: String = "Especialista en arritmias e hipertensión, formación en la Clínica Mayo.",
    val sedeLocation: String = "Sede Central - San Isidro",
    val precioConsulta: String = "S/ 120.00",
    val disponibilidad: String = "Disponible hoy",
    val opiniones: List<ResenaOpinion> = listOf(
        ResenaOpinion("Carlos M.", 5.0, "Excelente atención, muy clara con el diagnóstico y puntual.", "Hace 2 días"),
        ResenaOpinion("Elena R.", 4.8, "Trato amable y muy profesional. Instalaciones impecables.", "Hace 1 semana")
    )
)

enum class EstadoCita {
    CONFIRMADA,
    COMPLETADA,
    CANCELADA
}

data class Cita(
    val id: String = "CS-${(1000..9999).random()}",
    val medicoNombre: String,
    val especialidad: String = "Especialista",
    val fechaHora: String,
    val estado: EstadoCita,
    val sedeLocation: String = "Sede Central - San Isidro"
)

val listaMedicos = listOf(
    Medico(
        nombre = "Dra. Ana Torres",
        especialidad = "Cardióloga",
        calificacion = 4.9,
        experiencia = "12 años exp.",
        resenas = "128 reseñas",
        bio = "Especialista en arritmias e hipertensión, formación en la Clínica Mayo con más de 12 años atendiendo pacientes de alta complejidad.",
        sedeLocation = "Sede Central - San Isidro",
        precioConsulta = "S/ 140.00",
        disponibilidad = "Disponible hoy",
        opiniones = listOf(
            ResenaOpinion("Carlos M.", 5.0, "Excelente atención, muy clara con el diagnóstico y puntual.", "Hace 2 días"),
            ResenaOpinion("Elena R.", 4.8, "Trato amable y muy profesional. Instalaciones impecables.", "Hace 1 semana")
        )
    ),
    Medico(
        nombre = "Dr. Luis Vega",
        especialidad = "Pediatra",
        calificacion = 4.7,
        experiencia = "8 años exp.",
        resenas = "95 reseñas",
        bio = "Especialista en pediatría general y desarrollo infantil, formado en el Hospital San José y enfocado en la atención integral pediátrica.",
        sedeLocation = "Sede Norte - Los Olivos",
        precioConsulta = "S/ 110.00",
        disponibilidad = "Próx. turno mañana",
        opiniones = listOf(
            ResenaOpinion("Mariana S.", 5.0, "Pacina única con los niños, mi hijo estuvo muy tranquilo durante la revisión.", "Hace 3 días")
        )
    ),
    Medico(
        nombre = "Dra. Rosa Díaz",
        especialidad = "Dermatóloga",
        calificacion = 4.8,
        experiencia = "10 años exp.",
        resenas = "110 reseñas",
        bio = "Especialista en dermatología clínica y estética, certificada internacionalmente en procedimientos dermatológicos avanzados.",
        sedeLocation = "Sede Sur - Miraflores",
        precioConsulta = "S/ 130.00",
        disponibilidad = "Disponible hoy",
        opiniones = listOf(
            ResenaOpinion("Jorge P.", 4.9, "Muy acertada en el tratamiento de piel. Resultados visibles rápido.", "Hace 5 días")
        )
    )
)

object CitasRepository {
    val citas = mutableListOf(
        Cita(
            id = "CS-8921",
            medicoNombre = "Dra. Ana Torres",
            especialidad = "Cardióloga",
            fechaHora = "Viernes 27, 10:30 am",
            estado = EstadoCita.CONFIRMADA,
            sedeLocation = "Sede Central - San Isidro"
        ),
        Cita(
            id = "CS-5432",
            medicoNombre = "Dr. Luis Vega",
            especialidad = "Pediatra",
            fechaHora = "Miércoles 15, 3:00 pm",
            estado = EstadoCita.COMPLETADA,
            sedeLocation = "Sede Norte - Los Olivos"
        )
    )

    fun agregarCita(medicoNombre: String, fechaHora: String, especialidad: String = "Cardióloga") {
        citas.add(
            0,
            Cita(
                id = "CS-${(1000..9999).random()}",
                medicoNombre = medicoNombre,
                especialidad = especialidad,
                fechaHora = fechaHora,
                estado = EstadoCita.CONFIRMADA,
                sedeLocation = "Sede Central - San Isidro"
            )
        )
    }

    fun cancelarCita(citaId: String) {
        val index = citas.indexOfFirst { it.id == citaId }
        if (index != -1) {
            citas[index] = citas[index].copy(estado = EstadoCita.CANCELADA)
        }
    }
}
