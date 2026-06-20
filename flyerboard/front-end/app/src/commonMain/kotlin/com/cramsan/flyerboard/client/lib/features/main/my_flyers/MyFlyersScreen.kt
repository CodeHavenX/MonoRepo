package com.cramsan.flyerboard.client.lib.features.main.my_flyers

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.cramsan.flyerboard.client.lib.features.main.flyer_list.GRID_COLUMN_MIN_WIDTH
import com.cramsan.flyerboard.client.lib.models.FlyerModel
import com.cramsan.flyerboard.client.ui.components.EmptyStateBox
import com.cramsan.flyerboard.client.ui.components.FlyerCardWithStatus
import com.cramsan.flyerboard.client.ui.components.LoadingStateBox
import com.cramsan.framework.core.compose.ui.ObserveViewModelEvents
import com.cramsan.ui.theme.Padding
import flyerboard_lib.Res
import flyerboard_lib.my_flyers_screen_button_submit
import flyerboard_lib.my_flyers_screen_empty_message
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

/**
 * My Flyers screen — displays the authenticated user's own flyers with status badges.
 */
@Composable
fun MyFlyersScreen(
    modifier: Modifier = Modifier,
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
        onSubmitFlyer = { viewModel.onSubmitFlyer() },
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
    onSubmitFlyer: () -> Unit = {},
    onFlyerSelected: (FlyerModel) -> Unit,
    onEditFlyer: (FlyerModel) -> Unit,
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
                    contentDescription = stringResource(Res.string.my_flyers_screen_button_submit),
                )
            }
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding).fillMaxSize(),
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                when (val state = uiState) {
                    is MyFlyersUIState.Loading -> {
                        LoadingStateBox()
                    }

                    is MyFlyersUIState.Empty,
                    is MyFlyersUIState.Error,
                    -> {
                        EmptyStateBox(
                            message = stringResource(Res.string.my_flyers_screen_empty_message),
                        )
                    }

                    is MyFlyersUIState.Content -> {
                        LazyVerticalStaggeredGrid(
                            columns = StaggeredGridCells.Adaptive(minSize = GRID_COLUMN_MIN_WIDTH),
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(Padding.MEDIUM),
                            horizontalArrangement = Arrangement.spacedBy(Padding.SMALL),
                            verticalItemSpacing = Padding.SMALL,
                        ) {
                            items(state.flyers, key = { it.id.flyerId }) { flyer ->
                                FlyerCardWithStatus(
                                    title = flyer.title,
                                    description = flyer.description,
                                    status = flyer.status,
                                    expiresAt = flyer.expiresAt,
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
}
