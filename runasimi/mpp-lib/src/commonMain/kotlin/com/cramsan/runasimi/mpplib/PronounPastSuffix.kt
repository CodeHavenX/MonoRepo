package com.cramsan.runasimi.mpplib

enum class PronounPastSuffix(
    val suffix: String,
) {
    @Suppress("EnumNaming")
    ÑUQA("ni"),
    QAM("nki"),
    PAY(""),

    @Suppress("EnumNaming")
    ÑUQANCHIK("nchik"),

    @Suppress("EnumNaming")
    ÑUQAYKU("niku"),
    QAMKUNA("nkichik"),
    PAYKUNA("ku"),
}
