package com.cramsan.edifikana.api

import com.cramsan.edifikana.lib.model.network.payment.CreatePaymentRecordNetworkRequest
import com.cramsan.edifikana.lib.model.network.payment.GetPaymentRecordsQueryParams
import com.cramsan.edifikana.lib.model.network.payment.PaymentRecordListNetworkResponse
import com.cramsan.edifikana.lib.model.network.payment.PaymentRecordNetworkResponse
import com.cramsan.edifikana.lib.model.network.payment.UpdatePaymentRecordNetworkRequest
import com.cramsan.edifikana.lib.model.payment.PaymentRecordId
import com.cramsan.framework.annotations.api.NoPathParam
import com.cramsan.framework.annotations.api.NoQueryParam
import com.cramsan.framework.annotations.api.NoRequestBody
import com.cramsan.framework.networkapi.AdditionalResponses
import com.cramsan.framework.networkapi.Api
import com.cramsan.framework.networkapi.UniversalResponsesOnly
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode

/**
 * API definition for payment record operations.
 *
 * Payment records are unit-scoped financial records tracking rent and other payments.
 * Write operations require ADMIN role or higher. Read operations require EMPLOYEE role or higher.
 */

object PaymentRecordApi : Api("payment-records") {
    val createPaymentRecord =
        operation<
            CreatePaymentRecordNetworkRequest,
            NoQueryParam,
            NoPathParam,
            PaymentRecordNetworkResponse,
            >(
            method = HttpMethod.Post,
            summary = "Create a payment record",
            description = "Creates a new payment record for a unit. Requires the ADMIN role or higher.",
            responses = UniversalResponsesOnly,
        )

    val getPaymentRecord =
        operation<
            NoRequestBody,
            NoQueryParam,
            PaymentRecordId,
            PaymentRecordNetworkResponse,
            >(
            method = HttpMethod.Get,
            summary = "Get a payment record",
            description = "Retrieves a single payment record by its identifier. Requires the EMPLOYEE role or higher.",
            responses =
            AdditionalResponses {
                HttpStatusCode.NotFound describedAs "No payment record exists for the given id."
            },
        )

    val listPaymentRecords =
        operation<
            NoRequestBody,
            GetPaymentRecordsQueryParams,
            NoPathParam,
            PaymentRecordListNetworkResponse,
            >(
            method = HttpMethod.Get,
            summary = "List payment records",
            description =
            "Lists payment records for a unit, optionally filtered by period. " +
                "Requires the EMPLOYEE role or higher.",
            responses = UniversalResponsesOnly,
        )

    val updatePaymentRecord =
        operation<
            UpdatePaymentRecordNetworkRequest,
            NoQueryParam,
            PaymentRecordId,
            PaymentRecordNetworkResponse,
            >(
            method = HttpMethod.Put,
            summary = "Update a payment record",
            description =
            "Updates the mutable fields of an existing payment record. " +
                "Requires the ADMIN role or higher.",
            responses =
            AdditionalResponses {
                HttpStatusCode.NotFound describedAs "No payment record exists for the given id."
            },
        )
}
