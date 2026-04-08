package com.cramsan.edifikana.api

import com.cramsan.edifikana.lib.model.network.asset.AssetNetworkResponse
import com.cramsan.edifikana.lib.model.network.asset.CreateAssetQueryParams
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.BytesRequestBody
import com.cramsan.framework.annotations.api.NoPathParam
import com.cramsan.framework.networkapi.Api
import io.ktor.http.HttpMethod

/**
 * API definition for storage related operations.
 */
@OptIn(NetworkModel::class)
object StorageApi : Api("storage") {

    val createAsset = operation<
        BytesRequestBody,
        CreateAssetQueryParams,
        NoPathParam,
        AssetNetworkResponse
        >(HttpMethod.Post)
}
