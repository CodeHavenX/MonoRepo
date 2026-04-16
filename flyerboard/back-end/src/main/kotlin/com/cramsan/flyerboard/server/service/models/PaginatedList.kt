package com.cramsan.flyerboard.server.service.models

/**
 * Generic paginated list wrapper returned by list service methods.
 */
data class PaginatedList<T>(
    val items: List<T>,
    val total: Int,
    val offset: Int,
    val limit: Int,
)
