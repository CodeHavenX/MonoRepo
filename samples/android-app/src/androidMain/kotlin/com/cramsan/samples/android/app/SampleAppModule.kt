package com.cramsan.samples.android.app

import com.cramsan.samples.android.app.homepage.HomePageViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

/** Koin DI module for the sample Android app. */
val SampleAppModule = module {
    viewModelOf(::HomePageViewModel)
}
