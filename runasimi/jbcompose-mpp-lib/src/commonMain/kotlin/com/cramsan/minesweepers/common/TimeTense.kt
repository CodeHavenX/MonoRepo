package com.cramsan.minesweepers.common

enum class TimeTense(
    val suffix: List<String>,
    val meaning: String,
) {
    PRESENT(listOf(""), "Presente"),
    PAST(listOf("Rqa", "Ra"), "Pasado"),
}