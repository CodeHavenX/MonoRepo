package com.cramsan.edifikana.lib.model.network

import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.ResponseBody
import kotlinx.serialization.Serializable

/**
 * Model representing a list of employees.
 */
@NetworkModel
@Serializable
data class EmployeeListNetworkResponse(
    val content: List<EmployeeNetworkResponse>,
) : ResponseBody
