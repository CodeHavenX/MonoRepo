package com.cramsan.templatereplaceme.lib.model.network

import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.RequestBody
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Network request body for creating a [ComponentReplaceme].
 *
 * All fields must use `@SerialName` for stable JSON keys that survive refactoring.
 * Avoid using Kotlin property names directly as JSON keys.
 *
 * TODO: Add fields for the data the backend needs to create a [ComponentReplaceme], e.g.:
 * ```
 * @SerialName("name")  val name: String,
 * @SerialName("owner") val ownerId: String,
 * ```
 */
@NetworkModel
@Serializable
data class CreateComponentReplacemeNetworkRequest(
    @SerialName("id")
    val id: String,
) : RequestBody
