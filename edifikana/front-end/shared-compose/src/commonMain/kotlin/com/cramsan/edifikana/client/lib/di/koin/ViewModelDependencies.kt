package com.cramsan.edifikana.client.lib.di.koin

import com.cramsan.edifikana.client.lib.features.eventlog.EventLogViewModel
import com.cramsan.edifikana.client.lib.features.eventlog.addrecord.AddRecordViewModel
import com.cramsan.edifikana.client.lib.features.eventlog.viewrecord.ViewRecordViewModel
import com.cramsan.edifikana.client.lib.features.formlist.FormListViewModel
import com.cramsan.edifikana.client.lib.features.formlist.entry.EntryViewModel
import com.cramsan.edifikana.client.lib.features.formlist.records.RecordsViewModel
import com.cramsan.edifikana.client.lib.features.formlist.records.read.RecordReadViewModel
import com.cramsan.edifikana.client.lib.features.main.MainActivityViewModel
import com.cramsan.edifikana.client.lib.features.timecard.TimeCartViewModel
import com.cramsan.edifikana.client.lib.features.timecard.addemployee.AddEmployeeViewModel
import com.cramsan.edifikana.client.lib.features.timecard.employeelist.EmployeeListViewModel
import com.cramsan.edifikana.client.lib.features.timecard.viewemployee.ViewEmployeeViewModel
import org.koin.dsl.module

val ViewModelModule = module {

    factory { EventLogViewModel(get(), get(), get()) }
    factory { TimeCartViewModel(get(), get(), get(), get()) }
    factory { RecordReadViewModel(get(), get(), get()) }
    factory { EmployeeListViewModel(get(), get(), get()) }
    factory { FormListViewModel(get(), get(), get()) }
    factory { ViewRecordViewModel(get(), get(), get(), get(), get()) }
    factory { AddEmployeeViewModel(get(), get(), get()) }
    factory { RecordsViewModel(get(), get(), get()) }
    factory { EntryViewModel(get(), get(), get(), get()) }
    factory { AddRecordViewModel(get(), get(), get(), get(), get()) }
    factory { ViewEmployeeViewModel(get(), get(), get(), get(), get(), get()) }
    factory { MainActivityViewModel(get(), get(), get(), get(), get(), get()) }
}
