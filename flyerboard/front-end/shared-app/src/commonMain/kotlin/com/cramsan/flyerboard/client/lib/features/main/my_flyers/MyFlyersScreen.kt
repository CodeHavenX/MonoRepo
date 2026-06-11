package com.cramsan.flyerboard.client.lib.features.main.my_flyers

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.cramsan.flyerboard.client.lib.models.FlyerModel
import com.cramsan.flyerboard.client.ui.components.EmptyStateBox
import com.cramsan.flyerboard.client.ui.components.FlyerCardWithStatus
import com.cramsan.flyerboard.client.ui.components.LoadingStateBox
import com.cramsan.framework.core.compose.ui.ObserveViewModelEvents
import com.cramsan.ui.theme.Padding
import flyerboard_lib.Res
import flyerboard_lib.my_flyers_screen_button_submit
import flyerboard_lib.my_flyers_screen_empty_message
import flyerboard_lib.my_flyers_screen_navigate_back
import flyerboard_lib.my_flyers_screen_title
import flyerboard_ui.app_bar_action_sign_out
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
    isAuthenticated: Boolean = false,
    onSignOut: () -> Unit = {},
    onNavigateBack: () -> Unit,
    onRefresh: () -> Unit,
    onSubmitFlyer: () -> Unit = {},
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
                    TextButton(onClick = onSubmitFlyer) {
                        Text(stringResource(Res.string.my_flyers_screen_button_submit))
                    }
                    if (isAuthenticated) {
                        TextButton(onClick = onSignOut) {
                            Text(stringResource(flyerboard_ui.Res.string.app_bar_action_sign_out))
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
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
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
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(Padding.MEDIUM),
                        verticalArrangement = Arrangement.spacedBy(Padding.SMALL),
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
