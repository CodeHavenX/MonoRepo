package com.cramsan.edifikana.lib.model

/**
 * Domain model representing the role assigned by an invite.
 *
 * Unlike [OrgRole], which represents membership roles stored in `user_organization_mapping`,
 * [InviteRole] covers all roles that can be invited — including [RESIDENT], which results
 * in a `unit_occupants` row rather than an org membership row.
 *
 * - [ADMIN]: Invited as an organizational administrator.
 * - [MANAGER]: Invited as a property/team manager.
 * - [EMPLOYEE]: Invited as a standard org member.
 * - [RESIDENT]: Invited as a unit resident; does NOT create a `user_organization_mapping` row.
 */
enum class InviteRole {
    ADMIN,
    MANAGER,
    EMPLOYEE,
    RESIDENT,
}
