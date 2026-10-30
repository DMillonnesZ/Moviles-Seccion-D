package com.tecsup.huellitasvet.model

object Razas {
    val porEspecie: Map<Especie, List<String>> = mapOf(
        Especie.PERRO to listOf("Labrador", "Golden Retriever", "Beagle", "Bulldog", "Pastor Alemán", "Poodle", "Schnauzer", "Shih Tzu", "Mestizo"),
        Especie.GATO to listOf("Siamés", "Persa", "Maine Coon", "Bengalí", "Angora", "Mestizo"),
        Especie.OTRO to listOf("Conejo", "Hámster", "Cuy", "Loro", "Tortuga", "Otro")
    )
    fun de(especie: Especie?): List<String> = if (especie == null) emptyList() else porEspecie[especie].orEmpty()
}
