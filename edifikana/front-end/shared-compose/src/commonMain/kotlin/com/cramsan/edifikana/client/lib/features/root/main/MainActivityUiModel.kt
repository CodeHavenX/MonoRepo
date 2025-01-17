@file:Suppress("Filename")

package com.cramsan.edifikana.client.lib.features.root.main

import com.cramsan.edifikana.client.lib.models.PropertyModel
import com.cramsan.edifikana.lib.model.PropertyId
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

/**
 * UI model for the main activity.
 */
data class MainActivityUiModel(
    val label: String,
    val availableProperties: List<PropertyUiModel>,
)

/**
 * UI model for a single property.
 */
data class PropertyUiModel(
    val propertyId: PropertyId,
    val name: String,
    val selected: Boolean,
)

/**
 * Convert a property model to a UI model.
 */
fun PropertyModel.toUIModel(selected: Boolean = false): PropertyUiModel {
    return PropertyUiModel(
        propertyId = id,
        name = name,
        selected = selected,
    )
}
