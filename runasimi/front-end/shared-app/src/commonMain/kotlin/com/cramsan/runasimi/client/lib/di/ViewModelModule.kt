package com.cramsan.runasimi.client.lib.di

import com.cramsan.runasimi.client.lib.features.main.menu.MenuViewModel
import com.cramsan.runasimi.client.lib.features.main.questions.QuestionsViewModel
import com.cramsan.runasimi.client.lib.features.main.verbs.VerbsViewModel
import com.cramsan.runasimi.client.lib.features.main.yupay.YupayViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

internal val ViewModelModule = module {
    viewModelOf(::VerbsViewModel)
    viewModelOf(::MenuViewModel)
    viewModelOf(::YupayViewModel)
    viewModelOf(::QuestionsViewModel)
}
