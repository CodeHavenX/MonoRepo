package com.codehavenx.alpaca.shared.api.model

import com.codehavenx.alpaca.shared.api.annotations.ApiNetwork
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@ApiNetwork
@Serializable
data class CreateUserRequest(
    @SerialName("user_name")
    val userName: String,
)
