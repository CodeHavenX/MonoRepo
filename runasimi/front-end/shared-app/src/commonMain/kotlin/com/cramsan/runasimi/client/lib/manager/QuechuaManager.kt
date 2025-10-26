package com.cramsan.runasimi.client.lib.manager

import kotlin.math.pow

/**
 * Manager that handles quechua related content generation.
 */
class QuechuaManager {

    /**
     * Generate a random verb conjugation content.
     */
    fun generateVerbConjugation(): Content {
        val conjugation = generateConjugation()
        return Content(
            translated = conjugation.toTranslation(),
            original = conjugation.toSentence(),
        )
    }

    /**
     * Generate a random number translation content.
     */
    fun generateNumberTranslation(digits: Int = 2): Content {
        val number = (10.0.pow(digits - 1).toInt() until 10.0.pow(digits).toInt()).random()
        val translation = number.toYupay()
        return Content(
            translated = translation,
            original = number.toString()
        )
    }
}

/**
 * Data class that represents a content with both original and translated forms.
 */
data class Content(
    val translated: String,
    val original: String
)
