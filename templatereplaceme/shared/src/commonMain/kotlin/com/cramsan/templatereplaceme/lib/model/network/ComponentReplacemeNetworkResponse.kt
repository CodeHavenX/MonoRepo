package com.cramsan.templatereplaceme.lib.model.network

import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.ResponseBody
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Network response body for a [ComponentReplaceme] operation.
 *
 * Add fields here to carry data from the backend to the client.
 * All fields must be annotated with [@SerialName] for stable JSON keys.
 */
@NetworkModel
@Serializable
data class ComponentReplacemeNetworkResponse(
    @SerialName("id")
    val id: String,
) : ResponseBody
