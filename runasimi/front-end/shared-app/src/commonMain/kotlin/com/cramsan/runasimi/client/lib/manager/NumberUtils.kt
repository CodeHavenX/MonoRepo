package com.cramsan.runasimi.client.lib.manager

import kotlin.math.pow

/**
 * Set of numbers from which other numbers will be created.
 */
@Suppress("MagicNumber")
enum class Yupay(val number: Int) {
    CHUSAQ(0),
    HUQ(1),
    ISKAY(2),
    KIMSA(3),
    TAWA(4),
    PICHQA(5),
    SUQTA(6),
    QANCHIS(7),
    PUSAQ(8),
    ISQUN(9),
    CHUNKA(10),
    PACHAK(100),
    WARANQA(1000),
    ;

    companion object {
        private val map = entries.associateBy(Yupay::number)

        /**
         * Get the [com.cramsan.runasimi.client.lib.manager.Yupay] that matches the [number]. This only works for basic
         * numbers. All other numbers it will return null.
         */
        fun fromInt(number: Int) = map[number]
    }
}

private fun numWord(n: Int) = Yupay.fromInt(n)?.name?.lowercase()

@Suppress("MagicNumber")
private fun extractDigits(number: Int, position: Int): Int? {
    if (10.0.pow(position).toInt() > number) {
        return null
    }
    return (number / 10.0.pow(position).toInt()) % 10
}

/**
 * Convert an [Int] to a quechua number. The result will be a string.
 */
@Suppress("MagicNumber")
fun Int.toYupay(): String {
    if (this < 0) error("Negative numbers are not supported")
    if (this >= 10000) error("Numbers greater than 1000 are not supported")
    if (this == 0) return requireNotNull(numWord(0))

    val parts = mutableListOf<String>()
    var segments = 0
    val digits = 4
    repeat(digits) { pos ->

        val digit = extractDigits(this, pos) ?: return@repeat
        if (digit !in 0..9) {
            error("Digit out of range")
        }

        if (digit == 0) {
            return@repeat
        }
        segments++

        val placeValue = 10.0.pow(pos).toInt()
        val placeValueTranslated = requireNotNull(numWord(placeValue))

        if (digit == 1) {
            parts.add(placeValueTranslated)
        } else {
            if (placeValue > 1) {
                parts.add(placeValueTranslated)
            }
            val digitTranslated = requireNotNull(numWord(digit))
            parts.add(digitTranslated)
        }
    }

    val numbers = parts.reversed().joinToString(" ")
    val lastCharacter = numbers.last()
    val suffix = if (segments > 1) {
        if (!VOWELS.contains(lastCharacter)) {
            "niyuq"
        } else {
            "yuq"
        }
    } else {
        ""
    }
    return "$numbers$suffix"
}

private const val VOWELS = "aeiou"
