package com.cramsan.edifikana.client.lib.features.admin.staff

import com.cramsan.edifikana.lib.model.IdType
import com.cramsan.edifikana.lib.model.StaffRole
import com.cramsan.framework.core.compose.ViewModelUIState

/**
 * UI state of the Staff feature.
 *
 * This class models the top level state of the page.
 * For modeling more specific details of the page, see the respective UI model class.
 */
data class StaffUIState(
    val title: String?,
    val isLoading: Boolean,
    val idType: IdType?,
    val firstName: String?,
    val lastName: String?,
    val role: StaffRole?,
) : ViewModelUIState {
    companion object {
        val Initial = StaffUIState(
            title = null,
            isLoading = true,
            idType = null,
            firstName = null,
            lastName = null,
            role = null,
        )
    }
}
