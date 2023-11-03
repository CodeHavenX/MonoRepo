package com.cramsan.runasimi.mpplib

enum class TimeTense(
    val suffix: List<String>,
    val meaning: String,
) {
    PRESENT(listOf(), "Presente"),
    PAST(listOf("Rqa", "Ra"), "Pasado"),
    FUTURE(listOf(), "Futuro"),
}
