package com.cramsan.edifikana.client.android.di.koin

import com.cramsan.edifikana.client.lib.features.eventlog.EventLogViewModel
import org.koin.dsl.module

val ViewModelModule = module {

    factory { EventLogViewModel(get(), get(), get()) }
}
