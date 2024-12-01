package com.codehavenx.alpaca.frontend.appcore.di

import com.codehavenx.alpaca.frontend.appcore.managers.AuthenticationManager
import com.codehavenx.alpaca.frontend.appcore.managers.ClientManager
import com.codehavenx.alpaca.frontend.appcore.managers.StaffManager
import com.codehavenx.alpaca.frontend.appcore.managers.UserManager
import com.codehavenx.alpaca.frontend.appcore.service.UserService
import com.codehavenx.alpaca.frontend.appcore.service.impl.UserServiceImpl
import com.cramsan.framework.core.ManagerDependencies
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val ManagerModule = module {
    singleOf(::ManagerDependencies)

    // Managers
    singleOf(::UserManager)
    singleOf(::AuthenticationManager)
    singleOf(::ClientManager)
    singleOf(::StaffManager)

    // Services
    singleOf(::UserServiceImpl) {
        bind<UserService>()
    }
}
