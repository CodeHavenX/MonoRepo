package com.cramsan.edifikana.lib.model.network.document

import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.ResponseBody
import kotlinx.serialization.Serializable

/**
 * Network response for a list of documents.
 */
@NetworkModel
@Serializable
data class DocumentListNetworkResponse(
    val documents: List<DocumentNetworkResponse>,
) : ResponseBody
