package com.cramsan.edifikana.api

import com.cramsan.edifikana.lib.model.network.asset.AssetNetworkResponse
import com.cramsan.edifikana.lib.model.network.asset.CreateSignedUploadQueryParams
import com.cramsan.edifikana.lib.model.network.asset.GetSignedDownloadQueryParams
import com.cramsan.edifikana.lib.model.network.asset.SignedUploadUrlNetworkResponse
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.NoPathParam
import com.cramsan.framework.annotations.api.NoRequestBody
import com.cramsan.framework.networkapi.Api
import io.ktor.http.HttpMethod

/**
 * API definition for storage related operations.
 */

object StorageApi : Api("storage") {
    val getSignedDownload =
        operation<
            NoRequestBody,
            GetSignedDownloadQueryParams,
            NoPathParam,
            AssetNetworkResponse,
            >(HttpMethod.Get, "signed-download")

    val createSignedUpload =
        operation<
            NoRequestBody,
            CreateSignedUploadQueryParams,
            NoPathParam,
            SignedUploadUrlNetworkResponse,
            >(HttpMethod.Post, "signed-upload")
}
