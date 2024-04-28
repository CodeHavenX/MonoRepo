package com.cramsan.cenit.lib

import kotlin.test.Test

class RowEntryTest {

    @Test
    fun `verify order of of RowEntry`() {
        val entry = RowEntry(
            "2021-01-01",
            "2021-01-02",
            "Hello, World!"
        )

        val list = entry.toList()

        assert(list[0] == "2021-01-01")
        assert(list[1] == "2021-01-02")
        assert(list[2] == "Hello, World!")
    }
}