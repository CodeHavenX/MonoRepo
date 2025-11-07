package com.cramsan.edifikana.client.lib.features.home.organizationhome

import androidx.compose.ui.graphics.vector.ImageVector
import com.cramsan.edifikana.lib.model.OrganizationId
import com.cramsan.framework.core.compose.ViewModelUIState
import org.jetbrains.compose.resources.StringResource

/**
 * Hub UI model. You can use one or multiple models to represent
 * specific sections and component of the UI.
 */
data class OrganizationHomeUIModel(
    val label: String,
    val selectedTab: Tabs,
    val availableOrganizations: List<OrganizationUIModel>,
) : ViewModelUIState {
    companion object {
        val Empty = OrganizationHomeUIModel(
            label = "",
            selectedTab = Tabs.Properties,
            availableOrganizations = listOf(),
        )
    }
}

/**
 * UI model for an organization in the list of available organizations.
 */
data class OrganizationUIModel(
    val id: OrganizationId,
    val name: String,
    val selected: Boolean = false,
)

/**
 * UI model for the bottom bar.
 */
data class BottomBarDestinationUiModel(
    val destination: Tabs,
    val icon: ImageVector,
    val text: StringResource,
    val isStartDestination: Boolean = false,
)

/**
 * UI model for the bottom bar.
 */
enum class Tabs {
    None,
    Properties,
    Employee,
}
