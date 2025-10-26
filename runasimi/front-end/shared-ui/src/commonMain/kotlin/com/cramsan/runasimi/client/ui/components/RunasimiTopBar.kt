package com.cramsan.runasimi.client.ui.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import org.jetbrains.compose.resources.stringResource
import runasimi_ui.Res
import runasimi_ui.string_back_navigation

/**
 * Runasimi top bar.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RunasimiTopBar(
    title: String? = null,
    navigationIcon: ImageVector? = Icons.Default.Close,
    onNavigationIconSelected: (() -> Unit)? = null,
    content: (@Composable RowScope.() -> Unit)? = null,
) {
    TopAppBar(
        title = {
            title?.let { Text(it) }
        },
        colors = TopAppBarDefaults.topAppBarColors(),
        navigationIcon = {
            if (navigationIcon != null && onNavigationIconSelected != null) {
                IconButton(
                    onClick = onNavigationIconSelected,
                ) {
                    Icon(
                        imageVector = navigationIcon,
                        contentDescription = stringResource(Res.string.string_back_navigation)
                    )
                }
            }
        },
        actions = { content?.invoke(this) }
    )
}
