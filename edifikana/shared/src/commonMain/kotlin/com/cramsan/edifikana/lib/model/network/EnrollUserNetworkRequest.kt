package com.cramsan.edifikana.lib.model.network

import com.cramsan.edifikana.lib.annotations.NetworkModel
import com.cramsan.edifikana.lib.model.EnrollmentType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Request model for enrolling a user.
 */
@NetworkModel
@Serializable
data class EnrollUserNetworkRequest(
    @SerialName("user_id")
    val userId: String,
    @SerialName("enrollment_identifier")
    val enrollmentIdentifier: String,
    @SerialName("enrollment_type")
    val enrollmentType: EnrollmentType,
)
