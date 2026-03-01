package com.cramsan.edifikana.client.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cramsan.edifikana.client.ui.resources.PropertyIcons
import com.cramsan.edifikana.client.ui.theme.AppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Preview
@Composable
private fun EdifikanaTextFieldPreview() {
    AppTheme {
        var username by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var passwordWithToggle by remember { mutableStateOf("") }
        var address by remember { mutableStateOf("") }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text("Text Fields", style = MaterialTheme.typography.headlineSmall)

            EdifikanaTextField(
                value = username,
                onValueChange = { username = it },
                placeholder = "Username",
            )

            EdifikanaTextField(
                value = password,
                onValueChange = { password = it },
                placeholder = "Password",
                isPassword = true,
            )

            EdifikanaPasswordTextField(
                value = passwordWithToggle,
                onValueChange = { passwordWithToggle = it },
                placeholder = "Password with toggle",
                label = "Password",
            )

            EdifikanaTextField(
                value = address,
                onValueChange = { address = it },
                placeholder = "Enter your address",
                label = "Address",
                isPassword = true,
            )
        }
    }
}

@Preview
@Composable
private fun EdifikanaPasswordTextFieldPreview() {
    AppTheme {
        var password1 by remember { mutableStateOf("") }
        var password2 by remember { mutableStateOf("SecurePassword123") }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text("Password Text Fields", style = MaterialTheme.typography.headlineSmall)

            EdifikanaPasswordTextField(
                value = password1,
                onValueChange = { password1 = it },
                placeholder = "Enter your password",
            )

            EdifikanaPasswordTextField(
                value = password2,
                onValueChange = { password2 = it },
                placeholder = "Enter your password",
                label = "Password",
            )

            EdifikanaPasswordTextField(
                value = "DisabledPassword",
                onValueChange = {},
                placeholder = "Enter your password",
                label = "Disabled Password",
                enabled = false,
            )
        }
    }
}

@Preview
@Composable
private fun EdifikanaButtonsPreview() {
    AppTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
        ) {
            Text("Buttons", style = MaterialTheme.typography.headlineSmall)

            EdifikanaPrimaryButton(
                text = "Sign In",
                onClick = {},
            )

            EdifikanaSecondaryButton(
                text = "Sign In with OTP",
                onClick = {},
            )

            EdifikanaPrimaryButton(
                text = "Disabled Button",
                onClick = {},
                enabled = false,
            )
        }
    }
}

@Preview
@Composable
private fun EdifikanaListItemPreview() {
    AppTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text("List Items - Events", style = MaterialTheme.typography.headlineSmall)

            EdifikanaListItem(
                title = "Maintenance Request",
                subtitle = "Reported by: Alex",
                onClick = {},
                icon = Icons.Default.Build,
            )

            EdifikanaListItem(
                title = "Security Incident",
                subtitle = "Reported by: Sophia",
                onClick = {},
                icon = Icons.Default.Settings,
            )

            EdifikanaListItem(
                title = "Cleaning Request",
                subtitle = "Reported by: Ethan",
                onClick = {},
                icon = Icons.Default.CheckCircle,
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

            Text("List Items - Visitors", style = MaterialTheme.typography.headlineSmall)

            EdifikanaListItem(
                title = "Arrived",
                subtitle = "Visitor: Olivia",
                onClick = {},
                imageUrl = "https://i.pravatar.cc/150?img=1",
            )

            EdifikanaListItem(
                title = "Departed",
                subtitle = "Visitor: Nathan",
                onClick = {},
                imageUrl = "https://i.pravatar.cc/150?img=2",
            )

            EdifikanaListItem(
                title = "Arrived",
                subtitle = "Visitor: Isabella",
                onClick = {},
                imageUrl = "https://i.pravatar.cc/150?img=3",
            )
        }
    }
}

@Preview
@Composable
private fun EdifikanaTopAndBottomNavigationPreview() {
    AppTheme {
        var selectedRoute by remember { mutableStateOf("home") }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
            ) {
                EdifikanaTopBar(
                    title = "Profile",
                    navigationIcon = Icons.Default.Home,
                    onNavigationIconSelected = {},
                    content = {
                        EdifikanaTextButton(
                            text = "Edit",
                            onClick = {},
                        )
                    },
                )

                Box(modifier = Modifier.weight(1f))

                EdifikanaBottomNavigation(
                    items = listOf(
                        EdifikanaNavigationItem(Icons.Default.Home, "Home", "home"),
                        EdifikanaNavigationItem(Icons.Default.Person, "Visitors", "visitors"),
                        EdifikanaNavigationItem(Icons.Default.Search, "Log", "log"),
                        EdifikanaNavigationItem(Icons.Default.Settings, "Profile", "profile"),
                    ),
                    selectedRoute = selectedRoute,
                    onItemClick = { selectedRoute = it },
                )
            }
        }
    }
}

@Preview
@Composable
private fun EdifikanaFloatingActionButtonPreview() {
    AppTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            contentAlignment = Alignment.BottomEnd,
        ) {
            EdifikanaFloatingActionButton(
                text = "Add Visitor",
                icon = Icons.Default.Add,
                onClick = {},
            )
        }
    }
}

@Preview
@Composable
private fun EdifikanaTextButtonPreview() {
    AppTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text("Text Buttons", style = MaterialTheme.typography.headlineSmall)

            EdifikanaTextButton(
                text = "View More",
                onClick = {},
            )

            EdifikanaTextButton(
                text = "See All Events",
                onClick = {},
            )
        }
    }
}

@Preview
@Composable
private fun EdifikanaAvatarPreview() {
    AppTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text("Avatars", style = MaterialTheme.typography.headlineSmall)

            EdifikanaAvatar(
                imageUrl = "https://i.pravatar.cc/300?img=5",
                contentDescription = "User avatar",
                size = 120.dp,
            )

            EdifikanaAvatar(
                imageUrl = null,
                contentDescription = "Empty avatar",
                size = 80.dp,
            )
        }
    }
}

@Preview
@Composable
private fun EdifikanaProfileHeaderPreview() {
    AppTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            Text("Profile Headers", style = MaterialTheme.typography.headlineSmall)

            EdifikanaProfileHeader(
                name = "Ethan Carter",
                avatarUrl = "https://i.pravatar.cc/300?img=5",
                role = "Administrator",
            )

            HorizontalDivider()

            EdifikanaProfileHeader(
                name = "Oakwood Estates",
                avatarUrl = "https://picsum.photos/300/300?random=1",
                org = "Acme Corporation",
            )

            HorizontalDivider()

            EdifikanaProfileHeader(
                name = "John Smith",
                avatarUrl = "https://i.pravatar.cc/300?img=8",
                role = "Manager",
                org = "Downtown Properties",
            )
        }
    }
}

@Preview
@Composable
private fun EdifikanaSectionHeaderPreview() {
    AppTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text("Section Headers", style = MaterialTheme.typography.headlineSmall)

            EdifikanaSectionHeader(title = "Account")
            EdifikanaSectionSubHeader("First Name")
            EdifikanaSectionSubHeader("Last Name")
            EdifikanaSectionHeader(title = "Properties")
            EdifikanaSectionHeader(title = "Settings")
        }
    }
}

@Preview
@Composable
private fun EdifikanaAccountInfoItemPreview() {
    AppTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text("Account Info Items", style = MaterialTheme.typography.headlineSmall)

            EdifikanaAccountInfoItem(
                label = "Name",
                value = "Ethan Carter",
            )

            EdifikanaAccountInfoItem(
                label = "Email",
                value = "ethan.carter@example.com",
            )

            EdifikanaAccountInfoItem(
                label = "Phone",
                value = "+1 (555) 123-4567",
            )

            EdifikanaAccountInfoItem(
                label = "Password",
                value = "********",
            )
        }
    }
}

@Preview
@Composable
private fun EdifikanaImageSelectorPreview() {
    AppTheme {
        var selectedOption by remember { mutableStateOf<ImageOptionUIModel?>(null) }

        val options = listOf(
            ImageOptionUIModel(
                id = "CASA",
                displayName = "Casa",
                imageSource = ImageSource.Drawable(PropertyIcons.CASA),
            ),
            ImageOptionUIModel(
                id = "QUINTA",
                displayName = "Quinta",
                imageSource = ImageSource.Drawable(PropertyIcons.QUINTA),
            ),
            ImageOptionUIModel(
                id = "L_DEPA",
                displayName = "Large Department",
                imageSource = ImageSource.Drawable(PropertyIcons.L_DEPA),
            ),
            ImageOptionUIModel(
                id = "M_DEPA",
                displayName = "Medium Department",
                imageSource = ImageSource.Drawable(PropertyIcons.M_DEPA),
            ),
            ImageOptionUIModel(
                id = "S_DEPA",
                displayName = "Small Department",
                imageSource = ImageSource.Drawable(PropertyIcons.S_DEPA),
            ),
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text("Image Selector", style = MaterialTheme.typography.headlineSmall)

            EdifikanaImageSelector(
                label = "Property Icon",
                options = options,
                selectedOption = selectedOption,
                onOptionSelected = { selectedOption = it },
                placeholder = "Select a property icon",
            )

            HorizontalDivider()

            Text("Grid (simulates open sheet)", style = MaterialTheme.typography.labelMedium)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
            ) {
                EdifikanaImageGrid(
                    options = options,
                    selectedOption = selectedOption,
                    onOptionSelected = { selectedOption = it },
                )
            }
        }
    }
}

@Preview
@Composable
private fun EdifikanaImageSelectorWithUploadPreview() {
    AppTheme {
        var selectedOption by remember { mutableStateOf<ImageOptionUIModel?>(null) }

        val optionsWithUpload = listOf(
            ImageOptionUIModel(
                id = "CASA",
                displayName = "Casa",
                imageSource = ImageSource.Drawable(PropertyIcons.CASA),
            ),
            ImageOptionUIModel(
                id = "QUINTA",
                displayName = "Quinta",
                imageSource = ImageSource.Drawable(PropertyIcons.QUINTA),
            ),
            ImageOptionUIModel(
                id = "L_DEPA",
                displayName = "Large Department",
                imageSource = ImageSource.Drawable(PropertyIcons.L_DEPA),
            ),
            ImageOptionUIModel(
                id = "M_DEPA",
                displayName = "Medium Department",
                imageSource = ImageSource.Drawable(PropertyIcons.M_DEPA),
            ),
            ImageOptionUIModel(
                id = "S_DEPA",
                displayName = "Small Department",
                imageSource = ImageSource.Drawable(PropertyIcons.S_DEPA),
            ),
            ImageOptionUIModel(
                id = "custom_upload",
                displayName = "Upload Custom Image",
                imageSource = ImageSource.UploadPlaceholder,
            ),
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text("Image Selector with Upload Option", style = MaterialTheme.typography.headlineSmall)

            EdifikanaImageSelector(
                label = "Property Icon",
                options = optionsWithUpload,
                selectedOption = selectedOption,
                onOptionSelected = { selectedOption = it },
                placeholder = "Select a property icon",
            )

            HorizontalDivider()

            Text("Grid (simulates open sheet)", style = MaterialTheme.typography.labelMedium)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
            ) {
                EdifikanaImageGrid(
                    options = optionsWithUpload,
                    selectedOption = selectedOption,
                    onOptionSelected = { selectedOption = it },
                )
            }
        }
    }
}

@Preview
@Composable
private fun EdifikanaImageSelectorWithCustomImagePreview() {
    AppTheme {
        val customImageOption = ImageOptionUIModel(
            id = "custom_uploaded",
            displayName = "Custom Image",
            imageSource = ImageSource.Url("https://picsum.photos/200"),
        )

        var selectedOption by remember { mutableStateOf<ImageOptionUIModel?>(customImageOption) }

        val optionsWithUpload = listOf(
            ImageOptionUIModel(
                id = "CASA",
                displayName = "Casa",
                imageSource = ImageSource.Drawable(PropertyIcons.CASA),
            ),
            ImageOptionUIModel(
                id = "QUINTA",
                displayName = "Quinta",
                imageSource = ImageSource.Drawable(PropertyIcons.QUINTA),
            ),
            ImageOptionUIModel(
                id = "custom_upload",
                displayName = "Upload Custom Image",
                imageSource = ImageSource.UploadPlaceholder,
            ),
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text("Image Selector with Uploaded Custom Image", style = MaterialTheme.typography.headlineSmall)

            EdifikanaImageSelector(
                label = "Property Icon",
                options = optionsWithUpload,
                selectedOption = selectedOption,
                onOptionSelected = { selectedOption = it },
                placeholder = "Select a property icon",
            )

            HorizontalDivider()

            Text("Grid (simulates open sheet)", style = MaterialTheme.typography.labelMedium)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
            ) {
                EdifikanaImageGrid(
                    options = optionsWithUpload,
                    selectedOption = selectedOption,
                    onOptionSelected = { selectedOption = it },
                )
            }
        }
    }
}

@Preview
@Composable
private fun EdifikanaImageSelectorGridOverflowPreview() {
    AppTheme {
        var selectedOption by remember { mutableStateOf<ImageOptionUIModel?>(null) }

        val options = (1..12).map { i ->
            ImageOptionUIModel(
                id = "OPTION_$i",
                displayName = "Option $i",
                imageSource = ImageSource.Drawable(PropertyIcons.CASA),
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text("Image Selector — Grid Overflow (12 items)", style = MaterialTheme.typography.headlineSmall)

            EdifikanaImageSelector(
                label = "Property Icon",
                options = options,
                selectedOption = selectedOption,
                onOptionSelected = { selectedOption = it },
                placeholder = "Select a property icon",
            )

            HorizontalDivider()

            Text("Grid (simulates open sheet — scrolls at runtime)", style = MaterialTheme.typography.labelMedium)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
            ) {
                EdifikanaImageGrid(
                    options = options,
                    selectedOption = selectedOption,
                    onOptionSelected = { selectedOption = it },
                )
            }
        }
    }
}

@Preview
@Composable
private fun EdifikanaImageSelectorGridEmptyPreview() {
    AppTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text("Image Selector — Empty Grid", style = MaterialTheme.typography.headlineSmall)

            EdifikanaImageSelector(
                label = "Property Icon",
                options = emptyList(),
                selectedOption = null,
                onOptionSelected = {},
                placeholder = "Select a property icon",
            )

            HorizontalDivider()

            Text("Grid (simulates open sheet — empty state)", style = MaterialTheme.typography.labelMedium)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
            ) {
                EdifikanaImageGrid(
                    options = emptyList(),
                    selectedOption = null,
                    onOptionSelected = {},
                )
            }
        }
    }
}

@Preview
@Composable
private fun EdifikanaComponentsFullPreview() {
    AppTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            Text("All Components", style = MaterialTheme.typography.headlineMedium)

            // Text Fields
            var username by remember { mutableStateOf("") }
            var password by remember { mutableStateOf("") }
            var passwordWithToggle by remember { mutableStateOf("") }

            Text("Text Fields", style = MaterialTheme.typography.titleMedium)
            EdifikanaTextField(
                value = username,
                onValueChange = { username = it },
                placeholder = "Username",
            )
            EdifikanaTextField(
                value = password,
                onValueChange = { password = it },
                placeholder = "Password",
                isPassword = true,
            )
            EdifikanaPasswordTextField(
                value = passwordWithToggle,
                onValueChange = { passwordWithToggle = it },
                placeholder = "Enter password",
                label = "Password",
            )

            HorizontalDivider()

            // Buttons
            Text("Buttons", style = MaterialTheme.typography.titleMedium)
            EdifikanaPrimaryButton(text = "Sign In", onClick = {})
            EdifikanaSecondaryButton(text = "Sign In with OTP", onClick = {})
            EdifikanaTextButton(text = "View More", onClick = {})

            HorizontalDivider()

            // List Items
            Text("List Items", style = MaterialTheme.typography.titleMedium)
            EdifikanaListItem(
                title = "Maintenance Request",
                subtitle = "Reported by: Alex",
                onClick = {},
                icon = Icons.Default.Build,
            )
            EdifikanaListItem(
                title = "Arrived",
                subtitle = "Visitor: Olivia",
                onClick = {},
                imageUrl = "https://i.pravatar.cc/150?img=1",
            )

            HorizontalDivider()

            // FAB
            Text("Floating Action Button", style = MaterialTheme.typography.titleMedium)
            EdifikanaFloatingActionButton(
                text = "Add Visitor",
                icon = Icons.Default.Add,
                onClick = {},
            )

            HorizontalDivider()

            // Profile Header
            Text("Profile Header", style = MaterialTheme.typography.titleMedium)
            EdifikanaProfileHeader(
                name = "Ethan Carter",
                avatarUrl = "https://i.pravatar.cc/300?img=5",
                role = "Administrator",
            )

            HorizontalDivider()

            // Section Headers and Account Info
            EdifikanaSectionHeader(title = "Account")
            EdifikanaAccountInfoItem(label = "Name", value = "Ethan Carter")
            EdifikanaAccountInfoItem(label = "Email", value = "ethan.carter@example.com")
            EdifikanaAccountInfoItem(label = "Phone", value = "+1 (555) 123-4567")
        }
    }
}
