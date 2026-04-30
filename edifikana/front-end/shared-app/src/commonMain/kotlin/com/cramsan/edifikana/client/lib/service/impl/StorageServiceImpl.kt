package com.cramsan.edifikana.client.lib.service.impl

import com.cramsan.edifikana.api.StorageApi
import com.cramsan.edifikana.client.lib.service.DownloadStrategy
import com.cramsan.edifikana.client.lib.service.StorageService
import com.cramsan.edifikana.lib.model.network.asset.CreateSignedUploadQueryParams
import com.cramsan.edifikana.lib.model.network.asset.GetSignedDownloadQueryParams
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.core.CoreUri
import com.cramsan.framework.core.runSuspendCatching
import com.cramsan.framework.networkapi.buildRequest
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

/**
 * Backend-routed implementation of [StorageService]. All storage access goes through the Ktor
 * backend, which holds the service-role key and enforces org-scoped auth.
 *
 * [http] is the authenticated client (carries the user JWT) used only for calls to the Ktor
 * backend. [rawHttp] is a plain client with no auth headers; it is used for the direct PUT/GET
 * to Supabase signed URLs, which embed their own auth token in the query string and must not
 * receive the user JWT (leakage risk; can also conflict with the signed token).
 */
@OptIn(NetworkModel::class)
class StorageServiceImpl(
    private val http: HttpClient,
    private val rawHttp: HttpClient,
    private val downloadStrategy: DownloadStrategy,
) : StorageService {
    override suspend fun uploadFile(data: ByteArray, targetRef: String, bucketId: String): Result<String> =
        runSuspendCatching(TAG) {
            val response =
                StorageApi.createSignedUpload
                    .buildRequest(queryParam = CreateSignedUploadQueryParams(filename = targetRef, bucketId = bucketId))
                    .execute(http)
            rawHttp.put(response.signedUrl) {
                setBody(data)
                contentType(ContentType.Application.OctetStream)
            }
            response.assetId
        }

    override suspend fun downloadFile(targetRef: String): Result<CoreUri> =
        runSuspendCatching(TAG) {
            if (downloadStrategy.isFileCached(targetRef)) {
                return@runSuspendCatching downloadStrategy.getCachedFile(targetRef)
            }
            val response =
                StorageApi.getSignedDownload
                    .buildRequest(queryParam = GetSignedDownloadQueryParams(assetId = targetRef))
                    .execute(http)
            val signedUrl = checkNotNull(response.signedUrl) { "No signed URL returned for $targetRef" }
            val bytes = rawHttp.get(signedUrl).body<ByteArray>()
            downloadStrategy.saveToFile(bytes, targetRef)
        }

    companion object {
        private const val TAG = "StorageServiceImpl"
    }
}
