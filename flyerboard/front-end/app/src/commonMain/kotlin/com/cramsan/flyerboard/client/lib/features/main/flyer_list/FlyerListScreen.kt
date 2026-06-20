package com.cramsan.flyerboard.client.lib.features.main.flyer_list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
import flyerboard_lib.flyer_list_screen_button_submit
import flyerboard_lib.flyer_list_screen_empty_message
import flyerboard_lib.flyer_list_screen_search_placeholder
import flyerboard_lib.flyer_list_screen_title
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

/**
 * Flyer List screen — displays the public flyer feed.
 */
@Composable
fun FlyerListScreen(
    modifier: Modifier = Modifier,
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
        onRefresh = { viewModel.refresh() },
        onQueryChanged = { viewModel.changeQuery(it) },
        onSubmitFlyer = { viewModel.primaryAction() },
        onFlyerSelected = { viewModel.selectFlyer(it.id) },
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
    onRefresh: () -> Unit,
    onQueryChanged: (String) -> Unit = {},
    onSubmitFlyer: () -> Unit = {},
    onFlyerSelected: (FlyerModel) -> Unit = {},
) {
    Scaffold(
        modifier = modifier,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onSubmitFlyer,
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(Res.string.flyer_list_screen_button_submit),
                )
            }
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding).fillMaxSize(),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = Padding.MEDIUM, vertical = Padding.SMALL),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                FlyerBoardSearchBar(
                    query = uiState.query,
                    onQueryChange = onQueryChanged,
                    placeholder = stringResource(Res.string.flyer_list_screen_search_placeholder),
                    modifier = Modifier.weight(1f),
                )
                IconButton(onClick = onRefresh) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = stringResource(Res.string.flyer_list_screen_title),
                    )
                }
            }
            Box(modifier = Modifier.fillMaxSize()) {
                when (uiState) {
                    is FlyerListUIState.Loading -> {
                        LoadingStateBox()
                    }

                    is FlyerListUIState.Empty,
                    is FlyerListUIState.Error,
                    -> {
                        EmptyStateBox(
                            message = stringResource(Res.string.flyer_list_screen_empty_message),
                        )
                    }

                    is FlyerListUIState.Content -> {
                        LazyVerticalStaggeredGrid(
                            columns = StaggeredGridCells.Adaptive(minSize = GRID_COLUMN_MIN_WIDTH),
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(Padding.MEDIUM),
                            horizontalArrangement = Arrangement.spacedBy(Padding.SMALL),
                            verticalItemSpacing = Padding.SMALL,
                        ) {
                            items(uiState.flyers, key = { it.id.flyerId }) { flyer ->
                                FlyerCard(
                                    title = flyer.title,
                                    description = flyer.description,
                                    imageUrl = flyer.fileUrl,
                                    expiresAt = flyer.expiresAt,
                                    accentColor = flyerAccentColor(flyer.id.flyerId.hashCode()),
                                    uploaderHandle = "@${flyer.uploaderId.userId}",
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

@Composable
private fun flyerAccentColor(seed: Int) =
    listOf(
        MaterialTheme.colorScheme.primaryContainer,
        MaterialTheme.colorScheme.tertiaryContainer,
        MaterialTheme.colorScheme.secondaryContainer,
    )[(seed % ACCENT_COLOR_COUNT).let { if (it < 0) it + ACCENT_COLOR_COUNT else it }]

private const val ACCENT_COLOR_COUNT = 3
val GRID_COLUMN_MIN_WIDTH = 280.dp
