package com.cramsan.framework.webroute.ksp

import kotlin.test.Test
import kotlin.test.assertEquals

class RouteEntryTest {
    @Test
    fun `propertyName strips Destination suffix and lowercases first char`() {
        val entry = RouteEntry("FlyerListDestination", "/")

        assertEquals("flyerListEntry", entry.propertyName)
    }

    @Test
    fun `propertyName appends Entry when name has no Destination suffix`() {
        val entry = RouteEntry("Foo", "/foo")

        assertEquals("fooEntry", entry.propertyName)
    }

    @Test
    fun `propertyName handles a name that is only the Destination suffix`() {
        val entry = RouteEntry("Destination", "/")

        assertEquals("Entry", entry.propertyName)
    }
}
