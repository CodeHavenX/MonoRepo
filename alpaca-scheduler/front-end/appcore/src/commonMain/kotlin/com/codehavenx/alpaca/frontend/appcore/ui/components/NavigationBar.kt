package com.codehavenx.alpaca.frontend.appcore.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.codehavenx.alpaca.frontend.appcore.features.application.NavBarSegment
import com.codehavenx.alpaca.frontend.appcore.ui.theme.Padding

/**
 * Navigation bar for the application.
 *
 * @param navBar The navigation bar segments to display.
 * @param navController The navigation controller for the application.
 * @param showNavigationBar Whether to show the navigation bar.
 */
@Composable
fun NavigationBar(
    navBar: List<NavBarSegment>,
    navController: NavHostController,
    showNavigationBar: Boolean,
) {
    AnimatedVisibility(
        showNavigationBar,
        enter = fadeIn() + expandHorizontally(),
        exit = shrinkHorizontally() + fadeOut(),
    ) {
        Column(
            modifier = Modifier.fillMaxHeight()
                .widthIn(max = 200.dp)
                .verticalScroll(rememberScrollState())
                .wrapContentWidth()
                .padding(vertical = Padding.large)
        ) {
            navBar.forEach {
                when (it) {
                    is NavBarSegment.NavBarItem -> {
                        ListItem(
                            title = it.name,
                            onClick = { navController.navigate(it.path) },
                        )
                        HorizontalDivider()
                    }
                    is NavBarSegment.NavBarGroup -> {
                        ListItem(
                            title = it.name,
                            onClick = null,
                        )
                        it.items.forEach { item ->
                            ListItem(
                                title = item.name,
                                style = MaterialTheme.typography.bodySmall,
                                onClick = { navController.navigate(item.path) },
                                modifier = Modifier.padding(start = Padding.medium),
                            )
                        }
                        HorizontalDivider()
                    }
                }
            }
        }
    }
}
