package com.cramsan.edifikana.server.core.controller

import com.cramsan.edifikana.lib.Routes
import com.cramsan.edifikana.lib.Routes.Employee.QueryParams.EMPLOYEE_ID
import com.cramsan.edifikana.lib.model.EmployeeId
import com.cramsan.edifikana.lib.model.network.CreateEmployeeNetworkRequest
import com.cramsan.edifikana.lib.model.network.UpdateEmployeeNetworkRequest
import com.cramsan.edifikana.server.core.controller.authentication.ContextRetriever
import com.cramsan.edifikana.server.core.service.EmployeeService
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.core.ktor.HttpResponse
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.request.receive
import io.ktor.server.routing.Routing
import io.ktor.server.routing.RoutingCall
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route

/**
 * Controller for mployee related operations. CRUD operations for employee.
 */
class EmployeeController(
    private val employeeService: EmployeeService,
    private val contextRetriever: ContextRetriever,
) : Controller {

    /**
     * Handles the creation of a new mployee. The [call] parameter is the request context.
     */
    @OptIn(NetworkModel::class)
    suspend fun createEmployee(call: ApplicationCall) = call.handleCall(
        TAG,
        "createEmployee",
        contextRetriever,
    ) { _ ->
        val createEmpRequest = call.receive<CreateEmployeeNetworkRequest>()

        val newEmployee = employeeService.createEmployee(
            idType = createEmpRequest.idType,
            firstName = createEmpRequest.firstName,
            lastName = createEmpRequest.lastName,
            role = createEmpRequest.role,
            propertyId = createEmpRequest.propertyId,
        ).toEmployeeNetworkResponse()

        HttpResponse(
            status = HttpStatusCode.OK,
            body = newEmployee,
        )
    }

    /**
     * Handles the retrieval of a employee. The [call] parameter is the request context.
     */
    @OptIn(NetworkModel::class)
    suspend fun getEmployee(call: ApplicationCall) = call.handleCall(
        TAG,
        "getEmployee" +
            "",
        contextRetriever,
    ) { _ ->
        val employeeId = requireNotNull(call.parameters[EMPLOYEE_ID])

        val employee = employeeService.getEmployee(
            EmployeeId(employeeId),
        )?.toEmployeeNetworkResponse()

        val statusCode = if (employee == null) {
            HttpStatusCode.NotFound
        } else {
            HttpStatusCode.OK
        }

        HttpResponse(
            status = statusCode,
            body = employee,
        )
    }

    /**
     * Handles the retrieval of all employee. The [call] parameter is the request context.
     */
    @OptIn(NetworkModel::class)
    suspend fun getEmployees(call: ApplicationCall) = call.handleCall(
        TAG,
        "getEmployees",
        contextRetriever,
    ) { context ->
        val authenticatedClientContext = requireAuthenticatedClientContext(context)
        val employees = employeeService.getEmployees(authenticatedClientContext).map { it.toEmployeeNetworkResponse() }

        HttpResponse(
            status = HttpStatusCode.OK,
            body = employees,
        )
    }

    /**
     * Handles the updating of a employee. The [call] parameter is the request context.
     */
    @OptIn(NetworkModel::class)
    suspend fun updateEmployee(call: ApplicationCall) = call.handleCall(
        TAG,
        "updateEmployee",
        contextRetriever,
    ) { _ ->
        val empId = requireNotNull(call.parameters[EMPLOYEE_ID])

        val updateEmpRequest = call.receive<UpdateEmployeeNetworkRequest>()

        val updatedEmployee = employeeService.updateEmployee(
            id = EmployeeId(empId),
            idType = updateEmpRequest.idType,
            firstName = updateEmpRequest.firstName,
            lastName = updateEmpRequest.lastName,
            role = updateEmpRequest.role,
        ).toEmployeeNetworkResponse()

        HttpResponse(
            status = HttpStatusCode.OK,
            body = updatedEmployee,
        )
    }

    /**
     * Handles the deletion of a employee. The [call] parameter is the request context.
     */
    suspend fun deleteEmployee(call: RoutingCall) = call.handleCall(TAG, "deleteEmployee", contextRetriever) {
        val empId = requireNotNull(call.parameters[EMPLOYEE_ID])

        val success = employeeService.deleteEmployee(
            EmployeeId(empId),
        )

        val statusCode = if (success) {
            HttpStatusCode.OK
        } else {
            HttpStatusCode.NotFound
        }

        HttpResponse(
            status = statusCode,
            body = null,
        )
    }

    /**
     * Registers the routes for the employee controller. The [route] parameter is the root path for the controller.
     */
    override fun registerRoutes(route: Routing) {
        route.route(Routes.Employee.PATH) {
            post {
                createEmployee(call)
            }
            get("{$EMPLOYEE_ID}") {
                getEmployee(call)
            }
            get {
                getEmployees(call)
            }
            put("{$EMPLOYEE_ID}") {
                updateEmployee(call)
            }
            delete("{$EMPLOYEE_ID}") {
                deleteEmployee(call)
            }
        }
    }

    /**
     * Companion object.
     */
    companion object {
        private const val TAG = "EmployeeController"
    }
}
