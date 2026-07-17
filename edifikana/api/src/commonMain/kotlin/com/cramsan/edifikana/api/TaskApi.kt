package com.cramsan.edifikana.api

import com.cramsan.edifikana.lib.model.network.task.CreateTaskNetworkRequest
import com.cramsan.edifikana.lib.model.network.task.GetTasksQueryParams
import com.cramsan.edifikana.lib.model.network.task.TaskListNetworkResponse
import com.cramsan.edifikana.lib.model.network.task.TaskNetworkResponse
import com.cramsan.edifikana.lib.model.network.task.UpdateTaskNetworkRequest
import com.cramsan.edifikana.lib.model.task.TaskId
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
 * API definition for task operations.
 *
 * Tasks are property-scoped resources representing maintenance or operational work items.
 * They can optionally be sub-scoped to a unit or common area within the property.
 *
 * RBAC:
 * - GET operations: EMPLOYEE role or higher
 * - POST / PUT / DELETE (create / update / delete): MANAGER role or higher
 */

object TaskApi : Api("task") {
    val createTask = operation<
        CreateTaskNetworkRequest,
        NoQueryParam,
        NoPathParam,
        TaskNetworkResponse,
    >(
        method = HttpMethod.Post,
        summary = "Create a task",
        description = "Creates a new task within a property. Requires the MANAGER role or higher.",
        responses = UniversalResponsesOnly,
    )

    val getTask = operation<
        NoRequestBody,
        NoQueryParam,
        TaskId,
        TaskNetworkResponse,
    >(
        method = HttpMethod.Get,
        summary = "Get a task",
        description = "Retrieves a single task by its identifier. Requires the EMPLOYEE role or higher.",
        responses =
        AdditionalResponses {
            HttpStatusCode.NotFound describedAs "No task exists for the given id."
        },
    )

    val getTasks = operation<
        NoRequestBody,
        GetTasksQueryParams,
        NoPathParam,
        TaskListNetworkResponse,
    >(
        method = HttpMethod.Get,
        path = "list",
        summary = "List tasks",
        description = "Lists tasks for a property with optional filtering by unit, status, assignee, and " +
            "priority. Requires the EMPLOYEE role or higher.",
        responses = UniversalResponsesOnly,
    )

    val updateTask = operation<
        UpdateTaskNetworkRequest,
        NoQueryParam,
        TaskId,
        TaskNetworkResponse,
    >(
        method = HttpMethod.Put,
        summary = "Update a task",
        description = "Updates the mutable fields of an existing task. Only provided fields are changed. " +
            "Requires the MANAGER role or higher.",
        responses =
        AdditionalResponses {
            HttpStatusCode.NotFound describedAs "No task exists for the given id."
        },
    )

    val deleteTask = operation<
        NoRequestBody,
        NoQueryParam,
        TaskId,
        NoResponseBody,
    >(
        method = HttpMethod.Delete,
        summary = "Delete a task",
        description = "Permanently deletes a task by its identifier. Requires the MANAGER role or higher.",
        responses =
        AdditionalResponses {
            HttpStatusCode.NotFound describedAs "No task exists for the given id."
        },
    )
}
