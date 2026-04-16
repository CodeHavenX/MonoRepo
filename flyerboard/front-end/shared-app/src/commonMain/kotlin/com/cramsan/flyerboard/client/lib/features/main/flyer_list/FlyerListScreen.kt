package com.cramsan.flyerboard.client.lib.features.main.flyer_list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.cramsan.flyerboard.client.lib.models.FlyerModel
import com.cramsan.framework.core.compose.ui.ObserveViewModelEvents
import com.cramsan.ui.theme.Padding
import flyerboard_lib.Res
import flyerboard_lib.app_bar_action_sign_in
import flyerboard_lib.app_bar_action_sign_out
import flyerboard_lib.flyer_list_screen_empty_message
import flyerboard_lib.flyer_list_screen_title
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

/**
 * Flyer List screen — displays the public flyer feed.
 */
@Composable
fun FlyerListScreen(
    modifier: Modifier = Modifier,
    isAuthenticated: Boolean = false,
    onSignIn: () -> Unit = {},
    onSignOut: () -> Unit = {},
    viewModel: FlyerListViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    LifecycleEventEffect(Lifecycle.Event.ON_CREATE) {
        viewModel.loadFlyers()
    }

    ObserveViewModelEvents(viewModel) { event ->
        when (event) {
            FlyerListEvent.Noop -> Unit
        }
    }

    FlyerListContent(
        uiState = uiState,
        modifier = modifier,
        isAuthenticated = isAuthenticated,
        onSignIn = onSignIn,
        onSignOut = onSignOut,
        onRefresh = { viewModel.refresh() },
        onFlyerSelected = { viewModel.onFlyerSelected(it.id) },
    )
}

/**
 * Content of the Flyer List screen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun FlyerListContent(
    uiState: FlyerListUIState,
    modifier: Modifier = Modifier,
    isAuthenticated: Boolean = false,
    onSignIn: () -> Unit = {},
    onSignOut: () -> Unit = {},
    onRefresh: () -> Unit,
    onFlyerSelected: (FlyerModel) -> Unit,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.flyer_list_screen_title)) },
                actions = {
                    if (isAuthenticated) {
                        TextButton(onClick = onSignOut) {
                            Text(stringResource(Res.string.app_bar_action_sign_out))
                        }
                    } else {
                        TextButton(onClick = onSignIn) {
                            Text(stringResource(Res.string.app_bar_action_sign_in))
                        }
                    }
                    IconButton(onClick = onRefresh) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = stringResource(Res.string.flyer_list_screen_title),
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier.padding(innerPadding).fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            when {
                uiState.isLoading -> CircularProgressIndicator()
                uiState.flyers.isEmpty() -> {
                    Text(
                        text = stringResource(Res.string.flyer_list_screen_empty_message),
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(Padding.MEDIUM),
                        verticalArrangement = Arrangement.spacedBy(Padding.SMALL),
                    ) {
                        items(uiState.flyers, key = { it.id.flyerId }) { flyer ->
                            FlyerCard(
                                flyer = flyer,
                                onClick = { onFlyerSelected(flyer) },
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * A card representing a single flyer in the list.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FlyerCard(
    flyer: FlyerModel,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick,
    ) {
        Column(
            modifier = Modifier.padding(Padding.MEDIUM),
            verticalArrangement = Arrangement.spacedBy(Padding.XX_SMALL),
        ) {
            Text(
                text = flyer.title,
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                text = flyer.description,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
            )
            flyer.expiresAt?.let { expires ->
                Text(
                    text = expires,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline,
                )
            }
        }
    }
}
