package com.cramsan.edifikana.api

import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.edifikana.lib.model.network.CreatePropertyNetworkRequest
import com.cramsan.edifikana.lib.model.network.PropertyListNetworkResponse
import com.cramsan.edifikana.lib.model.network.PropertyNetworkResponse
import com.cramsan.edifikana.lib.model.network.UpdatePropertyNetworkRequest
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.NoPathParam
import com.cramsan.framework.annotations.api.NoQueryParam
import com.cramsan.framework.annotations.api.NoRequestBody
import com.cramsan.framework.annotations.api.NoResponseBody
import com.cramsan.framework.networkapi.Api
import io.ktor.http.HttpMethod

/**
 * API definition for property related operations.
 */
@OptIn(NetworkModel::class)
object PropertyApi : Api("property") {
    val createProperty = operation<
        CreatePropertyNetworkRequest,
        NoQueryParam,
        NoPathParam,
        PropertyNetworkResponse
        >(HttpMethod.Post)

    val getProperty = operation<
        NoRequestBody,
        NoQueryParam,
        PropertyId,
        PropertyNetworkResponse
        >(HttpMethod.Get)

    val getAssignedProperties = operation<
        NoRequestBody,
        NoQueryParam,
        NoPathParam,
        PropertyListNetworkResponse
        >(HttpMethod.Get)

    val updateProperty = operation<
        UpdatePropertyNetworkRequest,
        NoQueryParam,
        PropertyId,
        PropertyNetworkResponse
        >(HttpMethod.Put)

    val deleteProperty = operation<
        NoRequestBody,
        NoQueryParam,
        PropertyId,
        NoResponseBody
        >(HttpMethod.Delete)
}
