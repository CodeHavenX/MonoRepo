package com.cramsan.edifikana.lib.model.network.employee

import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.ResponseBody
import io.ktor.openapi.JsonSchema
import kotlinx.serialization.Serializable

/**
 * Model representing a list of employees.
 */
@NetworkModel
@Serializable
@JsonSchema.Description("A list of employees.")
data class EmployeeListNetworkResponse(
    @JsonSchema.Description("The employees matching the request.")
    val content: List<EmployeeNetworkResponse>,
) : ResponseBody
