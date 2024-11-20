package com.codehavenx.alpaca.frontend.appcore.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import appcore.Res
import appcore.string_back_navigation
import com.codehavenx.alpaca.frontend.appcore.ui.theme.Size
import org.jetbrains.compose.resources.stringResource

/**
 * Top bar for the application.
 *
 * @param navController The navigation controller for the application.
 * @param onNavIconClick The action to perform when the navigation icon is clicked.
 * @param onSignOutClick The action to perform when the sign out button is clicked.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    navController: NavHostController,
    onNavIconClick: () -> Unit,
    onSignOutClick: () -> Unit,
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val backStack by navController.currentBackStack.collectAsState()

    @Suppress("UnusedPrivateProperty")
    val currentDestination = navBackStackEntry?.destination

    val interactionSource = remember { MutableInteractionSource() }
    var staffMode by remember { mutableStateOf(false) }

    TopAppBar(
        title = { },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
        ),
        navigationIcon = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = { onNavIconClick() }) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = ""
                    )
                }

                AnimatedVisibility(
                    visible = (backStack.size > 2),
                    enter = fadeIn(),
                    exit = fadeOut(),
                ) {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(Res.string.string_back_navigation)
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                Row(
                    modifier = Modifier
                        .clickable(
                            interactionSource = interactionSource,
                            // This is for removing ripple when Row is clicked
                            indication = null,
                            role = Role.Switch,
                            onClick = { staffMode = !staffMode }
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AnimatedContent(
                        targetState = staffMode,
                        transitionSpec = {
                            fadeIn() togetherWith fadeOut()
                        }
                    ) { staffMode ->
                        Text(
                            text = if (staffMode) "Staff Mode" else "Client Mode",
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                    Spacer(modifier = Modifier.padding(start = 8.dp))
                    Switch(
                        checked = staffMode,
                        onCheckedChange = { staffMode = it },
                    )
                }

                var expanded by remember { mutableStateOf(false) }
                Box {
                    IconButton(onClick = {
                        expanded = !expanded
                    }) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "",
                            modifier = Modifier
                                .size(Size.large),
                        )
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                    ) {
                        DropdownMenuItem(
                            text = { Text("Sign Out") },
                            onClick = {
                                expanded = false
                                onSignOutClick()
                            }
                        )
                    }
                }
            }
        },
    )
}
