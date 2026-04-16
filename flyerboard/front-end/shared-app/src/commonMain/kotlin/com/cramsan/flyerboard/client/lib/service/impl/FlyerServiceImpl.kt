package com.cramsan.flyerboard.client.lib.service.impl

import com.cramsan.architecture.client.service.execute
import com.cramsan.flyerboard.api.FlyerApi
import com.cramsan.flyerboard.api.ModerationApi
import com.cramsan.flyerboard.client.lib.models.FlyerModel
import com.cramsan.flyerboard.client.lib.models.PaginatedFlyerModel
import com.cramsan.flyerboard.client.lib.service.AuthService
import com.cramsan.flyerboard.client.lib.service.FlyerService
import com.cramsan.flyerboard.lib.model.FlyerId
import com.cramsan.flyerboard.lib.model.FlyerStatus
import com.cramsan.flyerboard.lib.model.network.FlyerListNetworkResponse
import com.cramsan.flyerboard.lib.model.network.FlyerNetworkResponse
import com.cramsan.flyerboard.lib.model.network.ListFlyersQueryParams
import com.cramsan.flyerboard.lib.model.network.ModerationActionNetworkRequest
import com.cramsan.flyerboard.lib.model.network.PaginationParams
import com.cramsan.flyerboard.lib.serialization.HEADER_TOKEN_AUTH
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.core.runSuspendCatching
import com.cramsan.framework.logging.logW
import com.cramsan.framework.networkapi.buildRequest
import com.cramsan.framework.utils.exceptions.ClientRequestExceptions
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.http.Headers
import io.ktor.http.HeadersBuilder
import io.ktor.http.HttpHeaders

/**
 * Ktor HTTP client implementation of [FlyerService].
 *
 * JSON operations use the framework's typed [execute] helper. Multipart file-upload operations
 * ([createFlyer], [updateFlyer]) use raw Ktor form-data requests because the framework's
 * [BytesRequestBody] path only supports a single binary blob, not keyed form fields.
 */
@OptIn(NetworkModel::class)
class FlyerServiceImpl(
    private val http: HttpClient,
    private val authService: AuthService,
) : FlyerService {

    // ── JSON operations ───────────────────────────────────────────────────────

    override suspend fun listFlyers(
        offset: Int,
        limit: Int,
        status: FlyerStatus?,
        query: String?,
    ): Result<PaginatedFlyerModel> = runSuspendCatching(TAG) {
        FlyerApi.listFlyers.buildRequest(
            ListFlyersQueryParams(offset = offset, limit = limit, status = status, q = query),
        ).execute(http).toPaginatedFlyerModel()
    }

    override suspend fun getFlyer(flyerId: FlyerId): Result<FlyerModel?> = runSuspendCatching(TAG) {
        try {
            FlyerApi.getFlyer.buildRequest(flyerId).execute(http).toFlyerModel()
        } catch (e: ClientRequestExceptions.NotFoundException) {
            logW(TAG, "Flyer not found: ${flyerId.flyerId}", e)
            null
        }
    }

    override suspend fun listArchived(offset: Int, limit: Int): Result<PaginatedFlyerModel> =
        runSuspendCatching(TAG) {
            FlyerApi.listArchived.buildRequest(
                PaginationParams(offset = offset, limit = limit),
            ).execute(http).toPaginatedFlyerModel()
        }

    override suspend fun listMyFlyers(offset: Int, limit: Int): Result<PaginatedFlyerModel> =
        runSuspendCatching(TAG) {
            FlyerApi.listMyFlyers.buildRequest(
                PaginationParams(offset = offset, limit = limit),
            ).execute(http, authHeader()).toPaginatedFlyerModel()
        }

    override suspend fun listPendingFlyers(offset: Int, limit: Int): Result<PaginatedFlyerModel> =
        runSuspendCatching(TAG) {
            ModerationApi.listPending.buildRequest(
                PaginationParams(offset = offset, limit = limit),
            ).execute(http, authHeader()).toPaginatedFlyerModel()
        }

    override suspend fun moderate(flyerId: FlyerId, action: String): Result<FlyerModel> =
        runSuspendCatching(TAG) {
            ModerationApi.moderate.buildRequest(
                flyerId,
                ModerationActionNetworkRequest(action = action),
            ).execute(http, authHeader()).toFlyerModel()
        }

    // ── Multipart operations ──────────────────────────────────────────────────

    override suspend fun createFlyer(
        title: String,
        description: String,
        expiresAt: String?,
        fileBytes: ByteArray,
        fileName: String,
        mimeType: String,
    ): Result<FlyerModel> = runSuspendCatching(TAG) {
        val response: FlyerNetworkResponse = http.post(FlyerApi.path) {
            headers { appendBearerToken() }
            setBody(
                MultiPartFormDataContent(
                    formData {
                        append("title", title)
                        append("description", description)
                        expiresAt?.let { append("expires_at", it) }
                        append(
                            "file",
                            fileBytes,
                            Headers.build {
                                append(HttpHeaders.ContentDisposition, "filename=\"$fileName\"")
                                append(HttpHeaders.ContentType, mimeType)
                            },
                        )
                    }
                )
            )
        }.body()
        response.toFlyerModel()
    }

    override suspend fun updateFlyer(
        flyerId: FlyerId,
        title: String?,
        description: String?,
        expiresAt: String?,
        fileBytes: ByteArray?,
        fileName: String?,
        mimeType: String?,
    ): Result<FlyerModel> = runSuspendCatching(TAG) {
        val response: FlyerNetworkResponse = http.put("${FlyerApi.path}/${flyerId.flyerId}") {
            headers { appendBearerToken() }
            setBody(
                MultiPartFormDataContent(
                    formData {
                        title?.let { append("title", it) }
                        description?.let { append("description", it) }
                        expiresAt?.let { append("expires_at", it) }
                        if (fileBytes != null && fileName != null && mimeType != null) {
                            append(
                                "file",
                                fileBytes,
                                Headers.build {
                                    append(HttpHeaders.ContentDisposition, "filename=\"$fileName\"")
                                    append(HttpHeaders.ContentType, mimeType)
                                },
                            )
                        }
                    }
                )
            )
        }.body()
        response.toFlyerModel()
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private fun authHeader(): HeadersBuilder.() -> Unit = { appendBearerToken() }

    private fun HeadersBuilder.appendBearerToken() {
        val token = authService.getAccessToken() ?: return
        append(HEADER_TOKEN_AUTH, "Bearer $token")
    }

    companion object {
        private const val TAG = "FlyerServiceImpl"
    }
}
