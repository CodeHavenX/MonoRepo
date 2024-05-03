package com.cramsan.edifikana.client.android.screens.clockinout

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.navigation.NavHostController
import com.cramsan.edifikana.client.android.Screens
import com.cramsan.edifikana.client.android.compose.LoadingAnimationOverlay
import com.cramsan.edifikana.lib.firestore.EmployeePK

@Composable
fun ClockInOutScreen(
    navController: NavHostController,
    viewModel: ClockInOutViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        viewModel.loadEmployees()
    }

    AnimatedContent(targetState = uiState, label = "") { state ->
        when (state) {
            is ClockInOutUIState.Loading -> {
                LoadingAnimationOverlay()
            }
            is ClockInOutUIState.Empty -> {
                // Empty
            }
            is ClockInOutUIState.Success -> {
                EmployeeList(
                    state.employees,
                    onEmployeeSelected = { employeePK ->
                        navController.navigate("clockin/${employeePK.documentPath}")
                    },
                    onAddEmployeeSelected = {
                        navController.navigate(Screens.ClockInOutAddEmployee.route)
                    },
                )
            }
            is ClockInOutUIState.Error -> {
                // Error
            }
        }
    }
}

@Composable
private fun EmployeeList(
    employees: List<EmployeeUIModel>,
    onEmployeeSelected: (EmployeePK) -> Unit,
    onAddEmployeeSelected: () -> Unit,
) {
    LazyColumn(
        Modifier.fillMaxSize(),
    ) {
        items(employees) { employee ->
            EmployeeItem(
                employee,
                Modifier.fillMaxWidth(),
                onEmployeeSelected,
            )
        }
        item {
            EmployeeOtherItem(
                Modifier.fillMaxWidth(),
                onAddEmployeeSelected,
            )
        }
    }
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
            .clickable { onEmployeeSelected(employee.employeePK) }
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
        "Otro",
        modifier = modifier
            .clickable { onAddEmployeeSelected() }
            .padding(16.dp),
    )
}

@Preview(
    showBackground = true,
)
@Composable
private fun PreviewClockInOutScreen() {
    EmployeeList(
        employees = listOf(
            EmployeeUIModel("Cesar Andres Ramirez Sanchez", EmployeePK("John")),
            EmployeeUIModel("2", EmployeePK("Jane")),
        ),
        onEmployeeSelected = {},
        onAddEmployeeSelected = {},
    )
}