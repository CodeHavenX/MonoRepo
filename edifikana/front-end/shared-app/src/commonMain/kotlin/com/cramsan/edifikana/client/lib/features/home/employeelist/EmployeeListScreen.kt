package com.cramsan.edifikana.client.lib.features.home.employeelist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.cramsan.edifikana.lib.model.EmployeeId
import com.cramsan.edifikana.lib.model.InviteId
import com.cramsan.edifikana.lib.model.UserId
import com.cramsan.framework.core.compose.ui.ObserveViewModelEvents
import com.cramsan.ui.components.ListCell
import com.cramsan.ui.components.LoadingAnimationOverlay
import com.cramsan.ui.components.ScreenLayout
import com.cramsan.ui.theme.Padding
import org.koin.compose.viewmodel.koinViewModel

/**
 * EmployeeList screen.
 *
 * This function provides the boilerplate needed to wire up the screen within the rest of the
 * application. This includes observing the view model's state and event flows and rendering the screen.
 */
@Composable
fun EmployeeListScreen(
    modifier: Modifier = Modifier,
    viewModel: EmployeeListViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    /**
     * For other possible lifecycle events, see the [Lifecycle.Event] documentation.
     */
    LifecycleEventEffect(Lifecycle.Event.ON_CREATE) {
        viewModel.loadEmployeeList()
    }
    LifecycleEventEffect(Lifecycle.Event.ON_START) {
        // Call this feature's viewModel
    }
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        // Call this feature's viewModel
    }

    ObserveViewModelEvents(viewModel) { event ->
        when (event) {
            EmployeeListEvent.Noop -> Unit
        }
    }

    // Render the screen
    EmployeeListContent(
        uiState,
        modifier = modifier,
        onEmployeeSelected = { empId ->
            viewModel.navigateToEmployee(empId)
        },
        onAddPrimaryEmployeeSelected = {
            viewModel.navigateToAddPrimaryEmployee()
        },
        onAddSecondaryEmployeeSelected = {
            viewModel.navigateToAddSecondaryEmployee()
        },
        onUserSelected = { userId ->
        },
        onInviteSelected = { inviteId ->
        },
    )
}

/**
 * Content of the AccountEdit screen.
 */
@Composable
internal fun EmployeeListContent(
    content: EmployeeListUIState,
    modifier: Modifier = Modifier,
    onEmployeeSelected: (EmployeeId) -> Unit,
    onAddPrimaryEmployeeSelected: () -> Unit,
    onAddSecondaryEmployeeSelected: () -> Unit,
    onUserSelected: (UserId) -> Unit,
    onInviteSelected: (InviteId) -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxSize(),
        contentAlignment = Alignment.TopCenter,
    ) {
        ScreenLayout(
            fixedFooter = true,
            maxWith = Dp.Unspecified,
            sectionContent = { sectionModifier ->
                content.employeeList.forEach { emp ->
                    when (emp) {
                        is EmployeeMemberUIModel -> EmployeeListItem(
                            employee = emp,
                            modifier = sectionModifier,
                            onEmployeeSelected = onEmployeeSelected,
                        )

                        is UserUIModel -> UserListItem(
                            user = emp,
                            modifier = sectionModifier,
                            onUserSelected = onUserSelected,
                        )

                        is InviteUIModel -> InviteListItem(
                            invite = emp,
                            modifier = sectionModifier,
                            onInviteSelected = onInviteSelected,
                        )
                    }
                }
            },
            buttonContent = { buttonModifier ->
                Button(
                    modifier = buttonModifier,
                    onClick = {
                        onAddPrimaryEmployeeSelected()
                    },
                ) {
                    Text(text = "Invite a user")
                }
                OutlinedButton(
                    modifier = buttonModifier,
                    onClick = {
                        onAddSecondaryEmployeeSelected()
                    },
                ) {
                    Text(text = "Create a user")
                }
            }
        )
        LoadingAnimationOverlay(content.isLoading)
    }
}

@Composable
private fun InviteListItem(
    invite: InviteUIModel,
    modifier: Modifier,
    onInviteSelected: (InviteId) -> Unit,
) {
    ListCell(
        modifier = modifier,
        onSelection = {
            onInviteSelected(invite.inviteId)
        },
        endSlot = {
            Text(
                text = "Invitation sent",
                color = MaterialTheme.colorScheme.secondary,
            )
        }
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(Padding.SMALL),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = invite.email,
                color = MaterialTheme.colorScheme.secondary,
            )
        }
    }
}

@Composable
private fun UserListItem(user: UserUIModel, modifier: Modifier, onUserSelected: (UserId) -> Unit) {
    ListCell(
        modifier = modifier,
        onSelection = {
            onUserSelected(user.userId)
        },
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(Padding.SMALL),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = user.name,
                color = MaterialTheme.colorScheme.onSurface,
            )
            user.email?.let {
                Text(
                    it,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
        }
    }
}

@Composable
private fun EmployeeListItem(
    employee: EmployeeMemberUIModel,
    modifier: Modifier,
    onEmployeeSelected: (EmployeeId) -> Unit
) {
    val textColor = MaterialTheme.colorScheme.onSurface
    ListCell(
        modifier = modifier,
        onSelection = {
            onEmployeeSelected(employee.employeeId)
        },
        endSlot = {
        }
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(Padding.SMALL),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = employee.name,
                color = textColor,
            )
            employee.email?.let {
                Text(
                    it,
                    style = MaterialTheme.typography.labelSmall,
                    color = textColor,
                )
            }
        }
    }
}
