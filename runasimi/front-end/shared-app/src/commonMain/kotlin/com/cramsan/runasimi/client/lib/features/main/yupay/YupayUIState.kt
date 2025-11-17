package com.cramsan.runasimi.client.lib.features.main.yupay

import com.cramsan.framework.core.compose.ViewModelUIState
import com.cramsan.runasimi.client.lib.manager.Content

/**
 * UI state of the Yupay feature.
 *
 * This class models the top level state of the page.
 * For modeling more specific details of the page, see the respective UI model class.
 */
data class YupayUIState(
    val content: Content? = null,
) : ViewModelUIState {
    companion object {
        val Initial = YupayUIState(
            content = null,
        )
    }
}
