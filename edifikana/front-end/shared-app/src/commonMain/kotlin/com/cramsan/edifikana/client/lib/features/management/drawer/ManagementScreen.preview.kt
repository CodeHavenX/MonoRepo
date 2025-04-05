package com.cramsan.edifikana.client.lib.features.management.drawer

import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * Preview for the Management feature screen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun ManagementScreenPreview() {
    ManagementContent(
        content = ManagementUIState.Initial,
        drawerState = rememberDrawerState(DrawerValue.Open),
        onDrawerItemSelected = { /* no-op */ },
    )
}
