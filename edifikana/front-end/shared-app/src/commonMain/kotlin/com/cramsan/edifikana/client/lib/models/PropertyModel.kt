package com.cramsan.edifikana.client.lib.models

import com.cramsan.edifikana.lib.model.OrganizationId
import com.cramsan.edifikana.lib.model.PropertyId

/**
 * Model to represent a property.
 *
 * @property id Unique identifier for the property.
 * @property name Name of the property.
 * @property address Address of the property.
 * @property organizationId Identifier of the organization the property belongs to.
 * @property imageUrl Property image URL following a string-based format convention.
 * See backend Property model documentation for format details.
 */
data class PropertyModel(
    val id: PropertyId,
    val name: String,
    val address: String,
    val organizationId: OrganizationId,
    val imageUrl: String? = null,
)
