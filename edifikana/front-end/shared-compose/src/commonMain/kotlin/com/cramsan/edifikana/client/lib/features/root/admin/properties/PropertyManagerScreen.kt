package com.cramsan.edifikana.client.lib.features.root.admin.properties

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.cramsan.edifikana.client.lib.features.root.EdifikanaApplicationViewModel
import com.cramsan.edifikana.client.lib.features.root.admin.AdminActivityViewModel
import com.cramsan.edifikana.client.lib.ui.components.LoadingAnimationOverlay
import com.cramsan.edifikana.client.lib.ui.theme.Padding
import com.cramsan.edifikana.lib.model.PropertyId
import org.koin.compose.koinInject

/**
 * PropertyManager screen.
 *
 * This function provides the boilerplate needed to wire up the screen within the rest of the
 * application. This includes observing the view model's state and event flows and rendering the screen.
 */
@Composable
fun PropertyManagerScreen(
    accountActivityViewModel: AdminActivityViewModel = koinInject(),
    applicationViewModel: EdifikanaApplicationViewModel = koinInject(),
    viewModel: PropertyManagerViewModel = koinInject()
) {
    val uiState by viewModel.uiState.collectAsState()
    val viewModelEvent by viewModel.event.collectAsState(PropertyManagerEvent.Noop)

    /**
     * For other possible lifecycle events, see the [Lifecycle.Event] documentation.
     */
    LifecycleEventEffect(Lifecycle.Event.ON_START) {
        viewModel.loadPage()
    }
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        // Call this feature's viewModel
    }

    LaunchedEffect(viewModelEvent) {
        when (val event = viewModelEvent) {
            PropertyManagerEvent.Noop -> Unit
            is PropertyManagerEvent.TriggerActivityEvent -> {
                accountActivityViewModel.executeAdminActivityEvent(event.activityEvent)
            }

            is PropertyManagerEvent.TriggerApplicationEvent -> {
                applicationViewModel.executeEvent(event.applicationEvent)
            }
        }
    }

    // Render the screen
    PropertyManagerContent(
        uiState.content,
        uiState.isLoading,
        onPropertyClicked = { property ->
            viewModel.navigateToPropertyDetails(property)
        },
        onAddPropertyClicked = {
            viewModel.navigateToAddProperty()
        }
    )
}

/**
 * Content of the AccountEdit screen.
 */
@Composable
internal fun PropertyManagerContent(
    content: PropertyManagerUIModel,
    loading: Boolean,
    onPropertyClicked: (PropertyId) -> Unit,
    onAddPropertyClicked: () -> Unit,
) {
    Column {
        Row {
            Button(onClick = onAddPropertyClicked) {
                Text("Add Property")
            }
        }
        LazyColumn {
            items(content.properties) {
                PropertyRow(it, onClick = { onPropertyClicked(it.id) })
            }
        }
    }
    LoadingAnimationOverlay(isLoading = loading)
}

@Composable
private fun PropertyRow(
    property: PropertyUIModel,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Row(
        modifier = modifier
            .padding(Padding.X_SMALL)
            .clickable { onClick() },
    ) {
        Column {
            Text(
                property.name,
                style = MaterialTheme.typography.bodyMedium,
            )
            Text(
                property.address,
                style = MaterialTheme.typography.labelSmall,
            )
        }
    }
}
