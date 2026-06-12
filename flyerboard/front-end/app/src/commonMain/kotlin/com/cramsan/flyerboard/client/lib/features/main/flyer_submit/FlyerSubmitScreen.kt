package com.cramsan.flyerboard.client.lib.features.main.flyer_submit

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import com.cramsan.flyerboard.client.lib.filepicker.FilePicker
import com.cramsan.framework.core.compose.ui.ObserveViewModelEvents
import com.cramsan.ui.theme.Padding
import flyerboard_lib.Res
import flyerboard_lib.flyer_submit_screen_button_cancel
import flyerboard_lib.flyer_submit_screen_button_submit
import flyerboard_lib.flyer_submit_screen_description_placeholder
import flyerboard_lib.flyer_submit_screen_expires_at_hint
import flyerboard_lib.flyer_submit_screen_label_description
import flyerboard_lib.flyer_submit_screen_label_expires_at
import flyerboard_lib.flyer_submit_screen_label_title
import flyerboard_lib.flyer_submit_screen_navigate_back
import flyerboard_lib.flyer_submit_screen_subtitle
import flyerboard_lib.flyer_submit_screen_title
import flyerboard_lib.flyer_submit_screen_title_placeholder
import flyerboard_lib.flyer_submit_screen_upload_hint
import flyerboard_lib.flyer_submit_screen_upload_size
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

/**
 * Flyer Submit screen — form for creating a new flyer.
 */
@Composable
fun FlyerSubmitScreen(
    modifier: Modifier = Modifier,
    viewModel: FlyerSubmitViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    ObserveViewModelEvents(viewModel) { event ->
        when (event) {
            FlyerSubmitEvent.Noop -> Unit
        }
    }

    FlyerSubmitContent(
        uiState = uiState,
        modifier = modifier,
        onNavigateBack = { viewModel.navigateBack() },
        onTitleChanged = { viewModel.onTitleChanged(it) },
        onDescriptionChanged = { viewModel.onDescriptionChanged(it) },
        onExpiresAtChanged = { viewModel.onExpiresAtChanged(it) },
        onFileSelected = { bytes, name, mime -> viewModel.onFileSelected(bytes, name, mime) },
        onSubmit = { viewModel.submit() },
    )
}

/**
 * Content of the Flyer Submit screen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun FlyerSubmitContent(
    uiState: FlyerSubmitUIState,
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit,
    onTitleChanged: (String) -> Unit,
    onDescriptionChanged: (String) -> Unit,
    onExpiresAtChanged: (String) -> Unit,
    onFileSelected: (ByteArray, String, String) -> Unit,
    onSubmit: () -> Unit,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.flyer_submit_screen_title)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(Res.string.flyer_submit_screen_navigate_back),
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
            FlyerSubmitForm(
                uiState = uiState,
                onNavigateBack = onNavigateBack,
                onTitleChanged = onTitleChanged,
                onDescriptionChanged = onDescriptionChanged,
                onExpiresAtChanged = onExpiresAtChanged,
                onFileSelected = onFileSelected,
                onSubmit = onSubmit,
            )
        }
    }
}

@Composable
private fun FlyerSubmitForm(
    uiState: FlyerSubmitUIState,
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit,
    onTitleChanged: (String) -> Unit,
    onDescriptionChanged: (String) -> Unit,
    onExpiresAtChanged: (String) -> Unit,
    onFileSelected: (ByteArray, String, String) -> Unit,
    onSubmit: () -> Unit,
) {
    val filePicker = remember { FilePicker() }
    val scope = rememberCoroutineScope()
    val isSubmitting = uiState.status is SubmitStatus.Submitting
    val outlineColor = MaterialTheme.colorScheme.secondary
    Column(
        modifier =
        modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(Padding.MEDIUM),
        verticalArrangement = Arrangement.spacedBy(Padding.MEDIUM),
    ) {
        Text(
            text = stringResource(Res.string.flyer_submit_screen_subtitle),
            style = MaterialTheme.typography.bodyMedium,
        )
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
                }.clickable(enabled = !isSubmitting) {
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
                        ?: stringResource(Res.string.flyer_submit_screen_upload_hint),
                    textAlign = TextAlign.Center,
                )
                if (uiState.selectedFileName == null) {
                    Text(
                        text = stringResource(Res.string.flyer_submit_screen_upload_size),
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
        }
        OutlinedTextField(
            value = uiState.title,
            onValueChange = onTitleChanged,
            label = { Text(stringResource(Res.string.flyer_submit_screen_label_title)) },
            placeholder = { Text(stringResource(Res.string.flyer_submit_screen_title_placeholder)) },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isSubmitting,
        )
        OutlinedTextField(
            value = uiState.description,
            onValueChange = onDescriptionChanged,
            label = { Text(stringResource(Res.string.flyer_submit_screen_label_description)) },
            placeholder = { Text(stringResource(Res.string.flyer_submit_screen_description_placeholder)) },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3,
            enabled = !isSubmitting,
        )
        OutlinedTextField(
            value = uiState.expiresAt.orEmpty(),
            onValueChange = onExpiresAtChanged,
            label = { Text(stringResource(Res.string.flyer_submit_screen_label_expires_at)) },
            supportingText = { Text(stringResource(Res.string.flyer_submit_screen_expires_at_hint)) },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isSubmitting,
        )
        if (uiState.status is SubmitStatus.Failed) {
            Text(text = uiState.status.message)
        }
        Button(
            onClick = onSubmit,
            modifier = Modifier.fillMaxWidth(),
            enabled = !isSubmitting,
        ) {
            if (isSubmitting) {
                CircularProgressIndicator()
            } else {
                Text(stringResource(Res.string.flyer_submit_screen_button_submit))
            }
        }
        OutlinedButton(
            onClick = onNavigateBack,
            modifier = Modifier.fillMaxWidth(),
            enabled = !isSubmitting,
        ) {
            Text(stringResource(Res.string.flyer_submit_screen_button_cancel))
        }
    }
}
