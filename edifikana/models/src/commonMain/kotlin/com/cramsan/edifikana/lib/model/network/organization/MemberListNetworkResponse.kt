package com.cramsan.edifikana.lib.model.network.organization

import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.ResponseBody
import io.ktor.openapi.JsonSchema
import kotlinx.serialization.Serializable

/**
 * Response model for a list of organization members.
 */
@NetworkModel
@Serializable
@JsonSchema.Description("A list of organization members.")
data class MemberListNetworkResponse(
    @JsonSchema.Description("The members matching the request.")
    val content: List<MemberNetworkResponse>,
) : ResponseBody
