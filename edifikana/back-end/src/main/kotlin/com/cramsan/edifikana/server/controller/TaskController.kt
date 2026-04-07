package com.cramsan.edifikana.server.controller

import com.cramsan.edifikana.api.TaskApi
import com.cramsan.edifikana.lib.model.TaskId
import com.cramsan.edifikana.lib.model.network.CreateTaskNetworkRequest
import com.cramsan.edifikana.lib.model.network.GetTasksQueryParams
import com.cramsan.edifikana.lib.model.network.TaskListNetworkResponse
import com.cramsan.edifikana.lib.model.network.TaskNetworkResponse
import com.cramsan.edifikana.lib.model.network.UpdateTaskNetworkRequest
import com.cramsan.edifikana.server.controller.authentication.SupabaseContextPayload
import com.cramsan.edifikana.server.service.TaskService
import com.cramsan.edifikana.server.service.authorization.RBACService
import com.cramsan.edifikana.server.service.models.UserRole
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.NoPathParam
import com.cramsan.framework.annotations.api.NoQueryParam
import com.cramsan.framework.annotations.api.NoRequestBody
import com.cramsan.framework.annotations.api.NoResponseBody
import com.cramsan.framework.core.ktor.Controller
import com.cramsan.framework.core.ktor.OperationHandler.register
import com.cramsan.framework.core.ktor.OperationRequest
import com.cramsan.framework.core.ktor.auth.ClientContext
import com.cramsan.framework.core.ktor.auth.ContextRetriever
import com.cramsan.framework.core.ktor.handler
import com.cramsan.framework.utils.exceptions.ClientRequestExceptions.NotFoundException
import com.cramsan.framework.utils.exceptions.ClientRequestExceptions.UnauthorizedException
import io.ktor.server.routing.Routing
import kotlin.time.ExperimentalTime

/**
 * Controller for task operations within a property.
 *
 * RBAC:
 * - GET operations require EMPLOYEE role or higher.
 * - POST / PUT / DELETE require MANAGER role or higher.
 */
@OptIn(NetworkModel::class, ExperimentalTime::class)
class TaskController(
    private val taskService: TaskService,
    private val rbacService: RBACService,
    private val contextRetriever: ContextRetriever<SupabaseContextPayload>,
) : Controller {

    private val unauthorizedMsg = "You are not authorized to perform this action in your organization."

    /**
     * Creates a new task. Requires MANAGER role or higher in the target property.
     */
    suspend fun createTask(
        request: OperationRequest<
            CreateTaskNetworkRequest,
            NoQueryParam,
            NoPathParam,
            ClientContext.AuthenticatedClientContext<SupabaseContextPayload>
            >
    ): TaskNetworkResponse {
        if (!rbacService.hasRoleOrHigher(request.context, request.requestBody.propertyId, UserRole.MANAGER)) {
            throw UnauthorizedException(unauthorizedMsg)
        }
        val callerUserId = request.context.payload.userId
        return taskService.createTask(
            propertyId = request.requestBody.propertyId,
            unitId = request.requestBody.unitId,
            commonAreaId = request.requestBody.commonAreaId,
            assigneeId = request.requestBody.assigneeId,
            createdBy = callerUserId,
            title = request.requestBody.title,
            description = request.requestBody.description,
            priority = request.requestBody.priority,
            dueDate = request.requestBody.dueDate,
        ).toTaskNetworkResponse()
    }

    /**
     * Retrieves a single task by [TaskId]. Requires EMPLOYEE role or higher.
     * Returns 404 if the task does not exist or the caller is not authorized.
     */
    suspend fun getTask(
        request: OperationRequest<
            NoRequestBody,
            NoQueryParam,
            TaskId,
            ClientContext.AuthenticatedClientContext<SupabaseContextPayload>
            >
    ): TaskNetworkResponse {
        if (!rbacService.hasRoleOrHigher(request.context, request.pathParam, UserRole.EMPLOYEE)) {
            throw NotFoundException("Task not found.")
        }
        return taskService.getTask(request.pathParam)?.toTaskNetworkResponse()
            ?: throw NotFoundException("Task not found.")
    }

    /**
     * Lists tasks for a property. Requires EMPLOYEE role or higher.
     */
    suspend fun getTasks(
        request: OperationRequest<
            NoRequestBody,
            GetTasksQueryParams,
            NoPathParam,
            ClientContext.AuthenticatedClientContext<SupabaseContextPayload>
            >
    ): TaskListNetworkResponse {
        if (!rbacService.hasRoleOrHigher(request.context, request.queryParam.propertyId, UserRole.EMPLOYEE)) {
            throw UnauthorizedException(unauthorizedMsg)
        }
        val tasks = taskService.getTasks(
            propertyId = request.queryParam.propertyId,
            unitId = request.queryParam.unitId,
            status = request.queryParam.status,
            assigneeId = request.queryParam.assigneeId,
            priority = request.queryParam.priority,
        ).map { it.toTaskNetworkResponse() }
        return TaskListNetworkResponse(tasks)
    }

    /**
     * Updates an existing task. Requires MANAGER role or higher.
     */
    suspend fun updateTask(
        request: OperationRequest<
            UpdateTaskNetworkRequest,
            NoQueryParam,
            TaskId,
            ClientContext.AuthenticatedClientContext<SupabaseContextPayload>
            >
    ): TaskNetworkResponse {
        if (!rbacService.hasRoleOrHigher(request.context, request.pathParam, UserRole.MANAGER)) {
            throw UnauthorizedException(unauthorizedMsg)
        }
        val callerUserId = request.context.payload.userId
        return taskService.updateTask(
            taskId = request.pathParam,
            title = request.requestBody.title,
            description = request.requestBody.description,
            priority = request.requestBody.priority,
            status = request.requestBody.status,
            assigneeId = request.requestBody.assigneeId,
            dueDate = request.requestBody.dueDate,
            callerUserId = callerUserId,
        ).toTaskNetworkResponse()
    }

    /**
     * Soft-deletes a task. Requires MANAGER role or higher.
     */
    suspend fun deleteTask(
        request: OperationRequest<
            NoRequestBody,
            NoQueryParam,
            TaskId,
            ClientContext.AuthenticatedClientContext<SupabaseContextPayload>
            >
    ): NoResponseBody {
        if (!rbacService.hasRoleOrHigher(request.context, request.pathParam, UserRole.MANAGER)) {
            throw UnauthorizedException(unauthorizedMsg)
        }
        taskService.deleteTask(request.pathParam)
        return NoResponseBody
    }

    /**
     * Registers all task routes.
     */
    override fun registerRoutes(route: Routing) {
        TaskApi.register(route) {
            handler(api.createTask, contextRetriever) { request ->
                createTask(request)
            }
            handler(api.getTask, contextRetriever) { request ->
                getTask(request)
            }
            handler(api.getTasks, contextRetriever) { request ->
                getTasks(request)
            }
            handler(api.updateTask, contextRetriever) { request ->
                updateTask(request)
            }
            handler(api.deleteTask, contextRetriever) { request ->
                deleteTask(request)
            }
        }
    }
}
