package com.cramsan.runasimi.client.lib.di

import com.cramsan.architecture.client.manager.PreferencesManager
import com.cramsan.runasimi.client.lib.manager.QuechuaManager
import com.cramsan.runasimi.client.lib.manager.QuestionsManager
import com.cramsan.runasimi.client.lib.manager.VerbTranslationRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

internal val ManagerModule = module {
    singleOf(::VerbTranslationRepository)
    singleOf(::QuechuaManager)
    singleOf(::QuestionsManager)
    singleOf(::PreferencesManager)
}
