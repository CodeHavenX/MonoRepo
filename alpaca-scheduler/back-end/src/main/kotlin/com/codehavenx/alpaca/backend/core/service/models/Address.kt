package com.codehavenx.alpaca.backend.core.service.models

/**
 * Holds the details of an address for all users
 */
data class Address (
    val streetAddress: String,
    val unit: String?,
    val city: String,
    val state: String,
    val zipCode: String,
    val country: String,
)