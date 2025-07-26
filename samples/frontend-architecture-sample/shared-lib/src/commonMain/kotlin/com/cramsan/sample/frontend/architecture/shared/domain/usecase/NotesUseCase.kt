package com.cramsan.sample.frontend.architecture.shared.domain.usecase

import com.cramsan.sample.frontend.architecture.shared.domain.model.Note
import com.cramsan.sample.frontend.architecture.shared.domain.model.NoteCategory
import com.cramsan.sample.frontend.architecture.shared.domain.model.Result
import com.cramsan.sample.frontend.architecture.shared.domain.repository.NotesRepository
import kotlinx.coroutines.flow.Flow

/**
 * Use case for managing notes operations
 */
class NotesUseCase(
    private val notesRepository: NotesRepository
) {
    fun getAllNotes(): Flow<List<Note>> = notesRepository.getAllNotes()

    suspend fun getNoteById(id: String): Result<Note> = notesRepository.getNoteById(id)

    suspend fun createNote(title: String, content: String, category: NoteCategory): Result<Note> {
        if (title.isBlank()) {
            return Result.Error(IllegalArgumentException("Title cannot be empty"))
        }
        return notesRepository.createNote(title.trim(), content.trim(), category)
    }

    suspend fun updateNote(note: Note): Result<Note> {
        if (note.title.isBlank()) {
            return Result.Error(IllegalArgumentException("Title cannot be empty"))
        }
        val updatedNote = note.copy(
            title = note.title.trim(),
            content = note.content.trim()
        )
        return notesRepository.updateNote(updatedNote)
    }

    suspend fun deleteNote(id: String): Result<Unit> = notesRepository.deleteNote(id)

    suspend fun searchNotes(query: String): Result<List<Note>> {
        if (query.isBlank()) {
            return Result.Success(emptyList())
        }
        return notesRepository.searchNotes(query.trim())
    }

    suspend fun getNotesByCategory(category: NoteCategory): Result<List<Note>> = 
        notesRepository.getNotesByCategory(category)

    suspend fun toggleFavorite(id: String): Result<Note> = notesRepository.toggleFavorite(id)

    suspend fun getFavoriteNotes(): Result<List<Note>> {
        return when (val result = notesRepository.searchNotes("")) {
            is Result.Success -> {
                val favorites = result.data.filter { it.isFavorite }
                Result.Success(favorites)
            }
            is Result.Error -> result
        }
    }
}