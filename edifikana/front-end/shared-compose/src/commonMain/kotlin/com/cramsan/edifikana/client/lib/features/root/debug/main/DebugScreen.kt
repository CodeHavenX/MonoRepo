package com.cramsan.edifikana.client.lib.features.root.debug.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.cramsan.edifikana.client.lib.features.root.EdifikanaApplicationViewModel
import com.cramsan.edifikana.client.lib.features.root.debug.DebugActivityViewModel
import org.koin.compose.koinInject

/**
 * Debug screen.
 *
 * This function provides the boilerplate needed to wire up the screen within the rest of the
 * application. This includes observing the view model's state and event flows and rendering the screen.
 */
@Composable
fun DebugScreen(
    activityViewModel: DebugActivityViewModel = koinInject(),
    applicationViewModel: EdifikanaApplicationViewModel = koinInject(),
    viewModel: DebugViewModel = koinInject()
) {
    val uiState by viewModel.uiState.collectAsState()
    val viewModelEvent by viewModel.event.collectAsState(DebugEvent.Noop)

    /**
     * For other possible lifecycle events, see the [Lifecycle.Event] documentation.
     */
    LifecycleEventEffect(Lifecycle.Event.ON_START) {
        viewModel.load()
    }
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        // Call this feature's viewModel
    }

    LaunchedEffect(viewModelEvent) {
        when (val event = viewModelEvent) {
            DebugEvent.Noop -> Unit
            is DebugEvent.TriggerActivityEvent -> {
                activityViewModel.executeDebugActivityEvent(event.activityEvent)
            }

            is DebugEvent.TriggerApplicationEvent -> {
                applicationViewModel.executeEvent(event.applicationEvent)
            }
        }
    }

    // Render the screen
    DebugContent(
        uiState.content,
    ) { key: String, value: Any ->
        viewModel.saveValue(key, value)
    }
}

/**
 * Content of the AccountEdit screen.
 */
@Composable
internal fun DebugContent(
    content: DebugUIModel,
    onFieldValueChanged: (key: String, value: Any) -> Unit,
) {
    Column {
        content.fields.forEach {
            when (it) {
                is Field.BooleanField -> {
                    BooleanRow(it.key, it.value, onFieldValueChanged)
                }
                is Field.StringField -> {
                    StringRow(it.key, it.value, onFieldValueChanged)
                }
                Field.Divider -> {
                    VerticalDivider()
                }
            }
        }
    }
}

@Composable
private fun BooleanRow(
    key: String,
    value: Boolean,
    onValueChanged: (String, Boolean) -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    Row(
        modifier = Modifier
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(),
                onClick = { onValueChanged(key, !value) }
            )
    ) {
        Text(key)
        Switch(
            checked = value,
            onCheckedChange = null,
            interactionSource = interactionSource,
        )
    }
}

@Composable
private fun StringRow(
    key: String,
    value: String,
    onValueChanged: (String, String) -> Unit,
) {
    TextField(
        label = { Text(key) },
        value = value,
        onValueChange = { onValueChanged(key, it) },
    )
}
