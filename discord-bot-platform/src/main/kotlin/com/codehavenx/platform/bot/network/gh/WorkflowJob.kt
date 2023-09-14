package com.codehavenx.platform.bot.network.gh

import kotlinx.serialization.SerialName
import java.util.Date

data class WorkflowJob(
    @SerialName("id")
    val id: Long = 0,
    @SerialName("run_id")
    val runId: Long = 0,
    @SerialName("workflow_name")
    val workflowName: String? = null,
    @SerialName("head_branch")
    val headBranch: String? = null,
    @SerialName("run_url")
    val runUrl: String? = null,
    @SerialName("run_attempt")
    val runAttempt: Long = 0,
    @SerialName("node_id")
    val nodeId: String? = null,
    @SerialName("head_sha")
    val headSha: String? = null,
    @SerialName("url")
    val url: String? = null,
    @SerialName("html_url")
    val htmlUrl: String? = null,
    @SerialName("status")
    val status: String? = null,
    @SerialName("conclusion")
    val conclusion: String? = null,
    @SerialName("created_at")
    val createdAt: Date? = null,
    @SerialName("started_at")
    val startedAt: Date? = null,
    @SerialName("completed_at")
    val completedAt: Date? = null,
    @SerialName("name")
    val name: String? = null,
    @SerialName("steps")
    val steps: ArrayList<Step>? = null,
    @SerialName("check_run_url")
    val checkRunUrl: String? = null,
    @SerialName("labels")
    val labels: ArrayList<String>? = null,
    @SerialName("runner_id")
    val runnerId: Long = 0,
    @SerialName("runner_name")
    val runnerName: String? = null,
    @SerialName("runner_group_id")
    val runnerGroupId: Long = 0,
    @SerialName("runner_group_name")
    val runnerGroupName: String? = null,
)
