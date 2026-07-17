package com.cramsan.edifikana.api

import com.cramsan.edifikana.lib.model.document.DocumentId
import com.cramsan.edifikana.lib.model.network.document.CreateDocumentNetworkRequest
import com.cramsan.edifikana.lib.model.network.document.DocumentListNetworkResponse
import com.cramsan.edifikana.lib.model.network.document.DocumentNetworkResponse
import com.cramsan.edifikana.lib.model.network.document.GetDocumentsQueryParams
import com.cramsan.edifikana.lib.model.network.document.UpdateDocumentNetworkRequest
import com.cramsan.framework.annotations.api.NoPathParam
import com.cramsan.framework.annotations.api.NoQueryParam
import com.cramsan.framework.annotations.api.NoRequestBody
import com.cramsan.framework.annotations.api.NoResponseBody
import com.cramsan.framework.networkapi.AdditionalResponses
import com.cramsan.framework.networkapi.Api
import com.cramsan.framework.networkapi.UniversalResponsesOnly
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode

/**
 * API definition for document metadata operations.
 *
 * Files are stored in the Supabase 'documents' bucket via [StorageApi].
 * This API manages the metadata records that reference those stored files.
 */

object DocumentApi : Api("document") {
    val createDocument = operation<
        CreateDocumentNetworkRequest,
        NoQueryParam,
        NoPathParam,
        DocumentNetworkResponse,
    >(
        method = HttpMethod.Post,
        summary = "Create a document",
        description = "Creates a new document metadata record referencing a previously uploaded asset.",
        responses = UniversalResponsesOnly,
    )

    val getDocument = operation<
        NoRequestBody,
        NoQueryParam,
        DocumentId,
        DocumentNetworkResponse,
    >(
        method = HttpMethod.Get,
        summary = "Get a document",
        description = "Retrieves a single document's metadata by its identifier.",
        responses =
        AdditionalResponses {
            HttpStatusCode.NotFound describedAs "No document exists for the given id."
        },
    )

    val getDocuments = operation<
        NoRequestBody,
        GetDocumentsQueryParams,
        NoPathParam,
        DocumentListNetworkResponse,
    >(
        method = HttpMethod.Get,
        summary = "List documents",
        description = "Lists document metadata records for an organization, optionally filtered by " +
            "property or unit.",
        responses = UniversalResponsesOnly,
    )

    val updateDocument = operation<
        UpdateDocumentNetworkRequest,
        NoQueryParam,
        DocumentId,
        DocumentNetworkResponse,
    >(
        method = HttpMethod.Put,
        summary = "Update a document",
        description = "Updates the mutable metadata fields of an existing document.",
        responses =
        AdditionalResponses {
            HttpStatusCode.NotFound describedAs "No document exists for the given id."
        },
    )

    val deleteDocument = operation<
        NoRequestBody,
        NoQueryParam,
        DocumentId,
        NoResponseBody,
    >(
        method = HttpMethod.Delete,
        summary = "Delete a document",
        description = "Permanently deletes a document's metadata record by its identifier.",
        responses =
        AdditionalResponses {
            HttpStatusCode.NotFound describedAs "No document exists for the given id."
        },
    )
}
