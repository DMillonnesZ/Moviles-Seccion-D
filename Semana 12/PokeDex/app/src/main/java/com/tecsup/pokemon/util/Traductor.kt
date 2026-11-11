package com.tecsup.pokemon.util

// Traduce al español los datos que la PokeAPI envía en inglés.
// Si algo no está en el diccionario, lo devuelve formateado: "rain-dish" -> "Rain Dish"
object Traductor {

    private val tipos = mapOf(
        "normal" to "Normal", "fire" to "Fuego", "water" to "Agua",
        "electric" to "Eléctrico", "grass" to "Planta", "ice" to "Hielo",
        "fighting" to "Lucha", "poison" to "Veneno", "ground" to "Tierra",
        "flying" to "Volador", "psychic" to "Psíquico", "bug" to "Bicho",
        "rock" to "Roca", "ghost" to "Fantasma", "dragon" to "Dragón",
        "dark" to "Siniestro", "steel" to "Acero", "fairy" to "Hada",
        "stellar" to "Astral", "unknown" to "Desconocido"
    )

    private val estadisticas = mapOf(
        "hp" to "PS",
        "attack" to "Ataque",
        "defense" to "Defensa",
        "special-attack" to "At. Esp.",
        "special-defense" to "Def. Esp.",
        "speed" to "Velocidad"
    )

    // Orden en que se muestran las estadísticas
    val ordenEstadisticas = listOf("hp", "attack", "defense", "special-attack", "special-defense", "speed")

    private val objetos = mapOf(
        "fire-stone" to "Piedra Fuego",
        "water-stone" to "Piedra Agua",
        "thunder-stone" to "Piedra Trueno",
        "leaf-stone" to "Piedra Hoja",
        "moon-stone" to "Piedra Lunar",
        "sun-stone" to "Piedra Solar",
        "shiny-stone" to "Piedra Día",
        "dusk-stone" to "Piedra Noche",
        "dawn-stone" to "Piedra Alba",
        "ice-stone" to "Piedra Hielo",
        "oval-stone" to "Piedra Oval",
        "kings-rock" to "Roca del Rey",
        "metal-coat" to "Revestimiento Metálico",
        "dragon-scale" to "Escama Dragón",
        "upgrade" to "Mejora",
        "dubious-disc" to "Disco Extraño",
        "protector" to "Protector",
        "electirizer" to "Electrizador",
        "magmarizer" to "Magmatizador",
        "razor-claw" to "Garra Afilada",
        "razor-fang" to "Colmillo Agudo",
        "reaper-cloth" to "Tela Terrible",
        "prism-scale" to "Escama Bella",
        "deep-sea-tooth" to "Diente Marino",
        "deep-sea-scale" to "Escama Marina",
        "sachet" to "Saquito Fragante",
        "whipped-dream" to "Dulce de Nata",
        "linking-cord" to "Cordón Unión",
        "black-augurite" to "Mineral Negro",
        "peat-block" to "Bloque de Turba",
        "sweet-apple" to "Manzana Dulce",
        "tart-apple" to "Manzana Ácida",
        "syrupy-apple" to "Manzana Melosa",
        "cracked-pot" to "Tetera Agrietada",
        "chipped-pot" to "Tetera Rota",
        "galarica-cuff" to "Brazal Galanuez",
        "galarica-wreath" to "Corona Galanuez",
        "auspicious-armor" to "Armadura Auspiciosa",
        "malicious-armor" to "Armadura Maldita",
        "metal-alloy" to "Metal Compuesto"
    )

    // "fire" -> "Fuego"
    fun tipoEs(tipo: String): String = tipos[tipo] ?: formatear(tipo)

    // "special-attack" -> "At. Esp."
    fun estadisticaEs(estadistica: String): String = estadisticas[estadistica] ?: formatear(estadistica)

    // "fire-stone" -> "Piedra Fuego"
    fun objetoEs(objeto: String): String = objetos[objeto] ?: formatear(objeto)

    // Cómo evoluciona: "Nv. 16", "Piedra Fuego", "Intercambio", "Amistad (noche)"...
    fun condicionEs(
        disparador: String?,
        nivelMinimo: Int?,
        objeto: String?,
        objetoEquipado: String?,
        amistadMinima: Int?,
        momentoDelDia: String?
    ): String {
        val momento = when (momentoDelDia) {
            "day" -> " (día)"
            "night" -> " (noche)"
            else -> ""
        }
        return when (disparador) {
            "level-up" -> when {
                nivelMinimo != null -> "Nv. $nivelMinimo$momento"
                amistadMinima != null -> "Amistad$momento"
                objetoEquipado != null -> "Nivel con ${objetoEs(objetoEquipado)}$momento"
                else -> "Subir nivel$momento"
            }
            "use-item" -> objeto?.let { objetoEs(it) } ?: "Usar objeto"
            "trade" -> objetoEquipado?.let { "Intercambio con ${objetoEs(it)}" } ?: "Intercambio"
            null -> ""
            else -> formatear(disparador)
        }
    }

    // gender_rate de la PokeAPI: -1 sin género; si no, octavos de hembra (0 a 8).
    // Devuelve (porcentaje macho, porcentaje hembra) o null si no tiene género
    fun genero(tasaGenero: Int): Pair<Double, Double>? {
        if (tasaGenero < 0) return null
        val hembra = tasaGenero * 12.5
        return Pair(100.0 - hembra, hembra)
    }

    // Quita saltos de línea raros de las descripciones de la Pokédex
    fun limpiarDescripcion(texto: String): String =
        texto.replace("\n", " ")
            .replace("\u000c", " ")
            .replace(Regex("\\s+"), " ")
            .trim()

    // "rain-dish" -> "Rain Dish"
    fun formatear(texto: String): String =
        texto.split("-").joinToString(" ") { palabra ->
            palabra.replaceFirstChar { it.uppercase() }
        }
}