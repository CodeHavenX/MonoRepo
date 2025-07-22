package com.cramsan.sample.frontend.shared.data

import com.cramsan.sample.frontend.shared.domain.entities.Task
import com.cramsan.sample.frontend.shared.domain.entities.TaskId
import com.cramsan.sample.frontend.shared.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

/**
 * In-memory implementation of TaskRepository.
 * In a real application, this would be replaced with a database implementation.
 */
class InMemoryTaskRepository : TaskRepository {
    
    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    
    override fun getAllTasks(): Flow<List<Task>> = _tasks
    
    override suspend fun getTaskById(id: TaskId): Task? {
        return _tasks.value.find { it.id == id }
    }
    
    override suspend fun addTask(task: Task): Result<Unit> {
        return try {
            val currentTasks = _tasks.value.toMutableList()
            // Check if task already exists
            if (currentTasks.any { it.id == task.id }) {
                Result.failure(IllegalArgumentException("Task with ID ${task.id.value} already exists"))
            } else {
                currentTasks.add(task)
                _tasks.value = currentTasks
                Result.success(Unit)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun updateTask(task: Task): Result<Unit> {
        return try {
            val currentTasks = _tasks.value.toMutableList()
            val index = currentTasks.indexOfFirst { it.id == task.id }
            
            if (index == -1) {
                Result.failure(IllegalArgumentException("Task with ID ${task.id.value} not found"))
            } else {
                currentTasks[index] = task
                _tasks.value = currentTasks
                Result.success(Unit)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun deleteTask(id: TaskId): Result<Unit> {
        return try {
            val currentTasks = _tasks.value.toMutableList()
            val removed = currentTasks.removeAll { it.id == id }
            
            if (removed) {
                _tasks.value = currentTasks
                Result.success(Unit)
            } else {
                Result.failure(IllegalArgumentException("Task with ID ${id.value} not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override fun getTasksByCompletionStatus(completed: Boolean): Flow<List<Task>> {
        return _tasks.map { tasks ->
            tasks.filter { it.isCompleted == completed }
        }
    }
    
    override suspend fun searchTasks(query: String): List<Task> {
        if (query.isBlank()) return _tasks.value
        
        val lowerQuery = query.lowercase()
        return _tasks.value.filter { task ->
            task.title.lowercase().contains(lowerQuery) ||
            task.description.lowercase().contains(lowerQuery)
        }
    }
}