package com.cramsan.edifikana.lib

/**
 * Routes for the API.
 */
object Routes {

    /**
     * Routes for the user API.
     */
    object User {
        const val PATH = "user"
    }

    /**
     * Routes for the property API.
     */
    object Property {
        const val PATH = "property"
    }

    /**
     * Routes for the staff API.
     */
    object Staff {
        const val PATH = "staff"
    }

    /**
     * Routes for the event log API.
     */
    object EventLog {
        const val PATH = "event_log"
    }

    /**
     * Routes for the time card API.
     */
    object TimeCard {
        const val PATH = "time_card"

        /**
         * Query parameters for the time card API.
         */
        object QueryParams {
            const val STAFF_ID = "staff_id"
        }
    }

    /**
     * Routes for the health check API.
     */
    object Health {
        const val PATH = "health"
    }
}
