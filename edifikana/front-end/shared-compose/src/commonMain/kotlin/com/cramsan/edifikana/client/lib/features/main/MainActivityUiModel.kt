@file:Suppress("Filename")

package com.cramsan.edifikana.client.lib.features.main

import androidx.compose.ui.graphics.vector.ImageVector
import org.jetbrains.compose.resources.StringResource

data class BottomBarDestinationUiModel(
    val route: String,
    val icon: ImageVector,
    val text: StringResource,
    val isStartDestination: Boolean = false,
)
