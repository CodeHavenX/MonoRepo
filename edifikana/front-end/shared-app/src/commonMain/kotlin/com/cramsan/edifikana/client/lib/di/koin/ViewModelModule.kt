package com.cramsan.edifikana.client.lib.di.koin

import com.cramsan.edifikana.client.lib.features.EdifikanaWindowDelegatedEvent
import com.cramsan.edifikana.client.lib.features.EdifikanaWindowViewModel
import com.cramsan.edifikana.client.lib.features.account.account.AccountViewModel
import com.cramsan.edifikana.client.lib.features.account.notifications.NotificationsViewModel
import com.cramsan.edifikana.client.lib.features.admin.addprimarystaff.AddPrimaryStaffViewModel
import com.cramsan.edifikana.client.lib.features.admin.addproperty.AddPropertyViewModel
import com.cramsan.edifikana.client.lib.features.admin.addsecondarystaff.AddSecondaryStaffViewModel
import com.cramsan.edifikana.client.lib.features.admin.hub.HubViewModel
import com.cramsan.edifikana.client.lib.features.admin.properties.PropertyManagerViewModel
import com.cramsan.edifikana.client.lib.features.admin.property.PropertyViewModel
import com.cramsan.edifikana.client.lib.features.admin.staff.StaffViewModel
import com.cramsan.edifikana.client.lib.features.admin.stafflist.StaffListViewModel
import com.cramsan.edifikana.client.lib.features.auth.signin.SignInViewModel
import com.cramsan.edifikana.client.lib.features.auth.signup.SignUpViewModel
import com.cramsan.edifikana.client.lib.features.auth.validation.ValidationViewModel
import com.cramsan.edifikana.client.lib.features.debug.main.DebugViewModel
import com.cramsan.edifikana.client.lib.features.main.eventlog.EventLogViewModel
import com.cramsan.edifikana.client.lib.features.main.eventlog.addrecord.AddRecordViewModel
import com.cramsan.edifikana.client.lib.features.main.eventlog.viewrecord.ViewRecordViewModel
import com.cramsan.edifikana.client.lib.features.main.home.HomeViewModel
import com.cramsan.edifikana.client.lib.features.main.timecard.TimeCartViewModel
import com.cramsan.edifikana.client.lib.features.main.timecard.viewstaff.ViewStaffViewModel
import com.cramsan.edifikana.client.lib.features.management.drawer.ManagementViewModel
import com.cramsan.edifikana.client.lib.features.splash.SplashViewModel
import com.cramsan.framework.core.compose.ApplicationEvent
import com.cramsan.framework.core.compose.ApplicationEventBus
import com.cramsan.framework.core.compose.EventBus
import com.cramsan.framework.core.compose.EventEmitter
import com.cramsan.framework.core.compose.EventReceiver
import com.cramsan.framework.core.compose.ViewModelDependencies
import com.cramsan.framework.core.compose.WindowEvent
import com.cramsan.framework.core.compose.WindowEventBus
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.module.dsl.withOptions
import org.koin.core.qualifier.named
import org.koin.dsl.module

typealias SelectStaffViewModel = com.cramsan.edifikana.client.lib.features.main.timecard.stafflist.StaffListViewModel

internal val ViewModelModule = module {

    // These objects are singletons and are not scoped to any particular navigation graph.
    single(named(APPLICATION_EVENT_SCOPE)) {
        ApplicationEventBus()
    } withOptions {
        bind<EventReceiver<ApplicationEvent>>()
        bind<EventEmitter<ApplicationEvent>>()
    }

    single(named(APPLICATION_EVENT_SCOPE)) {
        WindowEventBus()
    } withOptions {
        bind<EventReceiver<WindowEvent>>()
        bind<EventEmitter<WindowEvent>>()
    }

    single(named(DELEGATED_EVENT_SCOPE)) {
        EventBus<EdifikanaWindowDelegatedEvent>()
    } withOptions {
        bind<EventReceiver<EdifikanaWindowDelegatedEvent>>()
        bind<EventEmitter<EdifikanaWindowDelegatedEvent>>()
    }

    single {
        ViewModelDependencies(
            get(),
            get(),
            get(),
            get(named(APPLICATION_EVENT_SCOPE)),
            get(named(APPLICATION_EVENT_SCOPE)),
        )
    }

    viewModel {
        EdifikanaWindowViewModel(
            get(),
            get(named(APPLICATION_EVENT_SCOPE)),
            get(named(DELEGATED_EVENT_SCOPE)),
        )
    }

    // These objects are scoped to the screen in which they are used.
    viewModelOf(::EventLogViewModel)
    viewModelOf(::TimeCartViewModel)
    viewModelOf(::StaffListViewModel)
    viewModelOf(::ViewRecordViewModel)
    viewModelOf(::AddRecordViewModel)
    viewModelOf(::ViewStaffViewModel)
    viewModelOf(::SignInViewModel)
    viewModelOf(::SignUpViewModel)
    viewModelOf(::AccountViewModel)
    viewModelOf(::PropertyManagerViewModel)
    viewModelOf(::PropertyViewModel)
    viewModelOf(::DebugViewModel)
    viewModelOf(::HomeViewModel)
    viewModelOf(::ValidationViewModel)
    viewModelOf(::AddPropertyViewModel)
    viewModelOf(::HubViewModel)
    viewModelOf(::AddPrimaryStaffViewModel)
    viewModelOf(::AddSecondaryStaffViewModel)
    viewModelOf(::StaffViewModel)
    viewModelOf(::SelectStaffViewModel)
    viewModelOf(::NotificationsViewModel)
    viewModelOf(::SplashViewModel)
    viewModelOf(::ManagementViewModel)
}

private const val APPLICATION_EVENT_SCOPE = "application_event_scope"
private const val DELEGATED_EVENT_SCOPE = "delegated_event_scope"
