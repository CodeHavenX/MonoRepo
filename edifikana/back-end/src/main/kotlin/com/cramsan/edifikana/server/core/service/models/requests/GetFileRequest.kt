package com.cramsan.edifikana.server.core.service.models.requests

import com.cramsan.edifikana.lib.model.AssetId

/**
 * Domain model representing a request to get a file by its ID.
 */
data class GetFileRequest (
    val id: AssetId,
)