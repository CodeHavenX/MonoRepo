package com.cramsan.edifikana.lib.model.network.asset

import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.QueryParam
import io.ktor.openapi.JsonSchema
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Query parameters for retrieving a signed download URL for an asset.
 */
@NetworkModel
@Serializable
@JsonSchema.Description("Query parameters for retrieving a signed download URL for an asset.")
data class GetSignedDownloadQueryParams(
    @SerialName("asset_id")
    @JsonSchema.Description("Identifier of the asset to download.")
    val assetId: String,
) : QueryParam
