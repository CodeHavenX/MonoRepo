package com.cramsan.edifikana.client.lib.features.timecard.stafflist

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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.cramsan.edifikana.client.lib.features.main.MainActivityEvent
import com.cramsan.edifikana.client.lib.ui.components.LoadingAnimationOverlay
import edifikana_lib.Res
import edifikana_lib.string_other
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.koinInject

/**
 * Represents the UI state of the Staff List screen.
 */
@Composable
fun StaffListScreen(
    onMainActivityEventInvoke: (MainActivityEvent) -> Unit,
    onTitleChange: (String) -> Unit,
    viewModel: StaffListViewModel = koinInject()
) {
    val uiState by viewModel.uiState.collectAsState()
    val event by viewModel.event.collectAsState(StaffListEvent.Noop)

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        viewModel.loadStaffs()
    }

    LaunchedEffect(event) {
        when (val viewModelEvent = event) {
            StaffListEvent.Noop -> Unit
            is StaffListEvent.TriggerMainActivityEvent -> {
                onMainActivityEventInvoke(viewModelEvent.mainActivityEvent)
            }
        }
    }

    onTitleChange(uiState.title)
    StaffList(
        isLoading = uiState.isLoading,
        uiState.staffs,
        onStaffClick = { staffPK ->
            viewModel.navigateToStaff(staffPK)
        },
        onAddStaffClick = {
            viewModel.navigateToAddStaff()
        },
    )
}

@Composable
private fun StaffList(
    isLoading: Boolean,
    staffs: List<StaffUIModel>,
    onStaffClick: (StaffPK) -> Unit,
    onAddStaffClick: () -> Unit,
) {
    LazyColumn(
        Modifier.fillMaxSize(),
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
    onStaffSelected: (StaffPK) -> Unit,
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

@Preview
@Composable
private fun StaffListScreenPreview() {
    StaffList(
        isLoading = true,
        staffs = listOf(
            StaffUIModel(
                "Cesar Andres Ramirez Sanchez",
                StaffPK("John"),
            ),
            StaffUIModel(
                "2",
                StaffPK("Jane"),
            ),
        ),
        onStaffClick = {},
        onAddStaffClick = {},
    )
}
