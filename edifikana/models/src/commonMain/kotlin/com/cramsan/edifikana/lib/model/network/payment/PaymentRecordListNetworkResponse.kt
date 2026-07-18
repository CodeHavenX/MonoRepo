package com.cramsan.edifikana.lib.model.network.payment

import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.ResponseBody
import io.ktor.openapi.JsonSchema
import kotlinx.serialization.Serializable

/**
 * Network response containing a list of payment records.
 */
@NetworkModel
@Serializable
@JsonSchema.Description("A list of payment records.")
data class PaymentRecordListNetworkResponse(
    @JsonSchema.Description("The payment records matching the request.")
    val paymentRecords: List<PaymentRecordNetworkResponse>,
) : ResponseBody
