package com.cramsan.edifikana.client.lib.di.koin

import com.cramsan.edifikana.client.lib.features.EdifikanaApplicationViewModel
import com.cramsan.edifikana.client.lib.features.account.account.AccountViewModel
import com.cramsan.edifikana.client.lib.features.admin.addproperty.AddPropertyViewModel
import com.cramsan.edifikana.client.lib.features.admin.hub.HubViewModel
import com.cramsan.edifikana.client.lib.features.admin.properties.PropertyManagerViewModel
import com.cramsan.edifikana.client.lib.features.admin.property.PropertyViewModel
import com.cramsan.edifikana.client.lib.features.admin.stafflist.StaffListViewModel
import com.cramsan.edifikana.client.lib.features.auth.signin.SignInViewModel
import com.cramsan.edifikana.client.lib.features.auth.signup.SignUpViewModel
import com.cramsan.edifikana.client.lib.features.debug.main.DebugViewModel
import com.cramsan.edifikana.client.lib.features.main.eventlog.EventLogViewModel
import com.cramsan.edifikana.client.lib.features.main.eventlog.addrecord.AddRecordViewModel
import com.cramsan.edifikana.client.lib.features.main.eventlog.viewrecord.ViewRecordViewModel
import com.cramsan.edifikana.client.lib.features.main.home.HomeViewModel
import com.cramsan.edifikana.client.lib.features.main.timecard.TimeCartViewModel
import com.cramsan.edifikana.client.lib.features.main.timecard.addstaff.AddStaffViewModel
import com.cramsan.edifikana.client.lib.features.main.timecard.viewstaff.ViewStaffViewModel
import com.cramsan.framework.core.compose.ViewModelDependencies
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

internal val ViewModelModule = module {

    // These objects are singletons and are not scoped to any particular navigation graph.
    singleOf(::ViewModelDependencies)
    singleOf(::EdifikanaApplicationViewModel)

    // These objects are scoped to the screen in which they are used.
    viewModelOf(::EventLogViewModel)
    viewModelOf(::TimeCartViewModel)
    viewModelOf(::StaffListViewModel)
    viewModelOf(::ViewRecordViewModel)
    viewModelOf(::AddStaffViewModel)
    viewModelOf(::AddRecordViewModel)
    viewModelOf(::ViewStaffViewModel)
    viewModelOf(::SignInViewModel)
    viewModelOf(::SignUpViewModel)
    viewModelOf(::AccountViewModel)
    viewModelOf(::PropertyManagerViewModel)
    viewModelOf(::PropertyViewModel)
    viewModelOf(::DebugViewModel)
    viewModelOf(::HomeViewModel)
    viewModelOf(::AddPropertyViewModel)
    viewModelOf(::HubViewModel)
}
