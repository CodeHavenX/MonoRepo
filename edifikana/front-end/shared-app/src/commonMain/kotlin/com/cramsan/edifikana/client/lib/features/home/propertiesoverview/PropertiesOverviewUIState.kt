package com.cramsan.edifikana.client.lib.features.home.propertiesoverview

import com.cramsan.edifikana.client.lib.models.PropertyModel
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.framework.core.compose.ViewModelUIState

/**
 * UI state of the PropertiesOverview feature.
 *
 * This class models the top level state of the page.
 * For modeling more specific details of the page, see the respective UI model class.
 */
data class PropertiesOverviewUIState(val isLoading: Boolean, val propertyList: List<PropertyItemUIModel>) :
    ViewModelUIState {
    companion object {
        val Initial = PropertiesOverviewUIState(
            isLoading = true,
            propertyList = emptyList(),
        )
    }
}

/**
 * UI model to represent a property in the properties list.
 */
data class PropertyItemUIModel(val id: PropertyId, val name: String, val address: String, val imageUrl: String?) {
    companion object {
        /**
         * Create a [PropertyItemUIModel] from a [PropertyModel].
         */
        fun fromDomainModel(property: PropertyModel): PropertyItemUIModel = PropertyItemUIModel(
            id = property.id,
            name = property.name,
            address = property.address,
            imageUrl = null,
        )
    }
}
