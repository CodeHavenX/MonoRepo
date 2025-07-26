package com.cramsan.sample.frontend.architecture.shared.domain.model

import kotlinx.datetime.LocalDateTime

/**
 * Represents a note in the application
 */
data class Note(
    val id: String,
    val title: String,
    val content: String,
    val category: NoteCategory,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val isFavorite: Boolean = false
)

/**
 * Categories for organizing notes
 */
enum class NoteCategory(val displayName: String) {
    PERSONAL("Personal"),
    WORK("Work"),
    IDEAS("Ideas"),
    REMINDERS("Reminders"),
    OTHER("Other")
}

/**
 * Represents the result of a repository operation
 */
sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val exception: Throwable) : Result<Nothing>()
}