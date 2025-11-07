package com.cramsan.edifikana.client.lib.di

import com.cramsan.edifikana.client.lib.features.account.account.AccountViewModel
import com.cramsan.edifikana.client.lib.features.account.changepassword.ChangePasswordDialogViewModel
import com.cramsan.edifikana.client.lib.features.account.notifications.NotificationsViewModel
import com.cramsan.edifikana.client.lib.features.auth.signin.SignInViewModel
import com.cramsan.edifikana.client.lib.features.auth.signup.SignUpViewModel
import com.cramsan.edifikana.client.lib.features.auth.validation.OtpValidationViewModel
import com.cramsan.edifikana.client.lib.features.debug.main.DebugViewModel
import com.cramsan.edifikana.client.lib.features.debug.screenselector.ScreenSelectorViewModel
import com.cramsan.edifikana.client.lib.features.home.addprimaryemployee.AddPrimaryEmployeeViewModel
import com.cramsan.edifikana.client.lib.features.home.addproperty.AddPropertyViewModel
import com.cramsan.edifikana.client.lib.features.home.addrecord.AddRecordViewModel
import com.cramsan.edifikana.client.lib.features.home.addsecondaryemployee.AddSecondaryEmployeeViewModel
import com.cramsan.edifikana.client.lib.features.home.drawer.DrawerViewModel
import com.cramsan.edifikana.client.lib.features.home.employee.EmployeeViewModel
import com.cramsan.edifikana.client.lib.features.home.employeelist.EmployeeListViewModel
import com.cramsan.edifikana.client.lib.features.home.eventlog.EventLogViewModel
import com.cramsan.edifikana.client.lib.features.home.gotoorganization.GoToOrganizationViewModel
import com.cramsan.edifikana.client.lib.features.home.organizationhome.OrganizationHomeViewModel
import com.cramsan.edifikana.client.lib.features.home.properties.PropertyManagerViewModel
import com.cramsan.edifikana.client.lib.features.home.property.PropertyViewModel
import com.cramsan.edifikana.client.lib.features.home.propertyhome.PropertyHomeViewModel
import com.cramsan.edifikana.client.lib.features.home.timecard.TimeCartViewModel
import com.cramsan.edifikana.client.lib.features.home.timecardemployeelist.TimeCardEmployeeListViewModel
import com.cramsan.edifikana.client.lib.features.home.viewemployee.ViewEmployeeViewModel
import com.cramsan.edifikana.client.lib.features.home.viewrecord.ViewRecordViewModel
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

internal val ViewModelModule = module {

    scope<String> {
        scoped(named(WindowIdentifier.EVENT_BUS)) {
            EventBus<WindowEvent>()
        } withOptions {
            bind<EventEmitter<WindowEvent>>()
            bind<EventReceiver<WindowEvent>>()
        }

        scoped(named(WindowIdentifier.DELEGATED_EVENT_BUS)) {
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
                get(named(WindowIdentifier.EVENT_BUS)),
                get(named(ApplicationIdentifier.EVENT_BUS)),
            )
        }

        viewModel {
            EdifikanaWindowViewModel(
                get(),
                get(named(WindowIdentifier.EVENT_BUS)),
                get(named(WindowIdentifier.DELEGATED_EVENT_BUS)),
            )
        }

        // These objects are scoped to the screen in which they are used.
        viewModelOf(::EventLogViewModel)
        viewModelOf(::TimeCartViewModel)
        viewModelOf(::EmployeeListViewModel)
        viewModelOf(::ViewRecordViewModel)
        viewModelOf(::AddRecordViewModel)
        viewModelOf(::ViewEmployeeViewModel)
        viewModelOf(::SignInViewModel)
        viewModelOf(::SignUpViewModel)
        viewModelOf(::AccountViewModel)
        viewModelOf(::PropertyManagerViewModel)
        viewModelOf(::PropertyViewModel)
        viewModelOf(::DebugViewModel)
        viewModelOf(::PropertyHomeViewModel)
        viewModelOf(::OtpValidationViewModel)
        viewModelOf(::AddPropertyViewModel)
        viewModelOf(::OrganizationHomeViewModel)
        viewModelOf(::AddPrimaryEmployeeViewModel)
        viewModelOf(::AddSecondaryEmployeeViewModel)
        viewModelOf(::EmployeeViewModel)
        viewModelOf(::TimeCardEmployeeListViewModel)
        viewModelOf(::NotificationsViewModel)
        viewModelOf(::SplashViewModel)
        viewModelOf(::DrawerViewModel)
        viewModelOf(::ScreenSelectorViewModel)
        viewModelOf(::ChangePasswordDialogViewModel)
        viewModelOf(::GoToOrganizationViewModel)
    }
}

/**
 * Identifiers for various window-level components.
 */
enum class WindowIdentifier {
    EVENT_BUS,
    DELEGATED_EVENT_BUS,
}
