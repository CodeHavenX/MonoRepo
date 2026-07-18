package com.cramsan.edifikana.lib.model.organization

import io.ktor.openapi.JsonSchema
import kotlinx.serialization.Serializable

/**
 * Domain model representing the membership status of a user within an organization.
 *
 * - [ACTIVE]: The user is an active, confirmed member of the organization.
 * - [INACTIVE]: The user's membership has been deactivated or revoked.
 * - [PENDING]: An invite has been sent but not yet accepted by the user.
 */
@Serializable
@JsonSchema.Description("Membership status of a user within an organization.")
enum class OrgMemberStatus {
    ACTIVE,
    INACTIVE,
    PENDING,
}
