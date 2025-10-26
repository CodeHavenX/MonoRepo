package com.cramsan.runasimi.client.lib.di

import com.cramsan.runasimi.client.lib.manager.QuechuaManager
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

internal val ManagerModule = module {
    singleOf(::QuechuaManager)
}
