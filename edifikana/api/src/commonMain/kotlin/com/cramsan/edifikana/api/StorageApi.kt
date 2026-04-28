package com.cramsan.edifikana.api

import com.cramsan.edifikana.lib.model.network.asset.AssetNetworkResponse
import com.cramsan.edifikana.lib.model.network.asset.CreateAssetQueryParams
import com.cramsan.edifikana.lib.model.network.asset.CreateSignedUploadQueryParams
import com.cramsan.edifikana.lib.model.network.asset.GetAssetQueryParams
import com.cramsan.edifikana.lib.model.network.asset.SignedUploadUrlNetworkResponse
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.BytesRequestBody
import com.cramsan.framework.annotations.api.NoPathParam
import com.cramsan.framework.annotations.api.NoRequestBody
import com.cramsan.framework.networkapi.Api
import io.ktor.http.HttpMethod

/**
 * API definition for storage related operations.
 */

object StorageApi : Api("storage") {
    val createAsset =
        operation<
            BytesRequestBody,
            CreateAssetQueryParams,
            NoPathParam,
            AssetNetworkResponse,
            >(HttpMethod.Post)

    val getAsset =
        operation<
            NoRequestBody,
            GetAssetQueryParams,
            NoPathParam,
            AssetNetworkResponse,
            >(HttpMethod.Get, "asset")

    val createSignedUpload =
        operation<
            NoRequestBody,
            CreateSignedUploadQueryParams,
            NoPathParam,
            SignedUploadUrlNetworkResponse,
            >(HttpMethod.Post, "signed-upload")
}
