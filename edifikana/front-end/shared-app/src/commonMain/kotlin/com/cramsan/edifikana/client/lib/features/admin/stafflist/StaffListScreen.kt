package com.cramsan.edifikana.client.lib.features.admin.stafflist

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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.cramsan.edifikana.lib.model.StaffId
import com.cramsan.edifikana.lib.model.StaffStatus
import com.cramsan.ui.components.ListCell
import com.cramsan.ui.components.LoadingAnimationOverlay
import com.cramsan.ui.components.ScreenLayout
import com.cramsan.ui.theme.Padding
import edifikana_lib.Res
import edifikana_lib.text_add
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

/**
 * StaffList screen.
 *
 * This function provides the boilerplate needed to wire up the screen within the rest of the
 * application. This includes observing the view model's state and event flows and rendering the screen.
 */
@Composable
fun StaffListScreen(
    modifier: Modifier = Modifier,
    viewModel: StaffListViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    /**
     * For other possible lifecycle events, see the [Lifecycle.Event] documentation.
     */
    LifecycleEventEffect(Lifecycle.Event.ON_CREATE) {
        viewModel.loadStaffList()
    }
    LifecycleEventEffect(Lifecycle.Event.ON_START) {
        // Call this feature's viewModel
    }
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        // Call this feature's viewModel
    }

    val screenScope = rememberCoroutineScope()
    screenScope.launch {
        viewModel.events.collect { event ->
            when (event) {
                StaffListEvent.Noop -> Unit
            }
        }
    }

    // Render the screen
    StaffListContent(
        uiState,
        modifier = modifier,
        onStaffSelected = { staffId ->
            viewModel.navigateToStaff(staffId)
        },
        onAddPrimaryStaffSelected = {
            viewModel.navigateToAddPrimaryStaff()
        },
        onAddSecondaryStaffSelected = {
            viewModel.navigateToAddSecondaryStaff()
        },
    )
}

/**
 * Content of the AccountEdit screen.
 */
@Composable
internal fun StaffListContent(
    content: StaffListUIState,
    modifier: Modifier = Modifier,
    onStaffSelected: (StaffId) -> Unit,
    onAddPrimaryStaffSelected: () -> Unit,
    onAddSecondaryStaffSelected: () -> Unit,
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
                content.staffList.forEach { staff ->
                    val isStaffPending = staff.status == StaffStatus.PENDING
                    val textColor = if (isStaffPending) {
                        MaterialTheme.colorScheme.secondary
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    }
                    ListCell(
                        modifier = sectionModifier,
                        onSelection = {
                            onStaffSelected(staff.id)
                        },
                        endSlot = {
                            if (isStaffPending) {
                                Text(
                                    text = "Invitation sent",
                                    color = textColor,
                                )
                            }
                        }
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(Padding.SMALL),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = staff.name,
                                color = textColor,
                            )
                            staff.email?.let {
                                Text(
                                    it,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = textColor,
                                )
                            }
                        }
                    }
                }
            },
            buttonContent = { buttonModifier ->
                Button(
                    modifier = buttonModifier,
                    onClick = {
                        onAddPrimaryStaffSelected()
                    },
                ) {
                    Text(text = stringResource(Res.string.text_add))
                }
                OutlinedButton(
                    modifier = buttonModifier,
                    onClick = {
                        onAddSecondaryStaffSelected()
                    },
                ) {
                    Text(text = stringResource(Res.string.text_add))
                }
            }
        )
        LoadingAnimationOverlay(content.isLoading)
    }
}
