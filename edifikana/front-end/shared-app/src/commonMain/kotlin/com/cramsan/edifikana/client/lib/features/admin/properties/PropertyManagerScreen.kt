package com.cramsan.edifikana.client.lib.features.admin.properties

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.cramsan.edifikana.lib.model.PropertyId
import com.cramsan.ui.components.LoadingAnimationOverlay
import com.cramsan.ui.components.ScreenLayout
import edifikana_lib.Res
import edifikana_lib.properties_screen_add_button
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

/**
 * PropertyManager screen.
 *
 * This function provides the boilerplate needed to wire up the screen within the rest of the
 * application. This includes observing the view model's state and event flows and rendering the screen.
 */
@Composable
fun PropertyManagerScreen(
    modifier: Modifier = Modifier,
    viewModel: PropertyManagerViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    /**
     * For other possible lifecycle events, see the [Lifecycle.Event] documentation.
     */
    LifecycleEventEffect(Lifecycle.Event.ON_START) {
        viewModel.loadPage()
    }
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        // Call this feature's viewModel
    }

    LaunchedEffect(Unit) {
        launch {
            viewModel.events.collect { event ->
                when (event) {
                    PropertyManagerEvent.Noop -> Unit
                }
            }
        }
    }

    PropertyManagerContent(
        uiState,
        modifier,
        onPropertyClicked = { property ->
            viewModel.navigateToPropertyDetails(property)
        },
        onAddPropertyClicked = {
            viewModel.navigateToAddProperty()
        },
    )
}

/**
 * Content of the AccountEdit screen.
 */
@Composable
internal fun PropertyManagerContent(
    content: PropertyManagerUIState,
    modifier: Modifier = Modifier,
    onPropertyClicked: (PropertyId) -> Unit,
    onAddPropertyClicked: () -> Unit,
) {
    // Render the screen
    Box(
        modifier = modifier
            .fillMaxSize(),
        contentAlignment = Alignment.TopCenter,
    ) {
        ScreenLayout(
            maxWith = Dp.Unspecified,
            fixedFooter = true,
            sectionContent = { sectionModifier ->
                content.content.properties.forEach {
                    PropertyRow(
                        it,
                        modifier = sectionModifier,
                        onClick = { onPropertyClicked(it.id) },
                    )
                }
            },
            buttonContent = { buttonModifier ->
                Button(
                    modifier = buttonModifier,
                    onClick = onAddPropertyClicked,
                ) {
                    Text(text = stringResource(Res.string.properties_screen_add_button))
                }
            }
        )
        LoadingAnimationOverlay(isLoading = content.isLoading)
    }
}

@Composable
private fun PropertyRow(
    property: PropertyUIModel,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .clickable { onClick() }
            .then(modifier),
    ) {
        Column {
            Text(
                property.name,
                style = MaterialTheme.typography.labelLarge,
            )
            Text(
                property.address,
                style = MaterialTheme.typography.labelMedium,
            )
        }
    }
}
