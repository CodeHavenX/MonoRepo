package com.cramsan.templatereplaceme.lib.model.network

import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.ResponseBody
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Network response body returned for [ComponentReplaceme] operations.
 *
 * All fields must use `@SerialName` for stable JSON keys that survive refactoring.
 *
 * TODO: Add fields for the data the backend returns about a [ComponentReplaceme], e.g.:
 * ```
 * @SerialName("id")         val id: String,
 * @SerialName("name")       val name: String,
 * @SerialName("created_at") val createdAt: String,
 * ```
 */
@NetworkModel
@Serializable
data class ComponentReplacemeNetworkResponse(
    @SerialName("id")
    val id: String,
) : ResponseBody
