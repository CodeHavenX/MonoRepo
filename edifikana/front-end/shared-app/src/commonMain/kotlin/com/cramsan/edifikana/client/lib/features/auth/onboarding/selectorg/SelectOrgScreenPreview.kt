package com.cramsan.edifikana.client.lib.features.auth.onboarding.selectorg

import androidx.compose.runtime.Composable
import com.cramsan.edifikana.client.ui.theme.AppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * Preview for the SelectOrg screen.
 */
@Preview
@Composable
private fun SelectOrgScreenPreview() = AppTheme {
    SelectOrgContent(
        onCreateWorkspaceClicked = { },
        onSignOutClicked = { },
    )
}
