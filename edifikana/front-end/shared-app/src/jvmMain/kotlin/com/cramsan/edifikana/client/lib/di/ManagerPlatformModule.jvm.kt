package com.cramsan.edifikana.client.lib.di

import com.cramsan.edifikana.client.lib.managers.FileManager
import com.cramsan.edifikana.client.lib.managers.FileManagerImpl
import com.cramsan.edifikana.client.lib.service.DownloadStrategy
import com.cramsan.edifikana.client.lib.service.JvmDownloadStrategy
import com.cramsan.edifikana.client.lib.utils.IODependencies
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

internal actual val ManagerPlatformModule = module {

    singleOf(::IODependencies)

    single<DownloadStrategy> {
        JvmDownloadStrategy()
    }

    single<FileManager> {
        FileManagerImpl(get())
    }
}
