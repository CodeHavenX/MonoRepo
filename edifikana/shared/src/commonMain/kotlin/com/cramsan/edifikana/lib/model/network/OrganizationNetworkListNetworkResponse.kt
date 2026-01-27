package com.cramsan.edifikana.lib.model.network

import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.ResponseBody
import kotlinx.serialization.Serializable

/**
 * Network response for a list of organizations.
 *
 * @property organizations The list of organizations.
 */
@NetworkModel
@Serializable
data class OrganizationNetworkListNetworkResponse(
    val organizations: List<OrganizationNetworkResponse>,
) : ResponseBody
