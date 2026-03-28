package com.cramsan.edifikana.api

import com.cramsan.edifikana.lib.model.PaymentRecordId
import com.cramsan.edifikana.lib.model.network.CreatePaymentRecordNetworkRequest
import com.cramsan.edifikana.lib.model.network.GetPaymentRecordsQueryParams
import com.cramsan.edifikana.lib.model.network.PaymentRecordListNetworkResponse
import com.cramsan.edifikana.lib.model.network.PaymentRecordNetworkResponse
import com.cramsan.edifikana.lib.model.network.UpdatePaymentRecordNetworkRequest
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.NoPathParam
import com.cramsan.framework.annotations.api.NoQueryParam
import com.cramsan.framework.annotations.api.NoRequestBody
import com.cramsan.framework.networkapi.Api
import io.ktor.http.HttpMethod

/**
 * API definition for payment record operations.
 */
@OptIn(NetworkModel::class)
object PaymentRecordApi : Api("payment-records") {

    val createPaymentRecord = operation<
        CreatePaymentRecordNetworkRequest,
        NoQueryParam,
        NoPathParam,
        PaymentRecordNetworkResponse
        >(HttpMethod.Post)

    val getPaymentRecords = operation<
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
