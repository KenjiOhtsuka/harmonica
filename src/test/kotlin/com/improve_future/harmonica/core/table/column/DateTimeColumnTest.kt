package com.improve_future.harmonica.core.table.column

import org.junit.Test
import kotlin.test.assertEquals

class DateTimeColumnTest {
    @Test
    fun testInstanceVariable() {
        val dateTimeColumn = DateTimeColumn("name")
        assertEquals("name", dateTimeColumn.name)
        assertEquals(false, dateTimeColumn.hasDefault)
        dateTimeColumn.default = "2018-01-02 21:24:23.234"
        assertEquals(true, dateTimeColumn.hasDefault)
    }
}