package com.cramsan.edifikana.api

import com.cramsan.edifikana.lib.model.document.DocumentId
import com.cramsan.edifikana.lib.model.network.document.CreateDocumentNetworkRequest
import com.cramsan.edifikana.lib.model.network.document.DocumentListNetworkResponse
import com.cramsan.edifikana.lib.model.network.document.DocumentNetworkResponse
import com.cramsan.edifikana.lib.model.network.document.GetDocumentsQueryParams
import com.cramsan.edifikana.lib.model.network.document.UpdateDocumentNetworkRequest
import com.cramsan.framework.annotations.NetworkModel
import com.cramsan.framework.annotations.api.NoPathParam
import com.cramsan.framework.annotations.api.NoQueryParam
import com.cramsan.framework.annotations.api.NoRequestBody
import com.cramsan.framework.annotations.api.NoResponseBody
import com.cramsan.framework.networkapi.Api
import io.ktor.http.HttpMethod

/**
 * API definition for document metadata operations.
 *
 * Files are stored in the Supabase 'documents' bucket via [StorageApi].
 * This API manages the metadata records that reference those stored files.
 */
@OptIn(NetworkModel::class)
object DocumentApi : Api("document") {

    val createDocument = operation<
        CreateDocumentNetworkRequest,
        NoQueryParam,
        NoPathParam,
        DocumentNetworkResponse
        >(HttpMethod.Post)

    val getDocument = operation<
        NoRequestBody,
        NoQueryParam,
        DocumentId,
        DocumentNetworkResponse
        >(HttpMethod.Get)

    val getDocuments = operation<
        NoRequestBody,
        GetDocumentsQueryParams,
        NoPathParam,
        DocumentListNetworkResponse
        >(HttpMethod.Get)

    val updateDocument = operation<
        UpdateDocumentNetworkRequest,
        NoQueryParam,
        DocumentId,
        DocumentNetworkResponse
        >(HttpMethod.Put)

    val deleteDocument = operation<
        NoRequestBody,
        NoQueryParam,
        DocumentId,
        NoResponseBody
        >(HttpMethod.Delete)
}
