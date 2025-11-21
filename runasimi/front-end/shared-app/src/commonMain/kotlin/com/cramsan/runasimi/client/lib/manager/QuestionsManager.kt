package com.cramsan.runasimi.client.lib.manager

/**
 * Manager that provides common daily questions and their translations.
 *
 * Translations are left as placeholders for now and can be filled later.
 */
class QuestionsManager {

    private val questions: List<Pair<String, String>> = listOf(
        "Mayqintaq situnyki?" to "What is your name?",
        "Maymamtaq kanki?" to "Where are you from?",
        "Pitaq kanki?" to "Who are you?",
        "Imataq ruwachkanki?" to "What are you doing?",
        "Maymantaq richkanki?" to "Where are you going?",
        "Imaynallam kachkanki?" to "How are you?",
        "Maypitaq tiyanki?" to "Where do you live?",
        "Piwanmi kanki?" to "With whom are you?",
        "Hayka qullqitaq kayqa" to "How much does this cost?",
        "Haykapitaq purichkanki?" to "When are you traveling?",
        "Imaynataq kayta ruwanki?" to "How do you do this?",
        "Imanasqataq chayta ruwarqanki?" to "Why did you do that?",
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
