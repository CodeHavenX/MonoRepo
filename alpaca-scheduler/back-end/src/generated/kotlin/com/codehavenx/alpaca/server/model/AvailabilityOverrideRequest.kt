package com.codehavenx.alpaca.server.model

import kotlinx.serialization.Serializable

/**
 * Request to set the availability override of a user.
 * @param availabilityRequest * @param reason */
@Serializable
data class AvailabilityOverrideRequest(
    val availabilityRequest: AvailabilityRequest,
    val reason: kotlin.String
)
