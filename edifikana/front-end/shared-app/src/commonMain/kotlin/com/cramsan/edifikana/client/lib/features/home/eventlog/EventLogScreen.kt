package com.cramsan.edifikana.client.lib.features.home.eventlog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.cramsan.edifikana.lib.model.EventLogEventType
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.framework.core.compose.ui.ObserveViewModelEvents
import com.cramsan.ui.components.LoadingAnimationOverlay
import com.cramsan.ui.theme.Padding
import org.koin.compose.viewmodel.koinViewModel

/**
 * EventLog screen.
 *
 * This function provides the boilerplate needed to wire up the screen within the rest of the
 * application. This includes observing the view model's state and event flows and rendering the screen.
 */
@Composable
fun EventLogScreen(
    propertyId: PropertyId,
    modifier: Modifier = Modifier,
    viewModel: EventLogViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    /**
     * For other possible lifecycle events, see the [Lifecycle.Event] documentation.
     */
    LifecycleEventEffect(Lifecycle.Event.ON_START) {
        viewModel.loadEvents(propertyId)
    }

    ObserveViewModelEvents(viewModel) { event ->
        when (event) {
            EventLogEvent.Noop -> Unit
        }
    }

    // Render the screen
    EventLogContent(
        uiState = uiState,
        onAddEventClick = { viewModel.onAddEventClicked() },
        modifier = modifier,
    )
}

/**
 * Content of the EventLog screen.
 */
@Composable
internal fun EventLogContent(
    uiState: EventLogUIState,
    onAddEventClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxSize(),
    ) {
        if (uiState.events.isEmpty() && !uiState.isLoading) {
            // Empty state
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "No events found",
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = Padding.MEDIUM),
                verticalArrangement = Arrangement.spacedBy(Padding.SMALL),
            ) {
                // Date header
                item {
                    Spacer(modifier = Modifier.height(Padding.MEDIUM))
                    Text(
                        text = "Today",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(vertical = Padding.SMALL),
                    )
                }

                items(uiState.events) { event ->
                    EventLogItem(event = event)
                }

                // Add spacing at bottom for FAB
                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }

        // Floating Action Button
        ExtendedFloatingActionButton(
            onClick = onAddEventClick,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(Padding.MEDIUM),
            icon = {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                )
            },
            text = {
                Text(text = "Add Event")
            },
        )

        LoadingAnimationOverlay(uiState.isLoading)
    }
}

/**
 * A single event log item.
 */
@Composable
private fun EventLogItem(
    event: EventLogUIModel,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = Padding.SMALL),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Padding.MEDIUM),
    ) {
        // Event type icon in circular background
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = getEventTypeIcon(event.eventType),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(24.dp),
            )
        }

        // Event details
        Column(
            modifier = Modifier.weight(1f),
        ) {
            Text(
                text = event.title,
                style = MaterialTheme.typography.bodyLarge,
            )
            event.employeeName?.let { name ->
                Text(
                    text = "Reported by: $name",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

/**
 * Get the appropriate icon for the event type.
 */
private fun getEventTypeIcon(eventType: EventLogEventType): ImageVector {
    return when (eventType) {
        EventLogEventType.INCIDENT -> Icons.Default.Warning
        EventLogEventType.DELIVERY -> Icons.Default.ShoppingCart
        EventLogEventType.GUEST -> Icons.Default.Person
        EventLogEventType.MAINTENANCE_SERVICE -> Icons.Default.Build
        else -> Icons.Default.Warning
    }
}
