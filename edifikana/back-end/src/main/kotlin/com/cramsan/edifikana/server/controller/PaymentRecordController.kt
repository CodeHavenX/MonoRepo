package com.cramsan.edifikana.server.controller

import com.cramsan.edifikana.api.PaymentRecordApi
import com.cramsan.edifikana.lib.model.network.payment.CreatePaymentRecordNetworkRequest
import com.cramsan.edifikana.lib.model.network.payment.GetPaymentRecordsQueryParams
import com.cramsan.edifikana.lib.model.network.payment.PaymentRecordListNetworkResponse
import com.cramsan.edifikana.lib.model.network.payment.PaymentRecordNetworkResponse
import com.cramsan.edifikana.lib.model.network.payment.UpdatePaymentRecordNetworkRequest
import com.cramsan.edifikana.lib.model.payment.PaymentRecordId
import com.cramsan.edifikana.server.controller.authentication.SupabaseContextPayload
import com.cramsan.edifikana.server.service.PaymentRecordService
import com.cramsan.edifikana.server.service.authorization.RBACService
import com.cramsan.edifikana.server.service.models.UserRole
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.NoPathParam
import com.cramsan.framework.annotations.api.NoQueryParam
import com.cramsan.framework.annotations.api.NoRequestBody
import com.cramsan.framework.core.ktor.Controller
import com.cramsan.framework.core.ktor.OperationHandler.register
import com.cramsan.framework.core.ktor.OperationRequest
import com.cramsan.framework.core.ktor.auth.ClientContext
import com.cramsan.framework.core.ktor.auth.ContextRetriever
import com.cramsan.framework.core.ktor.handler
import com.cramsan.framework.utils.exceptions.ClientRequestExceptions.NotFoundException
import com.cramsan.framework.utils.exceptions.ClientRequestExceptions.UnauthorizedException
import io.ktor.server.routing.Routing

/**
 * Controller for payment record operations.
 *
 * Write operations (create, update) require ADMIN role or higher.
 * Read operations (get, list) require EMPLOYEE role or higher.
 *
 * RBAC for create resolves via the unitId in the request body.
 * RBAC for get/update resolves via the paymentRecordId (unitId → orgId lookup).
 * RBAC for list resolves via the unitId query param.
 *
 * TODO: Resident read-only access via unit_occupants is deferred to a future phase.
 */
@OptIn(NetworkModel::class)
class PaymentRecordController(
    private val paymentRecordService: PaymentRecordService,
    private val rbacService: RBACService,
    private val contextRetriever: ContextRetriever<SupabaseContextPayload>,
) : Controller {

    private val unauthorizedMsg = "You are not authorized to perform this action in your organization."

    /**
     * Creates a new payment record. Requires ADMIN role or higher for the unit's organization.
     */
    suspend fun createPaymentRecord(
        request: OperationRequest<
            CreatePaymentRecordNetworkRequest,
            NoQueryParam,
            NoPathParam,
            ClientContext.AuthenticatedClientContext<SupabaseContextPayload>
            >
    ): PaymentRecordNetworkResponse {
        if (!rbacService.hasRoleOrHigher(request.context, request.requestBody.unitId, UserRole.ADMIN)) {
            throw UnauthorizedException(unauthorizedMsg)
        }
        return paymentRecordService.createPaymentRecord(
            unitId = request.requestBody.unitId,
            paymentType = request.requestBody.paymentType,
            periodMonth = request.requestBody.periodMonth,
            amountDue = request.requestBody.amountDue,
            dueDate = request.requestBody.dueDate,
            recordedBy = request.context.payload.userId,
            notes = request.requestBody.notes,
        ).toPaymentRecordNetworkResponse()
    }

    /**
     * Retrieves a single payment record. Requires EMPLOYEE role or higher.
     * Returns 404 if not found or unauthorized (to avoid leaking existence).
     */
    suspend fun getPaymentRecord(
        request: OperationRequest<
            NoRequestBody,
            NoQueryParam,
            PaymentRecordId,
            ClientContext.AuthenticatedClientContext<SupabaseContextPayload>
            >
    ): PaymentRecordNetworkResponse {
        if (!rbacService.hasRoleOrHigher(request.context, request.pathParam, UserRole.EMPLOYEE)) {
            throw NotFoundException("Payment record not found.")
        }
        return paymentRecordService.getPaymentRecord(request.pathParam)?.toPaymentRecordNetworkResponse()
            ?: throw NotFoundException("Payment record not found.")
    }

    /**
     * Lists payment records for a unit. Requires EMPLOYEE role or higher.
     * Optionally filters by period_month (format "YYYY-MM").
     */
    suspend fun listPaymentRecords(
        request: OperationRequest<
            NoRequestBody,
            GetPaymentRecordsQueryParams,
            NoPathParam,
            ClientContext.AuthenticatedClientContext<SupabaseContextPayload>
            >
    ): PaymentRecordListNetworkResponse {
        if (!rbacService.hasRoleOrHigher(request.context, request.queryParam.unitId, UserRole.EMPLOYEE)) {
            throw UnauthorizedException(unauthorizedMsg)
        }
        val records = paymentRecordService.listPaymentRecords(
            unitId = request.queryParam.unitId,
            periodMonth = request.queryParam.periodMonth,
        ).map { it.toPaymentRecordNetworkResponse() }
        return PaymentRecordListNetworkResponse(records)
    }

    /**
     * Updates an existing payment record. Requires ADMIN role or higher.
     */
    suspend fun updatePaymentRecord(
        request: OperationRequest<
            UpdatePaymentRecordNetworkRequest,
            NoQueryParam,
            PaymentRecordId,
            ClientContext.AuthenticatedClientContext<SupabaseContextPayload>
            >
    ): PaymentRecordNetworkResponse {
        if (!rbacService.hasRoleOrHigher(request.context, request.pathParam, UserRole.ADMIN)) {
            throw UnauthorizedException(unauthorizedMsg)
        }
        return paymentRecordService.updatePaymentRecord(
            paymentRecordId = request.pathParam,
            amountPaid = request.requestBody.amountPaid,
            paidDate = request.requestBody.paidDate,
            status = request.requestBody.status,
            notes = request.requestBody.notes,
        ).toPaymentRecordNetworkResponse()
    }

    /**
     * Registers all payment record routes.
     */
    override fun registerRoutes(route: Routing) {
        PaymentRecordApi.register(route) {
            handler(api.createPaymentRecord, contextRetriever) { request ->
                createPaymentRecord(request)
            }
            handler(api.getPaymentRecord, contextRetriever) { request ->
                getPaymentRecord(request)
            }
            handler(api.listPaymentRecords, contextRetriever) { request ->
                listPaymentRecords(request)
            }
            handler(api.updatePaymentRecord, contextRetriever) { request ->
                updatePaymentRecord(request)
            }
        }
    }
}
