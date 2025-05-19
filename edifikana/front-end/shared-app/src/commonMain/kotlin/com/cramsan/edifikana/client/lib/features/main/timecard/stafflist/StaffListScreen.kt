package com.cramsan.edifikana.client.lib.features.main.timecard.stafflist

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
import com.cramsan.edifikana.lib.model.StaffId
import com.cramsan.ui.components.ListCell
import com.cramsan.ui.components.LoadingAnimationOverlay
import com.cramsan.ui.components.ScreenLayout
import edifikana_lib.Res
import edifikana_lib.employee_list_screen_title
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

/**
 * Represents the UI state of the Staff List screen.
 */
@Composable
fun StaffListScreen(
    viewModel: StaffListViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        viewModel.loadStaffs()
    }

    LaunchedEffect(Unit) {
        launch {
            viewModel.events.collect { event ->
                when (event) {
                    StaffListEvent.Noop -> Unit
                }
            }
        }
    }

    StaffList(
        uiState,
        onStaffClick = { staffPK ->
            viewModel.navigateToStaff(staffPK)
        },
        onCloseSelected = {
            viewModel.navigateBack()
        },
    )
}

@Composable
internal fun StaffList(
    uiState: StaffListUIState,
    modifier: Modifier = Modifier,
    onStaffClick: (StaffId) -> Unit,
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
                    uiState.staffs.forEach { staff ->
                        StaffItem(
                            staff,
                            modifier,
                            onStaffClick,
                        )
                    }
                }
            )
            LoadingAnimationOverlay(uiState.isLoading)
        }
    }
}

@Composable
private fun StaffItem(
    staff: StaffUIModel,
    modifier: Modifier = Modifier,
    onStaffSelected: (StaffId) -> Unit,
) {
    ListCell(
        modifier = modifier,
        onSelection = { onStaffSelected(staff.staffPK) },
    ) {
        Text(staff.fullName)
    }
    HorizontalDivider()
}
