package com.cramsan.edifikana.client.lib.features.settings.organizations.transferownership

import androidx.compose.runtime.Composable
import com.cramsan.edifikana.client.ui.theme.AppTheme
import com.cramsan.edifikana.lib.model.user.UserId
import com.cramsan.ui.preview.DevicePreviews
import com.cramsan.ui.preview.ScreenPreviews

@DevicePreviews
@Composable
private fun TransferOwnershipScreenPreview() =
    AppTheme {
        TransferOwnershipContent(
            uiState =
                TransferOwnershipUIState(
                isLoading = false,
                eligibleAdmins =
                    listOf(
                    AdminUIModel(userId = UserId("user-1"), displayName = "Maria Garcia", email = "maria@example.com"),
                    AdminUIModel(userId = UserId("user-2"), displayName = "John Smith", email = "john@example.com"),
                ),
                confirmingTarget = null,
            ),
                onBackSelected = {},
            onAdminSelected = {},
        )
    }

@ScreenPreviews
@Composable
private fun TransferOwnershipScreenEmptyPreview() =
    AppTheme {
        TransferOwnershipContent(
            uiState =
                TransferOwnershipUIState(
                isLoading = false,
                eligibleAdmins = emptyList(),
                confirmingTarget = null,
            ),
                onBackSelected = {},
            onAdminSelected = {},
        )
    }
