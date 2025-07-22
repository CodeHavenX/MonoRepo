package com.cramsan.sample.frontend.app.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cramsan.sample.frontend.shared.domain.entities.Task
import com.cramsan.sample.frontend.shared.domain.entities.TaskId
import com.cramsan.sample.frontend.shared.domain.entities.TaskPriority
import com.cramsan.sample.frontend.shared.domain.usecases.CreateTaskUseCase
import com.cramsan.sample.frontend.shared.domain.usecases.GetAllTasksUseCase
import com.cramsan.sample.frontend.shared.domain.usecases.ToggleTaskCompletionUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel for the task list screen.
 * Demonstrates proper state management and use case integration.
 */
class TaskListViewModel(
    private val getAllTasksUseCase: GetAllTasksUseCase,
    private val toggleTaskCompletionUseCase: ToggleTaskCompletionUseCase,
    private val createTaskUseCase: CreateTaskUseCase
) : ViewModel() {

    // UI State
    var uiState by mutableStateOf(TaskListUiState())
        private set

    // Tasks flow from use case
    val tasks: StateFlow<List<Task>> = getAllTasksUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Filtered tasks based on current filter
    val filteredTasks: StateFlow<List<Task>> = tasks.map { taskList ->
        when (uiState.currentFilter) {
            TaskFilter.ALL -> taskList
            TaskFilter.ACTIVE -> taskList.filter { !it.isCompleted }
            TaskFilter.COMPLETED -> taskList.filter { it.isCompleted }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun onTaskClick(task: Task) {
        uiState = uiState.copy(selectedTaskId = task.id)
    }

    fun onToggleTaskCompletion(task: Task) {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null)
            
            toggleTaskCompletionUseCase(task.id)
                .onSuccess {
                    uiState = uiState.copy(isLoading = false)
                }
                .onFailure { error ->
                    uiState = uiState.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Failed to update task"
                    )
                }
        }
    }

    fun onFilterChange(filter: TaskFilter) {
        uiState = uiState.copy(currentFilter = filter)
    }

    fun onShowCreateTask() {
        uiState = uiState.copy(showCreateTaskDialog = true)
    }

    fun onHideCreateTask() {
        uiState = uiState.copy(showCreateTaskDialog = false)
    }

    fun onCreateTask(title: String, description: String, priority: TaskPriority) {
        if (title.isBlank()) {
            uiState = uiState.copy(errorMessage = "Title cannot be empty")
            return
        }

        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null)
            
            createTaskUseCase(title, description, priority)
                .onSuccess {
                    uiState = uiState.copy(
                        isLoading = false,
                        showCreateTaskDialog = false
                    )
                }
                .onFailure { error ->
                    uiState = uiState.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Failed to create task"
                    )
                }
        }
    }

    fun onErrorDismissed() {
        uiState = uiState.copy(errorMessage = null)
    }
}

/**
 * UI state for the task list screen
 */
data class TaskListUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val currentFilter: TaskFilter = TaskFilter.ALL,
    val selectedTaskId: TaskId? = null,
    val showCreateTaskDialog: Boolean = false
)

/**
 * Task filter options
 */
enum class TaskFilter(val displayName: String) {
    ALL("All"),
    ACTIVE("Active"),
    COMPLETED("Completed")
}