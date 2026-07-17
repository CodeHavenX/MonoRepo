package com.cramsan.edifikana.lib.model.network.task

import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.ResponseBody
import io.ktor.openapi.JsonSchema
import kotlinx.serialization.Serializable

/**
 * Network response containing a list of tasks.
 */
@NetworkModel
@Serializable
@JsonSchema.Description("A list of tasks.")
data class TaskListNetworkResponse(
    @JsonSchema.Description("The tasks matching the request.")
    val tasks: List<TaskNetworkResponse>,
) : ResponseBody
