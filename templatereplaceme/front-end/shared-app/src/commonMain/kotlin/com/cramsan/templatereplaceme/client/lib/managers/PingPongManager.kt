package com.cramsan.templatereplaceme.client.lib.managers

import com.cramsan.framework.annotations.FrontendManager
import com.cramsan.framework.core.ManagerDependencies
import com.cramsan.framework.core.getOrCatch
import com.cramsan.framework.logging.logI
import com.cramsan.templatereplaceme.client.lib.models.PongModel
import com.cramsan.templatereplaceme.client.lib.service.PingPongService

/**
 * Manager to handle ping pong operations.
 */
@FrontendManager
class PingPongManager(private val dependencies: ManagerDependencies, private val authService: PingPongService) {
    /**
     * Make a ping request.
     */
    suspend fun ping(
        firstName: String,
        lastName: String,
    ): Result<PongModel> =
        dependencies.getOrCatch(TAG) {
            logI(TAG, "Make a ping request for $firstName $lastName")
            authService.ping(firstName, lastName).getOrThrow()
        }

    companion object {
        private const val TAG = "PingPongManager"
    }
}
