package com.cramsan.edifikana.lib.model.network.user

import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.QueryParam
import io.ktor.openapi.JsonSchema
import kotlinx.serialization.Serializable

/**
 * Query param model for user email.
 */
@NetworkModel
@Serializable
@JsonSchema.Description("Query parameters carrying an email address to check.")
data class UserEmailQueryParam(
    @JsonSchema.Description("Email address to check.")
    val email: String,
) : QueryParam
