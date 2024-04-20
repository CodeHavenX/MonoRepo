package com.codehavenx.alpaca.server

object Paths {
    const val CreateAppointment = "/appointment"

    const val DeleteAppointmentById = "/appointment/{appointmentId}"

    const val GetAppointmentById = "/appointment/{appointmentId}"

    const val GetAppointmentsFromCriteria = "/appointment"

    const val UpdateAppointmentById = "/appointment/{appointmentId}"

    const val CreateAppointmentConfiguration = "/appointment/configuration"

    const val DeleteAppointmentConfigurationById = "/appointment/configuration/{appointmentConfigurationId}"

    const val GetAppointmentConfigurationById = "/appointment/configuration/{appointmentConfigurationId}"

    const val GetAppointmentConfigurationsForBusiness = "/appointment/configuration"

    const val UpdateAppointmentConfigurationById = "/appointment/configuration/{appointmentConfigurationId}"

    const val GetAvailability = "/availability"

    const val GetAvailabilityTimeSlots = "/availability/timeslots"

    const val setAvailability = "/availability"

    const val setAvailabilityOverride = "/availability/override"

    const val addUserToBusiness = "/business/{businessId}/relation/{userId}"

    const val CreateBusiness = "/business"

    const val DeleteBusinessById = "/business/{businessId}"

    const val GetBusinessById = "/business/{businessId}"

    const val GetBusinessesFromCriteria = "/business"

    const val remoteUserFromBusiness = "/business/{businessId}/relation/{userId}"

    const val UpdateBusinessById = "/business/{businessId}"

    const val CreateUser = "/user"

    const val DeleteUserById = "/user/{userId}"

    const val GetUserById = "/user/{userId}"

    const val GetUsersFromCriteria = "/user"

    const val UpdateUserById = "/user/{userId}"
}
