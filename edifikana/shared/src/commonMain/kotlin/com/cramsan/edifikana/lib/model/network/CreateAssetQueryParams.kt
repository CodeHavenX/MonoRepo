package com.cramsan.edifikana.lib.model.network

import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.QueryParam
import kotlinx.serialization.Serializable

/**
 * Query parameters for creating an asset.
 */
@NetworkModel
@Serializable
data class CreateAssetQueryParams(
    val filename: String,
) : QueryParam
