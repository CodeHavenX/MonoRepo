package com.cramsan.edifikana.api

import com.cramsan.edifikana.lib.model.EmployeeId
import com.cramsan.edifikana.lib.model.network.CreateEmployeeNetworkRequest
import com.cramsan.edifikana.lib.model.network.EmployeeListNetworkResponse
import com.cramsan.edifikana.lib.model.network.EmployeeNetworkResponse
import com.cramsan.edifikana.lib.model.network.UpdateEmployeeNetworkRequest
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.NoPathParam
import com.cramsan.framework.annotations.api.NoQueryParam
import com.cramsan.framework.annotations.api.NoRequestBody
import com.cramsan.framework.annotations.api.NoResponseBody
import com.cramsan.framework.networkapi.Api
import io.ktor.http.HttpMethod

/**
 * API definition for employee related operations.
 */
@OptIn(NetworkModel::class)
object EmployeeApi : Api("employee") {
    val createEmployee = operation<
        CreateEmployeeNetworkRequest,
        NoQueryParam,
        NoPathParam,
        EmployeeNetworkResponse,
        >(HttpMethod.Post)

    val getEmployee = operation<
        NoRequestBody,
        NoQueryParam,
        EmployeeId,
        EmployeeNetworkResponse,
        >(HttpMethod.Get)

    val getEmployees = operation<
        NoRequestBody,
        NoQueryParam,
        NoPathParam,
        EmployeeListNetworkResponse,
        >(HttpMethod.Get)

    val updateEmployee = operation<
        UpdateEmployeeNetworkRequest,
        NoQueryParam,
        EmployeeId,
        EmployeeNetworkResponse,
        >(HttpMethod.Put)

    val deleteEmployee = operation<
        NoRequestBody,
        NoQueryParam,
        EmployeeId,
        NoResponseBody,
        >(HttpMethod.Delete)
}
