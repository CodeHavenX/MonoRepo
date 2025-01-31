package com.cramsan.edifikana.client.lib.features.root.admin.property

/**
 * UI state of the Property feature.
 *
 * This class models the top level state of the page.
 * For modeling more specific details of the page, see the respective UI model class.
 */
data class PropertyUIState(
    val content: PropertyUIModel?,
    val isLoading: Boolean,
)
