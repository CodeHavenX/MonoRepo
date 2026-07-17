package com.cramsan.edifikana.lib.model.network.employee

import com.cramsan.edifikana.lib.model.employee.EmployeeRole
import com.cramsan.edifikana.lib.model.identification.IdType
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.RequestBody
import io.ktor.openapi.JsonSchema
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Request to update an existing property.
 */
@NetworkModel
@Serializable
@JsonSchema.Description(
    "Request payload to update an existing employee. Only provided (non-null) fields are updated.",
)
data class UpdateEmployeeNetworkRequest(
    @SerialName("id_type")
    @JsonSchema.Description("New identification document type, or null to leave unchanged.")
    val idType: IdType?,
    @SerialName("first_name")
    @JsonSchema.Description("New first name, or null to leave unchanged.")
    val firstName: String?,
    @SerialName("last_name")
    @JsonSchema.Description("New last name, or null to leave unchanged.")
    val lastName: String?,
    @JsonSchema.Description("New role, or null to leave unchanged.")
    val role: EmployeeRole?,
) : RequestBody
