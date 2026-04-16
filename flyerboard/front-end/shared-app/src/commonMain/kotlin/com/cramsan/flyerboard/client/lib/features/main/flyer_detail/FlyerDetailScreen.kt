package com.cramsan.flyerboard.client.lib.features.main.flyer_detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import coil3.compose.AsyncImage
import com.cramsan.flyerboard.client.lib.features.main.MainDestination
import com.cramsan.flyerboard.client.lib.models.FlyerModel
import com.cramsan.framework.core.compose.ui.ObserveViewModelEvents
import com.cramsan.ui.theme.Padding
import flyerboard_lib.Res
import flyerboard_lib.flyer_detail_screen_expires_label
import flyerboard_lib.flyer_detail_screen_navigate_back
import flyerboard_lib.flyer_detail_screen_not_found
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
            contentAlignment = Alignment.Center,
        ) {
            when {
                uiState.isLoading -> CircularProgressIndicator()
                uiState.flyer == null -> {
                    Text(
                        text = stringResource(Res.string.flyer_detail_screen_not_found),
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
                else -> FlyerDetailBody(flyer = uiState.flyer)
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
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(Padding.MEDIUM),
        verticalArrangement = Arrangement.spacedBy(Padding.MEDIUM),
    ) {
        flyer.fileUrl?.let { url ->
            AsyncImage(
                model = url,
                contentDescription = flyer.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(FLYER_ASPECT_RATIO),
            )
        }
        Text(
            text = flyer.title,
            style = MaterialTheme.typography.headlineSmall,
        )
        Text(
            text = flyer.description,
            style = MaterialTheme.typography.bodyLarge,
        )
        flyer.expiresAt?.let { expires ->
            Text(
                text = "${stringResource(Res.string.flyer_detail_screen_expires_label)} $expires",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.outline,
            )
        }
    }
}

private const val FLYER_ASPECT_RATIO = 4f / 3f
