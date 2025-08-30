package com.cramsan.edifikana.server.core.controller

import com.cramsan.edifikana.lib.Routes
import com.cramsan.edifikana.lib.Routes.Staff.QueryParams.STAFF_ID
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.edifikana.lib.model.StaffId
import com.cramsan.edifikana.lib.model.network.CreateStaffNetworkRequest
import com.cramsan.edifikana.lib.model.network.UpdateStaffNetworkRequest
import com.cramsan.edifikana.lib.utils.ClientRequestExceptions
import com.cramsan.edifikana.server.core.controller.authentication.ClientContext
import com.cramsan.edifikana.server.core.controller.authentication.ContextRetriever
import com.cramsan.edifikana.server.core.service.StaffService
import com.cramsan.edifikana.server.core.service.authorization.RoleBasedAccessControlService
import com.cramsan.edifikana.server.core.service.models.UserRole
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
 * Controller for staff related operations. CRUD operations for staff.
 */
class StaffController(
    private val staffService: StaffService,
    private val rbacService: RoleBasedAccessControlService,
    private val contextRetriever: ContextRetriever,
) : Controller {

    /**
     * Handles the creation of a new staff. The [call] parameter is the request context.
     */
    @OptIn(NetworkModel::class)
    suspend fun createStaff(call: ApplicationCall) = call.handleCall(
        TAG,
        "createStaff",
        contextRetriever,
    ) { context ->
        // Only OWNERS can create new staff
        val createStaffRequest = call.receive<CreateStaffNetworkRequest>()

        if (!rbacService.hasRoleOrHigher(context, PropertyId(createStaffRequest.propertyId), UserRole.ADMIN)) {
            throw ClientRequestExceptions.UnauthorizedException(
                "You do not have permissions to create new employees."
            )
        }

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
    suspend fun getStaff(call: ApplicationCall) = call.handleCall(
        TAG,
        "getStaff",
        contextRetriever,
    ) { context ->
        val staffId = requireNotNull(call.parameters[STAFF_ID])
        checkAuthorization(context, StaffId(staffId), UserRole.MANAGER)

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
     * TODO: ADD A CHECK THAT THE REQUESTER IS REQUESTING ALL STAFF FROM THEIR ORG/PROPERTY
     */
    @OptIn(NetworkModel::class)
    suspend fun getStaffs(call: ApplicationCall) = call.handleCall(
        TAG,
        "getStaffs",
        contextRetriever,
    ) { context ->
        if (!rbacService.hasRoleOrHigher(context, UserRole.MANAGER)) {
            throw ClientRequestExceptions.UnauthorizedException(
                "You do not have permission to see all Staff for your organization."
            )
        }

        val staffs = staffService.getStaffs(context).map { it.toStaffNetworkResponse() }

        HttpResponse(
            status = HttpStatusCode.OK,
            body = staffs,
        )
    }

    /**
     * Handles the updating of a staff. The [call] parameter is the request context.
     */
    @OptIn(NetworkModel::class)
    suspend fun updateStaff(call: ApplicationCall) = call.handleCall(
        TAG,
        "updateStaff",
        contextRetriever,
    ) { context ->
        val staffId = requireNotNull(call.parameters[STAFF_ID])
        checkAuthorization(context, StaffId(staffId), UserRole.MANAGER)

        val updateStaffRequest = call.receive<UpdateStaffNetworkRequest>()

        val updatedStaff = staffService.updateStaff(
            id = StaffId(staffId),
            idType = updateStaffRequest.idType,
            firstName = updateStaffRequest.firstName,
            lastName = updateStaffRequest.lastName,
            role = updateStaffRequest.role,
        ).toStaffNetworkResponse()

        HttpResponse(
            status = HttpStatusCode.OK,
            body = updatedStaff,
        )
    }

    /**
     * Handles the deletion of a staff. The [call] parameter is the request context.
     */
    suspend fun deleteStaff(call: RoutingCall) = call.handleCall(
        TAG,
        "deleteStaff",
        contextRetriever
    ) { context ->
        val staffId = requireNotNull(call.parameters[STAFF_ID])
        checkAuthorization(context, StaffId(staffId), UserRole.MANAGER)

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
     * Checks if the authenticated user has the required role or higher to perform actions on the target staff.
     */
    private suspend fun checkAuthorization(
        context: ClientContext.AuthenticatedClientContext,
        targetStaff: StaffId,
        requiredRole: UserRole
    ) {
        if (!rbacService.hasRoleOrHigher(context, targetStaff, requiredRole)) {
            throw ClientRequestExceptions.UnauthorizedException(
                "You do not have permissions to edit/delete staff."
            )
        }
    }

    /**
     * Registers the routes for the staff controller. The [route] parameter is the root path for the controller.
     */
    override fun registerRoutes(route: Routing) {
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
            put("{$STAFF_ID}") {
                updateStaff(call)
            }
            delete("{$STAFF_ID}") {
                deleteStaff(call)
            }
        }
    }

    /**
     * Companion object.
     */
    companion object {
        private const val TAG = "StaffController"
    }
}
