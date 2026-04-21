package com.cramsan.flyerboard.client.lib.service.impl

import com.cramsan.flyerboard.client.lib.models.FlyerModel
import com.cramsan.flyerboard.client.lib.models.PaginatedFlyerModel
import com.cramsan.flyerboard.lib.model.network.FlyerListNetworkResponse
import com.cramsan.flyerboard.lib.model.network.FlyerNetworkResponse
import com.cramsan.framework.annotations.NetworkModel

/**
 * Maps a [FlyerNetworkResponse] to a [FlyerModel].
 */
@OptIn(NetworkModel::class)
fun FlyerNetworkResponse.toFlyerModel(): FlyerModel = FlyerModel(
    id = id,
    title = title,
    description = description,
    fileUrl = fileUrl,
    status = status,
    expiresAt = expiresAt,
    uploaderId = uploaderId,
    createdAt = createdAt,
    updatedAt = updatedAt,
)

/**
 * Maps a [FlyerListNetworkResponse] to a [PaginatedFlyerModel].
 */
@OptIn(NetworkModel::class)
fun FlyerListNetworkResponse.toPaginatedFlyerModel(): PaginatedFlyerModel = PaginatedFlyerModel(
    flyers = flyers.map { it.toFlyerModel() },
    total = total,
    offset = offset,
    limit = limit,
)
