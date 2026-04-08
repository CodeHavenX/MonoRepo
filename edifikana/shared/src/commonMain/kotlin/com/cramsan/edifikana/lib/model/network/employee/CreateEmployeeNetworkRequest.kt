package com.cramsan.edifikana.lib.model.network.employee

import com.cramsan.edifikana.lib.model.employee.EmployeeRole
import com.cramsan.edifikana.lib.model.identification.IdType
import com.cramsan.edifikana.lib.model.property.PropertyId
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.RequestBody
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Request to create a new property.
 */
@NetworkModel
@Serializable
data class CreateEmployeeNetworkRequest(
    @SerialName("id_type")
    val idType: IdType,
    @SerialName("first_name")
    val firstName: String,
    @SerialName("last_name")
    val lastName: String,
    val role: EmployeeRole,
    @SerialName("property_id")
    val propertyId: PropertyId,
) : RequestBody
