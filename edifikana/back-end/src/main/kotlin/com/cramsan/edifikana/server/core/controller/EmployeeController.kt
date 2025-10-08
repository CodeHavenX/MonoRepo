package com.cramsan.edifikana.server.core.controller

import com.cramsan.edifikana.api.EmployeeApi
import com.cramsan.edifikana.lib.model.EmployeeId
import com.cramsan.edifikana.lib.model.network.CreateEmployeeNetworkRequest
import com.cramsan.edifikana.lib.model.network.EmployeeListNetworkResponse
import com.cramsan.edifikana.lib.model.network.EmployeeNetworkResponse
import com.cramsan.edifikana.lib.model.network.UpdateEmployeeNetworkRequest
import com.cramsan.edifikana.server.core.controller.authentication.ClientContext
import com.cramsan.edifikana.server.core.controller.authentication.ContextRetriever
import com.cramsan.edifikana.server.core.service.EmployeeService
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.NoPathParam
import com.cramsan.framework.annotations.api.NoQueryParam
import com.cramsan.framework.annotations.api.NoRequestBody
import com.cramsan.framework.annotations.api.NoResponseBody
import com.cramsan.framework.core.ktor.OperationHandler.register
import com.cramsan.framework.core.ktor.OperationRequest
import io.ktor.server.routing.Routing

/**
 * Controller for employee related operations. CRUD operations for employee.
 */
class EmployeeController(
    private val employeeService: EmployeeService,
    private val contextRetriever: ContextRetriever,
) : Controller {

    /**
     * Creates a new employee using the provided request data.
     * Returns the created employee as a network response.
     */
    @OptIn(NetworkModel::class)
    suspend fun createEmployee(
        createEmpRequest: CreateEmployeeNetworkRequest,
    ): EmployeeNetworkResponse {
        val newEmployee = employeeService.createEmployee(
            idType = createEmpRequest.idType,
            firstName = createEmpRequest.firstName,
            lastName = createEmpRequest.lastName,
            role = createEmpRequest.role,
            propertyId = createEmpRequest.propertyId,
        ).toEmployeeNetworkResponse()
        return newEmployee
    }

    /**
     * Retrieves an employee by their [employeeId].
     * Returns the employee as a network response, or null if not found.
     */
    @OptIn(NetworkModel::class)
    suspend fun getEmployee(
        employeeId: EmployeeId,
    ): EmployeeNetworkResponse? {
        return employeeService.getEmployee(
            employeeId,
        )?.toEmployeeNetworkResponse()
    }

    /**
     * Retrieves all employees for the authenticated context.
     * Returns a list of employees as a network response.
     */
    @OptIn(NetworkModel::class)
    suspend fun getEmployees(
        request: OperationRequest<NoRequestBody, NoQueryParam, NoPathParam, ClientContext.AuthenticatedClientContext>
    ): EmployeeListNetworkResponse {
        val employees = employeeService.getEmployees(request.context).map { it.toEmployeeNetworkResponse() }
        return EmployeeListNetworkResponse(employees)
    }

    /**
     * Updates an employee identified by [employeeId] with the provided request data.
     * Returns the updated employee as a network response.
     */
    @OptIn(NetworkModel::class)
    suspend fun updateEmployee(
        updateEmpRequest: UpdateEmployeeNetworkRequest,
        employeeId: EmployeeId,
    ): EmployeeNetworkResponse {
        val updatedEmployee = employeeService.updateEmployee(
            id = employeeId,
            idType = updateEmpRequest.idType,
            firstName = updateEmpRequest.firstName,
            lastName = updateEmpRequest.lastName,
            role = updateEmpRequest.role,
        ).toEmployeeNetworkResponse()
        return updatedEmployee
    }

    /**
     * Deletes an employee identified by [employeeId].
     * Returns [NoResponseBody] to indicate successful deletion.
     */
    suspend fun deleteEmployee(
        employeeId: EmployeeId,
    ): NoResponseBody {
        employeeService.deleteEmployee(employeeId)
        return NoResponseBody
    }

    /**
     * Registers the routes for the employee controller.
     * Sets up the API endpoints and handlers for employee operations.
     */
    @OptIn(NetworkModel::class)
    override fun registerRoutes(route: Routing) {
        EmployeeApi.register(route) {
            handler(api.createEmployee, contextRetriever) { request ->
                createEmployee(request.requestBody)
            }
            handler(api.getEmployee, contextRetriever) { request ->
                getEmployee(request.pathParam)
            }
            handler(api.getEmployees, contextRetriever) { request ->
                getEmployees(request)
            }
            handler(api.updateEmployee, contextRetriever) { request ->
                updateEmployee(request.requestBody, request.pathParam)
            }
            handler(api.deleteEmployee, contextRetriever) { request ->
                deleteEmployee(request.pathParam)
            }
        }
    }
}
