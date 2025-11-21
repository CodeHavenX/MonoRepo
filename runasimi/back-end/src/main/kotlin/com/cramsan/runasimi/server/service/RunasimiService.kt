package com.cramsan.runasimi.server.service

import com.cramsan.framework.annotations.NetworkModel

/**
 * Simple service that responds to pings.
 */
@OptIn(NetworkModel::class)
class RunasimiService {

    /**
     * Simple ping method to check if the service is alive.
     */
    fun ping() = Unit
}
