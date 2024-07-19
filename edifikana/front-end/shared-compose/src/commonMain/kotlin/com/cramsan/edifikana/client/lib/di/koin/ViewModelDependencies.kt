package com.cramsan.edifikana.client.lib.di.koin

import com.cramsan.edifikana.client.lib.features.eventlog.EventLogViewModel
import com.cramsan.edifikana.client.lib.features.eventlog.addrecord.AddRecordViewModel
import com.cramsan.edifikana.client.lib.features.eventlog.viewrecord.ViewRecordViewModel
import com.cramsan.edifikana.client.lib.features.formlist.FormListViewModel
import com.cramsan.edifikana.client.lib.features.formlist.entry.EntryViewModel
import com.cramsan.edifikana.client.lib.features.formlist.records.RecordsViewModel
import com.cramsan.edifikana.client.lib.features.formlist.records.read.RecordReadViewModel
import com.cramsan.edifikana.client.lib.features.main.MainActivityViewModel
import com.cramsan.edifikana.client.lib.features.signinv2.SignInV2ViewModel
import com.cramsan.edifikana.client.lib.features.timecard.TimeCartViewModel
import com.cramsan.edifikana.client.lib.features.timecard.addemployee.AddEmployeeViewModel
import com.cramsan.edifikana.client.lib.features.timecard.employeelist.EmployeeListViewModel
import com.cramsan.edifikana.client.lib.features.timecard.viewemployee.ViewEmployeeViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val ViewModelModule = module {

    // TODO: Currently we cannot scope a viewmodel to a navigation graph until koin supports the viewModel function
    // in compose multiplatform code. Until then we will have all viewmodels as singletons.
    singleOf(::EventLogViewModel)
    singleOf(::TimeCartViewModel)
    singleOf(::RecordReadViewModel)
    singleOf(::EmployeeListViewModel)
    singleOf(::FormListViewModel)
    singleOf(::ViewRecordViewModel)
    singleOf(::AddEmployeeViewModel)
    singleOf(::RecordsViewModel)
    singleOf(::EntryViewModel)
    singleOf(::AddRecordViewModel)
    singleOf(::ViewEmployeeViewModel)
    singleOf(::MainActivityViewModel)
    singleOf(::SignInV2ViewModel)
}
