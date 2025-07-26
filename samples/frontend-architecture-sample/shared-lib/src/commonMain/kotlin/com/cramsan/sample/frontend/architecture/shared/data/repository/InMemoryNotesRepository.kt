package com.cramsan.sample.frontend.architecture.shared.data.repository

import com.cramsan.sample.frontend.architecture.shared.domain.model.Note
import com.cramsan.sample.frontend.architecture.shared.domain.model.NoteCategory
import com.cramsan.sample.frontend.architecture.shared.domain.model.Result
import com.cramsan.sample.frontend.architecture.shared.domain.repository.NotesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

/**
 * In-memory implementation of NotesRepository for demonstration purposes
 */
@OptIn(ExperimentalUuidApi::class)
class InMemoryNotesRepository : NotesRepository {

    private val _notes = MutableStateFlow(generateSampleNotes())
    
    override fun getAllNotes(): Flow<List<Note>> = _notes.asStateFlow()

    override suspend fun getNoteById(id: String): Result<Note> {
        return try {
            val note = _notes.value.find { it.id == id }
            if (note != null) {
                Result.Success(note)
            } else {
                Result.Error(Exception("Note not found"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun createNote(title: String, content: String, category: NoteCategory): Result<Note> {
        return try {
            val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
            val newNote = Note(
                id = Uuid.random().toString(),
                title = title,
                content = content,
                category = category,
                createdAt = now,
                updatedAt = now
            )
            _notes.value = _notes.value + newNote
            Result.Success(newNote)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun updateNote(note: Note): Result<Note> {
        return try {
            val updatedNote = note.copy(
                updatedAt = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
            )
            _notes.value = _notes.value.map { if (it.id == note.id) updatedNote else it }
            Result.Success(updatedNote)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun deleteNote(id: String): Result<Unit> {
        return try {
            _notes.value = _notes.value.filter { it.id != id }
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun searchNotes(query: String): Result<List<Note>> {
        return try {
            val filteredNotes = _notes.value.filter {
                it.title.contains(query, ignoreCase = true) || 
                it.content.contains(query, ignoreCase = true)
            }
            Result.Success(filteredNotes)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun getNotesByCategory(category: NoteCategory): Result<List<Note>> {
        return try {
            val filteredNotes = _notes.value.filter { it.category == category }
            Result.Success(filteredNotes)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun toggleFavorite(id: String): Result<Note> {
        return try {
            val note = _notes.value.find { it.id == id }
            if (note != null) {
                val updatedNote = note.copy(
                    isFavorite = !note.isFavorite,
                    updatedAt = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
                )
                _notes.value = _notes.value.map { if (it.id == id) updatedNote else it }
                Result.Success(updatedNote)
            } else {
                Result.Error(Exception("Note not found"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    private fun generateSampleNotes(): List<Note> {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        return listOf(
            Note(
                id = "1",
                title = "Welcome to Notes App",
                content = "This is a sample note demonstrating the Kotlin Multiplatform frontend architecture. You can create, edit, and delete notes across all platforms!",
                category = NoteCategory.PERSONAL,
                createdAt = now,
                updatedAt = now,
                isFavorite = true
            ),
            Note(
                id = "2", 
                title = "Project Ideas",
                content = "1. Build a weather app\n2. Create a task manager\n3. Develop a recipe book\n4. Design a fitness tracker",
                category = NoteCategory.IDEAS,
                createdAt = now,
                updatedAt = now
            ),
            Note(
                id = "3",
                title = "Meeting Notes",
                content = "Discussed the new feature requirements:\n- User authentication\n- Data synchronization\n- Offline support\n- Cross-platform compatibility",
                category = NoteCategory.WORK,
                createdAt = now,
                updatedAt = now
            ),
            Note(
                id = "4",
                title = "Grocery List",
                content = "- Milk\n- Bread\n- Eggs\n- Apples\n- Chicken\n- Rice",
                category = NoteCategory.REMINDERS,
                createdAt = now,
                updatedAt = now,
                isFavorite = true
            )
        )
    }
}