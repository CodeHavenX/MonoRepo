package com.cramsan.edifikana.lib.model.network

import com.cramsan.edifikana.lib.model.CommonAreaType
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.RequestBody
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Request to update an existing common area.
 */
@NetworkModel
@Serializable
data class UpdateCommonAreaNetworkRequest(
    @SerialName("name")
    val name: String? = null,
    @SerialName("type")
    val type: CommonAreaType? = null,
    @SerialName("description")
    val description: String? = null,
) : RequestBody
