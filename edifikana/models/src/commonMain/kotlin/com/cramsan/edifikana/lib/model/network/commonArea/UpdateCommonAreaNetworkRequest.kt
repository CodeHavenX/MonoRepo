package com.cramsan.edifikana.lib.model.network.commonArea

import com.cramsan.edifikana.lib.model.commonArea.CommonAreaType
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.RequestBody
import io.ktor.openapi.JsonSchema
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Request to update an existing common area.
 */
@NetworkModel
@Serializable
@JsonSchema.Description(
    "Request payload to update an existing common area. Only provided (non-null) fields are updated.",
)
data class UpdateCommonAreaNetworkRequest(
    @SerialName("name")
    @JsonSchema.Description("New display name for the common area, or null to leave unchanged.")
    val name: String?,
    @SerialName("type")
    @JsonSchema.Description("New type for the common area, or null to leave unchanged.")
    val type: CommonAreaType?,
    @SerialName("description")
    @JsonSchema.Description("New description for the common area, or null to leave unchanged.")
    val description: String?,
) : RequestBody
