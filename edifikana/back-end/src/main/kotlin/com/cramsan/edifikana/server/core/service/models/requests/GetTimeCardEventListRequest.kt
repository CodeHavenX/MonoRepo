package com.cramsan.edifikana.server.core.service.models.requests

import com.cramsan.edifikana.lib.model.StaffId

/**
 * Request to get a list of time card events.
 */
data class GetTimeCardEventListRequest(
    val staffId: StaffId?,
)
