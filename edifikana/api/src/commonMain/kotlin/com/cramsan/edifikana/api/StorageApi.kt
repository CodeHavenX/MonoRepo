package com.cramsan.edifikana.api

import com.cramsan.edifikana.lib.model.network.asset.AssetNetworkResponse
import com.cramsan.edifikana.lib.model.network.asset.CreateSignedUploadQueryParams
import com.cramsan.edifikana.lib.model.network.asset.GetSignedDownloadQueryParams
import com.cramsan.edifikana.lib.model.network.asset.SignedUploadUrlNetworkResponse
import com.cramsan.framework.annotations.api.NoPathParam
import com.cramsan.framework.annotations.api.NoRequestBody
import com.cramsan.framework.networkapi.Api
import io.ktor.http.HttpMethod

/**
 * API definition for storage related operations.
 */

object StorageApi : Api("storage") {
    val getSignedDownload = operation<
        NoRequestBody,
        GetSignedDownloadQueryParams,
        NoPathParam,
        AssetNetworkResponse,
    >(HttpMethod.Get, "signed-download")

    val createProfileSignedUpload = operation<
        NoRequestBody,
        CreateSignedUploadQueryParams,
        NoPathParam,
        SignedUploadUrlNetworkResponse,
    >(HttpMethod.Post, "profile/signed-upload")

    val createTimeCardSignedUpload = operation<
        NoRequestBody,
        CreateSignedUploadQueryParams,
        NoPathParam,
        SignedUploadUrlNetworkResponse,
    >(HttpMethod.Post, "time-card/signed-upload")

    val createTaskSignedUpload = operation<
        NoRequestBody,
        CreateSignedUploadQueryParams,
        NoPathParam,
        SignedUploadUrlNetworkResponse,
    >(HttpMethod.Post, "task/signed-upload")

    val createEventLogSignedUpload = operation<
        NoRequestBody,
        CreateSignedUploadQueryParams,
        NoPathParam,
        SignedUploadUrlNetworkResponse,
    >(HttpMethod.Post, "event-log/signed-upload")

    val createPropertySignedUpload = operation<
        NoRequestBody,
        CreateSignedUploadQueryParams,
        NoPathParam,
        SignedUploadUrlNetworkResponse,
    >(HttpMethod.Post, "property/signed-upload")

    val createOrganizationSignedUpload = operation<
        NoRequestBody,
        CreateSignedUploadQueryParams,
        NoPathParam,
        SignedUploadUrlNetworkResponse,
    >(HttpMethod.Post, "organization/signed-upload")
}
