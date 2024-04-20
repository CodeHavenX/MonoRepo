package com.codehavenx.alpaca.server.model

/**
 * A single user and it's basic public information.
 * @param userId An Id that identifies a user.
 * @param name * @param relations */
data class User(
    /* An Id that identifies a user. */
    val userId: kotlin.String,
    val name: kotlin.String,
    val relations: kotlin.collections.List<Relation>? = null
)
