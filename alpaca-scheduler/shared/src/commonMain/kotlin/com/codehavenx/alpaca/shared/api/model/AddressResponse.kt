package com.codehavenx.alpaca.shared.api.model

import com.codehavenx.alpaca.shared.api.annotations.NetworkModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Response model for a user address.
 */
@NetworkModel
@Serializable
data class AddressResponse(
    @SerialName("street_address")
    val streetAddress: String,
    val unit: String?,
    val city: String,
    val state: String,
    @SerialName("zip_code")
    val zipCode: String,
    val country: String,
)
