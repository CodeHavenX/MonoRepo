package com.cramsan.edifikana.lib.model.network.asset

import kotlinx.serialization.Serializable

/**
 * Identifies the domain resource a storage asset belongs to.
 * Used by the backend to determine the minimum required role before issuing a signed URL.
 */
@Serializable
enum class StorageResourceType {
    /** User profile picture. Upload: self only. Download: any org member. */
    PROFILE,

    /** Time card photo. Resource ID is a PropertyId. Upload/Download: EMPLOYEE+ in property's org. */
    TIME_CARD,

    /** Task attachment. Resource ID is a TaskId. Upload/Download: EMPLOYEE+ in task's property's org. */
    TASK,

    /** Event log attachment. Resource ID is an EventLogEntryId. Upload/Download: EMPLOYEE+ in property's org. */
    EVENT_LOG,

    /** Property image. Resource ID is a PropertyId. Upload: MANAGER+. Download: any org member. */
    PROPERTY,

    /** Org-level asset. Resource ID is an OrganizationId. Upload: ADMIN+. Download: any org member. */
    ORGANIZATION,
}
