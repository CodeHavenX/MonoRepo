package com.codehavenx.alpaca.backend.storage.entity

import kotlinx.serialization.Serializable

const val COLLECTION = "users"

@Serializable
@SupabaseModel
data class UserEntity(
    val id: String,
    val username: String,
)

@Serializable
@SupabaseModel
data class CreateUserEntity(
    val username: String,
)
