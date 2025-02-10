package com.cramsan.edifikana.client.lib.features.main.timecard.stafflist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.cramsan.edifikana.client.lib.features.EdifikanaApplicationViewModel
import com.cramsan.edifikana.client.ui.components.EdifikanaTopBar
import com.cramsan.edifikana.lib.model.StaffId
import com.cramsan.ui.components.LoadingAnimationOverlay
import edifikana_lib.Res
import edifikana_lib.string_other
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

/**
 * Represents the UI state of the Staff List screen.
 */
@Composable
fun StaffListScreen(
    viewModel: StaffListViewModel = koinViewModel(),
    applicationViewModel: EdifikanaApplicationViewModel = koinInject(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val event by viewModel.events.collectAsState(StaffListEvent.Noop)

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        viewModel.loadStaffs()
    }

    LaunchedEffect(event) {
        when (val viewModelEvent = event) {
            StaffListEvent.Noop -> Unit
            is StaffListEvent.TriggerEdifikanaApplicationEvent -> {
                applicationViewModel.executeEvent(viewModelEvent.edifikanaApplicationEvent)
            }
        }
    }

    Scaffold(
        topBar = {
            EdifikanaTopBar(
                title = "Staff List",
                onCloseClicked = { viewModel.navigateBack() },
            )
        },
    ) { innerPadding ->
        StaffList(
            isLoading = uiState.isLoading,
            Modifier.padding(innerPadding),
            uiState.staffs,
            onStaffClick = { staffPK ->
                viewModel.navigateToStaff(staffPK)
            },
            onAddStaffClick = {
                viewModel.navigateToAddStaff()
            },
        )
    }
}

@Composable
internal fun StaffList(
    isLoading: Boolean,
    modifier: Modifier = Modifier,
    staffs: List<StaffUIModel>,
    onStaffClick: (StaffId) -> Unit,
    onAddStaffClick: () -> Unit,
) {
    LazyColumn(
        modifier.fillMaxSize(),
    ) {
        items(staffs) { staff ->
            StaffItem(
                staff,
                Modifier.fillMaxWidth(),
                onStaffClick,
            )
        }
        item {
            StaffOtherItem(
                Modifier.fillMaxWidth(),
                onAddStaffClick,
            )
        }
    }
    LoadingAnimationOverlay(isLoading)
}

@Composable
private fun StaffItem(
    staff: StaffUIModel,
    modifier: Modifier = Modifier,
    onStaffSelected: (StaffId) -> Unit,
) {
    Text(
        staff.fullName,
        modifier = modifier
            .clickable { onStaffSelected(staff.staffPK) }
            .padding(16.dp),
    )
    HorizontalDivider()
}

@Composable
private fun StaffOtherItem(
    modifier: Modifier = Modifier,
    onAddStaffSelected: () -> Unit,
) {
    Text(
        stringResource(Res.string.string_other),
        modifier = modifier
            .clickable { onAddStaffSelected() }
            .padding(16.dp),
    )
}
