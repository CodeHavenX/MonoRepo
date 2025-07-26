package com.cramsan.sample.frontend.architecture.shared.presentation.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cramsan.sample.frontend.architecture.shared.domain.model.Note
import com.cramsan.sample.frontend.architecture.shared.domain.model.NoteCategory
import com.cramsan.sample.frontend.architecture.shared.domain.model.Result
import com.cramsan.sample.frontend.architecture.shared.domain.usecase.NotesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * UI state for the notes screen
 */
data class NotesUiState(
    val notes: List<Note> = emptyList(),
    val filteredNotes: List<Note> = emptyList(),
    val selectedCategory: NoteCategory? = null,
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val showFavoritesOnly: Boolean = false
)

/**
 * ViewModel for managing notes screen state and operations
 */
class NotesViewModel(
    private val notesUseCase: NotesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotesUiState())
    val uiState: StateFlow<NotesUiState> = _uiState.asStateFlow()

    var showCreateDialog by mutableStateOf(false)
        private set

    var editingNote by mutableStateOf<Note?>(null)
        private set

    init {
        loadNotes()
    }

    private fun loadNotes() {
        viewModelScope.launch {
            notesUseCase.getAllNotes().collect { notes ->
                val currentState = _uiState.value
                val filteredNotes = filterNotes(notes, currentState.searchQuery, currentState.selectedCategory, currentState.showFavoritesOnly)
                _uiState.value = currentState.copy(
                    notes = notes,
                    filteredNotes = filteredNotes,
                    isLoading = false
                )
            }
        }
    }

    fun searchNotes(query: String) {
        val currentState = _uiState.value
        val filteredNotes = filterNotes(currentState.notes, query, currentState.selectedCategory, currentState.showFavoritesOnly)
        _uiState.value = currentState.copy(
            searchQuery = query,
            filteredNotes = filteredNotes
        )
    }

    fun filterByCategory(category: NoteCategory?) {
        val currentState = _uiState.value
        val filteredNotes = filterNotes(currentState.notes, currentState.searchQuery, category, currentState.showFavoritesOnly)
        _uiState.value = currentState.copy(
            selectedCategory = category,
            filteredNotes = filteredNotes
        )
    }

    fun toggleFavoritesFilter() {
        val currentState = _uiState.value
        val showFavoritesOnly = !currentState.showFavoritesOnly
        val filteredNotes = filterNotes(currentState.notes, currentState.searchQuery, currentState.selectedCategory, showFavoritesOnly)
        _uiState.value = currentState.copy(
            showFavoritesOnly = showFavoritesOnly,
            filteredNotes = filteredNotes
        )
    }

    fun createNote(title: String, content: String, category: NoteCategory) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            when (val result = notesUseCase.createNote(title, content, category)) {
                is Result.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = null
                    )
                    showCreateDialog = false
                }
                is Result.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = result.exception.message
                    )
                }
            }
        }
    }

    fun updateNote(note: Note) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            when (val result = notesUseCase.updateNote(note)) {
                is Result.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = null
                    )
                    editingNote = null
                }
                is Result.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = result.exception.message
                    )
                }
            }
        }
    }

    fun deleteNote(id: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            when (val result = notesUseCase.deleteNote(id)) {
                is Result.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = null
                    )
                }
                is Result.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = result.exception.message
                    )
                }
            }
        }
    }

    fun toggleFavorite(id: String) {
        viewModelScope.launch {
            notesUseCase.toggleFavorite(id)
        }
    }

    fun showCreateDialog() {
        showCreateDialog = true
    }

    fun hideCreateDialog() {
        showCreateDialog = false
    }

    fun startEditing(note: Note) {
        editingNote = note
    }

    fun stopEditing() {
        editingNote = null
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    private fun filterNotes(
        notes: List<Note>,
        searchQuery: String,
        selectedCategory: NoteCategory?,
        showFavoritesOnly: Boolean
    ): List<Note> {
        return notes.filter { note ->
            val matchesSearch = searchQuery.isBlank() || 
                note.title.contains(searchQuery, ignoreCase = true) || 
                note.content.contains(searchQuery, ignoreCase = true)
            
            val matchesCategory = selectedCategory == null || note.category == selectedCategory
            
            val matchesFavorites = !showFavoritesOnly || note.isFavorite
            
            matchesSearch && matchesCategory && matchesFavorites
        }.sortedByDescending { it.updatedAt }
    }
}