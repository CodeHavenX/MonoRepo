package com.cramsan.runasimi.client.lib.features.main.questions

import com.cramsan.framework.core.compose.ViewModelUIState
import com.cramsan.runasimi.client.lib.manager.Content

/**
 * UI state of the Questions feature.
 */
data class QuestionsUIState(
    val content: Content? = null,
) : ViewModelUIState {
    companion object {
        val Initial = QuestionsUIState(
            content = null,
        )
    }
}
