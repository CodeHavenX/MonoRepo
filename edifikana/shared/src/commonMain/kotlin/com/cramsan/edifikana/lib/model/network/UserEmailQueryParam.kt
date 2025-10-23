package com.cramsan.edifikana.lib.model.network

import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.QueryParam
import kotlinx.serialization.Serializable

/**
 * Query param model for user email.
 */
@NetworkModel
@Serializable
data class UserEmailQueryParam(
    val email: String,
) : QueryParam
