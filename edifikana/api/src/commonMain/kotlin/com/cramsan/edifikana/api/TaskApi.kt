package com.cramsan.edifikana.api

import com.cramsan.edifikana.lib.model.task.TaskId
import com.cramsan.edifikana.lib.model.network.task.CreateTaskNetworkRequest
import com.cramsan.edifikana.lib.model.network.task.GetTasksQueryParams
import com.cramsan.edifikana.lib.model.network.task.TaskListNetworkResponse
import com.cramsan.edifikana.lib.model.network.task.TaskNetworkResponse
import com.cramsan.edifikana.lib.model.network.task.UpdateTaskNetworkRequest
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.NoPathParam
import com.cramsan.framework.annotations.api.NoQueryParam
import com.cramsan.framework.annotations.api.NoRequestBody
import com.cramsan.framework.annotations.api.NoResponseBody
import com.cramsan.framework.networkapi.Api
import io.ktor.http.HttpMethod

/**
 * API definition for task operations.
 *
 * Tasks are property-scoped resources representing maintenance or operational work items.
 * They can optionally be sub-scoped to a unit or common area within the property.
 *
 * RBAC:
 * - GET operations: EMPLOYEE role or higher
 * - POST / PUT / DELETE (create / update / delete): MANAGER role or higher
 */
@OptIn(NetworkModel::class)
object TaskApi : Api("task") {

    val createTask = operation<
        CreateTaskNetworkRequest,
        NoQueryParam,
        NoPathParam,
        TaskNetworkResponse
        >(HttpMethod.Post)

    val getTask = operation<
        NoRequestBody,
        NoQueryParam,
        TaskId,
        TaskNetworkResponse
        >(HttpMethod.Get)

    val getTasks = operation<
        NoRequestBody,
        GetTasksQueryParams,
        NoPathParam,
        TaskListNetworkResponse
        >(HttpMethod.Get, "list")

    val updateTask = operation<
        UpdateTaskNetworkRequest,
        NoQueryParam,
        TaskId,
        TaskNetworkResponse
        >(HttpMethod.Put)

    val deleteTask = operation<
        NoRequestBody,
        NoQueryParam,
        TaskId,
        NoResponseBody
        >(HttpMethod.Delete)
}
