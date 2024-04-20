package com.codehavenx.alpaca.server.model

import kotlinx.serialization.Serializable

/**
 * Request to create or update a user.
 * @param name */
@Serializable
data class UserRequest(
    val name: kotlin.String
)
