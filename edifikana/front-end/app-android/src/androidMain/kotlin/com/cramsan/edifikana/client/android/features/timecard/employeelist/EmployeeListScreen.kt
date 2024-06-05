package com.cramsan.edifikana.client.android.features.timecard.employeelist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.cramsan.edifikana.client.android.R
import com.cramsan.edifikana.client.android.features.main.MainActivityEvent
import com.cramsan.edifikana.client.android.ui.components.LoadingAnimationOverlay
import com.cramsan.edifikana.lib.firestore.EmployeePK

@Composable
fun EmployeeListScreen(
    onMainActivityEventInvoke: (MainActivityEvent) -> Unit,
    onTitleChange: (String) -> Unit,
    viewModel: EmployeeListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val event by viewModel.event.collectAsState(EmployeeListEvent.Noop)

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        viewModel.loadEmployees()
    }

    LaunchedEffect(event) {
        when (val viewModelEvent = event) {
            EmployeeListEvent.Noop -> Unit
            is EmployeeListEvent.TriggerMainActivityEvent -> {
                onMainActivityEventInvoke(viewModelEvent.mainActivityEvent)
            }
        }
    }

    onTitleChange(uiState.title)
    EmployeeList(
        isLoading = uiState.isLoading,
        uiState.employees,
        onEmployeeClick = { employeePK ->
            viewModel.navigateToEmployee(employeePK)
        },
        onAddEmployeeClick = {
            viewModel.navigateToAddEmployee()
        },
    )
}

@Composable
private fun EmployeeList(
    isLoading: Boolean,
    employees: List<EmployeeUIModel>,
    onEmployeeClick: (EmployeePK) -> Unit,
    onAddEmployeeClick: () -> Unit,
) {
    LazyColumn(
        Modifier.fillMaxSize(),
    ) {
        items(employees) { employee ->
            EmployeeItem(
                employee,
                Modifier.fillMaxWidth(),
                onEmployeeClick,
            )
        }
        item {
            EmployeeOtherItem(
                Modifier.fillMaxWidth(),
                onAddEmployeeClick,
            )
        }
    }
    LoadingAnimationOverlay(isLoading)
}

@Composable
private fun EmployeeItem(
    employee: EmployeeUIModel,
    modifier: Modifier = Modifier,
    onEmployeeSelected: (EmployeePK) -> Unit,
) {
    Text(
        employee.fullName,
        modifier = modifier
            .clickable(
                enabled = employee.clickable
            ) { onEmployeeSelected(requireNotNull(employee.employeePK)) }
            .padding(16.dp),
    )
    HorizontalDivider()
}

@Composable
private fun EmployeeOtherItem(
    modifier: Modifier = Modifier,
    onAddEmployeeSelected: () -> Unit,
) {
    Text(
        stringResource(R.string.string_other),
        modifier = modifier
            .clickable { onAddEmployeeSelected() }
            .padding(16.dp),
    )
}

@Preview(
    showBackground = true,
)
@Composable
private fun EmployeeListScreenPreview() {
    EmployeeList(
        isLoading = true,
        employees = listOf(
            EmployeeUIModel(
                "Cesar Andres Ramirez Sanchez",
                EmployeePK("John"),
                true,
            ),
            EmployeeUIModel(
                "2",
                EmployeePK("Jane"),
                false,
            ),
        ),
        onEmployeeClick = {},
        onAddEmployeeClick = {},
    )
}
