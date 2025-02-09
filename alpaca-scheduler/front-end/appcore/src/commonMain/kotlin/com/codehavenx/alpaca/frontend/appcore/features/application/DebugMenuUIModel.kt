package com.codehavenx.alpaca.frontend.appcore.features.application

import com.cramsan.framework.core.compose.ViewModelUIState

/**
 * The UI model for the application.
 */
data class ApplicationUIModelUI(
    val navBar: List<NavBarSegment>,

) : ViewModelUIState {
    companion object {
        val Initial = ApplicationUIModelUI(
            navBar = emptyList(),
        )
    }
}

/**
 * A segment of the navigation bar.
 */
sealed class NavBarSegment {

    /**
     * A navigation bar item.
     */
    data class NavBarItem(
        val name: String,
        val path: String,
    ) : NavBarSegment()

    /**
     * A navigation bar group.
     */
    data class NavBarGroup(
        val name: String,
        val items: List<NavBarItem>,
    ) : NavBarSegment()
}
