package com.cramsan.edifikana.lib.model.network

import com.cramsan.edifikana.lib.model.TaskPriority
import com.cramsan.edifikana.lib.model.TaskStatus
import com.cramsan.edifikana.lib.model.UserId
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.RequestBody
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/**
 * Network request to update an existing task. Only provided (non-null) fields are updated.
 */
@NetworkModel
@Serializable
@OptIn(ExperimentalTime::class)
data class UpdateTaskNetworkRequest(
    val title: String? = null,
    val description: String? = null,
    val priority: TaskPriority? = null,
    val status: TaskStatus? = null,
    @SerialName("assignee_id") val assigneeId: UserId? = null,
    @SerialName("due_date") val dueDate: Instant? = null,
) : RequestBody
