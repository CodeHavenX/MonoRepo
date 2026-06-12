package com.cramsan.edifikana.lib.model.organization

/**
 * Domain model representing the membership status of a user within an organization.
 *
 * - [ACTIVE]: The user is an active, confirmed member of the organization.
 * - [INACTIVE]: The user's membership has been deactivated or revoked.
 * - [PENDING]: An invite has been sent but not yet accepted by the user.
 */
enum class OrgMemberStatus {
    ACTIVE,
    INACTIVE,
    PENDING,
}
