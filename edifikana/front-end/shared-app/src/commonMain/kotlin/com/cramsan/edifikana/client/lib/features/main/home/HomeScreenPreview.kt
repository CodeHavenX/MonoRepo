package com.cramsan.edifikana.client.lib.features.main.home

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * Preview for the Home feature screen.
 */
@Preview
@Composable
private fun HomeScreenPreview() {
    HomeContent(
        modifier = Modifier,
        selectedTab = Tabs.EventLog,
    )
}
