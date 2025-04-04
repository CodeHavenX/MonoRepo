package com.codehavenx.alpaca.backend.core.controller

import com.codehavenx.alpaca.backend.core.service.models.Address
import com.codehavenx.alpaca.backend.core.service.models.StaffId
import com.codehavenx.alpaca.backend.core.service.models.User
import com.codehavenx.alpaca.shared.api.annotations.NetworkModel
import com.codehavenx.alpaca.shared.api.model.AddressResponse
import com.codehavenx.alpaca.shared.api.model.UserResponse
import kotlinx.datetime.LocalDateTime

/**
 * Converts a string to a local date time.
 */
fun String.toLocalDateTime(): LocalDateTime {
    return LocalDateTime.parse(this)
}

/**
 * Converts a string to a staff id.
 */
fun String.toStaffId(): StaffId {
    return StaffId(this)
}

/**
 * Converts a [User] domain model to a [UserResponse] network model.
 */
@NetworkModel
fun User.toUserResponse(): UserResponse {
    return UserResponse.create(
        id = id.userId,
        username = username,
        isVerified = isVerified,
        phoneNumbers = phoneNumbers,
        firstName = firstName,
        lastName = lastName,
        address = address?.toAddressResponse(),
        emails = emails
    )
}

@NetworkModel
private fun Address.toAddressResponse(): AddressResponse {
    return AddressResponse.create(
        streetAddress = streetAddress,
        unit = unit,
        city = city,
        state = state,
        zipCode = zipCode,
        country = country,
    )
}
