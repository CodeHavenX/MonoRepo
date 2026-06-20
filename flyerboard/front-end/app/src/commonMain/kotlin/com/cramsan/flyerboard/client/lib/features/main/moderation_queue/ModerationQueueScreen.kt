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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import flyerboard_lib.moderation_queue_screen_empty_message
import flyerboard_lib.moderation_queue_screen_pending_badge
import flyerboard_lib.moderation_queue_screen_reject_dialog_cancel
import flyerboard_lib.moderation_queue_screen_reject_dialog_confirm
import flyerboard_lib.moderation_queue_screen_reject_dialog_title
import flyerboard_lib.moderation_queue_screen_reject_reason_label
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

/**
 * Moderation Queue screen — displays pending flyers for admin review with approve/reject actions.
 */
@Composable
fun ModerationQueueScreen(
    modifier: Modifier = Modifier,
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
        onApprove = { viewModel.approveFlyer(it.id) },
        onRejectTapped = { viewModel.onRejectTapped(it.id) },
        onConfirmReject = { flyerId, reason -> viewModel.rejectFlyer(flyerId, reason) },
        onDismissRejectDialog = { viewModel.onRejectDialogDismissed() },
    )
}

/**
 * Content of the Moderation Queue screen.
 */
@Composable
internal fun ModerationQueueContent(
    uiState: ModerationQueueUIState,
    modifier: Modifier = Modifier,
    onApprove: (FlyerModel) -> Unit,
    onRejectTapped: (FlyerModel) -> Unit,
    onConfirmReject: (FlyerId, String) -> Unit,
    onDismissRejectDialog: () -> Unit,
) {
    Scaffold(modifier = modifier) { innerPadding ->
        var rejectReason by remember { mutableStateOf("") }
        Column(
            modifier =
            Modifier
                .padding(innerPadding)
                .fillMaxSize(),
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
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
                                    uploaderHandle = flyer.uploaderId.userId,
                                    postedAt = flyer.createdAt,
                                    imageUrl = flyer.fileUrl,
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
}

@Composable
private fun PendingCountBadge(
    count: Int,
    modifier: Modifier = Modifier,
) {
    Surface(
        color = Color(PENDING_BADGE_COLOR),
        shape = CircleShape,
        modifier = modifier,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = Padding.SMALL, vertical = Padding.X_SMALL),
            horizontalArrangement = Arrangement.spacedBy(Padding.XX_SMALL),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier =
                Modifier
                    .size(Padding.XX_SMALL)
                    .background(Color.White, CircleShape),
            )
            Text(
                text = "$count ${stringResource(Res.string.moderation_queue_screen_pending_badge)}",
                style = MaterialTheme.typography.labelMedium,
                color = Color.White,
            )
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
private const val PENDING_BADGE_COLOR = 0xFFF43F5EL
