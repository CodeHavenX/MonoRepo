package com.cramsan.edifikana.client.lib.features.home.appshell

import com.cramsan.framework.core.compose.ViewModelUIState

/**
 * UI state for the App Shell screen.
 *
 * This class models the top-level state of the app shell, including which navigation tab is active.
 */
data class AppShellUIState(val selectedTab: AppShellTab) : ViewModelUIState {
    companion object {
        /**
         * Initial state with Dashboard as the selected tab.
         */
        val Initial =
            AppShellUIState(
                selectedTab = AppShellTab.Dashboard,
            )
    }
}

/**
 * Navigation tabs available in the App Shell.
 */
enum class AppShellTab {
    Dashboard,
    Properties,
    Tasks,
    More,
}
