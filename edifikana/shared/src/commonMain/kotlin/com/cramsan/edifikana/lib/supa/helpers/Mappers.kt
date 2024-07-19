package com.cramsan.edifikana.lib.supa.helpers

import com.cramsan.edifikana.lib.supa.Employee
import com.cramsan.edifikana.lib.supa.SupabaseModel

@SupabaseModel
fun Employee.fullName() = "$name $lastName".trim()
