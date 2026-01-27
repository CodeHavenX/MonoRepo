package com.cramsan.runasimi.client.lib.features.main.verbs

import com.cramsan.framework.core.compose.ViewModelUIState
import com.cramsan.runasimi.client.lib.manager.Content

/**
 * UI state of the Verbs feature.
 *
 * This class models the top level state of the page.
 * For modeling more specific details of the page, see the respective UI model class.
 */
data class VerbsUIState(val content: Content? = null) : ViewModelUIState {
    companion object {
        val Initial = VerbsUIState(
            content = null,
        )
    }
}
