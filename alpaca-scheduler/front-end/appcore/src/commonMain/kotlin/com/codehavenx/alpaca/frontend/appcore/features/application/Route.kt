@file:Suppress("TooManyFunctions")
@file:OptIn(RouteUnsafePath::class)

package com.codehavenx.alpaca.frontend.appcore.features.application

/**
 * This file contains the routes of the application.
 */
enum class Route(
    @RouteUnsafePath
    val route: String,
) {
    // Here we will list the enums that represent a route in our application
    // These routes should not be instantiated directly, but rather you should use the functions that return them
    MAIN_MENU("/"),
    ;
    companion object {
        // Here we will have the functions that return the routes
        /**
         * Returns the main menu route.
         */
        fun mainMenu(): String = MAIN_MENU.route
    }
}
