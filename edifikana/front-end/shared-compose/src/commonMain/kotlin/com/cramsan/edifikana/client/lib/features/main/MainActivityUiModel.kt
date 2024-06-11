@file:Suppress("Filename")

package com.cramsan.edifikana.client.lib.features.main

import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource

data class BottomBarDestinationUiModel(
    val route: String,
    val icon: DrawableResource,
    val text: StringResource,
    val isStartDestination: Boolean = false,
)
