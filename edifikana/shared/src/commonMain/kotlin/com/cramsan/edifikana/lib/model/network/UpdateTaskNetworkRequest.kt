package com.cramsan.edifikana.lib.model.network

import com.cramsan.edifikana.lib.model.TaskPriority
import com.cramsan.edifikana.lib.model.TaskStatus
import com.cramsan.edifikana.lib.model.UserId
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.RequestBody
import kotlinx.datetime.LocalDate
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Network request to update an existing task. Only provided (non-null) fields are updated.
 */
@NetworkModel
@Serializable
data class UpdateTaskNetworkRequest(
    val title: String?,
    val description: String?,
    val priority: TaskPriority?,
    val status: TaskStatus?,
    @SerialName("assignee_id") val assigneeId: UserId?,
    @SerialName("due_date") val dueDate: LocalDate?,
) : RequestBody
