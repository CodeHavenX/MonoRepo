package com.cramsan.edifikana.server.core.controller

import com.cramsan.edifikana.lib.Routes
import com.cramsan.edifikana.lib.STAFF_ID
import com.cramsan.edifikana.lib.annotations.NetworkModel
import com.cramsan.edifikana.lib.model.CreateStaffNetworkRequest
import com.cramsan.edifikana.server.core.service.StaffService
import com.cramsan.edifikana.server.core.service.models.PropertyId
import com.cramsan.edifikana.server.core.service.models.StaffId
import com.cramsan.edifikana.server.core.service.models.requests.UpdateStaffRequest
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
 * Controller for staff related operations. CRUD operations for staff.
 */
class StaffController(
    private val staffService: StaffService,
) {

    /**
     * Handles the creation of a new staff. The [call] parameter is the request context.
     */
    @OptIn(NetworkModel::class)
    suspend fun createStaff(call: ApplicationCall) = call.handleCall(TAG, "createStaff") {
        val createStaffRequest = call.receive<CreateStaffNetworkRequest>()

        val newStaff = staffService.createStaff(
            idType = createStaffRequest.idType,
            firstName = createStaffRequest.firstName,
            lastName = createStaffRequest.lastName,
            role = createStaffRequest.role,
            propertyId = PropertyId(createStaffRequest.propertyId),
        ).toStaffNetworkResponse()

        HttpResponse(
            status = HttpStatusCode.OK,
            body = newStaff,
        )
    }

    /**
     * Handles the retrieval of a staff. The [call] parameter is the request context.
     */
    @OptIn(NetworkModel::class)
    suspend fun getStaff(call: ApplicationCall) = call.handleCall(TAG, "getStaff") {
        val staffId = requireNotNull(call.parameters[STAFF_ID])

        val staff = staffService.getStaff(
            StaffId(staffId),
        )?.toStaffNetworkResponse()

        val statusCode = if (staff == null) {
            HttpStatusCode.NotFound
        } else {
            HttpStatusCode.OK
        }

        HttpResponse(
            status = statusCode,
            body = staff,
        )
    }

    /**
     * Handles the retrieval of all staff. The [call] parameter is the request context.
     */
    @OptIn(NetworkModel::class)
    suspend fun getStaffs(call: ApplicationCall) = call.handleCall(TAG, "getStaffs") {
        val staffs = staffService.getStaffs().map { it.toStaffNetworkResponse() }

        HttpResponse(
            status = HttpStatusCode.OK,
            body = staffs,
        )
    }

    /**
     * Handles the updating of a staff. The [call] parameter is the request context.
     */
    @OptIn(NetworkModel::class)
    suspend fun updateStaff(call: ApplicationCall) = call.handleCall(TAG, "updateStaff") {
        val staffId = requireNotNull(call.parameters[STAFF_ID])

        val updateStaffRequest = call.receive<UpdateStaffRequest>()

        val updatedStaff = staffService.updateStaff(
            id = StaffId(staffId),
            idType = updateStaffRequest.idType,
            firstName = updateStaffRequest.firstName,
            lastName = updateStaffRequest.lastName,
            role = updateStaffRequest.role,
            propertyId = updateStaffRequest.propertyId,
        ).toStaffNetworkResponse()

        HttpResponse(
            status = HttpStatusCode.OK,
            body = updatedStaff,
        )
    }

    /**
     * Handles the deletion of a staff. The [call] parameter is the request context.
     */
    suspend fun deleteStaff(call: RoutingCall) {
        val staffId = requireNotNull(call.parameters[STAFF_ID])

        val success = staffService.deleteStaff(
            StaffId(staffId),
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
     * Companion object.
     */
    companion object {
        private const val TAG = "StaffController"

        /**
         * Registers the routes for the staff controller. The [route] parameter is the root path for the controller.
         */
        fun StaffController.registerRoutes(route: Routing) {
            route.route(Routes.Staff.PATH) {
                post {
                    createStaff(call)
                }
                get("{$STAFF_ID}") {
                    getStaff(call)
                }
                get {
                    getStaffs(call)
                }
                put {
                    updateStaff(call)
                }
                delete {
                    deleteStaff(call)
                }
            }
        }
    }
}
