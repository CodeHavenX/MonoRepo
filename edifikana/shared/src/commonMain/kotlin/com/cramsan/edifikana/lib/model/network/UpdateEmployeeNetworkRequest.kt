package com.cramsan.edifikana.lib.model.network

import com.cramsan.edifikana.lib.model.EmployeeRole
import com.cramsan.edifikana.lib.model.IdType
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.RequestBody
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Request to update an existing property.
 */
@NetworkModel
@Serializable
data class UpdateEmployeeNetworkRequest(
    @SerialName("id_type")
    val idType: IdType?,
    @SerialName("first_name")
    val firstName: String?,
    @SerialName("last_name")
    val lastName: String?,
    val role: EmployeeRole?,
) : RequestBody
