package com.cramsan.edifikana.client.lib.models

import com.cramsan.edifikana.lib.model.InviteId

/**
 * Client-side model representing an invite.
 */
data class Invite(
    val id: InviteId,
    val email: String,
)
