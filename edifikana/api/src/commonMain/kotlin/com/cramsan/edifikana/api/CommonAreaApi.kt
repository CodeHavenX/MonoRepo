package com.cramsan.edifikana.api

import com.cramsan.edifikana.lib.model.commonArea.CommonAreaId
import com.cramsan.edifikana.lib.model.property.PropertyId
import com.cramsan.edifikana.lib.model.network.commonArea.CommonAreaListNetworkResponse
import com.cramsan.edifikana.lib.model.network.commonArea.CommonAreaNetworkResponse
import com.cramsan.edifikana.lib.model.network.commonArea.CreateCommonAreaNetworkRequest
import com.cramsan.edifikana.lib.model.network.commonArea.UpdateCommonAreaNetworkRequest
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.NoPathParam
import com.cramsan.framework.annotations.api.NoQueryParam
import com.cramsan.framework.annotations.api.NoRequestBody
import com.cramsan.framework.annotations.api.NoResponseBody
import com.cramsan.framework.networkapi.Api
import io.ktor.http.HttpMethod

/**
 * API definition for common area operations within a property.
 *
 * Common areas are shared spaces (e.g. Lobby, Pool, Gym) that belong to a single property.
 * All operations require MANAGER role or higher in the property's organization, depending on
 * the operation. In some cases it will be ADMIN+, in others it will be MANAGER+
 */
@OptIn(NetworkModel::class)
object CommonAreaApi : Api("common-area") {

    val createCommonArea = operation<
        CreateCommonAreaNetworkRequest,
        NoQueryParam,
        NoPathParam,
        CommonAreaNetworkResponse
        >(HttpMethod.Post)

    val getCommonArea = operation<
        NoRequestBody,
        NoQueryParam,
        CommonAreaId,
        CommonAreaNetworkResponse
        >(HttpMethod.Get)

    val getCommonAreasForProperty = operation<
        NoRequestBody,
        NoQueryParam,
        PropertyId,
        CommonAreaListNetworkResponse
        >(HttpMethod.Get, "by-property")

    val updateCommonArea = operation<
        UpdateCommonAreaNetworkRequest,
        NoQueryParam,
        CommonAreaId,
        CommonAreaNetworkResponse
        >(HttpMethod.Put)

    val deleteCommonArea = operation<
        NoRequestBody,
        NoQueryParam,
        CommonAreaId,
        NoResponseBody
        >(HttpMethod.Delete)
}
