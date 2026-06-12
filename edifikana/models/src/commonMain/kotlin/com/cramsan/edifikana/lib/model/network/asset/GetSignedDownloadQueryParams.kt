package com.cramsan.edifikana.lib.model.network.asset

import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.QueryParam
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Query parameters for retrieving a signed download URL for an asset.
 */
@NetworkModel
@Serializable
data class GetSignedDownloadQueryParams(@SerialName("asset_id") val assetId: String) : QueryParam
