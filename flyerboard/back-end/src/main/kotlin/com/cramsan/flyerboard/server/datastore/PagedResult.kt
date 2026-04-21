package com.cramsan.flyerboard.server.datastore

/**
 * Wraps a page of results from the datastore together with the total row count for the query.
 *
 * The datastore is responsible for populating [total] via a database count query so that the
 * service layer can build accurate
 * [PaginatedList][com.cramsan.flyerboard.server.service.models.PaginatedList]
 * responses without issuing a separate round-trip.
 */
data class PagedResult<T>(
    val items: List<T>,
    val total: Long,
)
