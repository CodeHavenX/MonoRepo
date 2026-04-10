package com.cramsan.edifikana.api

import com.cramsan.edifikana.lib.model.network.payment.CreatePaymentRecordNetworkRequest
import com.cramsan.edifikana.lib.model.network.payment.GetPaymentRecordsQueryParams
import com.cramsan.edifikana.lib.model.network.payment.PaymentRecordListNetworkResponse
import com.cramsan.edifikana.lib.model.network.payment.PaymentRecordNetworkResponse
import com.cramsan.edifikana.lib.model.network.payment.UpdatePaymentRecordNetworkRequest
import com.cramsan.edifikana.lib.model.payment.PaymentRecordId
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.NoPathParam
import com.cramsan.framework.annotations.api.NoQueryParam
import com.cramsan.framework.annotations.api.NoRequestBody
import com.cramsan.framework.networkapi.Api
import io.ktor.http.HttpMethod

/**
 * API definition for payment record operations.
 *
 * Payment records are unit-scoped financial records tracking rent and other payments.
 * Write operations require ADMIN role or higher. Read operations require EMPLOYEE role or higher.
 */
@OptIn(NetworkModel::class)
object PaymentRecordApi : Api("payment-records") {

    val createPaymentRecord = operation<
        CreatePaymentRecordNetworkRequest,
        NoQueryParam,
        NoPathParam,
        PaymentRecordNetworkResponse
        >(HttpMethod.Post)

    val getPaymentRecord = operation<
        NoRequestBody,
        NoQueryParam,
        PaymentRecordId,
        PaymentRecordNetworkResponse
        >(HttpMethod.Get)

    val listPaymentRecords = operation<
        NoRequestBody,
        GetPaymentRecordsQueryParams,
        NoPathParam,
        PaymentRecordListNetworkResponse
        >(HttpMethod.Get)

    val updatePaymentRecord = operation<
        UpdatePaymentRecordNetworkRequest,
        NoQueryParam,
        PaymentRecordId,
        PaymentRecordNetworkResponse
        >(HttpMethod.Put)
}
