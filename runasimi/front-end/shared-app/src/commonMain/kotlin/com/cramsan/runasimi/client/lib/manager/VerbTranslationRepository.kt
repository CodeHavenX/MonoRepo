package com.cramsan.runasimi.client.lib.manager

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.jetbrains.compose.resources.ExperimentalResourceApi
import runasimi_lib.Res

/**
 * A single translation entry from the JSON resource file.
 */
@Serializable
data class TranslationEntry(
    val verbRoot: String,
    val tense: String,
    val person: String,
    val plurality: String,
    val inclusive: Boolean,
    val translation: String,
)

/**
 * The root object for the translation JSON file.
 */
@Serializable
data class TranslationFile(
    val translations: List<TranslationEntry>,
)

/**
 * A key for looking up translations, composed of verb conjugation attributes.
 */
data class TranslationKey(
    val verbRoot: String,
    val tense: Tense,
    val person: Person,
    val plurality: Plurality,
    val inclusive: Boolean,
)

/**
 * Repository for loading and providing verb translations in English and Spanish.
 * Translations are loaded from JSON resource files at initialization time.
 */
class VerbTranslationRepository {

    private var englishTranslations: Map<TranslationKey, String> = emptyMap()
    private var spanishTranslations: Map<TranslationKey, String> = emptyMap()
    private var isInitialized = false

    private val json = Json { ignoreUnknownKeys = true }

    /**
     * Initialize the repository by loading translation files.
     * Must be called before using translation lookup methods.
     */
    @OptIn(ExperimentalResourceApi::class)
    suspend fun initialize() {
        if (isInitialized) return

        englishTranslations = loadTranslations("files/verb_translations_en.json")
        spanishTranslations = loadTranslations("files/verb_translations_es.json")
        isInitialized = true
    }

    @OptIn(ExperimentalResourceApi::class)
    private suspend fun loadTranslations(path: String): Map<TranslationKey, String> {
        val bytes = Res.readBytes(path)
        val jsonString = bytes.decodeToString()
        val file = json.decodeFromString<TranslationFile>(jsonString)
        return file.translations.associate { entry ->
            TranslationKey(
                verbRoot = entry.verbRoot,
                tense = Tense.valueOf(entry.tense),
                person = Person.valueOf(entry.person),
                plurality = Plurality.valueOf(entry.plurality),
                inclusive = entry.inclusive,
            ) to entry.translation
        }
    }

    /**
     * Get the English translation for a conjugation.
     * @return The English translation string, or null if not found.
     */
    fun getEnglishTranslation(conjugation: Conjugation): String? {
        return englishTranslations[conjugation.toTranslationKey()]
    }

    /**
     * Get the Spanish translation for a conjugation.
     * @return The Spanish translation string, or null if not found.
     */
    fun getSpanishTranslation(conjugation: Conjugation): String? {
        return spanishTranslations[conjugation.toTranslationKey()]
    }
}

/**
 * Convert a [Conjugation] to a [TranslationKey] for lookup in translation tables.
 */
fun Conjugation.toTranslationKey() = TranslationKey(
    verbRoot = verb.root,
    tense = tense,
    person = person,
    plurality = plurality,
    inclusive = inclusive,
)
