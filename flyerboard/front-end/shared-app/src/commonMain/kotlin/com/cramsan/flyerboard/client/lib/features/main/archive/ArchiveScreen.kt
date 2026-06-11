package com.cramsan.flyerboard.client.lib.features.main.archive

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.cramsan.flyerboard.client.lib.models.FlyerModel
import com.cramsan.flyerboard.client.ui.components.EmptyStateBox
import com.cramsan.flyerboard.client.ui.components.FlyerBoardSearchBar
import com.cramsan.flyerboard.client.ui.components.FlyerCard
import com.cramsan.flyerboard.client.ui.components.LoadingStateBox
import com.cramsan.framework.core.compose.ui.ObserveViewModelEvents
import com.cramsan.ui.theme.Padding
import flyerboard_lib.Res
import flyerboard_lib.archive_screen_empty_message
import flyerboard_lib.archive_screen_navigate_back
import flyerboard_lib.archive_screen_search_placeholder
import flyerboard_lib.archive_screen_title
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

/**
 * Archive screen — displays publicly browsable expired/archived flyers with search.
 */
@Composable
fun ArchiveScreen(
    modifier: Modifier = Modifier,
    viewModel: ArchiveViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    LifecycleEventEffect(Lifecycle.Event.ON_CREATE) {
        viewModel.loadFlyers()
    }

    ObserveViewModelEvents(viewModel) { event ->
        when (event) {
            ArchiveEvent.Noop -> Unit
        }
    }

    ArchiveContent(
        uiState = uiState,
        modifier = modifier,
        onNavigateBack = { viewModel.navigateBack() },
        onRefresh = { viewModel.refresh() },
        onQueryChanged = { viewModel.onQueryChanged(it) },
        onFlyerSelected = { viewModel.onFlyerSelected(it.id) },
    )
}

/**
 * Content of the Archive screen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ArchiveContent(
    uiState: ArchiveUIState,
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit,
    onRefresh: () -> Unit,
    onQueryChanged: (String) -> Unit = {},
    onFlyerSelected: (FlyerModel) -> Unit,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.archive_screen_title)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(Res.string.archive_screen_navigate_back),
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onRefresh) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = stringResource(Res.string.archive_screen_title),
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding).fillMaxSize(),
        ) {
            FlyerBoardSearchBar(
                query = uiState.query,
                onQueryChange = onQueryChanged,
                placeholder = stringResource(Res.string.archive_screen_search_placeholder),
                modifier = Modifier.padding(horizontal = Padding.MEDIUM, vertical = Padding.SMALL),
            )
            Box(modifier = Modifier.fillMaxSize()) {
                when (val state = uiState) {
                    is ArchiveUIState.Loading -> {
                        LoadingStateBox()
                    }

                    is ArchiveUIState.Empty,
                    is ArchiveUIState.Error,
                    -> {
                        EmptyStateBox(
                            message = stringResource(Res.string.archive_screen_empty_message),
                        )
                    }

                    is ArchiveUIState.Content -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(Padding.MEDIUM),
                            verticalArrangement = Arrangement.spacedBy(Padding.SMALL),
                        ) {
                            items(state.flyers, key = { it.id.flyerId }) { flyer ->
                                FlyerCard(
                                    title = flyer.title,
                                    description = flyer.description,
                                    imageUrl = flyer.fileUrl,
                                    expiresAt = flyer.expiresAt,
                                    onClick = { onFlyerSelected(flyer) },
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
