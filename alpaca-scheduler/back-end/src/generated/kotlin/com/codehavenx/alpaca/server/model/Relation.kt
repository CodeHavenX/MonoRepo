package com.codehavenx.alpaca.server.model

/**
 * * @param businessId An Id that identifies a business.
 * @param userId An Id that identifies a user.
 * @param relationType */
data class Relation(
    /* An Id that identifies a business. */
    val businessId: kotlin.String? = null,
    /* An Id that identifies a user. */
    val userId: kotlin.String? = null,
    val relationType: BusinessRelation? = null
)
