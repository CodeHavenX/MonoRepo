package com.cramsan.edifikana.client.lib.settings

import com.cramsan.architecture.client.manager.PreferencesManager
import com.cramsan.edifikana.lib.model.OrganizationId
import com.cramsan.edifikana.lib.model.PropertyId

/**
 * Utility functions to get and set specific preferences related to Edifikana.
 */
suspend fun PreferencesManager.getLastSelectedOrganizationId(): OrganizationId? =
    this.getStringPreference(EdifikanaSettingKey.lastSelectedOrganization).getOrNull()?.let {
        OrganizationId(it)
    }

/**
 * Set the last selected organization ID in preferences.
 * If [organizationId] is null, the preference will be removed.
 */
suspend fun PreferencesManager.setLastSelectedOrganizationId(organizationId: OrganizationId?) {
    val valueToStore = organizationId?.id
    if (valueToStore == null) {
        this.removePreference(EdifikanaSettingKey.lastSelectedOrganization)
    } else {
        this.updatePreference(EdifikanaSettingKey.lastSelectedOrganization, valueToStore)
    }
}

/**
 * Get the last selected property ID from preferences.
 */
suspend fun PreferencesManager.getLastSelectedPropertyId(): PropertyId? =
    this.getStringPreference(EdifikanaSettingKey.lastSelectedProperty).getOrNull()?.let {
        PropertyId(it)
    }

/**
 * Set the last selected property ID in preferences.
 * If [propertyId] is null, the preference will be removed.
 */
suspend fun PreferencesManager.setLastSelectedPropertyId(propertyId: PropertyId?) {
    val valueToStore = propertyId?.propertyId
    if (valueToStore == null) {
        this.removePreference(EdifikanaSettingKey.lastSelectedProperty)
    } else {
        this.updatePreference(EdifikanaSettingKey.lastSelectedProperty, valueToStore)
    }
}
