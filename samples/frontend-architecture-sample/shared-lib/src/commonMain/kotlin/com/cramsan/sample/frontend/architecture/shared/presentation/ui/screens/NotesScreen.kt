package com.cramsan.sample.frontend.architecture.shared.presentation.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.cramsan.sample.frontend.architecture.shared.domain.model.Note
import com.cramsan.sample.frontend.architecture.shared.domain.model.NoteCategory
import com.cramsan.sample.frontend.architecture.shared.presentation.ui.components.NoteCard
import com.cramsan.sample.frontend.architecture.shared.presentation.ui.components.NoteDialog
import com.cramsan.sample.frontend.architecture.shared.presentation.viewmodel.NotesUiState
import com.cramsan.sample.frontend.architecture.shared.presentation.viewmodel.NotesViewModel
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesScreen(
    viewModel: NotesViewModel = koinViewModel(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val showCreateDialog = viewModel.showCreateDialog
    val editingNote = viewModel.editingNote

    LaunchedEffect(uiState.errorMessage) {
        if (uiState.errorMessage != null) {
            // In a real app, you might want to show a snackbar here
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notes") },
                actions = {
                    IconButton(
                        onClick = { viewModel.toggleFavoritesFilter() }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "Toggle favorites filter",
                            tint = if (uiState.showFavoritesOnly) {
                                MaterialTheme.colorScheme.error
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.showCreateDialog() }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add note")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Search bar
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = { viewModel.searchNotes(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text("Search notes...") },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "Search")
                },
                trailingIcon = {
                    if (uiState.searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.searchNotes("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear search")
                        }
                    }
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                singleLine = true
            )

            // Category filter chips
            LazyRow(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        onClick = { viewModel.filterByCategory(null) },
                        label = { Text("All") },
                        selected = uiState.selectedCategory == null
                    )
                }
                
                items(NoteCategory.entries) { category ->
                    FilterChip(
                        onClick = { viewModel.filterByCategory(category) },
                        label = { Text(category.displayName) },
                        selected = uiState.selectedCategory == category
                    )
                }
            }

            // Content
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (uiState.filteredNotes.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = when {
                                uiState.searchQuery.isNotEmpty() -> "No notes found for \"${uiState.searchQuery}\""
                                uiState.selectedCategory != null -> "No notes in ${uiState.selectedCategory.displayName}"
                                uiState.showFavoritesOnly -> "No favorite notes"
                                else -> "No notes yet"
                            },
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center
                        )
                        
                        if (uiState.searchQuery.isEmpty() && uiState.selectedCategory == null && !uiState.showFavoritesOnly) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Tap the + button to create your first note",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.filteredNotes, key = { it.id }) { note ->
                        NoteCard(
                            note = note,
                            onNoteClick = { /* Navigate to detail view if needed */ },
                            onFavoriteClick = { viewModel.toggleFavorite(it) },
                            onEditClick = { viewModel.startEditing(it) },
                            onDeleteClick = { viewModel.deleteNote(it) }
                        )
                    }
                }
            }
        }
    }

    // Create dialog
    if (showCreateDialog) {
        NoteDialog(
            onDismiss = { viewModel.hideCreateDialog() },
            onSave = { title, content, category ->
                viewModel.createNote(title, content, category)
            }
        )
    }

    // Edit dialog
    editingNote?.let { note ->
        NoteDialog(
            note = note,
            onDismiss = { viewModel.stopEditing() },
            onSave = { title, content, category ->
                viewModel.updateNote(
                    note.copy(
                        title = title,
                        content = content,
                        category = category
                    )
                )
            }
        )
    }
}

@Composable
private fun LazyRow(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    content: androidx.compose.foundation.lazy.LazyListScope.() -> Unit
) {
    androidx.compose.foundation.lazy.LazyRow(
        modifier = modifier,
        horizontalArrangement = horizontalArrangement,
        content = content
    )
}