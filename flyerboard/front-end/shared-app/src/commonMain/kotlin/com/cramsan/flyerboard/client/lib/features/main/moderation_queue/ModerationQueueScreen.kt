package com.cramsan.flyerboard.client.lib.features.main.moderation_queue

import androidx.compose.foundation.background
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.cramsan.flyerboard.client.lib.models.FlyerModel
import com.cramsan.flyerboard.client.ui.components.EmptyStateBox
import com.cramsan.flyerboard.client.ui.components.LoadingStateBox
import com.cramsan.flyerboard.client.ui.components.ModerationFlyerCard
import com.cramsan.flyerboard.lib.model.FlyerId
import com.cramsan.framework.core.compose.ui.ObserveViewModelEvents
import com.cramsan.ui.theme.Padding
import flyerboard_lib.Res
import flyerboard_lib.app_bar_action_sign_out
import flyerboard_lib.moderation_queue_screen_empty_message
import flyerboard_lib.moderation_queue_screen_navigate_back
import flyerboard_lib.moderation_queue_screen_reject_dialog_cancel
import flyerboard_lib.moderation_queue_screen_reject_dialog_confirm
import flyerboard_lib.moderation_queue_screen_reject_dialog_title
import flyerboard_lib.moderation_queue_screen_reject_reason_label
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
        onRejectTapped = { viewModel.onRejectTapped(it.id) },
        onConfirmReject = { flyerId, reason -> viewModel.rejectFlyer(flyerId, reason) },
        onDismissRejectDialog = { viewModel.onRejectDialogDismissed() },
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
    onRejectTapped: (FlyerModel) -> Unit,
    onConfirmReject: (FlyerId, String) -> Unit,
    onDismissRejectDialog: () -> Unit,
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
        var rejectReason by remember { mutableStateOf("") }
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            when (val state = uiState) {
                is ModerationQueueUIState.Loading -> {
                    LoadingStateBox()
                }

                is ModerationQueueUIState.Empty,
                is ModerationQueueUIState.Error,
                -> {
                    EmptyStateBox(
                        message = stringResource(Res.string.moderation_queue_screen_empty_message),
                    )
                }

                is ModerationQueueUIState.Content -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(Padding.MEDIUM),
                        verticalArrangement = Arrangement.spacedBy(Padding.SMALL),
                    ) {
                        items(state.flyers, key = { it.id.flyerId }) { flyer ->
                            ModerationFlyerCard(
                                title = flyer.title,
                                description = flyer.description,
                                expiresAt = flyer.expiresAt,
                                onApprove = { onApprove(flyer) },
                                onReject = { onRejectTapped(flyer) },
                            )
                        }
                    }
                    if (state.pendingRejectionFlyerId != null) {
                        RejectReasonDialog(
                            flyerId = state.pendingRejectionFlyerId,
                            reason = rejectReason,
                            onReasonChanged = { rejectReason = it },
                            onConfirm = { flyerId, reason ->
                                rejectReason = ""
                                onConfirmReject(flyerId, reason)
                            },
                            onDismiss = {
                                rejectReason = ""
                                onDismissRejectDialog()
                            },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RejectReasonDialog(
    flyerId: FlyerId,
    reason: String,
    onReasonChanged: (String) -> Unit,
    onConfirm: (FlyerId, String) -> Unit,
    onDismiss: () -> Unit,
) {
    Box(
        modifier =
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.scrim.copy(alpha = DIALOG_SCRIM_ALPHA)),
        contentAlignment = Alignment.Center,
    ) {
        Card(modifier = Modifier.padding(Padding.LARGE).fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(Padding.MEDIUM),
                verticalArrangement = Arrangement.spacedBy(Padding.SMALL),
            ) {
                Text(
                    text = stringResource(Res.string.moderation_queue_screen_reject_dialog_title),
                    style = MaterialTheme.typography.headlineSmall,
                )
                OutlinedTextField(
                    value = reason,
                    onValueChange = onReasonChanged,
                    label = { Text(stringResource(Res.string.moderation_queue_screen_reject_reason_label)) },
                    modifier = Modifier.fillMaxWidth(),
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Padding.SMALL, Alignment.End),
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(stringResource(Res.string.moderation_queue_screen_reject_dialog_cancel))
                    }
                    Button(onClick = { onConfirm(flyerId, reason) }) {
                        Text(stringResource(Res.string.moderation_queue_screen_reject_dialog_confirm))
                    }
                }
            }
        }
    }
}

private const val DIALOG_SCRIM_ALPHA = 0.5f
