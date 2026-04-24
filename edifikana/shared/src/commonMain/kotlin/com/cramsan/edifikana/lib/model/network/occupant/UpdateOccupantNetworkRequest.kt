package com.cramsan.edifikana.lib.model.network.occupant

import com.cramsan.edifikana.lib.model.occupant.OccupancyStatus
import com.cramsan.edifikana.lib.model.occupant.OccupantType
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.RequestBody
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Network request to update an existing unit occupant record. Only provided (non-null) fields are updated.
 *
 * Date fields (endDate) are ISO 8601 strings ("YYYY-MM-DD").
 */
@NetworkModel
@Serializable
data class UpdateOccupantNetworkRequest(
    val name: String?,
    val email: String?,
    @SerialName("occupant_type") val occupantType: OccupantType?,
    @SerialName("is_primary") val isPrimary: Boolean?,
    @SerialName("end_date") val endDate: String?,
    val status: OccupancyStatus?,
) : RequestBody
