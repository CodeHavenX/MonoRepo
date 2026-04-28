package com.cramsan.edifikana.client.lib.service.impl

import com.cramsan.edifikana.api.StorageApi
import com.cramsan.edifikana.client.lib.service.DownloadStrategy
import com.cramsan.edifikana.client.lib.service.StorageService
import com.cramsan.edifikana.lib.model.network.asset.CreateSignedUploadQueryParams
import com.cramsan.edifikana.lib.model.network.asset.GetAssetQueryParams
import com.cramsan.framework.annotations.FrontendService
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
 */
@FrontendService
class StorageServiceImpl(
    private val http: HttpClient,
    private val downloadStrategy: DownloadStrategy,
) : StorageService {

    override suspend fun uploadFile(data: ByteArray, targetRef: String, bucketId: String): Result<String> =
        runSuspendCatching(TAG) {
            val response = StorageApi.createSignedUpload
                .buildRequest(queryParam = CreateSignedUploadQueryParams(filename = targetRef, bucketId = bucketId))
                .execute(http)
            // Get response and upload to that url
            http.put(response.signedUrl) {
                setBody(data)
                contentType(ContentType.Application.OctetStream)
            }
            targetRef
        }

    override suspend fun downloadFile(targetRef: String): Result<CoreUri> =
        runSuspendCatching(TAG) {
            if (downloadStrategy.isFileCached(targetRef)) {
                return@runSuspendCatching downloadStrategy.getCachedFile(targetRef)
            }
            val response = StorageApi.getAsset
                .buildRequest(queryParam = GetAssetQueryParams(assetId = targetRef))
                .execute(http)
            val signedUrl = checkNotNull(response.signedUrl) { "No signed URL returned for $targetRef" }
            val bytes = http.get(signedUrl).body<ByteArray>()
            downloadStrategy.saveToFile(bytes, targetRef)
        }

    companion object {
        private const val TAG = "StorageServiceImpl"
    }
}
