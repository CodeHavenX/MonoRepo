package com.cramsan.edifikana.client.lib.koin

import com.cramsan.edifikana.client.lib.service.DownloadStrategy
import com.cramsan.edifikana.client.lib.service.JvmDownloadStrategy
import com.cramsan.edifikana.client.lib.utils.IODependencies
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val ManagerPlatformModule = module {

    singleOf(::IODependencies)

    single<DownloadStrategy> {
        JvmDownloadStrategy()
    }
}
