package com.cramsan.edifikana.client.lib.features.home.propertiesoverview

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.sharp.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import coil3.compose.AsyncImage
import com.cramsan.framework.core.compose.ui.ObserveViewModelEvents
import com.cramsan.ui.components.LoadingAnimationOverlay
import com.cramsan.ui.components.ScreenLayout
import com.cramsan.ui.theme.Padding
import com.cramsan.ui.theme.Size
import org.koin.compose.viewmodel.koinViewModel

/**
 * PropertiesOverview screen.
 *
 * This function provides the boilerplate needed to wire up the screen within the rest of the
 * application. This includes observing the view model's state and event flows and rendering the screen.
 */
@Composable
fun PropertiesOverviewScreen(
    modifier: Modifier = Modifier,
    viewModel: PropertiesOverviewViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    /**
     * For other possible lifecycle events, see the [Lifecycle.Event] documentation.
     */
    LifecycleEventEffect(Lifecycle.Event.ON_CREATE) {
        viewModel.initialize()
    }
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        // Call this feature's viewModel
    }

    ObserveViewModelEvents(viewModel) { event ->
        when (event) {
            PropertiesOverviewEvent.Noop -> Unit
        }
    }

    // Render the screen
    PropertiesOverviewContent(
        content = uiState,
        modifier = modifier,
        onAddPropertySelected = {
            viewModel.onAddPropertySelected()
        },
        onPropertySelected = { property ->
            viewModel.onPropertySelected(property)
        },
    )
}

/**
 * Content of the PropertiesOverview screen.
 */
@Composable
internal fun PropertiesOverviewContent(
    content: PropertiesOverviewUIState,
    modifier: Modifier = Modifier,
    onAddPropertySelected: () -> Unit = {},
    onPropertySelected: (PropertyItemUIModel) -> Unit = { _ -> },
) {
    Scaffold(
        modifier = modifier,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddPropertySelected,
            ) {
                Icon(
                    imageVector = Icons.Sharp.Add,
                    contentDescription = null,
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentAlignment = Alignment.TopCenter,
        ) {
            ScreenLayout(
                verticalArrangement = Arrangement.spacedBy(Padding.XX_SMALL),
                sectionContent = { sectionModifier ->
                    content.propertyList.forEach {
                        PropertyItem(
                            property = it,
                            modifier = sectionModifier,
                            onPropertySelected = onPropertySelected,
                        )
                    }
                },
            )
            LoadingAnimationOverlay(content.isLoading)
        }
    }
}

@Composable
private fun PropertyItem(
    property: PropertyItemUIModel,
    modifier: Modifier = Modifier,
    onPropertySelected: (PropertyItemUIModel) -> Unit,
) {
    Row(
        modifier
            .clip(MaterialTheme.shapes.medium)
            .border(
                width = 1.dp,
                color = Color.LightGray,
                shape = MaterialTheme.shapes.medium,
            )
            .clickable { onPropertySelected(property) },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        val imageModifier = Modifier
            .size(Size.xx_large)
        if (property.imageUrl != null) {
            AsyncImage(
                model = property.imageUrl,
                contentDescription = null,
                modifier = imageModifier,
            )
        } else {
            Icon(
                imageVector = Icons.Default.Apartment,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = imageModifier.padding(
                    Padding.SMALL
                ),
            )
        }
        Spacer(Modifier.size(Padding.MEDIUM))
        Column(
            modifier = Modifier.padding(Padding.SMALL)
        ) {
            Text(
                property.name,
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                property.address,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}
