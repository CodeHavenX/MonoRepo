package com.cramsan.edifikana.server.service.models

import com.cramsan.edifikana.lib.model.asset.AssetId

/**
 * Domain model representing a file [Asset].
 */
data class Asset(val id: AssetId, val fileName: String, val signedUrl: String?)
