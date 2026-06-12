package com.cramsan.edifikana.lib.model.network.invite

import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.RequestBody
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Request model for joining an organization via an invite code.
 */
@NetworkModel
@Serializable
data class JoinViaCodeNetworkRequest(
    @SerialName("invite_code")
    val inviteCode: String,
) : RequestBody
