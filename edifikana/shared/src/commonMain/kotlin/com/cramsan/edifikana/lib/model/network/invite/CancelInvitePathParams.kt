package com.cramsan.edifikana.lib.model.network.invite

import com.cramsan.edifikana.lib.model.invite.InviteId
import com.cramsan.edifikana.lib.model.organization.OrganizationId
import com.cramsan.framework.annotations.api.PathParam

/**
 * Path parameters for canceling an invite.
 * The path will be formatted as "{organizationId}/{inviteId}".
 */
data class CancelInvitePathParams(
    val organizationId: OrganizationId,
    val inviteId: InviteId,
) : PathParam {
    override fun toString(): String = "${organizationId.id}/${inviteId.id}"

    companion object {
        /**
         * Parses a path string in the format "organizationId/inviteId" into a CancelInvitePathParams.
         */
        fun fromString(path: String): CancelInvitePathParams {
            val parts = path.split("/")
            require(parts.size == 2) { "Invalid path format. Expected 'organizationId/inviteId', got: $path" }
            return CancelInvitePathParams(
                organizationId = OrganizationId(parts[0]),
                inviteId = InviteId(parts[1]),
            )
        }
    }
}
