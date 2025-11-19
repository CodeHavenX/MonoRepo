package com.cramsan.runasimi.client.lib.manager

/**
 * Manager that provides common daily questions and their translations.
 *
 * Translations are left as placeholders for now and can be filled later.
 */
class QuestionsManager {

    private val questions: List<Pair<String, String>> = listOf(
        "How are you?" to "TRANSLATION_PLACEHOLDER",
        "What's your name?" to "TRANSLATION_PLACEHOLDER",
        "Where are you from?" to "TRANSLATION_PLACEHOLDER",
        "What time is it?" to "TRANSLATION_PLACEHOLDER",
        "Can you help me?" to "TRANSLATION_PLACEHOLDER",
        "Where is the bathroom?" to "TRANSLATION_PLACEHOLDER",
        "How much does this cost?" to "TRANSLATION_PLACEHOLDER",
        "I don't understand." to "TRANSLATION_PLACEHOLDER",
        "Please speak slower." to "TRANSLATION_PLACEHOLDER",
        "Thank you." to "TRANSLATION_PLACEHOLDER"
    )

    /**
     * Return a randomly selected question as a [Content] instance.
     */
    fun generateQuestion(): Content {
        val (original, translated) = questions.random()
        return Content(
            translated = translated,
            original = original,
        )
    }
}
