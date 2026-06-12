package com.cramsan.edifikana.client.lib.service

import com.cramsan.framework.annotations.FrontendService
import com.cramsan.framework.core.CoreUri

/**
 * Service for managing storage.
 */
@FrontendService
interface StorageService {
    /**
     * Upload a file to [bucketId] using [filename] as the leaf name.
     * [resourceType] selects the upload endpoint and determines the backend RBAC check.
     * [resourceId] identifies the domain resource that owns this asset (e.g. propertyId).
     * The backend constructs the canonical storage path server-side.
     */
    suspend fun uploadFile(
        data: ByteArray,
        filename: String,
        bucketId: String,
        resourceType: String,
        resourceId: String,
    ): Result<String>

    /**
     * Download a file identified by [targetRef] (the full canonical asset path).
     * The backend derives resource type and authorization scope from the path.
     */
    suspend fun downloadFile(
        targetRef: String,
    ): Result<CoreUri>

    companion object {
        const val RESOURCE_TYPE_PROFILE = "profile"
        const val RESOURCE_TYPE_TIME_CARD = "time-card"
        const val RESOURCE_TYPE_TASK = "task"
        const val RESOURCE_TYPE_EVENT_LOG = "event-log"
        const val RESOURCE_TYPE_PROPERTY = "property"
        const val RESOURCE_TYPE_ORGANIZATION = "organization"
    }
}
