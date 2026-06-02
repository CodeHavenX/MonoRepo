package com.cramsan.templatereplaceme.client.lib.service

import com.cramsan.framework.annotations.FrontendService
import com.cramsan.templatereplaceme.client.lib.models.PongModel

/**
 * Service to for the ping pong api.
 */
@FrontendService
interface PingPongService {
    /**
     * Make a ping request.
     */
    suspend fun ping(
        firstName: String,
        lastName: String,
    ): Result<PongModel>
}
