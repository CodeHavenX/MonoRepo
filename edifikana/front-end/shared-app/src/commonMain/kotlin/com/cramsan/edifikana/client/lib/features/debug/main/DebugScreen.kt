package com.cramsan.edifikana.client.lib.features.debug.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.cramsan.edifikana.client.lib.features.EdifikanaApplicationViewModel
import com.cramsan.edifikana.client.ui.components.EdifikanaTopBar
import com.cramsan.edifikana.client.ui.components.ScreenLayout
import com.cramsan.ui.components.PasswordOutlinedTextField
import com.cramsan.ui.theme.Padding
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

/**
 * Debug screen.
 *
 * This function provides the boilerplate needed to wire up the screen within the rest of the
 * application. This includes observing the view model's state and event flows and rendering the screen.
 */
@Composable
fun DebugScreen(
    viewModel: DebugViewModel = koinViewModel(),
    applicationViewModel: EdifikanaApplicationViewModel = koinInject()
) {
    val uiState by viewModel.uiState.collectAsState()
    val viewModelEvent by viewModel.events.collectAsState(DebugEvent.Noop)

    /**
     * For other possible lifecycle events, see the [Lifecycle.Event] documentation.
     */
    LifecycleEventEffect(Lifecycle.Event.ON_START) {
        viewModel.loadData()
    }
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        // Call this feature's viewModel
    }
    LifecycleEventEffect(Lifecycle.Event.ON_PAUSE) {
        viewModel.saveBufferedChanges()
    }

    LaunchedEffect(viewModelEvent) {
        when (val event = viewModelEvent) {
            DebugEvent.Noop -> Unit
            is DebugEvent.TriggerApplicationEvent -> {
                applicationViewModel.executeEvent(event.event)
            }
        }
    }

    DebugContent(
        uiState,
        bufferChanges = { key: String, value: Any ->
            viewModel.bufferChanges(key, value)
        },
        saveChanges = { key: String, value: Any ->
            viewModel.saveValue(key, value)
        },
        onCloseSelected = { viewModel.navigateBack() },
    )
}

/**
 * Content of the AccountEdit screen.
 */
@Composable
internal fun DebugContent(
    content: DebugUIModelUI,
    modifier: Modifier = Modifier,
    bufferChanges: (String, Any) -> Unit,
    saveChanges: (key: String, value: Any) -> Unit,
    onCloseSelected: () -> Unit,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            EdifikanaTopBar(
                title = "Debug Menu",
                onCloseClicked = onCloseSelected,
            )
        },
    ) { innerPadding ->
        // Render the screen
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentAlignment = Alignment.TopCenter,
        ) {
            ScreenLayout(
                sectionContent = { modifier ->
                    content.fields.forEach {
                        when (it) {
                            is Field.BooleanField -> {
                                BooleanRow(it, modifier, saveChanges)
                            }

                            is Field.StringField -> {
                                StringRow(it, modifier, bufferChanges, saveChanges)
                            }

                            Field.Divider -> {
                                HorizontalDivider(modifier)
                            }

                            is Field.Label -> {
                                LabelRow(it, modifier)
                            }
                        }
                    }
                }
            )
        }
    }
}

@Composable
private fun LabelRow(label: Field.Label, modifier: Modifier) {
    Column(
        modifier = modifier,
    ) {
        Text(
            text = label.label,
            style = MaterialTheme.typography.labelLarge,
        )
        label.subtitle?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.labelMedium,
            )
        }
    }
}

@Composable
private fun BooleanRow(
    field: Field.BooleanField,
    modifier: Modifier,
    onValueChanged: (String, Boolean) -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    Row(
        modifier = Modifier
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(),
                onClick = { onValueChanged(field.key, !field.value) }
            )
            .then(modifier),
        horizontalArrangement = Arrangement.spacedBy(Padding.SMALL),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier
                .wrapContentHeight()
                .weight(1f)
        ) {
            Text(
                field.title,
                style = MaterialTheme.typography.labelLarge,
            )
            field.subtitle?.let {
                Text(
                    it,
                    style = MaterialTheme.typography.labelMedium,
                )
            }
        }
        Switch(
            checked = field.value,
            onCheckedChange = null,
            interactionSource = null,
        )
    }
}

@Composable
private fun StringRow(
    field: Field.StringField,
    modifier: Modifier,
    onValueChanged: (String, String) -> Unit,
    onFocusChange: (String, String) -> Unit,
) {
    var stringValue by remember(field.value) { mutableStateOf(field.value) }

    val fieldModifier = Modifier
        .fillMaxWidth()
        .onFocusChanged {
            if (!it.isFocused) {
                onFocusChange(field.key, stringValue)
            }
        }

    Column(
        modifier = modifier,
    ) {
        if (field.secure) {
            PasswordOutlinedTextField(
                label = { Text(field.title) },
                value = stringValue,
                modifier = fieldModifier,
                onValueChange = {
                    stringValue = it
                    onValueChanged(field.key, it)
                },
            )
        } else {
            OutlinedTextField(
                label = { Text(field.title) },
                singleLine = true,
                value = stringValue,
                modifier = fieldModifier,
                onValueChange = {
                    stringValue = it
                    onValueChanged(field.key, it)
                },
            )
        }
        field.subtitle?.let {
            Text(
                it,
                style = MaterialTheme.typography.labelMedium,
            )
        }
    }
}
