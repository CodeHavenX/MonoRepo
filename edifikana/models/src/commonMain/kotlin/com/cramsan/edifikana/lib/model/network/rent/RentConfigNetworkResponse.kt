@file:OptIn(ExperimentalTime::class)

package com.cramsan.edifikana.lib.model.network.rent

import com.cramsan.edifikana.lib.model.common.CurrencyCode
import com.cramsan.edifikana.lib.model.common.MonetaryAmount
import com.cramsan.edifikana.lib.model.rent.RentConfigId
import com.cramsan.edifikana.lib.model.unit.UnitId
import com.cramsan.edifikana.lib.model.user.UserId
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.ResponseBody
import io.ktor.openapi.JsonSchema
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/**
 * Network response for a rent configuration.
 */
@NetworkModel
@Serializable
@JsonSchema.Description("Rent configuration for a unit.")
data class RentConfigNetworkResponse(
    @SerialName("rent_config_id")
    @JsonSchema.Description("Unique identifier of the rent configuration.")
    val rentConfigId: RentConfigId,
    @SerialName("unit_id")
    @JsonSchema.Description("Identifier of the unit the rent configuration belongs to.")
    val unitId: UnitId,
    @SerialName("monthly_amount")
    @JsonSchema.Description("Monthly rent amount.")
    val monthlyAmount: MonetaryAmount,
    @SerialName("due_day")
    @JsonSchema.Description("Day of the month rent is due.")
    @JsonSchema.Minimum(1.0)
    val dueDay: Int,
    @JsonSchema.Description("ISO 4217 currency code the rent amount is denominated in.")
    val currency: CurrencyCode,
    @SerialName("updated_at")
    @JsonSchema.Description("ISO-8601 timestamp when the rent configuration was last updated.")
    @JsonSchema.Format("date-time")
    val updatedAt: Instant,
    @SerialName("updated_by")
    @JsonSchema.Description("Identifier of the user who last updated the rent configuration, or null if unknown.")
    val updatedBy: UserId?,
    @SerialName("created_at")
    @JsonSchema.Description("ISO-8601 timestamp when the rent configuration was created.")
    @JsonSchema.Format("date-time")
    val createdAt: Instant,
) : ResponseBody
