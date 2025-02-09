package com.cramsan.edifikana.client.lib.features.admin.properties

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.cramsan.edifikana.client.lib.features.EdifikanaApplicationViewModel
import com.cramsan.edifikana.client.ui.components.EdifikanaTopBar
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.ui.components.LoadingAnimationOverlay
import com.cramsan.ui.theme.Padding
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

/**
 * PropertyManager screen.
 *
 * This function provides the boilerplate needed to wire up the screen within the rest of the
 * application. This includes observing the view model's state and event flows and rendering the screen.
 */
@Composable
fun PropertyManagerScreen(
    applicationViewModel: EdifikanaApplicationViewModel = koinInject(),
    viewModel: PropertyManagerViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val viewModelEvent by viewModel.events.collectAsState(PropertyManagerEvent.Noop)

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
            is PropertyManagerEvent.TriggerApplicationEvent -> {
                applicationViewModel.executeEvent(event.applicationEvent)
            }
        }
    }

    Scaffold(
        topBar = {
            EdifikanaTopBar(
                title = "Properties",
                onCloseClicked = { viewModel.navigateBack() },
            )
        },
    ) { innerPadding ->
        // Render the screen
        PropertyManagerContent(
            uiState.content,
            Modifier.padding(innerPadding),
            uiState.isLoading,
            onPropertyClicked = { property ->
                viewModel.navigateToPropertyDetails(property)
            },
            onAddPropertyClicked = {
                viewModel.navigateToAddProperty()
            }
        )
    }
}

/**
 * Content of the AccountEdit screen.
 */
@Composable
internal fun PropertyManagerContent(
    content: PropertyManagerUIModel,
    modifier: Modifier = Modifier,
    loading: Boolean,
    onPropertyClicked: (PropertyId) -> Unit,
    onAddPropertyClicked: () -> Unit,
) {
    Column(
        modifier = modifier,
    ) {
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
