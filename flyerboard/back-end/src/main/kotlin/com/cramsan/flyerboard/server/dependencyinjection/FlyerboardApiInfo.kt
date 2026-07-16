package com.cramsan.flyerboard.server.dependencyinjection

import com.cramsan.framework.networkapi.ApiInfo

/**
 * OpenAPI metadata describing the Flyerboard back-end API.
 */
val FlyerboardApiInfo =
    ApiInfo(
        title = "Flyerboard Back-End API",
        version = "1.0.0",
        description = "Backend API for the Flyerboard platform.",
    )
