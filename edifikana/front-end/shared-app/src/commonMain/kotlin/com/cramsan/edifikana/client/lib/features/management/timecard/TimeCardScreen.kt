package com.cramsan.edifikana.client.lib.features.management.timecard

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.cramsan.edifikana.lib.model.EmployeeId
import com.cramsan.ui.components.LoadingAnimationOverlay
import com.cramsan.ui.components.ScreenLayout
import edifikana_lib.Res
import edifikana_lib.time_card_screen_add_event
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

/**
 * Represents the UI state of the Time Card screen.
 */
@Composable
fun TimeCardScreen(
    modifier: Modifier,
    viewModel: TimeCartViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        viewModel.loadEvents()
    }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                TimeCardEvent.Noop -> Unit
            }
        }
    }

    EventList(
        isLoading = uiState.isLoading,
        uiState.timeCardEvents,
        modifier = modifier,
        onEmployeeClick = { empPK ->
            viewModel.navigateToEmployee(empPK)
        },
        onAddEventClick = {
            viewModel.navigateToEmployeeList()
        },
    )
}

@Composable
internal fun EventList(
    isLoading: Boolean,
    events: List<com.cramsan.edifikana.client.lib.features.management.timecard.TimeCardUIModel>,
    modifier: Modifier = Modifier,
    onEmployeeClick: (EmployeeId) -> Unit,
    onAddEventClick: () -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxSize(),
    ) {
        ScreenLayout(
            fixedFooter = true,
            maxWith = Dp.Unspecified,
            sectionContent = { sectionModifier ->
                events.forEach { employee ->
                    EmployeeItem(
                        employee,
                        Modifier.fillMaxWidth(),
                        onEmployeeClick,
                    )
                }
            },
            buttonContent = { buttonModifier ->
                Button(
                    modifier = buttonModifier,
                    onClick = onAddEventClick,
                ) {
                    Text(text = stringResource(Res.string.time_card_screen_add_event))
                }
            },
        )
        LoadingAnimationOverlay(isLoading)
    }
}

@Composable
private fun EmployeeItem(
    employee: TimeCardUIModel,
    modifier: Modifier = Modifier,
    onEmployeeSelected: (EmployeeId) -> Unit,
) {
    Column(
        modifier = modifier
            .clickable { onEmployeeSelected(employee.employeePK) }
            .padding(16.dp),
        horizontalAlignment = Alignment.End,
    ) {
        Row {
            Text(employee.fullName)
            Spacer(modifier = Modifier.weight(1f))
            Text(employee.eventDescription)
        }
        Text(employee.eventTime)
    }
    HorizontalDivider()
}
