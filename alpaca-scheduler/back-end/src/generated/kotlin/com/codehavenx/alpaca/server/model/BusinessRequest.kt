package com.codehavenx.alpaca.server.model

import kotlinx.serialization.Serializable

/**
 * Request to create or update a business.
 * @param name */
@Serializable
data class BusinessRequest(
    val name: kotlin.String
)
