package com.codehavenx.platform.bot.network.gh

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class WorkflowJobsPayload(
    val action: String? = null,
    @SerialName("workflow_job")
    val workflowJob: WorkflowJob? = null,
    val repository: Repository? = null,
    val organization: Organization? = null,
    val sender: Sender? = null,
)
