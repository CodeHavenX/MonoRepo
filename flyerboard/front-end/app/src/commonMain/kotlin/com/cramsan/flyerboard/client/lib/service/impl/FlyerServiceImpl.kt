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
import com.cramsan.flyerboard.lib.model.network.CreateFlyerNetworkRequest
import com.cramsan.flyerboard.lib.model.network.ListFlyersQueryParams
import com.cramsan.flyerboard.lib.model.network.ModerationActionNetworkRequest
import com.cramsan.flyerboard.lib.model.network.PaginationParams
import com.cramsan.flyerboard.lib.model.network.UpdateFlyerNetworkRequest
import com.cramsan.framework.annotations.FrontendService
import com.cramsan.framework.core.runSuspendCatching
import com.cramsan.framework.logging.logW
import com.cramsan.framework.networkapi.buildRequest
import com.cramsan.framework.utils.exceptions.ClientRequestExceptions
import io.ktor.client.HttpClient
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

/**
 * Ktor HTTP client implementation of [FlyerService].
 */
@FrontendService
class FlyerServiceImpl(private val http: HttpClient, private val authService: AuthService) : FlyerService {
    // ── JSON operations ───────────────────────────────────────────────────────

    override suspend fun listFlyers(
        offset: Int,
        limit: Int,
        status: FlyerStatus?,
        query: String?,
    ): Result<PaginatedFlyerModel> =
        runSuspendCatching(TAG) {
            FlyerApi.listFlyers
                .buildRequest(
                    ListFlyersQueryParams(offset = offset, limit = limit, status = status, q = query),
                ).execute(http)
                .toPaginatedFlyerModel()
        }

    override suspend fun getFlyer(flyerId: FlyerId): Result<FlyerModel?> =
        runSuspendCatching(TAG) {
            try {
                FlyerApi.getFlyer
                    .buildRequest(flyerId)
                    .execute(http)
                    .toFlyerModel()
            } catch (e: ClientRequestExceptions.NotFoundException) {
                logW(TAG, "Flyer not found: ${flyerId.flyerId}", e)
                null
            }
        }

    override suspend fun listArchived(offset: Int, limit: Int, query: String?): Result<PaginatedFlyerModel> =
        runSuspendCatching(TAG) {
            FlyerApi.listArchived
                .buildRequest(
                    ListFlyersQueryParams(offset = offset, limit = limit, q = query, status = FlyerStatus.ARCHIVED),
                ).execute(http)
                .toPaginatedFlyerModel()
        }

    override suspend fun listMyFlyers(offset: Int, limit: Int): Result<PaginatedFlyerModel> =
        runSuspendCatching(TAG) {
            FlyerApi.listMyFlyers
                .buildRequest(
                    PaginationParams(offset = offset, limit = limit),
                ).execute(http)
                .toPaginatedFlyerModel()
        }

    override suspend fun listPendingFlyers(offset: Int, limit: Int): Result<PaginatedFlyerModel> =
        runSuspendCatching(TAG) {
            ModerationApi.listPending
                .buildRequest(
                    PaginationParams(offset = offset, limit = limit),
                ).execute(http)
                .toPaginatedFlyerModel()
        }

    override suspend fun moderate(flyerId: FlyerId, action: String, reason: String?): Result<FlyerModel> =
        runSuspendCatching(TAG) {
            ModerationApi.moderate
                .buildRequest(
                    flyerId,
                    ModerationActionNetworkRequest(action = action, reason = reason),
                ).execute(http)
                .toFlyerModel()
        }

    // ── Upload operations ─────────────────────────────────────────────────────

    override suspend fun createFlyer(
        title: String,
        description: String,
        expiresAt: String?,
        fileBytes: ByteArray,
        mimeType: String,
    ): Result<FlyerModel> =
        runSuspendCatching(TAG) {
            val response =
                FlyerApi.createFlyer
                    .buildRequest(CreateFlyerNetworkRequest(title, description, expiresAt))
                    .execute(http)
            uploadAsset(response.upload.signedUrl, fileBytes, mimeType)
            response.flyer.toFlyerModel()
        }

    override suspend fun updateFlyer(
        flyerId: FlyerId,
        title: String?,
        description: String?,
        expiresAt: String?,
        fileBytes: ByteArray?,
        mimeType: String?,
    ): Result<FlyerModel> =
        runSuspendCatching(TAG) {
            val response =
                FlyerApi.updateFlyer
                    .buildRequest(
                        flyerId,
                        UpdateFlyerNetworkRequest(title, description, expiresAt, requestUpload = fileBytes != null),
                    ).execute(http)
            if (fileBytes != null) {
                val upload = requireNotNull(response.upload) { "Server did not return an upload URL" }
                uploadAsset(upload.signedUrl, fileBytes, mimeType.orEmpty())
            }
            response.flyer.toFlyerModel()
        }

    private suspend fun uploadAsset(signedUrl: String, fileBytes: ByteArray, mimeType: String) {
        http.put(signedUrl) {
            contentType(ContentType.parse(mimeType))
            setBody(fileBytes)
        }
    }

    companion object {
        private const val TAG = "FlyerServiceImpl"
    }
}
