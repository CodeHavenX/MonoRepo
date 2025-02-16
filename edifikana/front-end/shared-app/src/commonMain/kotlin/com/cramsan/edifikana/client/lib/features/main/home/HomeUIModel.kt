package com.cramsan.edifikana.client.lib.features.main.home

import com.cramsan.edifikana.client.lib.models.PropertyModel
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.framework.core.compose.ViewModelUIState
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource

/**
 * Home UI model. You can use one or multiple models to represent
 * specific sections and component of the UI.
 */
data class HomeUIModel(
    val label: String,
    val availableProperties: List<PropertyUiModel>,
    val selectedTab: Tabs,
) : ViewModelUIState {
    companion object {
        val Empty = HomeUIModel(
            label = "",
            selectedTab = Tabs.EventLog,
            availableProperties = emptyList(),
        )
    }
}

/**
 * UI model for the bottom bar.
 */
data class BottomBarDestinationUiModel(
    val destination: Tabs,
    val icon: DrawableResource,
    val text: StringResource,
    val isStartDestination: Boolean = false,
)

/**
 * UI model for the bottom bar.
 */
enum class Tabs {
    None,
    TimeCard,
    EventLog,
}

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
