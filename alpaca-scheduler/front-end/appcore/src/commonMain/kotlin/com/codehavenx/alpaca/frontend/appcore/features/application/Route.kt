@file:Suppress("TooManyFunctions")
@file:OptIn(RouteUnsafePath::class)

package com.codehavenx.alpaca.frontend.appcore.features.application

/**
 * This file contains the routes of the application.
 */
enum class Route(
    @RouteUnsafePath
    val route: String,
) {
    HOME("/"),

    CLIENTS_LIST("/clients"),

    CLIENTS_ADD("/clients/add"),

    CLIENTS_VIEW("/clients/{clientId}"),

    CLIENTS_UPDATE("/clients/{clientId}/update"),

    STAFF_LIST("/staff"),

    STAFF_ADD("/staff/add"),

    STAFF_VIEW("/staff/{staffId}"),

    STAFF_UPDATE("/staff/{staffId}/update"),

    APPOINTMENTS("/appointments"),

    COURSES_AND_CLASSES("/courses-and-classes"),

    CREATE_ACCOUNT("/create-account"),
    ;
    companion object {

        /**
         * Returns the home route.
         */
        fun home(): String = HOME.route

        /**
         * Returns the list clients route.
         */
        fun listClients(): String = CLIENTS_LIST.route

        /**
         * Returns the add client route.
         */
        fun addClient(): String = CLIENTS_ADD.route

        /**
         * Returns the update client route.
         */
        fun updateClient(clientId: String): String = CLIENTS_UPDATE.route.replace("{clientId}", clientId)

        /**
         * Returns the view client route.
         */
        fun viewClient(clientId: String): String = CLIENTS_VIEW.route.replace("{clientId}", clientId)

        /**
         * Returns the list staff route.
         */
        fun listStaff(): String = STAFF_LIST.route

        /**
         * Returns the add staff route.
         */
        fun addStaff(): String = STAFF_ADD.route

        /**
         * Returns the update staff route.
         */
        fun updateStaff(staffId: String): String = STAFF_UPDATE.route.replace("{staffId}", staffId)

        /**
         * Returns the view staff route.
         */
        fun viewStaff(staffId: String): String = STAFF_VIEW.route.replace("{staffId}", staffId)

        /**
         * Returns the appointments route.
         */
        fun appointments(): String = APPOINTMENTS.route

        /**
         * Returns the courses and classes route.
         */
        fun coursesAndClasses(): String = COURSES_AND_CLASSES.route

        /**
         * Returns the create account route.
         */
        fun createAccount(): String = CREATE_ACCOUNT.route
    }
}
