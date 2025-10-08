package com.cramsan.edifikana.lib.model.network

import com.cramsan.edifikana.lib.model.EmployeeId
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.QueryParam
import kotlinx.serialization.Serializable

/**
 * Query parameters for fetching time card events, optionally filtered by employee ID.
 */
@NetworkModel
@Serializable
data class GetTimeCardEventsQueryParams(
    val employeeId: EmployeeId?,
) : QueryParam
