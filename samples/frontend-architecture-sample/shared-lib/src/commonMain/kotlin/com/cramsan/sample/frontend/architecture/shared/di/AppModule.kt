package com.cramsan.sample.frontend.architecture.shared.di

import com.cramsan.sample.frontend.architecture.shared.data.repository.InMemoryNotesRepository
import com.cramsan.sample.frontend.architecture.shared.domain.repository.NotesRepository
import com.cramsan.sample.frontend.architecture.shared.domain.usecase.NotesUseCase
import com.cramsan.sample.frontend.architecture.shared.presentation.viewmodel.NotesViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

/**
 * Koin module for dependency injection
 */
val appModule = module {
    // Repository
    single<NotesRepository> { InMemoryNotesRepository() }
    
    // Use Cases
    single { NotesUseCase(get()) }
    
    // ViewModels
    viewModel { NotesViewModel(get()) }
}