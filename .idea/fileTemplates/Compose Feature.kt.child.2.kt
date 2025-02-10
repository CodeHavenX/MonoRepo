package ${PACKAGE_NAME}

import com.cramsan.framework.core.compose.ViewModelUIState

/**
 * UI state of the ${NAME} feature.
 *
 * This class models the top level state of the page.
 * For modeling more specific details of the page, see the respective UI model class.
 */
data class ${NAME}UIState(
    val isLoading: Boolean,
) : ViewModelUIState {
    companion object {
        val Initial = ${NAME}UIState(true)
    }
}
