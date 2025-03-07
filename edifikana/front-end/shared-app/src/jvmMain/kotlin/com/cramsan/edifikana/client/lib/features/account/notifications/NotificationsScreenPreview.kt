package com.cramsan.edifikana.client.lib.features.account.notifications

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import com.cramsan.edifikana.client.ui.theme.AppTheme

/**
 * Preview for the Notifications feature screen.
 * TODO: Move this file to the JVM target, since the common target does not support previews.
 */
@Preview
@Composable
private fun NotificationsScreenPreview() = AppTheme {
    NotificationsContent(
        content = NotificationsUIState(
            title = "NotificationsScreenPreview",
            isLoading = true,
        ),
        onBackSelected = {},
    )
}
