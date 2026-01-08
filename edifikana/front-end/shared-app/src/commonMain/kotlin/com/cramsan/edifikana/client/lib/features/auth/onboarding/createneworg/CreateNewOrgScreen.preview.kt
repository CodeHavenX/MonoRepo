package com.cramsan.edifikana.client.lib.features.auth.onboarding.createneworg

import androidx.compose.runtime.Composable
import com.cramsan.edifikana.client.ui.theme.AppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * Preview for the CreateNewOrg feature screen.
 */
@Preview
@Composable
private fun CreateNewOrgScreenPreview() {
    AppTheme {
        CreateNewOrgContent(
            content = CreateNewOrgUIState(
                organizationName = "",
                organizationDescription = "",
                isLoading = false,
            ),
            onBackSelected = {},
            onOrganizationNameChanged = {},
            onOrganizationDescriptionChanged = {},
            onCreateOrganizationClicked = {},
        )
    }
}

/**
 * Preview for the CreateNewOrg feature screen with content.
 */
@Preview
@Composable
private fun CreateNewOrgScreenWithContentPreview() {
    AppTheme {
        CreateNewOrgContent(
            content = CreateNewOrgUIState(
                organizationName = "Acme Properties",
                organizationDescription = "Managing residential properties in downtown area.",
                isLoading = false,
            ),
            onBackSelected = {},
            onOrganizationNameChanged = {},
            onOrganizationDescriptionChanged = {},
            onCreateOrganizationClicked = {},
        )
    }
}
