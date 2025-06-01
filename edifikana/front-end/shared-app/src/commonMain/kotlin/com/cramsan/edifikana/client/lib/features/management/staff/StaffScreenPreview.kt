package com.cramsan.edifikana.client.lib.features.management.staff

import androidx.compose.runtime.Composable
import com.cramsan.edifikana.client.ui.theme.AppTheme
import com.cramsan.edifikana.lib.model.IdType
import com.cramsan.edifikana.lib.model.StaffRole
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * Preview for the Staff feature screen.
 */
@Preview
@Composable
private fun StaffScreenPreview() = AppTheme {
    StaffContent(
        content = StaffUIState(
            title = "Cesar Vargas",
            isLoading = true,
            idType = IdType.DNI,
            firstName = "Cesar",
            lastName = "Vargas",
            role = StaffRole.SECURITY_COVER,
        ),
        onBackSelected = {},
    )
}
