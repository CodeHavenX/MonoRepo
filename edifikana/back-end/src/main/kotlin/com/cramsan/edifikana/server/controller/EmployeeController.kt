package com.cramsan.edifikana.server.controller

import com.cramsan.edifikana.api.EmployeeApi
import com.cramsan.edifikana.lib.model.EmployeeId
import com.cramsan.edifikana.lib.model.network.CreateEmployeeNetworkRequest
import com.cramsan.edifikana.lib.model.network.EmployeeListNetworkResponse
import com.cramsan.edifikana.lib.model.network.EmployeeNetworkResponse
import com.cramsan.edifikana.lib.model.network.UpdateEmployeeNetworkRequest
import com.cramsan.edifikana.server.controller.authentication.ClientContext
import com.cramsan.edifikana.server.controller.authentication.ContextRetriever
import com.cramsan.edifikana.server.service.EmployeeService
import com.cramsan.edifikana.server.service.authorization.RBACService
import com.cramsan.edifikana.server.service.models.UserRole
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.NoPathParam
import com.cramsan.framework.annotations.api.NoQueryParam
import com.cramsan.framework.annotations.api.NoRequestBody
import com.cramsan.framework.annotations.api.NoResponseBody
import com.cramsan.framework.core.ktor.OperationHandler.register
import com.cramsan.framework.core.ktor.OperationRequest
import com.cramsan.framework.utils.exceptions.UnauthorizedException
import io.ktor.server.routing.Routing

/**
 * Controller for employee related operations. CRUD operations for employee.
 */
class EmployeeController(
    private val employeeService: EmployeeService,
    private val contextRetriever: ContextRetriever,
    private val rbacService: RBACService,
) : Controller {

    val unauthorizedMsg = "You are not authorized to perform this action in your organization."

    /**
     * Creates a new employee using the provided request data.
     * Returns the created employee as a network response.
     */
    @OptIn(NetworkModel::class)
    suspend fun createEmployee(
        request:
        OperationRequest<
            CreateEmployeeNetworkRequest,
            NoQueryParam,
            NoPathParam,
            ClientContext.AuthenticatedClientContext
            >,
    ): EmployeeNetworkResponse {
        if (!rbacService.hasRoleOrHigher(
                request.context,
                request.requestBody.propertyId,
                UserRole.ADMIN
            )
        ) {
            throw UnauthorizedException(unauthorizedMsg)
        }

        val createEmpRequest = request.requestBody
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
        request:
        OperationRequest<
            NoRequestBody,
            NoQueryParam,
            EmployeeId,
            ClientContext.AuthenticatedClientContext
            >,
    ): EmployeeNetworkResponse? {
        checkHasRole(
            request.context,
            request.pathParam,
            UserRole.MANAGER
        )
        return employeeService.getEmployee(
            request.pathParam,
        )?.toEmployeeNetworkResponse()
    }

    /**
     * Retrieves all employees for the authenticated context.
     * Returns a list of employees as a network response.
     * TODO: Update to pass the organizationID with the request
     */
    @OptIn(NetworkModel::class)
    suspend fun getEmployees(
        request:
        OperationRequest<
            NoRequestBody,
            NoQueryParam,
            NoPathParam,
            ClientContext.AuthenticatedClientContext
            >
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
        request:
        OperationRequest<
            UpdateEmployeeNetworkRequest,
            NoQueryParam,
            EmployeeId,
            ClientContext.AuthenticatedClientContext
            >,
    ): EmployeeNetworkResponse {
        checkHasRole(
            request.context,
            request.pathParam,
            UserRole.ADMIN
        )
        val updateEmpRequest = request.requestBody
        val updatedEmployee = employeeService.updateEmployee(
            id = request.pathParam,
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
        request:
        OperationRequest<
            NoRequestBody,
            NoQueryParam,
            EmployeeId,
            ClientContext.AuthenticatedClientContext
            >,
    ): NoResponseBody {
        checkHasRole(
            request.context,
            request.pathParam,
            UserRole.ADMIN
        )
        employeeService.deleteEmployee(request.pathParam)
        return NoResponseBody
    }

    /**
     * Checks if the user in the given context has at least the required role for the specified employee.
     * Throws [UnauthorizedException] if the user does not have the required role.
     *
     * @param context The authenticated client context containing user information.
     * @param empId The ID of the employee to check against.
     * @param requireRole The minimum required role to perform the action.
     * @throws UnauthorizedException if the user lacks the required role.
     */
    private suspend fun checkHasRole(
        context: ClientContext.AuthenticatedClientContext,
        empId: EmployeeId,
        requireRole: UserRole,
    ) {
        if (!rbacService.hasRoleOrHigher(context, empId, requireRole)) {
            throw UnauthorizedException(unauthorizedMsg)
        }
    }

    /**
     * Registers the routes for the employee controller.
     * Sets up the API endpoints and handlers for employee operations.
     */
    @OptIn(NetworkModel::class)
    override fun registerRoutes(route: Routing) {
        EmployeeApi.register(route) {
            handler(api.createEmployee, contextRetriever) { request ->
                createEmployee(request)
            }
            handler(api.getEmployee, contextRetriever) { request ->
                getEmployee(request)
            }
            handler(api.getEmployees, contextRetriever) { request ->
                getEmployees(request)
            }
            handler(api.updateEmployee, contextRetriever) { request ->
                updateEmployee(request)
            }
            handler(api.deleteEmployee, contextRetriever) { request ->
                deleteEmployee(request)
            }
        }
    }
}
