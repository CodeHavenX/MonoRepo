package com.cramsan.flyerboard.server.controller

import com.cramsan.flyerboard.api.ModerationApi
import com.cramsan.flyerboard.lib.model.UserRole
import com.cramsan.flyerboard.server.controller.authentication.FlyerBoardContextPayload
import com.cramsan.flyerboard.server.service.ModerationService
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.core.ktor.Controller
import com.cramsan.framework.core.ktor.OperationHandler.register
import com.cramsan.framework.core.ktor.auth.ContextRetriever
import com.cramsan.framework.core.ktor.handler
import com.cramsan.framework.utils.exceptions.ClientRequestExceptions
import io.ktor.server.routing.Routing

/**
 * Controller for admin moderation operations.
 *
 * All endpoints require an authenticated caller with [UserRole.ADMIN].
 */
@OptIn(NetworkModel::class)
class ModerationController(
    private val moderationService: ModerationService,
    private val contextRetriever: ContextRetriever<FlyerBoardContextPayload>,
) : Controller {

    override fun registerRoutes(route: Routing) {
        ModerationApi.register(route) {

            // GET /api/v1/moderation — list pending flyers (admin-only)
            handler(api.listPending, contextRetriever) { request ->
                if (request.context.payload.role != UserRole.ADMIN) {
                    throw ClientRequestExceptions.ForbiddenException("Admin role required")
                }
                moderationService.listPendingFlyers(
                    offset = request.queryParam.offset,
                    limit = request.queryParam.limit,
                ).getOrThrow().toFlyerListNetworkResponse()
            }

            // POST /api/v1/moderation/{param} — approve or reject a flyer (admin-only)
            handler(api.moderate, contextRetriever) { request ->
                if (request.context.payload.role != UserRole.ADMIN) {
                    throw ClientRequestExceptions.ForbiddenException("Admin role required")
                }
                val adminUserId = request.context.payload.userId
                val flyerId = request.pathParam
                when (request.requestBody.action) {
                    "approve" -> moderationService.approveFlyer(flyerId, adminUserId)
                    "reject" -> moderationService.rejectFlyer(flyerId, adminUserId)
                    else -> throw ClientRequestExceptions.InvalidRequestException(
                        "Invalid action '${request.requestBody.action}'. Must be 'approve' or 'reject'."
                    )
                }.getOrThrow().toFlyerNetworkResponse()
            }
        }
    }
}
