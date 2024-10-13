package com.codehavenx.alpaca.frontend.appcore.features.staff.liststaff

/**
 * UI Model for the List Staff screen.
 */
data class StaffPageUIModel(
    val users: List<StaffUIModel>,
)

/**
 * UI Model for a staff member.
 */
data class StaffUIModel(
    val id: String,
    val displayName: String,
)

/**
 * UI Model for the pagination of the List Staff screen.
 */
data class StaffPaginationUIModel(
    val firstPage: String?,
    val nextPage: String?,
    val previousPage: String?,
    val lastPage: String?,
    val pages: List<StaffPageReferenceUIModel>,
)

/**
 * UI Model for a reference to a page of staff members.
 */
data class StaffPageReferenceUIModel(
    val displayName: String,
    val id: String,
    val selected: Boolean,
)
