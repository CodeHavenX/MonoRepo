package com.cramsan.flyerboard.client.lib.features.main.flyer_detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.cramsan.flyerboard.client.lib.features.main.MainDestination
import com.cramsan.flyerboard.client.lib.models.FlyerModel
import com.cramsan.flyerboard.client.ui.components.EmptyStateBox
import com.cramsan.flyerboard.client.ui.components.FlyerAsyncImage
import com.cramsan.flyerboard.client.ui.components.LoadingStateBox
import com.cramsan.flyerboard.client.ui.components.StatusBadge
import com.cramsan.flyerboard.lib.model.FlyerStatus
import com.cramsan.framework.core.compose.ui.ObserveViewModelEvents
import com.cramsan.ui.theme.Padding
import flyerboard_lib.Res
import flyerboard_lib.flyer_detail_screen_expires_label
import flyerboard_lib.flyer_detail_screen_metadata_separator
import flyerboard_lib.flyer_detail_screen_navigate_back
import flyerboard_lib.flyer_detail_screen_not_found
import flyerboard_lib.flyer_detail_screen_rejection_reason_label
import flyerboard_lib.flyer_detail_screen_title
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
        onNavigateBack = { viewModel.navigateBack() },
    )
}

/**
 * Content of the Flyer Detail screen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun FlyerDetailContent(
    uiState: FlyerDetailUIState,
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.flyer_detail_screen_title)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(Res.string.flyer_detail_screen_navigate_back),
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier.padding(innerPadding).fillMaxSize(),
        ) {
            when (val state = uiState) {
                is FlyerDetailUIState.Loading -> {
                    LoadingStateBox()
                }

                is FlyerDetailUIState.NotFound -> {
                    EmptyStateBox(
                        message = stringResource(Res.string.flyer_detail_screen_not_found),
                    )
                }

                is FlyerDetailUIState.Content -> {
                    FlyerDetailBody(flyer = state.flyer)
                }
            }
        }
    }
}

@Composable
private fun FlyerDetailBody(
    flyer: FlyerModel,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
        modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        HorizontalDivider(
            thickness = Padding.XX_SMALL,
            color = MaterialTheme.colorScheme.primary,
        )
        FlyerAsyncImage(
            url = flyer.fileUrl,
            contentDescription = flyer.title,
            modifier = Modifier.fillMaxWidth(),
        )
        Column(
            modifier = Modifier.padding(Padding.MEDIUM),
            verticalArrangement = Arrangement.spacedBy(Padding.SMALL),
        ) {
            StatusBadge(status = flyer.status)
            Text(
                text = flyer.title,
                style = MaterialTheme.typography.headlineSmall,
            )
            val metadataParts =
                buildList {
                    add(flyer.createdAt)
                    flyer.expiresAt?.let {
                        add("${stringResource(Res.string.flyer_detail_screen_expires_label)} $it")
                    }
                }
            Text(
                text = metadataParts.joinToString(stringResource(Res.string.flyer_detail_screen_metadata_separator)),
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
        }
    }
}
