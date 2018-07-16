package com.improve_future.harmonica.core.table.column

import org.junit.Test
import java.time.LocalDate
import java.time.LocalTime
import java.util.*
import kotlin.test.assertEquals

class DateColumnTest {
    @Test
    fun testDateConversion() {
        val dateColumn = DateColumn("name")
        assertEquals("name", dateColumn.name)

        // null default value check
        fun checkAllNull() {
            assertEquals(null, dateColumn.default)
            assertEquals(null, dateColumn.defaultDate)
            assertEquals(null, dateColumn.defaultLocalDate)
            assertEquals(false, dateColumn.hasDefault)
        }
        checkAllNull()
        dateColumn.defaultDate = null
        checkAllNull()
        dateColumn.defaultLocalDate = null
        checkAllNull()

        dateColumn.default = "2345-08-19"
        assertEquals(true, dateColumn.hasDefault)
        assertEquals("2345-08-19", dateColumn.default)
        val calendar = Calendar.getInstance()
        calendar.set(2345, 8, 19)
        val expectedDate = calendar.time
        assertEquals(
            calendar.time,
            dateColumn.defaultDate)
        assertEquals(
            LocalDate.of(2345, 8, 19), dateColumn.defaultLocalDate
        )

        dateColumn.defaultLocalDate = LocalDate.of(2435, 18, 9)
        assertEquals(true, dateColumn.hasDefault)
        assertEquals("2345-18-09", dateColumn.default)
        calendar.time = dateColumn.defaultDate
        //assertEquals()
    }
}