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

        /**
         * Query parameters for the user API.
         */
        object QueryParams {
            const val USER_ID = "user_id"
            const val ORG_ID = "org_id"
        }
    }

    /**
     * Routes for the property API.
     */
    object Property {
        const val PATH = "property"
        const val ADMIN = "admin"

        /**
         * Query parameters for the property API.
         */
        object QueryParams {
            const val PROPERTY_ID = "property_id"
        }
    }

    /**
     * Routes for the staff API.
     */
    object Staff {
        const val PATH = "staff"

        /**
         * Query parameters for the staff API.
         */
        object QueryParams {
            const val STAFF_ID = "staff_id"
        }
    }

    /**
     * Routes for the event log API.
     */
    object EventLog {
        const val PATH = "event_log"

        /**
         * Query parameters for the event log API.
         */
        object QueryParams {
            const val EVENT_LOG_ENTRY_ID = "event_log_entry_id"
        }
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
            const val TIMECARD_EVENT_ID = "timecard_event_id"
        }
    }

    /**
     * Routes for the organization API.
     */
    object Organization {
        const val PATH = "organization"

        /**
         * Query parameters for the organization API.
         */
        object QueryParams {
            const val ORGANIZATION_ID = "organization_id"
        }
    }

    /**
     * Routes for the health check API.
     */
    object Health {
        const val PATH = "health"
    }

    /**
     * Routes for the storage API.
     */
    object Storage {
        const val PATH = "storage"

        /**
         * Query parameters for the storage API.
         */
        object QueryParams {
            const val ASSET_ID = "asset_id"
        }
    }
}
