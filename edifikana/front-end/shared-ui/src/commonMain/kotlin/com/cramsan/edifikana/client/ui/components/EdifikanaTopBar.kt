package com.cramsan.edifikana.client.ui.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import edifikana_ui.Res
import edifikana_ui.string_back_navigation
import org.jetbrains.compose.resources.stringResource

/**
 * Edifikana top bar.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EdifikanaTopBar(
    title: String,
    onCloseClicked: (() -> Unit)? = null,
    content: (@Composable RowScope.() -> Unit)? = null,
) {
    TopAppBar(
        title = {
            Text(title)
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
        ),
        navigationIcon = onCloseClicked?.let {
            {
                IconButton(
                    onClick = it,
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(Res.string.string_back_navigation)
                    )
                }
            }
        } ?: {},
        actions = { content?.invoke(this) }
    )
}
