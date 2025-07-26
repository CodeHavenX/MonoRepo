package com.cramsan.sample.frontend.architecture.shared.domain.repository

import com.cramsan.sample.frontend.architecture.shared.domain.model.Note
import com.cramsan.sample.frontend.architecture.shared.domain.model.NoteCategory
import com.cramsan.sample.frontend.architecture.shared.domain.model.Result
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for note operations
 */
interface NotesRepository {
    /**
     * Get all notes as a Flow for reactive updates
     */
    fun getAllNotes(): Flow<List<Note>>

    /**
     * Get a specific note by ID
     */
    suspend fun getNoteById(id: String): Result<Note>

    /**
     * Create a new note
     */
    suspend fun createNote(title: String, content: String, category: NoteCategory): Result<Note>

    /**
     * Update an existing note
     */
    suspend fun updateNote(note: Note): Result<Note>

    /**
     * Delete a note by ID
     */
    suspend fun deleteNote(id: String): Result<Unit>

    /**
     * Search notes by title or content
     */
    suspend fun searchNotes(query: String): Result<List<Note>>

    /**
     * Get notes by category
     */
    suspend fun getNotesByCategory(category: NoteCategory): Result<List<Note>>

    /**
     * Toggle favorite status of a note
     */
    suspend fun toggleFavorite(id: String): Result<Note>
}