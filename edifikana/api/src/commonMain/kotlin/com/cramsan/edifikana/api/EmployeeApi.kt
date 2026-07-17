package com.cramsan.edifikana.api

import com.cramsan.edifikana.lib.model.employee.EmployeeId
import com.cramsan.edifikana.lib.model.network.employee.CreateEmployeeNetworkRequest
import com.cramsan.edifikana.lib.model.network.employee.EmployeeListNetworkResponse
import com.cramsan.edifikana.lib.model.network.employee.EmployeeNetworkResponse
import com.cramsan.edifikana.lib.model.network.employee.UpdateEmployeeNetworkRequest
import com.cramsan.framework.annotations.api.NoPathParam
import com.cramsan.framework.annotations.api.NoQueryParam
import com.cramsan.framework.annotations.api.NoRequestBody
import com.cramsan.framework.annotations.api.NoResponseBody
import com.cramsan.framework.networkapi.AdditionalResponses
import com.cramsan.framework.networkapi.Api
import com.cramsan.framework.networkapi.UniversalResponsesOnly
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode

/**
 * API definition for employee related operations.
 */

object EmployeeApi : Api("employee") {
    val createEmployee = operation<
        CreateEmployeeNetworkRequest,
        NoQueryParam,
        NoPathParam,
        EmployeeNetworkResponse,
    >(
        method = HttpMethod.Post,
        summary = "Create an employee",
        description = "Creates a new employee within a property. Requires the MANAGER role or higher.",
        responses = UniversalResponsesOnly,
    )

    val getEmployee = operation<
        NoRequestBody,
        NoQueryParam,
        EmployeeId,
        EmployeeNetworkResponse,
    >(
        method = HttpMethod.Get,
        summary = "Get an employee",
        description = "Retrieves a single employee by their identifier. Requires the EMPLOYEE role or higher.",
        responses =
        AdditionalResponses {
            HttpStatusCode.NotFound describedAs "No employee exists for the given id."
        },
    )

    val getEmployees = operation<
        NoRequestBody,
        NoQueryParam,
        NoPathParam,
        EmployeeListNetworkResponse,
    >(
        method = HttpMethod.Get,
        summary = "List employees",
        description = "Lists all employees accessible to the authenticated user. Requires the EMPLOYEE role or higher.",
        responses = UniversalResponsesOnly,
    )

    val updateEmployee = operation<
        UpdateEmployeeNetworkRequest,
        NoQueryParam,
        EmployeeId,
        EmployeeNetworkResponse,
    >(
        method = HttpMethod.Put,
        summary = "Update an employee",
        description = "Updates the mutable fields of an existing employee. Requires the MANAGER role or higher.",
        responses =
        AdditionalResponses {
            HttpStatusCode.NotFound describedAs "No employee exists for the given id."
        },
    )

    val deleteEmployee = operation<
        NoRequestBody,
        NoQueryParam,
        EmployeeId,
        NoResponseBody,
    >(
        method = HttpMethod.Delete,
        summary = "Delete an employee",
        description = "Permanently deletes an employee by their identifier. Requires the MANAGER role or higher.",
        responses =
        AdditionalResponses {
            HttpStatusCode.NotFound describedAs "No employee exists for the given id."
        },
    )
}
