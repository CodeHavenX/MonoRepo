package com.cramsan.edifikana.client.lib.di

import com.cramsan.edifikana.client.lib.features.account.account.AccountViewModel
import com.cramsan.edifikana.client.lib.features.account.notifications.NotificationsViewModel
import com.cramsan.edifikana.client.lib.features.auth.signin.SignInViewModel
import com.cramsan.edifikana.client.lib.features.auth.signup.SignUpViewModel
import com.cramsan.edifikana.client.lib.features.auth.validation.ValidationViewModel
import com.cramsan.edifikana.client.lib.features.debug.main.DebugViewModel
import com.cramsan.edifikana.client.lib.features.management.addprimarystaff.AddPrimaryStaffViewModel
import com.cramsan.edifikana.client.lib.features.management.addproperty.AddPropertyViewModel
import com.cramsan.edifikana.client.lib.features.management.addrecord.AddRecordViewModel
import com.cramsan.edifikana.client.lib.features.management.addsecondarystaff.AddSecondaryStaffViewModel
import com.cramsan.edifikana.client.lib.features.management.drawer.ManagementViewModel
import com.cramsan.edifikana.client.lib.features.management.eventlog.EventLogViewModel
import com.cramsan.edifikana.client.lib.features.management.home.HomeViewModel
import com.cramsan.edifikana.client.lib.features.management.hub.HubViewModel
import com.cramsan.edifikana.client.lib.features.management.properties.PropertyManagerViewModel
import com.cramsan.edifikana.client.lib.features.management.property.PropertyViewModel
import com.cramsan.edifikana.client.lib.features.management.staff.StaffViewModel
import com.cramsan.edifikana.client.lib.features.management.timecard.TimeCartViewModel
import com.cramsan.edifikana.client.lib.features.management.timecardstafflist.StaffListViewModel
import com.cramsan.edifikana.client.lib.features.management.viewrecord.ViewRecordViewModel
import com.cramsan.edifikana.client.lib.features.management.viewstaff.ViewStaffViewModel
import com.cramsan.edifikana.client.lib.features.splash.SplashViewModel
import com.cramsan.edifikana.client.lib.features.window.EdifikanaWindowDelegatedEvent
import com.cramsan.edifikana.client.lib.features.window.EdifikanaWindowViewModel
import com.cramsan.framework.core.compose.EventBus
import com.cramsan.framework.core.compose.EventEmitter
import com.cramsan.framework.core.compose.EventReceiver
import com.cramsan.framework.core.compose.ViewModelDependencies
import com.cramsan.framework.core.compose.WindowEvent
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.module.dsl.withOptions
import org.koin.core.qualifier.named
import org.koin.dsl.module

typealias SelectStaffViewModel = com.cramsan.edifikana.client.lib.features.management.stafflist.StaffListViewModel

internal val ViewModelModule = module {

    scope<String> {
        scoped(named(VIEW_MODEL_WINDOW_EVENT_BUS)) {
            EventBus<WindowEvent>()
        } withOptions {
            bind<EventEmitter<WindowEvent>>()
            bind<EventReceiver<WindowEvent>>()
        }

        scoped(named(VIEW_MODEL_DELEGATED_EVENT_BUS)) {
            EventBus<EdifikanaWindowDelegatedEvent>()
        } withOptions {
            bind<EventEmitter<EdifikanaWindowDelegatedEvent>>()
            bind<EventReceiver<EdifikanaWindowDelegatedEvent>>()
        }

        scoped {
            ViewModelDependencies(
                get(),
                get(),
                get(),
                get(named(VIEW_MODEL_WINDOW_EVENT_BUS)),
                get(named(APPLICATION_EVENT_BUS)),
            )
        }

        viewModel {
            EdifikanaWindowViewModel(
                get(),
                get(named(VIEW_MODEL_WINDOW_EVENT_BUS)),
                get(named(VIEW_MODEL_DELEGATED_EVENT_BUS)),
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
}

private const val VIEW_MODEL_WINDOW_EVENT_BUS = "viewModelWindowEventBus"
const val VIEW_MODEL_DELEGATED_EVENT_BUS = "viewModelDelegatedEventBus"
