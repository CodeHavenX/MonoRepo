package com.cramsan.sample.frontend.app.di

import com.cramsan.sample.frontend.app.viewmodel.TaskListViewModel
import com.cramsan.sample.frontend.shared.data.InMemoryTaskRepository
import com.cramsan.sample.frontend.shared.domain.repository.TaskRepository
import com.cramsan.sample.frontend.shared.domain.usecases.CreateTaskUseCase
import com.cramsan.sample.frontend.shared.domain.usecases.GetAllTasksUseCase
import com.cramsan.sample.frontend.shared.domain.usecases.ToggleTaskCompletionUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

/**
 * Dependency injection configuration using Koin.
 * Demonstrates proper separation of layers and dependency management.
 */
val appModule = module {
    // Data layer
    singleOf(::InMemoryTaskRepository) bind TaskRepository::class
    
    // Domain layer - Use cases
    factoryOf(::GetAllTasksUseCase)
    factoryOf(::ToggleTaskCompletionUseCase)
    factoryOf(::CreateTaskUseCase)
    
    // Presentation layer - ViewModels
    factoryOf(::TaskListViewModel)
}