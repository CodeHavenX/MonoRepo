@file:Suppress("Filename")

package com.cramsan.edifikana.client.lib.features.root.main

import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource

/**
 * UI model for the bottom bar.
 */
data class BottomBarDestinationUiModel(
    val destination: MainRouteDestination,
    val icon: DrawableResource,
    val text: StringResource,
    val isStartDestination: Boolean = false,
)
