package com.cramsan.edifikana.client.lib.features.home

import kotlin.test.Test
import kotlin.test.assertIs
import kotlin.test.assertNull

class HomeDestinationTest {

    @Test
    fun `fromWebPath returns ManagementHub`() {
        assertIs<HomeDestination.ManagementHub>(HomeDestination.fromWebPath("/home"))
    }

    @Test
    fun `fromWebPath returns PropertyManagementDestination`() {
        assertIs<HomeDestination.PropertyManagementDestination>(
            HomeDestination.fromWebPath("/home/property?propertyId=prop-1")
        )
    }

    @Test
    fun `fromWebPath returns AddPropertyManagementDestination`() {
        assertIs<HomeDestination.AddPropertyManagementDestination>(
            HomeDestination.fromWebPath("/home/add-property?orgId=org-1")
        )
    }

    @Test
    fun `fromWebPath returns AddSecondaryEmployeeManagementDestination`() {
        assertIs<HomeDestination.AddSecondaryEmployeeManagementDestination>(
            HomeDestination.fromWebPath("/home/add-secondary-employee?propertyId=prop-1")
        )
    }

    @Test
    fun `fromWebPath returns EmployeeDestination`() {
        assertIs<HomeDestination.EmployeeDestination>(
            HomeDestination.fromWebPath("/home/employee?employeeId=emp-1")
        )
    }

    @Test
    fun `fromWebPath returns TimeCardEmployeeListDestination`() {
        assertIs<HomeDestination.TimeCardEmployeeListDestination>(
            HomeDestination.fromWebPath("/home/timecard?propertyId=prop-1")
        )
    }

    @Test
    fun `fromWebPath returns TimeCardSingleEmployeeDestination`() {
        assertIs<HomeDestination.TimeCardSingleEmployeeDestination>(
            HomeDestination.fromWebPath("/home/timecard?employeePk=emp-1&propertyId=prop-1")
        )
    }

    @Test
    fun `fromWebPath returns EventLogSingleItemDestination`() {
        assertIs<HomeDestination.EventLogSingleItemDestination>(
            HomeDestination.fromWebPath("/home/event-log?eventLogRecordPk=log-1")
        )
    }

    @Test
    fun `fromWebPath returns EventLogAddItemDestination`() {
        assertIs<HomeDestination.EventLogAddItemDestination>(
            HomeDestination.fromWebPath("/home/event-log-add?propertyId=prop-1")
        )
    }

    @Test
    fun `fromWebPath returns InviteStaffMemberDestination`() {
        assertIs<HomeDestination.InviteStaffMemberDestination>(
            HomeDestination.fromWebPath("/home/invite?orgId=org-1")
        )
    }

    @Test
    fun `fromWebPath returns null for unrecognized path`() {
        assertNull(HomeDestination.fromWebPath("/unknown"))
    }

    @Test
    fun `fromWebPath returns null for path missing required params`() {
        assertNull(HomeDestination.fromWebPath("/home/employee"))
    }
}
