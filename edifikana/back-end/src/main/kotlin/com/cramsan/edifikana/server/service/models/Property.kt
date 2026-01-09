package com.cramsan.edifikana.server.service.models

import com.cramsan.edifikana.lib.model.OrganizationId
import com.cramsan.edifikana.lib.model.PropertyId

/**
 * Domain model representing a property.
 *
 * @property imageUrl Property image URL following a string-based format convention:
 *   - `"drawable:CASA"` - Casa (House) icon
 *   - `"drawable:QUINTA"` - Quinta icon
 *   - `"drawable:L_DEPA"` - Large Department icon
 *   - `"drawable:M_DEPA"` - Medium Department icon
 *   - `"drawable:S_DEPA"` - Small Department icon
 *   - `"https://..."` - Custom image URL
 *   - `null` - No image
 *
 * TODO(Future Enhancement - Type-Safe Property Images):
 * Consider migrating to a sealed class approach for better type safety:
 * ```
 * sealed class PropertyImage {
 *     data class DefaultIcon(val iconType: PropertyIconType) : PropertyImage()
 *     data class CustomUrl(val url: String) : PropertyImage()
 *     object None : PropertyImage()
 * }
 * ```
 * This would provide compile-time validation and eliminate string parsing errors.
 * The current string format serves as a stable serialization protocol that
 * would be preserved during migration for database/API compatibility.
 */
data class Property(
    val id: PropertyId,
    val name: String,
    val address: String,
    val organizationId: OrganizationId,
    val imageUrl: String? = null,
)
