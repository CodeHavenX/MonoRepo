package com.cramsan.runasimi.server.service

import com.cramsan.framework.annotations.BackendService

/**
 * Simple service that responds to pings.
 */
@BackendService
class RunasimiService {
    /**
     * Simple ping method to check if the service is alive.
     */
    fun ping() = Unit
}
