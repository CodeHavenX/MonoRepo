package com.cramsan.templatereplaceme.lib.model.network

import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.RequestBody
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Network request body for creating a [ComponentReplaceme].
 *
 * Add fields here to carry data from the client to the backend.
 * All fields must be annotated with [@SerialName] for stable JSON keys.
 */
@NetworkModel
@Serializable
data class CreateComponentReplacemeNetworkRequest(
    @SerialName("id")
    val id: String,
) : RequestBody
