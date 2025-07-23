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
 * Timeout in milliseconds for shared flow subscriptions
 */
private const val SUBSCRIPTION_TIMEOUT_MS = 5000L

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
            started = SharingStarted.WhileSubscribed(SUBSCRIPTION_TIMEOUT_MS),
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
        started = SharingStarted.WhileSubscribed(SUBSCRIPTION_TIMEOUT_MS),
        initialValue = emptyList()
    )

    /**
     * Handles task click events.
     * @param task The task that was clicked
     */
    fun onTaskClick(task: Task) {
        uiState = uiState.copy(selectedTaskId = task.id)
    }

    /**
     * Toggles the completion status of a task.
     * @param task The task to toggle completion for
     */
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

    /**
     * Changes the current task filter.
     * @param filter The new filter to apply
     */
    fun onFilterChange(filter: TaskFilter) {
        uiState = uiState.copy(currentFilter = filter)
    }

    /**
     * Shows the create task dialog.
     */
    fun onShowCreateTask() {
        uiState = uiState.copy(showCreateTaskDialog = true)
    }

    /**
     * Hides the create task dialog.
     */
    fun onHideCreateTask() {
        uiState = uiState.copy(showCreateTaskDialog = false)
    }

    /**
     * Creates a new task with the specified parameters.
     * @param title The task title
     * @param description The task description
     * @param priority The task priority
     */
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

    /**
     * Dismisses the current error message.
     */
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
