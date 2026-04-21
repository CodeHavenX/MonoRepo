package com.cramsan.flyerboard.server.service.models

import com.cramsan.flyerboard.lib.model.FlyerId
import com.cramsan.flyerboard.lib.model.FlyerStatus
import com.cramsan.flyerboard.lib.model.UserId
import kotlin.time.Instant

/**
 * Domain model representing a flyer.
 *
 * [fileUrl] is populated by the service layer with a short-lived signed URL for accessing the
 * stored file. It is null when the model is returned from the datastore directly.
 */
data class Flyer(
    val id: FlyerId,
    val title: String,
    val description: String,
    val filePath: String,
    val status: FlyerStatus,
    val expiresAt: Instant?,
    val uploaderId: UserId,
    val createdAt: Instant,
    val updatedAt: Instant,
    val fileUrl: String? = null,
)
