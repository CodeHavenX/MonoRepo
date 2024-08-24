package com.codehavenx.alpaca.backend.core.service.models.requests

import com.codehavenx.alpaca.backend.core.service.models.UserId

/**
 * Domain model representing a configuration deletion request.
 */
data class DeleteConfigurationRequest(
    val id: UserId,
)
