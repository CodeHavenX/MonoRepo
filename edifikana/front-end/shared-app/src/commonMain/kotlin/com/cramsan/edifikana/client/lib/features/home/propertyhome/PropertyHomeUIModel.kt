package com.cramsan.edifikana.client.lib.features.home.propertyhome

import com.cramsan.edifikana.client.lib.models.PropertyModel
import com.cramsan.edifikana.lib.model.OrganizationId
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.framework.core.compose.ViewModelUIState
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource

/**
 * Home UI model. You can use one or multiple models to represent
 * specific sections and component of the UI.
 */
data class PropertyHomeUIModel(
    val label: String,
    val availableProperties: List<PropertyUiModel>,
    val selectedTab: Tabs,
    val propertyId: PropertyId?,
    val orgId: OrganizationId?,
) : ViewModelUIState {
    companion object {
        val Empty = PropertyHomeUIModel(
            label = "",
            selectedTab = Tabs.None,
            availableProperties = emptyList(),
            propertyId = null,
            orgId = null,
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
    GoToOrganization,
    EventLog,
}

/**
 * UI model for a single property.
 */
data class PropertyUiModel(val propertyId: PropertyId, val name: String, val selected: Boolean)

/**
 * Convert a property model to a UI model.
 */
fun PropertyModel.toUIModel(selected: Boolean = false): PropertyUiModel = PropertyUiModel(
    propertyId = id,
    name = name,
    selected = selected,
)
