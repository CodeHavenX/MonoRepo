package com.cramsan.edifikana.lib.model.network

import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.ResponseBody
import kotlinx.serialization.Serializable

/**
 * Network response for a list of payment records.
 */
@NetworkModel
@Serializable
data class PaymentRecordListNetworkResponse(
    val paymentRecords: List<PaymentRecordNetworkResponse>,
) : ResponseBody
