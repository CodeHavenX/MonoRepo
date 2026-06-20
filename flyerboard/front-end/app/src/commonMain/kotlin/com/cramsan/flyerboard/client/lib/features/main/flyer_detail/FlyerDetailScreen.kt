package com.cramsan.flyerboard.client.lib.features.main.flyer_detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.cramsan.flyerboard.client.lib.features.main.MainDestination
import com.cramsan.flyerboard.client.ui.components.EmptyStateBox
import com.cramsan.flyerboard.client.ui.components.FlyerAsyncImage
import com.cramsan.flyerboard.client.ui.components.FlyerBoardPageShell
import com.cramsan.flyerboard.client.ui.components.LoadingStateBox
import com.cramsan.flyerboard.client.ui.components.StatusBadge
import com.cramsan.flyerboard.lib.model.FlyerStatus
import com.cramsan.framework.core.compose.ui.ObserveViewModelEvents
import com.cramsan.ui.theme.Padding
import flyerboard_lib.Res
import flyerboard_lib.flyer_detail_screen_button_approve
import flyerboard_lib.flyer_detail_screen_button_edit_flyer
import flyerboard_lib.flyer_detail_screen_button_reject
import flyerboard_lib.flyer_detail_screen_expires_label
import flyerboard_lib.flyer_detail_screen_metadata_separator
import flyerboard_lib.flyer_detail_screen_not_found
import flyerboard_lib.flyer_detail_screen_posted_by
import flyerboard_lib.flyer_detail_screen_rejection_reason_label
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

/**
 * Flyer Detail screen — shows the full information for a single flyer.
 */
@Composable
fun FlyerDetailScreen(
    destination: MainDestination.FlyerDetailDestination,
    modifier: Modifier = Modifier,
    viewModel: FlyerDetailViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    LifecycleEventEffect(Lifecycle.Event.ON_CREATE) {
        viewModel.loadFlyer(destination.flyerId)
    }

    ObserveViewModelEvents(viewModel) { event ->
        when (event) {
            FlyerDetailEvent.Noop -> Unit
        }
    }

    FlyerDetailContent(
        uiState = uiState,
        modifier = modifier,
        onEditFlyer = { viewModel.editFlyer() },
        onApproveFlyer = { viewModel.approveFlyer() },
        onRejectFlyer = { viewModel.rejectFlyer() },
    )
}

/**
 * Content of the Flyer Detail screen.
 */
@Composable
internal fun FlyerDetailContent(
    uiState: FlyerDetailUIState,
    modifier: Modifier = Modifier,
    onEditFlyer: () -> Unit = {},
    onApproveFlyer: () -> Unit = {},
    onRejectFlyer: () -> Unit = {},
) {
    when (val state = uiState) {
        is FlyerDetailUIState.Loading -> {
            Box(modifier = modifier.fillMaxSize()) {
                LoadingStateBox()
            }
        }

        is FlyerDetailUIState.NotFound -> {
            Box(modifier = modifier.fillMaxSize()) {
                EmptyStateBox(
                    message = stringResource(Res.string.flyer_detail_screen_not_found),
                )
            }
        }

        is FlyerDetailUIState.Content -> {
            FlyerBoardPageShell(
                modifier = modifier,
                contentAlignment = Alignment.TopCenter,
                cardPadding = Padding.MEDIUM,
            ) {
                FlyerDetailBody(
                    state = state,
                    onEditFlyer = onEditFlyer,
                    onApproveFlyer = onApproveFlyer,
                    onRejectFlyer = onRejectFlyer,
                )
            }
        }
    }
}

@Composable
private fun FlyerDetailBody(
    state: FlyerDetailUIState.Content,
    modifier: Modifier = Modifier,
    onEditFlyer: () -> Unit,
    onApproveFlyer: () -> Unit,
    onRejectFlyer: () -> Unit,
) {
    val flyer = state.flyer
    FlyerAsyncImage(
        url = flyer.fileUrl,
        contentDescription = flyer.title,
        modifier = Modifier.fillMaxWidth(),
    )
    Column(
        modifier = modifier.padding(Padding.MEDIUM),
        verticalArrangement = Arrangement.spacedBy(Padding.SMALL),
    ) {
        StatusBadge(status = flyer.status)
        Text(
            text = flyer.title,
            style = MaterialTheme.typography.headlineSmall,
        )
        val postedBy = stringResource(Res.string.flyer_detail_screen_posted_by)
        val expiresLabel = stringResource(Res.string.flyer_detail_screen_expires_label)
        val separator = stringResource(Res.string.flyer_detail_screen_metadata_separator)
        val metadataParts =
            buildList {
                add("$postedBy ${flyer.uploaderId.userId}")
                add(flyer.createdAt)
                flyer.expiresAt?.let { add("$expiresLabel $it") }
            }
        Text(
            text = metadataParts.joinToString(separator),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.outline,
        )
        Text(
            text = flyer.description,
            style = MaterialTheme.typography.bodyLarge,
        )
        if (flyer.status == FlyerStatus.REJECTED && flyer.rejectionReason != null) {
            Text(
                text = "${stringResource(
                    Res.string.flyer_detail_screen_rejection_reason_label,
                )} ${flyer.rejectionReason}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error,
            )
        }
        if (state.canEdit || state.canModerate) {
            Row(
                modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(top = Padding.SMALL),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (state.canEdit) {
                    OutlinedButton(onClick = onEditFlyer) {
                        Text(stringResource(Res.string.flyer_detail_screen_button_edit_flyer))
                    }
                } else {
                    Spacer(modifier = Modifier.weight(1f))
                }
                if (state.canModerate) {
                    Row(horizontalArrangement = Arrangement.spacedBy(Padding.SMALL)) {
                        Button(
                            onClick = onApproveFlyer,
                            colors =
                            ButtonDefaults.buttonColors(
                                containerColor = Color(BUTTON_APPROVE_COLOR),
                                contentColor = Color.White,
                            ),
                        ) {
                            Text(stringResource(Res.string.flyer_detail_screen_button_approve))
                        }
                        Button(
                            onClick = onRejectFlyer,
                            colors =
                            ButtonDefaults.buttonColors(
                                containerColor = Color(BUTTON_REJECT_COLOR),
                                contentColor = Color.White,
                            ),
                        ) {
                            Text(stringResource(Res.string.flyer_detail_screen_button_reject))
                        }
                    }
                }
            }
        }
    }
}

@Suppress("MagicNumber")
private const val BUTTON_APPROVE_COLOR = 0xFF84CC16L

@Suppress("MagicNumber")
private const val BUTTON_REJECT_COLOR = 0xFFF43F5EL
