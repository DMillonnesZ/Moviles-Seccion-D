package com.saludplus.citas.data.model

data class ParametroResultado(
    val nombre: String,
    val valor: String,
    val rangoReferencia: String,
    val normal: Boolean = true
)

data class Resultado(
    val id: Int,
    val examen: String,
    val fecha: String,
    val especialidadId: Int,
    val estado: String,
    val observaciones: String = "Resultado verificado por el laboratorio central de la Clínica SaludPlus.",
    val parametros: List<ParametroResultado> = listOf(
        ParametroResultado("Hemoglobina", "14.2 g/dL", "12.0 - 16.0 g/dL", true),
        ParametroResultado("Glucosa en ayunas", "92 mg/dL", "70 - 100 mg/dL", true),
        ParametroResultado("Colesterol Total", "185 mg/dL", "120 - 200 mg/dL", true),
        ParametroResultado("Triglicéridos", "210 mg/dL", "< 150 mg/dL", false)
    )
)
