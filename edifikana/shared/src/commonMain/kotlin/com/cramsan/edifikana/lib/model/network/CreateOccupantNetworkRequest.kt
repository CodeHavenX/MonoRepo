package com.cramsan.edifikana.lib.model.network

import com.cramsan.edifikana.lib.model.OccupantType
import com.cramsan.edifikana.lib.model.OrganizationId
import com.cramsan.edifikana.lib.model.UnitId
import com.cramsan.edifikana.lib.model.UserId
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.RequestBody
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Network request to create a new unit occupant record.
 *
 * Date fields (startDate, endDate) are ISO 8601 strings ("YYYY-MM-DD").
 */
@NetworkModel
@Serializable
data class CreateOccupantNetworkRequest(
    @SerialName("unit_id") val unitId: UnitId,
    @SerialName("org_id") val orgId: OrganizationId,
    @SerialName("user_id") val userId: UserId?,
    @SerialName("occupant_type") val occupantType: OccupantType,
    @SerialName("is_primary") val isPrimary: Boolean,
    @SerialName("start_date") val startDate: String,
    @SerialName("end_date") val endDate: String?,
) : RequestBody
