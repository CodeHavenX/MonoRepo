package com.cramsan.edifikana.client.lib.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import edifikana_lib.Res
import edifikana_lib.string_back_navigation
import org.jetbrains.compose.resources.stringResource

/**
 * Edifikana top bar.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EdifikanaTopBar(
    title: String,
    showUpArrow: Boolean,
    onUpArrowClicked: () -> Unit,
    onAccountClicked: (() -> Unit)?,
) {
    TopAppBar(
        title = {
            AnimatedContent(title) {
                Text(it)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
        ),
        navigationIcon = {
            AnimatedVisibility(
                visible = showUpArrow,
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                IconButton(onClick = onUpArrowClicked) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(Res.string.string_back_navigation)
                    )
                }
            }
        },
        actions = {
            onAccountClicked?.let {
                IconButton(onClick = it) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = ""
                    )
                }
            }
        }
    )
}
