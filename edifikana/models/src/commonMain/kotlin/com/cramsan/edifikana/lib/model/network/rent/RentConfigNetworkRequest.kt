package com.cramsan.edifikana.lib.model.network.rent

import com.cramsan.edifikana.lib.model.common.CurrencyCode
import com.cramsan.edifikana.lib.model.common.MonetaryAmount
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.RequestBody
import io.ktor.openapi.JsonSchema
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Request to create or update the rent configuration for a unit.
 */
@NetworkModel
@Serializable
@JsonSchema.Description("Request payload to create or update (upsert) the rent configuration for a unit.")
data class RentConfigNetworkRequest(
    @SerialName("monthly_amount")
    @JsonSchema.Description("Monthly rent amount.")
    val monthlyAmount: MonetaryAmount,
    @SerialName("due_day")
    @JsonSchema.Description("Day of the month rent is due.")
    @JsonSchema.Minimum(1.0)
    val dueDay: Int,
    @JsonSchema.Description("ISO 4217 currency code the rent amount is denominated in.")
    val currency: CurrencyCode,
) : RequestBody
