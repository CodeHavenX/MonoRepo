package com.codehavenx.alpaca.frontend.appcore.features.staff.liststaff

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.LastPage
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.FirstPage
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.codehavenx.alpaca.frontend.appcore.ui.components.ListItem
import com.codehavenx.alpaca.frontend.appcore.ui.components.LoadingAnimationOverlay
import com.codehavenx.alpaca.frontend.appcore.ui.theme.Padding
import org.koin.compose.koinInject

/**
 * The List Staff screen.
 */
@Composable
fun ListStaffsScreen(
    viewModel: ListStaffsViewModel = koinInject()
) {
    val uiState by viewModel.uiState.collectAsState()

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        viewModel.loadPage()
    }

    ListStaffsContent(
        uiState.users,
        uiState.pagination,
        uiState.isLoading,
        onAddStaffSelected = { viewModel.addStaff() },
        onStaffSelected = { viewModel.openStaffPage(it) },
        onPageSelected = { },
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun ListStaffsContent(
    content: StaffPageUIModel,
    pagination: StaffPaginationUIModel,
    loading: Boolean,
    onStaffSelected: (String) -> Unit,
    onPageSelected: (String) -> Unit,
    onAddStaffSelected: () -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column {
            FlowRow(
                modifier = Modifier.fillMaxWidth()
                    .wrapContentHeight(),
            ) {
                StaffSearchBar()
                VerticalDivider(
                    modifier = Modifier
                        .padding(horizontal = Padding.medium)
                        .height(Padding.x_large)
                        .align(Alignment.CenterVertically)
                )
                Button(
                    onClick = onAddStaffSelected,
                    modifier = Modifier.align(Alignment.CenterVertically)
                ) {
                    Text("Add Staff")
                }
            }
            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                items(content.users) { user ->
                    ListItem(user.displayName) {
                        onStaffSelected(user.id)
                    }
                }
            }
            StaffPagination(pagination, onPageSelected)
        }
        LoadingAnimationOverlay(isLoading = loading)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RowScope.StaffSearchBar() {
    var searchText by remember { mutableStateOf("") }

    Row(
        modifier = Modifier.weight(1f),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        val onActiveChange = { _: Boolean -> } // the callback to be invoked when
        // this search bar's active state is changed
        SearchBar(
            inputField = {
                SearchBarDefaults.InputField(
                    query = searchText,
                    onQueryChange = { searchText = it },
                    onSearch = { },
                    expanded = false,
                    onExpandedChange = onActiveChange,
                    enabled = true,
                    placeholder = { Text("Search") },
                    leadingIcon = null,
                    trailingIcon = null,
                    interactionSource = null,
                )
                // text showed on SearchBar
                // update the value of searchText
                // the callback to be invoked when the input service triggers the ImeAction.Search action
            },
            expanded = false, // whether the user is searching or not
            onExpandedChange = onActiveChange,
            modifier = Modifier.fillMaxWidth(),
            shape = SearchBarDefaults.inputFieldShape,
            tonalElevation = SearchBarDefaults.TonalElevation,
            shadowElevation = SearchBarDefaults.ShadowElevation,
            windowInsets = SearchBarDefaults.windowInsets,
            content = { },
        )
    }
}

@Composable
private fun StaffPagination(
    pagination: StaffPaginationUIModel,
    onPageSelected: (String) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        pagination.firstPage?.let {
            IconButton(onClick = { onPageSelected(it) }) {
                Icon(
                    imageVector = Icons.Default.FirstPage,
                    contentDescription = "",
                )
            }
        }
        pagination.previousPage?.let {
            IconButton(onClick = { onPageSelected(it) }) {
                Icon(
                    imageVector = Icons.Default.ChevronLeft,
                    contentDescription = "",
                )
            }
        }
        pagination.pages.forEach {
            IconButton(
                onClick = { onPageSelected(it.id) },
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = if (it.selected) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.surfaceDim
                    },
                ),
            ) {
                Text(
                    it.displayName,
                )
            }
        }
        pagination.nextPage?.let {
            IconButton(onClick = { onPageSelected(it) }) {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "",
                )
            }
        }
        pagination.lastPage?.let {
            IconButton(onClick = { onPageSelected(it) }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.LastPage,
                    contentDescription = "",
                )
            }
        }
    }
}
