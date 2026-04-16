package com.cramsan.flyerboard.client.lib.features.main.my_flyers

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
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.cramsan.flyerboard.client.lib.models.FlyerModel
import com.cramsan.flyerboard.lib.model.FlyerStatus
import com.cramsan.framework.core.compose.ui.ObserveViewModelEvents
import com.cramsan.ui.theme.Padding
import flyerboard_lib.Res
import flyerboard_lib.app_bar_action_sign_out
import flyerboard_lib.archive_screen_navigate_back
import flyerboard_lib.flyer_status_approved
import flyerboard_lib.flyer_status_archived
import flyerboard_lib.flyer_status_pending
import flyerboard_lib.flyer_status_rejected
import flyerboard_lib.my_flyers_screen_button_edit
import flyerboard_lib.my_flyers_screen_empty_message
import flyerboard_lib.my_flyers_screen_navigate_back
import flyerboard_lib.my_flyers_screen_title
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

/**
 * My Flyers screen — displays the authenticated user's own flyers with status badges.
 */
@Composable
fun MyFlyersScreen(
    modifier: Modifier = Modifier,
    isAuthenticated: Boolean = false,
    onSignOut: () -> Unit = {},
    viewModel: MyFlyersViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    LifecycleEventEffect(Lifecycle.Event.ON_CREATE) {
        viewModel.loadFlyers()
    }

    ObserveViewModelEvents(viewModel) { event ->
        when (event) {
            MyFlyersEvent.Noop -> Unit
        }
    }

    MyFlyersContent(
        uiState = uiState,
        modifier = modifier,
        isAuthenticated = isAuthenticated,
        onSignOut = onSignOut,
        onNavigateBack = { viewModel.navigateBack() },
        onRefresh = { viewModel.refresh() },
        onFlyerSelected = { viewModel.onFlyerSelected(it.id) },
        onEditFlyer = { viewModel.onEditFlyer(it.id) },
    )
}

/**
 * Content of the My Flyers screen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun MyFlyersContent(
    uiState: MyFlyersUIState,
    modifier: Modifier = Modifier,
    isAuthenticated: Boolean = false,
    onSignOut: () -> Unit = {},
    onNavigateBack: () -> Unit,
    onRefresh: () -> Unit,
    onFlyerSelected: (FlyerModel) -> Unit,
    onEditFlyer: (FlyerModel) -> Unit,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.my_flyers_screen_title)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(Res.string.my_flyers_screen_navigate_back),
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
                            contentDescription = stringResource(Res.string.my_flyers_screen_title),
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
                        text = stringResource(Res.string.my_flyers_screen_empty_message),
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
                            MyFlyerCard(
                                flyer = flyer,
                                onClick = { onFlyerSelected(flyer) },
                                onEdit = { onEditFlyer(flyer) },
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * A card representing a single flyer in the My Flyers list, with a status badge and optional edit button.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MyFlyerCard(
    flyer: FlyerModel,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    onEdit: () -> Unit,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick,
    ) {
        Column(
            modifier = Modifier.padding(Padding.MEDIUM),
            verticalArrangement = Arrangement.spacedBy(Padding.XX_SMALL),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = flyer.title,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f),
                )
                StatusBadge(status = flyer.status)
            }
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
            if (flyer.status != FlyerStatus.ARCHIVED) {
                Button(
                    onClick = onEdit,
                    modifier = Modifier.align(Alignment.End),
                ) {
                    Text(stringResource(Res.string.my_flyers_screen_button_edit))
                }
            }
        }
    }
}

/**
 * A small colored text label indicating the flyer's moderation status.
 */
@Composable
private fun StatusBadge(
    status: FlyerStatus,
    modifier: Modifier = Modifier,
) {
    val (label, color) = when (status) {
        FlyerStatus.PENDING -> stringResource(Res.string.flyer_status_pending) to Color(COLOR_STATUS_PENDING)
        FlyerStatus.APPROVED -> stringResource(Res.string.flyer_status_approved) to Color(COLOR_STATUS_APPROVED)
        FlyerStatus.REJECTED -> stringResource(Res.string.flyer_status_rejected) to Color(COLOR_STATUS_REJECTED)
        FlyerStatus.ARCHIVED -> stringResource(Res.string.flyer_status_archived) to Color(COLOR_STATUS_ARCHIVED)
    }
    Text(
        text = label,
        style = MaterialTheme.typography.labelSmall,
        color = color,
        modifier = modifier,
    )
}

private const val COLOR_STATUS_PENDING = 0xFFF59E0BL
private const val COLOR_STATUS_APPROVED = 0xFF22C55EL
private const val COLOR_STATUS_REJECTED = 0xFFEF4444L
private const val COLOR_STATUS_ARCHIVED = 0xFF6B7280L
