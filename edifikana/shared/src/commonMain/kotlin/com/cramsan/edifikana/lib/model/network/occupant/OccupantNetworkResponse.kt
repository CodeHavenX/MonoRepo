package com.cramsan.edifikana.lib.model.network.occupant

import com.cramsan.edifikana.lib.model.occupant.OccupancyStatus
import com.cramsan.edifikana.lib.model.occupant.OccupantId
import com.cramsan.edifikana.lib.model.occupant.OccupantType
import com.cramsan.edifikana.lib.model.organization.OrganizationId
import com.cramsan.edifikana.lib.model.unit.UnitId
import com.cramsan.edifikana.lib.model.user.UserId
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.ResponseBody
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Network response for a unit occupant record.
 *
 * Date fields (startDate, endDate) are ISO 8601 strings ("YYYY-MM-DD") as returned by PostgREST.
 * Timestamp fields (addedAt) are epoch seconds (Long).
 */
@NetworkModel
@Serializable
data class OccupantNetworkResponse(
    @SerialName("occupant_id") val id: OccupantId,
    @SerialName("unit_id") val unitId: UnitId,
    @SerialName("org_id") val orgId: OrganizationId,
    @SerialName("user_id") val userId: UserId?,
    @SerialName("added_by") val addedBy: UserId?,
    val name: String,
    val email: String?,
    @SerialName("occupant_type") val occupantType: OccupantType,
    @SerialName("is_primary") val isPrimary: Boolean,
    @SerialName("start_date") val startDate: String,
    @SerialName("end_date") val endDate: String?,
    val status: OccupancyStatus,
    @SerialName("added_at") val addedAt: Long,
) : ResponseBody
