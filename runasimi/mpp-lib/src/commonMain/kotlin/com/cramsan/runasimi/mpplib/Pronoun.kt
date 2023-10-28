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
    ÑUQANCHIK("Ñuqanchik", "Nosotros/as(i*)"),
    @Suppress("EnumNaming")
    ÑUQAYKU("Ñuqayku", "Nosotros/as(e*)"),
    QAMKUNA("Qamkuna", "Ustedes"),
    PAYKUNA("Paykuna", "Ellos/as"),
}
