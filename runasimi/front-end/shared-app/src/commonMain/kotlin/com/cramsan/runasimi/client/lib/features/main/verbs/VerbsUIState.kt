package com.cramsan.runasimi.client.lib.features.main.verbs

import com.cramsan.framework.core.compose.ViewModelUIState
import com.cramsan.runasimi.client.lib.manager.Content

/**
 * UI state of the Verbs feature.
 *
 * This class models the top level state of the page.
 * For modeling more specific details of the page, see the respective UI model class.
 */
data class VerbsUIState(
    val content: Content? = null,
    val showSpanish: Boolean = false,
) : ViewModelUIState {

    /**
     * Returns the translated content based on the current language setting.
     * Falls back to grammatical description if translation is not available.
     */
    val displayedTranslation: String?
        get() = if (showSpanish) {
            content?.spanishTranslation ?: content?.translated
        } else {
            content?.englishTranslation ?: content?.translated
        }

    companion object {
        val Initial = VerbsUIState(
            content = null,
            showSpanish = false,
        )
    }
}
