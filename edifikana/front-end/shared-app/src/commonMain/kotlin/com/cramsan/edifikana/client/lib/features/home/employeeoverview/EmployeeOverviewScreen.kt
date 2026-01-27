package com.cramsan.edifikana.client.lib.features.home.employeeoverview

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.sharp.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.cramsan.edifikana.client.ui.components.EdifikanaImage
import com.cramsan.edifikana.client.ui.components.ImageSource
import com.cramsan.edifikana.lib.model.OrganizationId
import com.cramsan.framework.core.compose.ui.ObserveViewModelEvents
import com.cramsan.ui.components.LoadingAnimationOverlay
import com.cramsan.ui.components.ScreenLayout
import com.cramsan.ui.theme.Padding
import com.cramsan.ui.theme.Size
import org.koin.compose.viewmodel.koinViewModel

/**
 * EmployeeOverview screen.
 *
 * This function provides the boilerplate needed to wire up the screen within the rest of the
 * application. This includes observing the view model's state and event flows and rendering the screen.
 */
@Composable
fun EmployeeOverviewScreen(
    modifier: Modifier = Modifier,
    viewModel: EmployeeOverviewViewModel = koinViewModel(),
    orgId: OrganizationId,
) {
    val uiState by viewModel.uiState.collectAsState()

    // For other possible lifecycle events, see the Lifecycle.Event documentation.
    LifecycleEventEffect(Lifecycle.Event.ON_CREATE) {
        viewModel.initialize()
    }
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        // Call this feature's viewModel
    }

    LaunchedEffect(orgId) {
        viewModel.setOrgId(orgId)
    }

    ObserveViewModelEvents(viewModel) { event ->
        when (event) {
            EmployeeOverviewEvent.Noop -> Unit
        }
    }

    // Render the screen
    EmployeeOverviewContent(
        content = uiState,
        modifier = modifier,
        onAddEmployeeSelected = {
            viewModel.navigateToAddEmployeeScreen()
        },
        onEmployeeSelected = { },
    )
}

/**
 * Content of the EmployeeOverview screen.
 */
@Composable
internal fun EmployeeOverviewContent(
    content: EmployeeOverviewUIState,
    modifier: Modifier = Modifier,
    onAddEmployeeSelected: () -> Unit = {},
    onEmployeeSelected: (EmployeeItemUIModel) -> Unit = { _ -> },
) {
    Scaffold(
        modifier = modifier,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddEmployeeSelected,
            ) {
                Icon(
                    imageVector = Icons.Sharp.Add,
                    contentDescription = "Add new employee",
                )
            }
        },
    ) { innerPadding ->
        ScreenLayout(
            modifier = Modifier.padding(innerPadding).fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(Padding.XX_SMALL),
            sectionContent = { sectionModifier ->
                content.employeeList.forEach {
                    when (it) {
                        is InviteItemUIModel -> {
                            InviteItem(
                                invite = it,
                                modifier = sectionModifier,
                            )
                        }

                        is UserItemUIModel -> {
                            UserItem(
                                employee = it,
                                modifier = sectionModifier,
                                onEmployeeSelected = onEmployeeSelected,
                            )
                        }
                    }
                }
            },
            overlay = {
                LoadingAnimationOverlay(content.isLoading)
            },
        )
    }
}

@Composable
private fun UserItem(
    employee: UserItemUIModel,
    modifier: Modifier = Modifier,
    onEmployeeSelected: (UserItemUIModel) -> Unit,
) {
    Row(
        modifier
            .clip(MaterialTheme.shapes.medium)
            .border(
                width = 1.dp,
                color = Color.LightGray,
                shape = MaterialTheme.shapes.medium,
            )
            .clickable { onEmployeeSelected(employee) },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
    ) {
        val imageModifier = Modifier
            .size(Size.xx_large)
        if (employee.imageUrl != null) {
            EdifikanaImage(
                imageSource = ImageSource.Url(employee.imageUrl),
                contentDescription = "Employee image for ${employee.name}",
                modifier = imageModifier,
            )
        } else {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "No image available for ${employee.name}",
                tint = MaterialTheme.colorScheme.primary,
                modifier = imageModifier.padding(
                    Padding.SMALL,
                ),
            )
        }
        Spacer(Modifier.size(Padding.MEDIUM))
        Column(
            modifier = Modifier.padding(Padding.SMALL),
        ) {
            Text(
                employee.name,
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                employee.email,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Composable
private fun InviteItem(invite: InviteItemUIModel, modifier: Modifier = Modifier) {
    Row(
        modifier
            .clip(MaterialTheme.shapes.medium)
            .border(
                width = 1.dp,
                color = Color.LightGray,
                shape = MaterialTheme.shapes.medium,
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
    ) {
        val imageModifier = Modifier
            .size(Size.xx_large)
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = "Pending invite for ${invite.email}",
            tint = MaterialTheme.colorScheme.outline,
            modifier = imageModifier.padding(
                Padding.SMALL,
            ),
        )
        Spacer(Modifier.size(Padding.MEDIUM))
        Column(
            modifier = Modifier.padding(Padding.SMALL),
        ) {
            Text(
                invite.email,
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                "Invite sent",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.outline,
            )
        }
    }
}
