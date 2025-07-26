package com.cramsan.sample.frontend.architecture.shared.domain.usecase

import com.cramsan.sample.frontend.architecture.shared.data.repository.InMemoryNotesRepository
import com.cramsan.sample.frontend.architecture.shared.domain.model.NoteCategory
import com.cramsan.sample.frontend.architecture.shared.domain.model.Result
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class NotesUseCaseTest {

    private val repository = InMemoryNotesRepository()
    private val useCase = NotesUseCase(repository)

    @Test
    fun `createNote with valid data should succeed`() = runTest {
        // Given
        val title = "Test Note"
        val content = "Test content"
        val category = NoteCategory.PERSONAL

        // When
        val result = useCase.createNote(title, content, category)

        // Then
        assertTrue(result is Result.Success)
        assertEquals(title, result.data.title)
        assertEquals(content, result.data.content)
        assertEquals(category, result.data.category)
    }

    @Test
    fun `createNote with blank title should fail`() = runTest {
        // Given
        val title = "   "
        val content = "Test content"
        val category = NoteCategory.PERSONAL

        // When
        val result = useCase.createNote(title, content, category)

        // Then
        assertTrue(result is Result.Error)
        assertTrue(result.exception is IllegalArgumentException)
    }

    @Test
    fun `searchNotes with blank query should return empty list`() = runTest {
        // Given
        val query = "   "

        // When
        val result = useCase.searchNotes(query)

        // Then
        assertTrue(result is Result.Success)
        assertTrue(result.data.isEmpty())
    }
}

// Simple test runner for common tests
expect fun runTest(block: suspend () -> Unit)