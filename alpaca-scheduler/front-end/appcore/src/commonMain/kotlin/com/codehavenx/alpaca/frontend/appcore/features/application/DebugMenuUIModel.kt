package com.codehavenx.alpaca.frontend.appcore.features.application

/**
 * The UI model for the application.
 */
sealed class ApplicationUIModel {

    /**
     * The user is signed in.
     */
    data class SignedIn(
        val navBar: List<NavBarSegment>,
    ) : ApplicationUIModel()

    /**
     * The user is signed out.
     */
    data object SignedOut : ApplicationUIModel()
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
