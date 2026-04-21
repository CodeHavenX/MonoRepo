package com.cramsan.flyerboard.client.lib.features.main.moderation_queue

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import flyerboard_lib.app_bar_action_sign_out
import flyerboard_lib.moderation_queue_screen_button_approve
import flyerboard_lib.moderation_queue_screen_button_reject
import flyerboard_lib.moderation_queue_screen_empty_message
import flyerboard_lib.moderation_queue_screen_navigate_back
import flyerboard_lib.moderation_queue_screen_title
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

/**
 * Moderation Queue screen — displays pending flyers for admin review with approve/reject actions.
 */
@Composable
fun ModerationQueueScreen(
    modifier: Modifier = Modifier,
    isAuthenticated: Boolean = false,
    onSignOut: () -> Unit = {},
    viewModel: ModerationQueueViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    LifecycleEventEffect(Lifecycle.Event.ON_CREATE) {
        viewModel.loadPendingFlyers()
    }

    ObserveViewModelEvents(viewModel) { event ->
        when (event) {
            ModerationQueueEvent.Noop -> Unit
        }
    }

    ModerationQueueContent(
        uiState = uiState,
        modifier = modifier,
        isAuthenticated = isAuthenticated,
        onSignOut = onSignOut,
        onNavigateBack = { viewModel.navigateBack() },
        onRefresh = { viewModel.refresh() },
        onApprove = { viewModel.approveFlyer(it.id) },
        onReject = { viewModel.rejectFlyer(it.id) },
    )
}

/**
 * Content of the Moderation Queue screen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ModerationQueueContent(
    uiState: ModerationQueueUIState,
    modifier: Modifier = Modifier,
    isAuthenticated: Boolean = false,
    onSignOut: () -> Unit = {},
    onNavigateBack: () -> Unit,
    onRefresh: () -> Unit,
    onApprove: (FlyerModel) -> Unit,
    onReject: (FlyerModel) -> Unit,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.moderation_queue_screen_title)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(Res.string.moderation_queue_screen_navigate_back),
                        )
                    }
                },
                actions = {
                    if (isAuthenticated) {
                        TextButton(onClick = onSignOut) {
                            Text(stringResource(Res.string.app_bar_action_sign_out))
                        }
                    }
                    IconButton(onClick = onRefresh) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = stringResource(Res.string.moderation_queue_screen_title),
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
                uiState.pendingFlyers.isEmpty() -> {
                    Text(
                        text = stringResource(Res.string.moderation_queue_screen_empty_message),
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(Padding.MEDIUM),
                        verticalArrangement = Arrangement.spacedBy(Padding.SMALL),
                    ) {
                        items(uiState.pendingFlyers, key = { it.id.flyerId }) { flyer ->
                            PendingFlyerCard(
                                flyer = flyer,
                                onApprove = { onApprove(flyer) },
                                onReject = { onReject(flyer) },
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * A card representing a single pending flyer with inline approve and reject actions.
 */
@Composable
private fun PendingFlyerCard(
    flyer: FlyerModel,
    modifier: Modifier = Modifier,
    onApprove: () -> Unit,
    onReject: () -> Unit,
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(Padding.MEDIUM),
            verticalArrangement = Arrangement.spacedBy(Padding.SMALL),
        ) {
            Text(
                text = flyer.title,
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                text = flyer.description,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 3,
            )
            flyer.expiresAt?.let { expires ->
                Text(
                    text = expires,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline,
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Padding.SMALL, Alignment.End),
            ) {
                OutlinedButton(
                    onClick = onReject,
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error,
                    ),
                ) {
                    Text(stringResource(Res.string.moderation_queue_screen_button_reject))
                }
                Button(onClick = onApprove) {
                    Text(stringResource(Res.string.moderation_queue_screen_button_approve))
                }
            }
        }
    }
}
