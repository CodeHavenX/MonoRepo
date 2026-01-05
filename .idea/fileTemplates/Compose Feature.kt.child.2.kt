package ${PACKAGE_NAME}.${Package_Name}

import com.cramsan.framework.core.compose.ViewModelUIState

/**
 * UI state of the ${Feature_Name} feature.
 *
 * This class models the top level state of the page.
 * For modeling more specific details of the page, see the respective UI model class.
 */
data class ${Feature_Name}UIState(
    val title: String?,
    val isLoading: Boolean,
) : ViewModelUIState {
    companion object {
        val Initial = ${Feature_Name}UIState(
            title = null,
            isLoading = true,
        )
    }
}
