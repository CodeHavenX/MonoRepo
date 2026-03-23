package com.cramsan.edifikana.lib.model.network

import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.RequestBody
import kotlinx.serialization.Serializable

/**
 * Network request model for requesting a password reset email.
 */
@NetworkModel
@Serializable
data class PasswordResetNetworkRequest(
    val email: String,
) : RequestBody
