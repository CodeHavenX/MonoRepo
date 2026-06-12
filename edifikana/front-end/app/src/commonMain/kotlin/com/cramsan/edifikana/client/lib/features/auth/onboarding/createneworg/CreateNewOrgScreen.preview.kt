package com.cramsan.edifikana.client.lib.features.auth.onboarding.createneworg

import androidx.compose.runtime.Composable
import com.cramsan.edifikana.client.ui.theme.AppTheme
import com.cramsan.ui.preview.ScreenPreviews

/**
 * Preview for the CreateNewOrg feature screen.
 */
@ScreenPreviews
@Composable
private fun CreateNewOrgScreenPreview() {
    AppTheme {
        CreateNewOrgContent(
            content =
            CreateNewOrgUIState(
                organizationName = "",
                organizationDescription = "",
                isLoading = false,
                isButtonEnabled = false,
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
@ScreenPreviews
@Composable
private fun CreateNewOrgScreenWithContentPreview() {
    AppTheme {
        CreateNewOrgContent(
            content =
            CreateNewOrgUIState(
                organizationName = "Acme Properties",
                organizationDescription = "Managing residential properties in downtown area.",
                isLoading = false,
                isButtonEnabled = true,
            ),
            onBackSelected = {},
            onOrganizationNameChanged = {},
            onOrganizationDescriptionChanged = {},
            onCreateOrganizationClicked = {},
        )
    }
}

@ScreenPreviews
@Composable
private fun CreateNewOrgScreenPreview_ES() {
    AppTheme {
        CreateNewOrgContent(
            content =
            CreateNewOrgUIState(
                organizationName = "",
                organizationDescription = "",
                isLoading = false,
                isButtonEnabled = false,
            ),
            onBackSelected = {},
            onOrganizationNameChanged = {},
            onOrganizationDescriptionChanged = {},
            onCreateOrganizationClicked = {},
        )
    }
}

@ScreenPreviews
@Composable
private fun CreateNewOrgScreenWithContentPreview_ES() {
    AppTheme {
        CreateNewOrgContent(
            content =
            CreateNewOrgUIState(
                organizationName = "Acme Propiedades",
                organizationDescription = "Gestión de propiedades residenciales en el centro.",
                isLoading = false,
                isButtonEnabled = true,
            ),
            onBackSelected = {},
            onOrganizationNameChanged = {},
            onOrganizationDescriptionChanged = {},
            onCreateOrganizationClicked = {},
        )
    }
}
