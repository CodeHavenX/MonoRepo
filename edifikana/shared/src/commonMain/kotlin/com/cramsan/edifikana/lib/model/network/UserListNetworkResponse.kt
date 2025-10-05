package com.cramsan.edifikana.lib.model.network

import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.ResponseBody
import kotlinx.serialization.Serializable

/**
 * Response model for a user.
 */
@NetworkModel
@Serializable
data class UserListNetworkResponse(
    val content: List<UserNetworkResponse>,
) : ResponseBody
