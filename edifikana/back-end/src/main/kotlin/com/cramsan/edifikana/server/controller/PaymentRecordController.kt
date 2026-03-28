package com.cramsan.edifikana.server.controller

import com.cramsan.edifikana.api.PaymentRecordApi
import com.cramsan.edifikana.lib.model.PaymentRecordId
import com.cramsan.edifikana.lib.model.network.CreatePaymentRecordNetworkRequest
import com.cramsan.edifikana.lib.model.network.GetPaymentRecordsQueryParams
import com.cramsan.edifikana.lib.model.network.PaymentRecordListNetworkResponse
import com.cramsan.edifikana.lib.model.network.PaymentRecordNetworkResponse
import com.cramsan.edifikana.lib.model.network.UpdatePaymentRecordNetworkRequest
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
 */
@OptIn(NetworkModel::class)
class PaymentRecordController(
    private val paymentRecordService: PaymentRecordService,
    private val rbacService: RBACService,
    private val contextRetriever: ContextRetriever<SupabaseContextPayload>,
) : Controller {

    private val unauthorizedMsg = "You are not authorized to perform this action in your organization."

    /**
     * Creates a new payment record. Requires MANAGER role or higher in the target org.
     */
    suspend fun createPaymentRecord(
        request: OperationRequest<
            CreatePaymentRecordNetworkRequest,
            NoQueryParam,
            NoPathParam,
            ClientContext.AuthenticatedClientContext<SupabaseContextPayload>
            >
    ): PaymentRecordNetworkResponse {
        if (!rbacService.hasRoleOrHigher(request.context, request.requestBody.orgId, UserRole.MANAGER)) {
            throw UnauthorizedException(unauthorizedMsg)
        }
        return paymentRecordService.createPaymentRecord(
            unitId = request.requestBody.unitId,
            orgId = request.requestBody.orgId,
            paymentType = request.requestBody.paymentType,
            periodMonth = request.requestBody.periodMonth,
            amountDue = request.requestBody.amountDue,
            amountPaid = request.requestBody.amountPaid,
            status = request.requestBody.status,
            dueDate = request.requestBody.dueDate,
            paidDate = request.requestBody.paidDate,
            recordedBy = request.context.payload.userId,
            notes = request.requestBody.notes,
        ).toPaymentRecordNetworkResponse()
    }

    /**
     * Lists payment records for the given unit and optional period month. Requires MANAGER role or higher.
     */
    suspend fun getPaymentRecords(
        request: OperationRequest<
            NoRequestBody,
            GetPaymentRecordsQueryParams,
            NoPathParam,
            ClientContext.AuthenticatedClientContext<SupabaseContextPayload>
            >
    ): PaymentRecordListNetworkResponse {
        if (!rbacService.hasRoleOrHigher(request.context, request.queryParam.orgId, UserRole.MANAGER)) {
            throw UnauthorizedException(unauthorizedMsg)
        }
        val records = paymentRecordService.getPaymentRecords(
            unitId = request.queryParam.unitId,
            periodMonth = request.queryParam.periodMonth,
        ).map { it.toPaymentRecordNetworkResponse() }
        return PaymentRecordListNetworkResponse(records)
    }

    /**
     * Updates an existing payment record. Requires MANAGER role or higher.
     */
    suspend fun updatePaymentRecord(
        request: OperationRequest<
            UpdatePaymentRecordNetworkRequest,
            NoQueryParam,
            PaymentRecordId,
            ClientContext.AuthenticatedClientContext<SupabaseContextPayload>
            >
    ): PaymentRecordNetworkResponse {
        if (!rbacService.hasRoleOrHigher(request.context, request.pathParam, UserRole.MANAGER)) {
            throw UnauthorizedException(unauthorizedMsg)
        }
        return paymentRecordService.updatePaymentRecord(
            paymentRecordId = request.pathParam,
            paymentType = request.requestBody.paymentType,
            amountDue = request.requestBody.amountDue,
            amountPaid = request.requestBody.amountPaid,
            status = request.requestBody.status,
            dueDate = request.requestBody.dueDate,
            paidDate = request.requestBody.paidDate,
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
            handler(api.getPaymentRecords, contextRetriever) { request ->
                getPaymentRecords(request)
            }
            handler(api.updatePaymentRecord, contextRetriever) { request ->
                updatePaymentRecord(request)
            }
        }
    }
}
