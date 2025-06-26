package com.cramsan.edifikana.server.core.service.models.requests

import com.cramsan.edifikana.lib.model.EnrollmentType
import com.cramsan.edifikana.lib.model.UserId

/**
 * Request to enroll an account. This is used to associate a [userId] with an account identified by
 * [enrollmentIdentifier] and of type [enrollmentType].
 *
 * This is typically used when a user wants to enroll an account into our system but the [userId] was generated
 * externally.
 */
data class EnrollUserRequest(
    val userId: UserId,
    val enrollmentIdentifier: String,
    val enrollmentType: EnrollmentType,
)
