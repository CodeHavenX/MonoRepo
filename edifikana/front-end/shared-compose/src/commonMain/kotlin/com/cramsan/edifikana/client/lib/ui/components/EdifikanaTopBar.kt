package com.cramsan.edifikana.client.lib.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
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
    navHostController: NavHostController,
    onUpArrowClicked: () -> Unit = { navHostController.navigateUp() },
    onCloseClicked: (() -> Unit)?,
    content: (@Composable RowScope.() -> Unit)? = null,
) {
    val backStack by navHostController.currentBackStack.collectAsState()
    val navigationIcon = when {
        backStack.size > 2 -> NavigationIconMode.ShowUpArrow
        onCloseClicked != null -> NavigationIconMode.ShowCloseButton
        else -> NavigationIconMode.Hide
    }
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
            AnimatedContent(
                targetState = navigationIcon,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "nav icon animated content"
            ) {
                when (it) {
                    NavigationIconMode.ShowUpArrow -> IconButton(onClick = onUpArrowClicked) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(Res.string.string_back_navigation)
                        )
                    }
                    NavigationIconMode.ShowCloseButton -> IconButton(
                        onClick = onCloseClicked ?: { }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = stringResource(Res.string.string_back_navigation)
                        )
                    }
                    NavigationIconMode.Hide -> Unit
                }
            }
        },
        actions = { content?.invoke(this) }
    )
}

private enum class NavigationIconMode {
    ShowUpArrow,
    ShowCloseButton,
    Hide
}
