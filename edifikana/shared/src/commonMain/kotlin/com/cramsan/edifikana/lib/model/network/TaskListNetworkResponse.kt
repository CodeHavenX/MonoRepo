package com.cramsan.edifikana.lib.model.network

import com.cramsan.edifikana.lib.model.network.task.TaskNetworkResponse
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.ResponseBody
import kotlinx.serialization.Serializable

/**
 * Network response containing a list of tasks.
 */
@NetworkModel
@Serializable
data class TaskListNetworkResponse(
    val tasks: List<TaskNetworkResponse>,
) : ResponseBody
