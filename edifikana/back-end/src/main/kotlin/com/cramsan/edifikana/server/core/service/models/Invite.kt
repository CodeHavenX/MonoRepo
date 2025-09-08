package com.cramsan.edifikana.server.core.service.models

import com.cramsan.edifikana.lib.model.InviteId
import com.cramsan.edifikana.lib.model.OrganizationId

data class Invite(
    val inviteId: InviteId,
    val email: String,
    val organizationId: OrganizationId,
)
