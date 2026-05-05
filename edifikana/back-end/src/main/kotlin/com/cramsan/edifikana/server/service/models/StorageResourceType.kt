package com.cramsan.edifikana.server.service.models

/**
 * Identifies the domain resource a storage asset belongs to.
 * Backend-only: determines the RBAC strategy and canonical storage path for each asset type.
 * Never sent to or accepted from clients — the resource type is always derived server-side
 * from the asset path or from the upload endpoint being called.
 */
enum class StorageResourceType(val pathPrefix: String) {
    /** User profile picture. Upload: self only. Download: any authenticated user. */
    PROFILE("private/profiles"),

    /** Time card photo. Resource ID is a PropertyId. Upload/Download: EMPLOYEE+ in property's org. */
    TIME_CARD("private/time_cards"),

    /** Task attachment. Resource ID is a PropertyId. Upload/Download: EMPLOYEE+ in property's org. */
    TASK("private/tasks"),

    /** Event log attachment. Resource ID is a PropertyId. Upload/Download: EMPLOYEE+ in property's org. */
    EVENT_LOG("private/event_logs"),

    /** Property image. Resource ID is a PropertyId. Upload: MANAGER+. Download: MANAGER+ in property's org. */
    PROPERTY("private/properties"),

    /** Org-level asset. Resource ID is an OrganizationId. Upload: ADMIN+. Download: ADMIN+ in the organization. */
    ORGANIZATION("private/organizations");

    fun buildPath(resourceId: String, filename: String): String = "$pathPrefix/$resourceId/$filename"

    companion object {
        /**
         * Derives resource type and resource ID from a canonical asset path.
         * Returns null if the path does not match any known prefix convention.
         */
        fun fromPath(assetId: String): Pair<StorageResourceType, String>? {
            val type = entries.firstOrNull { assetId.startsWith("${it.pathPrefix}/") } ?: return null
            val remainder = assetId.removePrefix("${type.pathPrefix}/")
            val resourceId = remainder.substringBefore("/")
            if (resourceId.isEmpty() || resourceId == remainder) return null
            return type to resourceId
        }
    }
}
