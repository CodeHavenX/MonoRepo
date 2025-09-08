package com.cramsan.edifikana.client.lib.models

import com.cramsan.edifikana.lib.model.InviteId

data class Invite(
    val id: InviteId,
    val email: String,
)
