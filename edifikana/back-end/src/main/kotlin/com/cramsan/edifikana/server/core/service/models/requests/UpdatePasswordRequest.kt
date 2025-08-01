package com.cramsan.edifikana.server.core.service.models.requests

import com.cramsan.edifikana.lib.model.UserId
import com.cramsan.framework.core.SecureString
import com.cramsan.framework.core.SecureStringAccess

/**
 * Request to update a user's password.
 *
 */
@OptIn(SecureStringAccess::class)
data class UpdatePasswordRequest(
    val id: UserId,
    val currentHashedPassword: SecureString?,
    val newPassword: SecureString,
)
