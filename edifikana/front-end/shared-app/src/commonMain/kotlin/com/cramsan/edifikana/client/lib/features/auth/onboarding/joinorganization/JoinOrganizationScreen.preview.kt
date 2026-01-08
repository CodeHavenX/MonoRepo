package com.cramsan.edifikana.client.lib.features.auth.onboarding.joinorganization

import androidx.compose.runtime.Composable
import com.cramsan.edifikana.client.ui.theme.AppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * Preview for the JoinOrganization feature screen.
 */
@Preview
@Composable
private fun JoinOrganizationScreenPreview() {
    AppTheme {
        JoinOrganizationContent(
            content = JoinOrganizationUIState(
                organizationNameOrCode = "",
                isLoading = false,
            ),
            onBackSelected = {},
            onOrganizationNameOrCodeChanged = {},
            onJoinOrganizationClicked = {},
            onCreateNewWorkspaceClicked = {},
        )
    }
}

/**
 * Preview for the JoinOrganization feature screen with loading state.
 */
@Preview
@Composable
private fun JoinOrganizationScreenLoadingPreview() {
    AppTheme {
        JoinOrganizationContent(
            content = JoinOrganizationUIState(
                organizationNameOrCode = "acme-properties",
                isLoading = true,
            ),
            onBackSelected = {},
            onOrganizationNameOrCodeChanged = {},
            onJoinOrganizationClicked = {},
            onCreateNewWorkspaceClicked = {},
        )
    }
}
