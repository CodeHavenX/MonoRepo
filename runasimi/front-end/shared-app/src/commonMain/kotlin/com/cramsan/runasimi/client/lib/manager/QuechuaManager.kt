package com.cramsan.runasimi.client.lib.manager

import kotlin.math.pow

/**
 * Manager that handles quechua related content generation.
 */
class QuechuaManager(
    private val verbTranslationRepository: VerbTranslationRepository,
) {

    /**
     * Generate a random verb conjugation content.
     */
    suspend fun generateVerbConjugation(): Content {
        verbTranslationRepository.initialize()
        val conjugation = generateConjugation()
        val englishTranslation = verbTranslationRepository.getEnglishTranslation(conjugation)
        val spanishTranslation = verbTranslationRepository.getSpanishTranslation(conjugation)
        return Content(
            translated = conjugation.toTranslation(),
            original = conjugation.toSentence(),
            englishTranslation = englishTranslation,
            spanishTranslation = spanishTranslation,
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
    val original: String,
    val englishTranslation: String? = null,
    val spanishTranslation: String? = null,
)
