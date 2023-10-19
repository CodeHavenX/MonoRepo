package com.cramsan.runasimi.mpplib

enum class Pronoun(
    val pronoun: String,
    val translation: String,
) {
    @Suppress("EnumNaming")
    ÑUQA("Ñuqa", "Yo"),
    QAM("Qam", "Tu"),
    PAY("Pay", "El/Ella"),
    @Suppress("EnumNaming")
    ÑUQANCHIK("Ñuqanchik", "Nosotros/as(inclusivo)"),
    @Suppress("EnumNaming")
    ÑUQAYKU("Ñuqayku", "Nosotros/as(exclusivo)"),
    QAMKUNA("Qamkuna", "Ustedes"),
    PAYKUNA("Paykuna", "Ellos/as"),
}
