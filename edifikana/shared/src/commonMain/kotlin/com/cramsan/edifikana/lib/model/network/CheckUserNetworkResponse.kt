package com.cramsan.edifikana.lib.model.network

import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.ResponseBody
import kotlinx.serialization.Serializable

/**
 * Response model for checking users are registered in our system.
 */
@NetworkModel
@Serializable
data class CheckUserNetworkResponse(
    val isUserRegistered: Boolean,
) : ResponseBody
