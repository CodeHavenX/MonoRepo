package com.cramsan.edifikana.client.lib.features.home.invitestaffmember

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.cramsan.edifikana.client.lib.features.home.HomeDestination
import com.cramsan.edifikana.client.ui.components.EdifikanaPrimaryButton
import com.cramsan.edifikana.client.ui.components.EdifikanaTextField
import com.cramsan.edifikana.client.ui.components.EdifikanaTopBar
import com.cramsan.framework.core.compose.ui.ObserveViewModelEvents
import com.cramsan.ui.components.LoadingAnimationOverlay
import com.cramsan.ui.components.ScreenLayout
import org.koin.compose.viewmodel.koinViewModel

/**
 * InviteStaffMember screen.
 *
 * This function provides the boilerplate needed to wire up the screen within the rest of the
 * application. This includes observing the view model's state and event flows and rendering the screen.
 */
@Composable
fun InviteStaffMemberScreen(
    destination: HomeDestination.InviteStaffMemberDestination,
    viewModel: InviteStaffMemberViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    /**
     * For other possible lifecycle events, see the [Lifecycle.Event] documentation.
     */
    LifecycleEventEffect(Lifecycle.Event.ON_CREATE) {
        viewModel.initialize(destination.orgId)
    }
    LifecycleEventEffect(Lifecycle.Event.ON_START) {
        // Call this feature's viewModel
    }
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        // Call this feature's viewModel
    }

    ObserveViewModelEvents(viewModel) { event ->
        when (event) {
            InviteStaffMemberEvent.Noop -> Unit
        }
    }

    // Render the screen
    InviteStaffMemberContent(
        uiState,
        onBackSelected = { viewModel.navigateBack() },
        onSendInvitationSelected = { email, role ->
            viewModel.sendInvitation(email, role)
        },
    )
}

/**
 * Content of the InviteStaffMember screen.
 */
@Composable
internal fun InviteStaffMemberContent(
    content: InviteStaffMemberUIState,
    modifier: Modifier = Modifier,
    onBackSelected: () -> Unit,
    onSendInvitationSelected: (email: String, role: StaffRoleUIModel?) -> Unit,
) {
    var email by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf<StaffRoleUIModel?>(null) }

    Scaffold(
        modifier = modifier,
        topBar = {
            EdifikanaTopBar(
                title = "Invite Staff Member",
                onNavigationIconSelected = onBackSelected,
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
                fixedFooter = true,
                sectionContent = { sectionModifier ->
                    EdifikanaTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = "Email address",
                        placeholder = "e.g. name@example.com",
                        modifier = sectionModifier,
                    )
                    RoleDropdown(
                        label = "Role",
                        roles = content.roles,
                        selectedRole = selectedRole,
                        onRoleSelected = { selectedRole = it },
                        modifier = sectionModifier,
                    )
                },
                buttonContent = { buttonModifier ->
                    EdifikanaPrimaryButton(
                        text = "Send Invitation",
                        modifier = buttonModifier,
                        onClick = {
                            onSendInvitationSelected(email, selectedRole)
                        },
                    )
                }
            )
            LoadingAnimationOverlay(content.isLoading)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RoleDropdown(
    label: String,
    roles: List<StaffRoleUIModel>,
    selectedRole: StaffRoleUIModel?,
    onRoleSelected: (StaffRoleUIModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }
    val displayValue = selectedRole?.displayName ?: "Select a role"

    Column(
        modifier = modifier.fillMaxWidth(),
    ) {
        Text(
            text = label,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp),
        )
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it },
        ) {
            OutlinedTextField(
                value = displayValue,
                modifier = Modifier
                    .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                    .fillMaxWidth(),
                trailingIcon = {
                    val icon = if (expanded) {
                        Icons.Filled.ArrowDropUp
                    } else {
                        Icons.Filled.ArrowDropDown
                    }
                    Icon(icon, displayValue)
                },
                singleLine = true,
                onValueChange = { },
                readOnly = true,
                shape = RoundedCornerShape(25.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                ),
                textStyle = MaterialTheme.typography.bodyLarge,
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
            ) {
                roles.forEach { role ->
                    DropdownMenuItem(
                        text = { Text(role.displayName) },
                        onClick = {
                            onRoleSelected(role)
                            expanded = false
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                    )
                }
            }
        }
    }
}
