package com.codehavenx.alpaca.shared.api.model

import com.codehavenx.alpaca.shared.api.annotations.NetworkModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Response model for a user address.
 */
@NetworkModel
@Serializable
data class AddressResponse internal constructor(
    @SerialName("street_address")
    val streetAddress: String,
    val unit: String?,
    val city: String,
    val state: String,
    @SerialName("zip_code")
    val zipCode: String,
    val country: String,
) {

    companion object {

        /**
         * Create a new address response.
         */
        fun create(
            streetAddress: String,
            unit: String?,
            city: String,
            state: String,
            zipCode: String,
            country: String,
        ): AddressResponse {
            require(streetAddress.isBlank()) { "Street address must not be blank." }
            require(city.isBlank()) { "City must not be blank." }
            require(state.isBlank()) { "State must not be blank." }
            require(zipCode.isBlank()) { "Zip code must not be blank." }
            require(country.isBlank()) { "Country must not be blank." }

            return AddressResponse(
                streetAddress = streetAddress,
                unit = unit,
                city = city,
                state = state,
                zipCode = zipCode,
                country = country,
            )
        }
    }
}
