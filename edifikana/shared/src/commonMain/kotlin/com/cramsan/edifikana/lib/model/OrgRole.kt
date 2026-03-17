package com.cramsan.edifikana.lib.model

/**
 * Domain model representing the role of a user within an organization.
 *
 * These values map directly to the DB `role` column in `user_organization_mapping`
 * and are constrained to org-level roles only. Application-level privilege roles
 * (e.g., SUPERUSER) are handled separately by the back-end RBAC layer.
 *
 * Note: [RESIDENT]-role users are NOT stored in `user_organization_mapping`.
 * Residents are linked to units via `unit_occupants`.
 *
 * - [OWNER]: Full control over the organization, including transferring ownership.
 * - [ADMIN]: Administrative access; can manage members, properties, and settings.
 * - [MANAGER]: Elevated access for property/team management; cannot manage org-level settings.
 * - [EMPLOYEE]: Standard org member with read access and limited write access.
 */
enum class OrgRole {
    OWNER,
    ADMIN,
    MANAGER,
    EMPLOYEE,
}
