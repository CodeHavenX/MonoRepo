package com.cramsan.flyerboard.client.lib.features.main.flyer_edit

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.cramsan.flyerboard.client.lib.features.main.MainDestination
import com.cramsan.flyerboard.client.lib.features.main.flyer_edit.FlyerEditUIState.Editing
import com.cramsan.flyerboard.client.lib.filepicker.FilePicker
import com.cramsan.flyerboard.client.ui.components.FlyerBoardFormCard
import com.cramsan.flyerboard.client.ui.components.LoadingStateBox
import com.cramsan.framework.core.compose.ui.ObserveViewModelEvents
import com.cramsan.ui.theme.Padding
import flyerboard_lib.Res
import flyerboard_lib.flyer_edit_screen_button_cancel
import flyerboard_lib.flyer_edit_screen_button_save
import flyerboard_lib.flyer_edit_screen_expires_at_hint
import flyerboard_lib.flyer_edit_screen_label_description
import flyerboard_lib.flyer_edit_screen_label_expires_at
import flyerboard_lib.flyer_edit_screen_label_title
import flyerboard_lib.flyer_edit_screen_subtitle
import flyerboard_lib.flyer_edit_screen_title
import flyerboard_lib.flyer_edit_screen_upload_hint
import flyerboard_lib.flyer_edit_screen_upload_size
import kotlinx.coroutines.launch
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
        onFileSelected = { bytes, name, mime -> viewModel.onFileSelected(bytes, name, mime) },
        onSave = { viewModel.saveFlyer(destination.flyerId) },
    )
}

/**
 * Content of the Flyer Edit screen.
 */
@Composable
internal fun FlyerEditContent(
    uiState: FlyerEditUIState,
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit,
    onTitleChanged: (String) -> Unit,
    onDescriptionChanged: (String) -> Unit,
    onExpiresAtChanged: (String) -> Unit,
    onFileSelected: (ByteArray, String, String) -> Unit,
    onSave: () -> Unit,
) {
    when (uiState) {
        is FlyerEditUIState.Loading -> {
            Box(modifier = modifier.fillMaxSize()) {
                LoadingStateBox()
            }
        }

        is Editing -> {
            FlyerEditForm(
                uiState = uiState,
                modifier = modifier,
                onNavigateBack = onNavigateBack,
                onTitleChanged = onTitleChanged,
                onDescriptionChanged = onDescriptionChanged,
                onExpiresAtChanged = onExpiresAtChanged,
                onFileSelected = onFileSelected,
                onSave = onSave,
            )
        }
    }
}

@Composable
private fun FlyerEditForm(
    uiState: Editing,
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit,
    onTitleChanged: (String) -> Unit,
    onDescriptionChanged: (String) -> Unit,
    onExpiresAtChanged: (String) -> Unit,
    onFileSelected: (ByteArray, String, String) -> Unit,
    onSave: () -> Unit,
) {
    val filePicker = remember { FilePicker() }
    val scope = rememberCoroutineScope()
    val outlineColor = MaterialTheme.colorScheme.secondary

    FlyerBoardFormCard(
        title = stringResource(Res.string.flyer_edit_screen_title),
        subtitle = stringResource(Res.string.flyer_edit_screen_subtitle),
        modifier = modifier,
        isLoading = uiState.isSaving,
    ) {
        Box(
            modifier =
            Modifier
                .fillMaxWidth()
                .drawBehind {
                    val strokeWidth = 2.dp.toPx()
                    val dashLength = 8.dp.toPx()
                    val gapLength = 4.dp.toPx()
                    drawRoundRect(
                        color = outlineColor,
                        style =
                        Stroke(
                            width = strokeWidth,
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(dashLength, gapLength)),
                        ),
                        cornerRadius = CornerRadius(12.dp.toPx()),
                    )
                }.clickable(enabled = !uiState.isSaving) {
                    scope.launch {
                        filePicker.pickFile()?.let { onFileSelected(it.bytes, it.name, it.mimeType) }
                    }
                }.padding(Padding.LARGE),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(Padding.SMALL),
            ) {
                Icon(
                    imageVector = Icons.Default.CloudUpload,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.secondary,
                )
                Text(
                    text =
                    uiState.selectedFileName
                        ?: stringResource(Res.string.flyer_edit_screen_upload_hint),
                    textAlign = TextAlign.Center,
                )
                if (uiState.selectedFileName == null) {
                    Text(
                        text = stringResource(Res.string.flyer_edit_screen_upload_size),
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
        }
        OutlinedTextField(
            value = uiState.title,
            onValueChange = onTitleChanged,
            label = { Text(stringResource(Res.string.flyer_edit_screen_label_title)) },
            modifier = Modifier.fillMaxWidth(),
            shape = CircleShape,
            singleLine = true,
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
            supportingText = { Text(stringResource(Res.string.flyer_edit_screen_expires_at_hint)) },
            modifier = Modifier.fillMaxWidth(),
            shape = CircleShape,
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
        OutlinedButton(
            onClick = onNavigateBack,
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.isSaving,
        ) {
            Text(stringResource(Res.string.flyer_edit_screen_button_cancel))
        }
    }
}
