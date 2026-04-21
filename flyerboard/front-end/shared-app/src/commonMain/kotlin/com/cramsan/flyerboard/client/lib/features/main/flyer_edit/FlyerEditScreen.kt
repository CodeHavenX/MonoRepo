package com.cramsan.flyerboard.client.lib.features.main.flyer_edit

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
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.cramsan.flyerboard.client.lib.features.main.MainDestination
import com.cramsan.framework.core.compose.ui.ObserveViewModelEvents
import com.cramsan.ui.theme.Padding
import flyerboard_lib.Res
import flyerboard_lib.flyer_edit_screen_button_save
import flyerboard_lib.flyer_edit_screen_label_description
import flyerboard_lib.flyer_edit_screen_label_expires_at
import flyerboard_lib.flyer_edit_screen_label_title
import flyerboard_lib.flyer_edit_screen_navigate_back
import flyerboard_lib.flyer_edit_screen_not_found
import flyerboard_lib.flyer_edit_screen_title
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

/**
 * Flyer Edit screen — allows editing the title, description and expiry of an owned flyer.
 */
@Composable
fun FlyerEditScreen(
    destination: MainDestination.FlyerEditDestination,
    modifier: Modifier = Modifier,
    viewModel: FlyerEditViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    LifecycleEventEffect(Lifecycle.Event.ON_CREATE) {
        viewModel.loadFlyer(destination.flyerId)
    }

    ObserveViewModelEvents(viewModel) { event ->
        when (event) {
            FlyerEditEvent.Noop -> Unit
        }
    }

    FlyerEditContent(
        uiState = uiState,
        modifier = modifier,
        onNavigateBack = { viewModel.navigateBack() },
        onTitleChanged = { viewModel.onTitleChanged(it) },
        onDescriptionChanged = { viewModel.onDescriptionChanged(it) },
        onExpiresAtChanged = { viewModel.onExpiresAtChanged(it) },
        onSave = { viewModel.saveFlyer(destination.flyerId) },
    )
}

/**
 * Content of the Flyer Edit screen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun FlyerEditContent(
    uiState: FlyerEditUIState,
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit,
    onTitleChanged: (String) -> Unit,
    onDescriptionChanged: (String) -> Unit,
    onExpiresAtChanged: (String) -> Unit,
    onSave: () -> Unit,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.flyer_edit_screen_title)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(Res.string.flyer_edit_screen_navigate_back),
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
                else -> FlyerEditForm(
                    uiState = uiState,
                    onTitleChanged = onTitleChanged,
                    onDescriptionChanged = onDescriptionChanged,
                    onExpiresAtChanged = onExpiresAtChanged,
                    onSave = onSave,
                )
            }
        }
    }
}

@Composable
private fun FlyerEditForm(
    uiState: FlyerEditUIState,
    modifier: Modifier = Modifier,
    onTitleChanged: (String) -> Unit,
    onDescriptionChanged: (String) -> Unit,
    onExpiresAtChanged: (String) -> Unit,
    onSave: () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(Padding.MEDIUM),
        verticalArrangement = Arrangement.spacedBy(Padding.MEDIUM),
    ) {
        OutlinedTextField(
            value = uiState.title,
            onValueChange = onTitleChanged,
            label = { Text(stringResource(Res.string.flyer_edit_screen_label_title)) },
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.isSaving,
        )
        OutlinedTextField(
            value = uiState.description,
            onValueChange = onDescriptionChanged,
            label = { Text(stringResource(Res.string.flyer_edit_screen_label_description)) },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3,
            enabled = !uiState.isSaving,
        )
        OutlinedTextField(
            value = uiState.expiresAt.orEmpty(),
            onValueChange = onExpiresAtChanged,
            label = { Text(stringResource(Res.string.flyer_edit_screen_label_expires_at)) },
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.isSaving,
        )
        uiState.errorMessage?.let { msg ->
            Text(text = msg)
        }
        Button(
            onClick = onSave,
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.isSaving,
        ) {
            if (uiState.isSaving) {
                CircularProgressIndicator()
            } else {
                Text(stringResource(Res.string.flyer_edit_screen_button_save))
            }
        }
    }
}
