package com.cramsan.edifikana.client.lib.features.root.admin.properties

import com.cramsan.edifikana.lib.model.PropertyId

/**
 * PropertyManager UI model. You can use one or multiple models to represent
 * specific sections and component of the UI.
 */
data class PropertyManagerUIModel(
    val properties: List<PropertyUIModel>,
)

/**
 * Property UI model.
 */
data class PropertyUIModel(
    val id: PropertyId,
    val name: String,
    val address: String,
)
