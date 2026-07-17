package com.cramsan.edifikana.lib.model.network.user

import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.ResponseBody
import io.ktor.openapi.JsonSchema
import kotlinx.serialization.Serializable

/**
 * Response model for checking users are registered in our system.
 */
@NetworkModel
@Serializable
@JsonSchema.Description("Result of checking whether a user is registered.")
data class CheckUserNetworkResponse(
    @JsonSchema.Description("Whether a user is already registered for the checked email.")
    val isUserRegistered: Boolean,
) : ResponseBody
