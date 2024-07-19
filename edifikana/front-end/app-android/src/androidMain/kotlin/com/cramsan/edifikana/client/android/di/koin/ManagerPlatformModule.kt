package com.cramsan.edifikana.client.android.di.koin

import com.cramsan.edifikana.client.lib.service.AndroidDownloadStrategy
import com.cramsan.edifikana.client.lib.service.DownloadStrategy
import com.cramsan.edifikana.client.lib.utils.IODependencies
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val ManagerPlatformModule = module {

    singleOf(::IODependencies)

    single<DownloadStrategy> {
        AndroidDownloadStrategy(get())
    }
}
