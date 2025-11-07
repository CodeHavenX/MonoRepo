package com.cramsan.edifikana.client.lib.features.home.timecardemployeelist

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.cramsan.edifikana.client.ui.components.EdifikanaTopBar
import com.cramsan.edifikana.lib.model.EmployeeId
import com.cramsan.ui.components.ListCell
import com.cramsan.ui.components.LoadingAnimationOverlay
import com.cramsan.ui.components.ScreenLayout
import edifikana_lib.Res
import edifikana_lib.employee_list_screen_title
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

/**
 * Represents the UI state of the Employee List screen.
 */
@Composable
fun TimeCardEmployeeListScreen(
    viewModel: TimeCardEmployeeListViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        viewModel.loadEmployees()
    }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                TimeCardEmployeeListEvent.Noop -> Unit
            }
        }
    }

    TimeCardEmployeeList(
        uiState,
        onEmployeeClick = { employeePK ->
            viewModel.navigateToEmployee(employeePK)
        },
        onCloseSelected = {
            viewModel.navigateBack()
        },
    )
}

@Composable
internal fun TimeCardEmployeeList(
    uiState: TimeCardEmployeeListUIState,
    modifier: Modifier = Modifier,
    onEmployeeClick: (EmployeeId) -> Unit,
    onCloseSelected: () -> Unit,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            EdifikanaTopBar(
                title = stringResource(Res.string.employee_list_screen_title),
                onNavigationIconSelected = onCloseSelected,
            )
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentAlignment = Alignment.TopCenter,
        ) {
            ScreenLayout(
                sectionContent = { modifier ->
                    uiState.employees.forEach { employee ->
                        EmployeeItem(
                            employee,
                            modifier,
                            onEmployeeClick,
                        )
                    }
                }
            )
            LoadingAnimationOverlay(uiState.isLoading)
        }
    }
}

@Composable
private fun EmployeeItem(
    employee: TimeCardEmployeeUIModel,
    modifier: Modifier = Modifier,
    onEmployeeSelected: (EmployeeId) -> Unit,
) {
    ListCell(
        modifier = modifier,
        onSelection = { onEmployeeSelected(employee.employeePK) },
    ) {
        Text(employee.fullName)
    }
    HorizontalDivider()
}
