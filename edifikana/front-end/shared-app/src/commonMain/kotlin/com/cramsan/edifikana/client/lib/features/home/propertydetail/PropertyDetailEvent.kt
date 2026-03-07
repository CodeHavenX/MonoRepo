package com.cramsan.edifikana.client.lib.features.home.propertydetail

import com.cramsan.framework.core.compose.ViewModelEvent

/**
 * Events that can be triggered within the domain of the PropertyDetail feature.
 *
 * Events are triggered from a ViewModel and are consumed by the UI.
 *
 */
sealed class PropertyDetailEvent : ViewModelEvent {
    /**
     * Open the image selector to select a new image for the property.
     */
    data object OpenImageSelector : PropertyDetailEvent()
}
