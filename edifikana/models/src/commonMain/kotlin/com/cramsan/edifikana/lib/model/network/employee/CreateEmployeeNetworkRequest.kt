package com.cramsan.edifikana.lib.model.network.employee

import com.cramsan.edifikana.lib.model.employee.EmployeeRole
import com.cramsan.edifikana.lib.model.identification.IdType
import com.cramsan.edifikana.lib.model.property.PropertyId
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.RequestBody
import io.ktor.openapi.JsonSchema
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Request to create a new property.
 */
@NetworkModel
@Serializable
@JsonSchema.Description("Request payload to create a new employee within a property.")
data class CreateEmployeeNetworkRequest(
    @SerialName("id_type")
    @JsonSchema.Description("Type of the employee's identification document.")
    val idType: IdType,
    @SerialName("first_name")
    @JsonSchema.Description("First name of the employee.")
    @JsonSchema.Example("\"Jane\"")
    val firstName: String,
    @SerialName("last_name")
    @JsonSchema.Description("Last name of the employee.")
    @JsonSchema.Example("\"Doe\"")
    val lastName: String,
    @JsonSchema.Description("Role of the employee.")
    val role: EmployeeRole,
    @SerialName("property_id")
    @JsonSchema.Description("Identifier of the property the employee belongs to.")
    val propertyId: PropertyId,
) : RequestBody
