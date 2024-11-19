package com.cramsan.edifikana.server.core.service.models.requests

import com.cramsan.edifikana.lib.model.UserId

/**
 * Request to update a user's password.
 *
 * TODO: Lets see if we want to keep this request or if we want to remove it in favor for a more comprehensive approach.
 */
data class UpdatePasswordRequest(
    val id: UserId,
    val password: String,
)
