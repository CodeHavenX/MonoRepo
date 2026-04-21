package com.cramsan.flyerboard.client.lib.models

import com.cramsan.flyerboard.lib.model.FlyerId
import com.cramsan.flyerboard.lib.model.FlyerStatus
import com.cramsan.flyerboard.lib.model.UserId

/**
 * UI-friendly model representing a single flyer.
 */
data class FlyerModel(
    val id: FlyerId,
    val title: String,
    val description: String,
    val fileUrl: String?,
    val status: FlyerStatus,
    val expiresAt: String?,
    val uploaderId: UserId,
    val createdAt: String,
    val updatedAt: String,
)

/**
 * A paginated list of [FlyerModel] items with total-count metadata.
 */
data class PaginatedFlyerModel(
    val flyers: List<FlyerModel>,
    val total: Int,
    val offset: Int,
    val limit: Int,
)
