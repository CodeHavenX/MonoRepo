package com.cramsan.edifikana.lib.model.network

import com.cramsan.edifikana.lib.model.EmployeeId
import com.cramsan.edifikana.lib.model.EmployeeRole
import com.cramsan.edifikana.lib.model.IdType
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.ResponseBody
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Model representing a employee.
 */
@NetworkModel
@Serializable
data class EmployeeNetworkResponse(
    val id: EmployeeId,
    @SerialName("id_type")
    val idType: IdType,
    @SerialName("first_name")
    val firstName: String,
    @SerialName("last_name")
    val lastName: String,
    val role: EmployeeRole,
    @SerialName("property_id")
    val propertyId: PropertyId,
) : ResponseBody
