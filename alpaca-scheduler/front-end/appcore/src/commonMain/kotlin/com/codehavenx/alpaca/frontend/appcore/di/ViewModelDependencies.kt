package com.codehavenx.alpaca.frontend.appcore.di

import com.codehavenx.alpaca.frontend.appcore.features.application.ApplicationViewModel
import com.codehavenx.alpaca.frontend.appcore.features.clients.addclient.AddClientViewModel
import com.codehavenx.alpaca.frontend.appcore.features.clients.listclients.ListClientViewModel
import com.codehavenx.alpaca.frontend.appcore.features.clients.updateclient.UpdateClientViewModel
import com.codehavenx.alpaca.frontend.appcore.features.clients.viewclient.ViewClientViewModel
import com.codehavenx.alpaca.frontend.appcore.features.createaccount.CreateAccountViewModel
import com.codehavenx.alpaca.frontend.appcore.features.home.HomeViewModel
import com.codehavenx.alpaca.frontend.appcore.features.signin.SignInViewModel
import com.codehavenx.alpaca.frontend.appcore.features.staff.addstaff.AddStaffViewModel
import com.codehavenx.alpaca.frontend.appcore.features.staff.liststaff.ListStaffsViewModel
import com.codehavenx.alpaca.frontend.appcore.features.staff.updatestaff.UpdateStaffViewModel
import com.codehavenx.alpaca.frontend.appcore.features.staff.viewstaff.ViewStaffViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

/**
 * Module for defining the view model for dependency injection of the application.
 */
val ViewModelModule = module {

    singleOf(::ViewModelDependencies)

    // TODO: Currently we cannot scope a viewmodel to a navigation graph until koin supports the viewModel function
    // in compose multiplatform code. Until then we will have all viewmodels as singletons.
    factoryOf(::ApplicationViewModel)
    factoryOf(::SignInViewModel)
    factoryOf(::HomeViewModel)
    factoryOf(::ListClientViewModel)
    factoryOf(::AddClientViewModel)
    factoryOf(::ViewClientViewModel)
    factoryOf(::UpdateClientViewModel)
    factoryOf(::ListStaffsViewModel)
    factoryOf(::AddStaffViewModel)
    factoryOf(::ViewStaffViewModel)
    factoryOf(::UpdateStaffViewModel)
    factoryOf(::CreateAccountViewModel)
}
