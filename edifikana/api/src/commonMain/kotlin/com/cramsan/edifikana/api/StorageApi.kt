package com.cramsan.edifikana.api

import com.cramsan.edifikana.lib.model.network.asset.AssetNetworkResponse
import com.cramsan.edifikana.lib.model.network.asset.CreateSignedUploadQueryParams
import com.cramsan.edifikana.lib.model.network.asset.GetSignedDownloadQueryParams
import com.cramsan.edifikana.lib.model.network.asset.SignedUploadUrlNetworkResponse
import com.cramsan.framework.annotations.api.NoPathParam
import com.cramsan.framework.annotations.api.NoRequestBody
import com.cramsan.framework.networkapi.Api
import com.cramsan.framework.networkapi.UniversalResponsesOnly
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
    >(
        method = HttpMethod.Get,
        path = "signed-download",
        summary = "Get a signed download URL",
        description = "Retrieves a signed URL to download a previously uploaded asset.",
        responses = UniversalResponsesOnly,
    )

    val createProfileSignedUpload = operation<
        NoRequestBody,
        CreateSignedUploadQueryParams,
        NoPathParam,
        SignedUploadUrlNetworkResponse,
    >(
        method = HttpMethod.Post,
        path = "profile/signed-upload",
        summary = "Create a signed upload URL for a profile asset",
        description = "Creates a signed URL the client can use to upload a profile image or file.",
        responses = UniversalResponsesOnly,
    )

    val createTimeCardSignedUpload = operation<
        NoRequestBody,
        CreateSignedUploadQueryParams,
        NoPathParam,
        SignedUploadUrlNetworkResponse,
    >(
        method = HttpMethod.Post,
        path = "time-card/signed-upload",
        summary = "Create a signed upload URL for a time card asset",
        description = "Creates a signed URL the client can use to upload a time card event photo.",
        responses = UniversalResponsesOnly,
    )

    val createTaskSignedUpload = operation<
        NoRequestBody,
        CreateSignedUploadQueryParams,
        NoPathParam,
        SignedUploadUrlNetworkResponse,
    >(
        method = HttpMethod.Post,
        path = "task/signed-upload",
        summary = "Create a signed upload URL for a task asset",
        description = "Creates a signed URL the client can use to upload a file attached to a task.",
        responses = UniversalResponsesOnly,
    )

    val createEventLogSignedUpload = operation<
        NoRequestBody,
        CreateSignedUploadQueryParams,
        NoPathParam,
        SignedUploadUrlNetworkResponse,
    >(
        method = HttpMethod.Post,
        path = "event-log/signed-upload",
        summary = "Create a signed upload URL for an event log asset",
        description = "Creates a signed URL the client can use to upload a file attached to an event log entry.",
        responses = UniversalResponsesOnly,
    )

    val createPropertySignedUpload = operation<
        NoRequestBody,
        CreateSignedUploadQueryParams,
        NoPathParam,
        SignedUploadUrlNetworkResponse,
    >(
        method = HttpMethod.Post,
        path = "property/signed-upload",
        summary = "Create a signed upload URL for a property asset",
        description = "Creates a signed URL the client can use to upload a property's cover image.",
        responses = UniversalResponsesOnly,
    )

    val createOrganizationSignedUpload = operation<
        NoRequestBody,
        CreateSignedUploadQueryParams,
        NoPathParam,
        SignedUploadUrlNetworkResponse,
    >(
        method = HttpMethod.Post,
        path = "organization/signed-upload",
        summary = "Create a signed upload URL for an organization asset",
        description = "Creates a signed URL the client can use to upload an organization's asset.",
        responses = UniversalResponsesOnly,
    )
}
