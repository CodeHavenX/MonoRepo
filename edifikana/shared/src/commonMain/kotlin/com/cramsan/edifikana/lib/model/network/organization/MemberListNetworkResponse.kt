package com.cramsan.edifikana.lib.model.network.organization

import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.ResponseBody
import kotlinx.serialization.Serializable

/**
 * Response model for a list of organization members.
 */
@NetworkModel
@Serializable
data class MemberListNetworkResponse(
    val content: List<MemberNetworkResponse>,
) : ResponseBody
