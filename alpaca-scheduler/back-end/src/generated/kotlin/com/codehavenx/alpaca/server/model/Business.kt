package com.codehavenx.alpaca.server.model

/**
 * A single business and its basic public information.
 * @param businessId An Id that identifies a business.
 * @param name
 */
data class Business(
    /* An Id that identifies a business. */
    val businessId: kotlin.String,
    val name: kotlin.String
)
